package uk.jimsimrodev.notifier.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import uk.jimsimrodev.notifier.dto.MailDetails;
import uk.jimsimrodev.notifier.dto.MailResponse;
import uk.jimsimrodev.notifier.enums.ApiError;
import uk.jimsimrodev.notifier.exception.ApiMailException;
import uk.jimsimrodev.notifier.service.impl.EmailNotifierService;

@Service
public class EmailNotifierServiceImpl implements EmailNotifierService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailNotifierServiceImpl.class);

    private JavaMailSender mailSender;

    @Value("${send.mail}")
    private String emailUser;

    public EmailNotifierServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    @CircuitBreaker(name="mailSender",fallbackMethod = "mailSenderFallback")
    public MailResponse sendMail(MailDetails mailDetails) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyy - HH:mm:ss");

        String fecha = LocalDateTime.now().format(formato);

        String cuerpo = String.format("""
                Nuevo formulario de envío en el portafolio
                Alguien acaba de enviar un formulario en dev.jimsimrodev.uk/
                 Esto es lo que tenía que decir:

                            NOMBRE:
                             %s

                            EMAIL DE CONTACTO:
                             %s

                            MENSAJE:
                             %s

                            FECHA: %s
                    """, mailDetails.name(), mailDetails.email(), mailDetails.message(), fecha);

        simpleMailMessage.setFrom(emailUser);// quien lo envia
        simpleMailMessage.setTo(emailUser);

        simpleMailMessage.setSubject("[Portafolio] Nuevo mensaje de " + mailDetails.name());
        LOGGER.debug("Construyendo mensaje de asunto {}",simpleMailMessage.getSubject());

        simpleMailMessage.setReplyTo(mailDetails.email());
        simpleMailMessage.setText(cuerpo);

        try {
            LOGGER.info("Intentando envio de Gmail para...: {}", mailDetails.email());
            mailSender.send(simpleMailMessage);
            LOGGER.info("Correo enviado exitosamente a ... {}", emailUser);
        } catch (MailException e) {
            LOGGER.error(e.getClass().getName()+" Error: {}", e.getMessage());
            throw new ApiMailException(ApiError.INTERNAL_SERVER_ERROR);
        }

        MailResponse response = new MailResponse("Mensaje Enviado");

        return response;
    }

    private MailResponse fallBackSendEamil(MailDetails mailDetails, CallNotPermittedException e){
        LOGGER.error("Circuito abierto. No se intento el envio para {}", mailDetails.email());
        return new MailResponse("Servicio temporalmente  deshabilitado por exceso de fallos");
    }

    private MailResponse fallBackSendEamil(MailDetails mailDetails, Exception e){
        LOGGER.error("Error inesperado al enviar correo: {}", e.getMessage());
        throw new ApiMailException(ApiError.VALIDATION_ERROR);
    }
}
