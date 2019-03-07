package finalproject.javaee.util;

import org.mindrot.jbcrypt.BCrypt;

public class Crypt {

    private static final int SALT = 7;

    public static String hashPassword(String password_plaintext){
        String salt = BCrypt.gensalt(SALT);
        return BCrypt.hashpw(password_plaintext,salt);
    }

    public static boolean checkPassword(String password_plaintext, String stored_hash){
        return BCrypt.checkpw(password_plaintext,stored_hash);
    }

}
