package com.example.store.repository;

import com.example.store.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {
    void deleteAllByCartId(String cartId);

    List<CartItem> findAllByCartId(String cartId);

    boolean existsByCartIdAndBookId(String cartId, String bookId);
}
