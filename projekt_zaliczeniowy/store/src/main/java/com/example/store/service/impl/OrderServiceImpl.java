package com.example.store.service.impl;

import com.example.store.model.Book;
import com.example.store.model.Order;
import com.example.store.model.OrderStatus;
import com.example.store.repository.BookRepository;
import com.example.store.repository.CartItemRepository;
import com.example.store.repository.OrderRepository;
import com.example.store.service.OrderService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;

    @Value("${STRIPE_API_KEY}")
    private String apiKey;
    @Value("${WEBHOOK_SECRET}")
    private String webhookSecret;

    @Override
    @Transactional
    public String createCheckoutSession(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order with ID: " + orderId + " not found"));

        Stripe.apiKey = apiKey;

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName("Order " + orderId)
                        .build();

        var amount = calculatePrice(order);

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("pln")
                        .setUnitAmount((long) (amount))
                        .setProductData(productData)
                        .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(priceData)
                        .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .addLineItem(lineItem)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .putMetadata("orderId", orderId)
                .setSuccessUrl("http://localhost:8080/api/payments/success")
                .setCancelUrl("http://localhost:8080/api/payments/cancel")
                .build();

        try {
            Session session = Session.create(params);

            order.setStripeSessionId(session.getId());
            order.setStatus(OrderStatus.PENDING);
            order.setCreatedAt(LocalDateTime.now());

            orderRepository.save(order);
            return session.getUrl();
        } catch (Exception e) {
            throw new RuntimeException("Stripe session creation failed", e);
        }
    }

    @Override
    @Transactional
    public void handleWebhook(String payload, String signature) {
        Stripe.apiKey = apiKey;

        Event event;

        try {
            event = Webhook.constructEvent(payload, signature, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new RuntimeException("Invalid signature", e);
        }

        if ("checkout.session.completed".equals(event.getType())) {
            StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElseThrow();
            String sessionId = ((Session) stripeObject).getId();

            if (sessionId != null) {
                orderRepository.findByStripeSessionId(sessionId).ifPresent(order -> {
                    // PAID
                    order.setStatus(OrderStatus.PAID);
                    order.setPaidAt(LocalDateTime.now());
                    orderRepository.save(order);

                    deactivateBooks(order);

                    scheduleShippingAndDelivery(order.getId());
                });
            }
        }
    }

    private void deactivateBooks(Order order) {
        order.getItems().forEach(item -> {
            Book book = item.getBook();
            book.setActive(false);
            bookRepository.save(book);
        });
    }

    private void scheduleShippingAndDelivery(String orderId) {
        // -> +30sec SHIPPED -> +15sec DELIVERED
        new Thread(() -> {
            try {
                Thread.sleep(30000);
                orderRepository.findById(orderId).ifPresent(o -> {
                    o.setStatus(OrderStatus.SHIPPED);
                    orderRepository.save(o);
                });

                Thread.sleep(15000);
                orderRepository.findById(orderId).ifPresent(o -> {
                    o.setStatus(OrderStatus.DELIVERED);
                    orderRepository.save(o);
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private double calculatePrice(Order order) { // zmienić na Order (wszystkie itemki * 100 żeby w groszach)
        double total = order.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        return total * 100; // cena * 100 żeby było w groszach
    }
}