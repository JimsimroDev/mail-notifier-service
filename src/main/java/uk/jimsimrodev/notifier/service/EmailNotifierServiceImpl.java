package uk.jimsimrodev.notifier.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import uk.jimsimrodev.notifier.connector.EmailConnector;
import uk.jimsimrodev.notifier.connector.request.MailRequestDTO;
import uk.jimsimrodev.notifier.connector.request.SenderDTO;
import uk.jimsimrodev.notifier.connector.request.ToDTO;
import uk.jimsimrodev.notifier.dto.MailDetails;
import uk.jimsimrodev.notifier.dto.MailResponse;
import uk.jimsimrodev.notifier.enums.ApiError;
import uk.jimsimrodev.notifier.exception.ApiMailException;
import uk.jimsimrodev.notifier.service.impl.EmailNotifierService;

@Service
public class EmailNotifierServiceImpl implements EmailNotifierService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailNotifierServiceImpl.class);

    private final EmailConnector connector;

    @Value("${send.mail}")
    private String correPrueba;

    @Value("${send.username}")
    private String senderName;

    public EmailNotifierServiceImpl(EmailConnector connector) {
        this.connector = connector;
    }

    @Override
    @CircuitBreaker(name = "mailSender", fallbackMethod = "mailSenderFallback")
    public MailResponse sendMail(MailDetails mailDetails) {

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("'el ' dd/MM/yyy 'a las' HH:mm:ss");

        String fecha = LocalDateTime.now().format(formato);

        // Se envia al correo del desarrollador
        String miCuerpo = """
                <html>
                  <head>
                  </head>
                   <body>
                    <p>Nuevo formulario de envío en el portafolio</p>
                     Alguien acaba de enviar un formulario en dev.jimsimrodev.uk/
                     Esto es lo que tenía que decir:
                            <br/>
                             NOMBRE:
                              %s
                            <br/>
                             EMAIL DE CONTACTO:
                              %s
                            <br/>
                             MENSAJE:
                              %s
                             <br/>
                             FECHA: %s
                    </p>
                   </body>
                  </html>
                     """.formatted(mailDetails.name(), mailDetails.email(), mailDetails.message(), fecha);

        // cuerpo del mensaje
        String htmlContent = miCuerpo;

        // Correo registrado en brevo
        SenderDTO sender = new SenderDTO(correPrueba, senderName);

        // subject o asunto
        String subject = " Nuevo mensaje de " + mailDetails.name();

        // Llega a mi correo
        ToDTO to = new ToDTO(correPrueba, senderName);

        // Se envia al correo del desarrollador
        MailRequestDTO request = new MailRequestDTO(htmlContent, sender, subject, List.of(to));

        // Cuerpo para quien me lo envio
        String cuerpo = """
                "Hola, gracias por tu interés en mi perfil profesional. He recibido tu mensaje correctamente a través de mi portafolio.

                Actualmente me encuentro atendiendo mis compromisos académicos de 8.º semestre de Ingeniería de Sistemas, por lo que este es un mensaje automático. Revisaré tu consulta personalmente y te daré una respuesta lo antes posible.

                Un saludo cordial,
                Jimmis Jhoan Simanca Rojas
                Backend Developer | Java Specialist"

                            FECHA: %s
                    """
                .formatted(fecha);

        LOGGER.debug("Construyendo mensaje de asunto {}", request.subject());

        try {
            LOGGER.info("Intentando envio de Gmail para...: {}", correPrueba);
            connector.sendMail(request);
            LOGGER.info("Correo enviado exitosamente a ... {}", correPrueba);
        } catch (MailException e) {
            LOGGER.error(e.getClass().getName() + " Error: {}", e.getMessage());
            throw new ApiMailException(ApiError.INTERNAL_SERVER_ERROR);
        }

        // subject o asunto
        String subject1 = "¡Gracias por contactarme! - " + sender.name();

        ToDTO to1 = new ToDTO(mailDetails.email(), "Jimmis J Simanca");

        MailRequestDTO request1 = new MailRequestDTO(cuerpo, sender, subject1, List.of(to1));
        LOGGER.debug("Construyendo mensaje de asunto {}", request1.subject());

        // Se envia a quien llena el formulario
        connector.sendMail(request1);

        return new MailResponse("Mensaje Enviado");
    }

    private MailResponse fallBackSendEamil(MailDetails mailDetails, CallNotPermittedException e) {
        LOGGER.error("Circuito abierto. No se intento el envio para {}", mailDetails.email());
        return new MailResponse("Servicio temporalmente  deshabilitado por exceso de fallos");
    }

    private MailResponse fallBackSendEamil(MailDetails mailDetails, Exception e) {
        LOGGER.error("Error inesperado al enviar correo: {}", e.getMessage());
        throw new ApiMailException(ApiError.EXCEED_NUMBER_REQUEST);
    }
}
