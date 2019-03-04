package finalproject.javaee.model.util;

import finalproject.javaee.model.pojo.User;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;


public class MailUtil {

public static void sendMail(String from, String to, String subject, String body) throws MessagingException {
    Properties properties = new Properties();
    properties.put("mail.smtp.auth", "true");
    properties.put("mail.smtp.starttls.enable", "true");
    properties.put("mail.smtp.host", "smtp.gmail.com");
    properties.put("mail.smtp.port", "587");

    Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication("ittalentsX@gmail.com", "itTalents10");
        }
    });

    Message msg = new MimeMessage(session);
    msg.setFrom(new InternetAddress(from, false));

    msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
    msg.setSubject(subject);
    msg.setText(body);
    msg.setSentDate(new Date());

    MimeBodyPart messageBodyPart = new MimeBodyPart();
    messageBodyPart.setContent("Tutorials point email", "text/html");
    Transport.send(msg);
}
}
