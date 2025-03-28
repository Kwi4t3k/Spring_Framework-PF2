import org.apache.commons.codec.digest.DigestUtils;

public class hashTest {
    // do hashowania
//        String s = "ling lang guli guli";
//        System.out.println(s);
//        String sha256hex = DigestUtils.sha256Hex(s);
//        System.out.println(sha256hex);

    public static void main(String[] args) {
//        admin;admin123;admin;null
//        adam;adam123;user;null
//        maria;maria123;user;null

        String admin = "admin123";
        String adam = "adam123";
        String maria = "maria123";

        String hashAdmin = DigestUtils.sha256Hex(admin);
        String hashAdam = DigestUtils.sha256Hex(adam);
        String hashMaria = DigestUtils.sha256Hex(maria);

        System.out.println(hashAdmin);
        System.out.println(hashAdam);
        System.out.println(hashMaria);
    }
}
