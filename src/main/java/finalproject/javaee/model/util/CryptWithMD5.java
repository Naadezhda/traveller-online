package finalproject.javaee.model.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class CryptWithMD5 {
        private static MessageDigest digester;

        static {
            try {
                digester = MessageDigest.getInstance("MD5");
            }
            catch (NoSuchAlgorithmException e) {
                System.out.println("Ops" + e.getMessage());
            }
        }
        public static String crypt(String password) {
            if (password == null || password.length() == 0) {
                throw new IllegalArgumentException("String to encript cannot be null or zero length.");
            }
            digester.update(password.getBytes());
            byte[] hash = digester.digest();
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                if ((0xff & hash[i]) < 0x10) {
                    hexString.append("0" + Integer.toHexString((0xFF & hash[i])));
                }
                else {
                    hexString.append(Integer.toHexString(0xFF & hash[i]));
                }
            }
            return hexString.toString();
        }

}
