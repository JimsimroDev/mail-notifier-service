package uk.jimsimrodev.notifier.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    @Value("${send.mail}")
    private String emailUser;

    @Value("${send.password}")
    private String password;

    @Value("${app.mail-settings.host}")
    private String mailHost;

    @Value("${app.mail-settings.port}")
    private int mailPort;
    @Value("${app.mail-settings.connectionTimeout}")
    private int mailConnectionTimeout;

    @Value("${app.mail-settings.timeout}")
    private int mailTimeout;

    @Value("${app.mail-settings.writeTimeout}")
    private int mailWriteTimeout;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();

        mailSenderImpl.setHost(mailHost);
        mailSenderImpl.setPort(mailPort);
        mailSenderImpl.setUsername(emailUser);
        mailSenderImpl.setPassword(password);

        Properties props = mailSenderImpl.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.debug", "true");

        //Manejo de timeouts
       props.put("mail.smtp.connectiontimeout", String.valueOf(mailConnectionTimeout));
       props.put("mail.smtp.timeout", String.valueOf(mailTimeout));
       props.put("mail.smtp.writetimeout", String.valueOf(mailWriteTimeout));

        return mailSenderImpl;
    }
}
