package uk.jimsimrodev.notifier.controller;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import uk.jimsimrodev.notifier.controller.resource.SendMailResource;
import uk.jimsimrodev.notifier.dto.MailDetails;
import uk.jimsimrodev.notifier.dto.MailResponse;
import uk.jimsimrodev.notifier.enums.ApiError;
import uk.jimsimrodev.notifier.exception.ApiMailException;
import uk.jimsimrodev.notifier.service.EmailNotifierServiceImpl;

@RestController
@RequestMapping("send/mail")
public class NotificationController implements SendMailResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);

    private final EmailNotifierServiceImpl emailNotifierServiceImpl;

    public NotificationController(EmailNotifierServiceImpl emailNotifierServiceImpl) {
        this.emailNotifierServiceImpl = emailNotifierServiceImpl;
    }

    @PostMapping
    @RateLimiter(name="post-sendMail", fallbackMethod = "fallbackSendMail")
    public ResponseEntity<MailResponse> sendMail(@RequestBody @Valid MailDetails mailDetails) {

        LOGGER.info("La información recibida es: {}", mailDetails);
        
        MailResponse response = emailNotifierServiceImpl.sendMail(mailDetails);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    private ResponseEntity<MailResponse> fallbackSendMail(MailDetails mailDetails, RequestNotPermitted e) {
        throw new ApiMailException(ApiError.EXCEED_NUMBER_REQUEST);
    }
}
