package uk.jimsimrodev.notifier.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {
private static final Logger LOGGER= LoggerFactory.getLogger(MailConfig.class);
    @Value("${send.username}")
    private String userName;

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
      mailSenderImpl.setUsername(userName);
       mailSenderImpl.setPassword(password);

        Properties props = mailSenderImpl.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");

        props.put("mail.debug", "true");

        //Manejo de timeouts
       props.put("mail.smtp.connectiontimeout", String.valueOf(mailConnectionTimeout));
       props.put("mail.smtp.timeout", String.valueOf(mailTimeout));
       props.put("mail.smtp.writetimeout", String.valueOf(mailWriteTimeout));

        return mailSenderImpl;
    }
}
