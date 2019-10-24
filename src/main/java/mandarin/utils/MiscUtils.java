package mandarin.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Component
public class MiscUtils {
    private static JavaMailSender mailSender;

    @Autowired
    public void setMailSender(JavaMailSender mailSender) {
        MiscUtils.mailSender = mailSender;
    }

    public static void sendMail(String title, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(((JavaMailSenderImpl) mailSender).getUsername());
        message.setSubject(title);
        message.setText(content);
        message.setSentDate(new Date());
        mailSender.send(message);
    }
}
