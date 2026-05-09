package uk.jimsimrodev.notifier.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    private final JavaMailSender mailSender;

    @Value("${send.mail}")
    private String correPrueba;

    public EmailNotifierServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    @Override
    @CircuitBreaker(name="mailSender",fallbackMethod = "mailSenderFallback")
    public MailResponse sendMail(MailDetails mailDetails) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("'el ' dd/MM/yyy 'a las' HH:mm:ss");

        String fecha = LocalDateTime.now().format(formato);

        String miCuerpo = """
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
                    """.formatted( mailDetails.name(), mailDetails.email(), mailDetails.message(), fecha);

        String cuerpo = """
                "Hola, gracias por tu interés en mi perfil profesional. He recibido tu mensaje correctamente a través de mi portafolio.
                
                Actualmente me encuentro atendiendo mis compromisos académicos de 8.º semestre de Ingeniería de Sistemas, por lo que este es un mensaje automático. Revisaré tu consulta personalmente y te daré una respuesta lo antes posible.
                
                Un saludo cordial,
                Jimmis Jhoan Simanca Rojas
                Backend Developer | Java Specialist"

                            FECHA: %s
                    """.formatted(fecha);
//Se envia al correo del  desarrollador
        simpleMailMessage.setFrom(correPrueba);// quien lo envia
        simpleMailMessage.setTo(correPrueba); // a donde se envia

        simpleMailMessage.setSubject("[Portafolio] Nuevo mensaje de " + mailDetails.name());
        LOGGER.debug("Construyendo mensaje de asunto {}",simpleMailMessage.getSubject());

        simpleMailMessage.setReplyTo(mailDetails.email());
        simpleMailMessage.setText(miCuerpo);


        try {
            LOGGER.info("Intentando envio de Gmail para...: {}", correPrueba);
            mailSender.send(simpleMailMessage);
            LOGGER.info("Correo enviado exitosamente a ... {}", correPrueba);
        } catch (MailException e) {
            LOGGER.error(e.getClass().getName()+" Error: {}", e.getMessage());
            throw new ApiMailException(ApiError.INTERNAL_SERVER_ERROR);
        }
        //Se envia a quien llena el formulario
        simpleMailMessage.setFrom(correPrueba);// quien lo envia
        simpleMailMessage.setTo(mailDetails.email()); // a donde se envia

        simpleMailMessage.setSubject("¡Gracias por contactarme! - Jimmis J. Simanca");
        LOGGER.debug("Construyendo mensaje de asunto {}",simpleMailMessage.getSubject());

        simpleMailMessage.setReplyTo(correPrueba);
        simpleMailMessage.setText(cuerpo);

        mailSender.send(simpleMailMessage);

        return new MailResponse("Mensaje Enviado");
    }

    private MailResponse fallBackSendEamil(MailDetails mailDetails, CallNotPermittedException e){
        LOGGER.error("Circuito abierto. No se intento el envio para {}", mailDetails.email());
        return new MailResponse("Servicio temporalmente  deshabilitado por exceso de fallos");
    }

    private MailResponse fallBackSendEamil(MailDetails mailDetails, Exception e){
        LOGGER.error("Error inesperado al enviar correo: {}", e.getMessage());
        throw new ApiMailException(ApiError.EXCEED_NUMBER_REQUEST);
    }
}
