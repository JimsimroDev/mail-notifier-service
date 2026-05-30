package uk.jimsimrodev.notifier.connector;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.resolver.DefaultAddressResolverGroup;
import reactor.netty.http.client.HttpClient;
import uk.jimsimrodev.notifier.connector.Response.MailResponseDTO;
import uk.jimsimrodev.notifier.connector.request.MailRequestDTO;        

@Component
public class EmailConnector {
        private final static String API_KEY = System.getenv("API_KEY");
        private final static String BASE_URL = System.getenv("BASE_URL");

        public MailResponseDTO sendMail(MailRequestDTO request) {
                WebClient webClient = WebClient.builder()
                                .baseUrl(BASE_URL)
                                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                                .clientConnector(new ReactorClientHttpConnector(
                                                HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE)))
                                .build();

                return webClient.post()
                                .header("api-key", API_KEY)
                                .bodyValue(request)
                                .retrieve()
                                .bodyToMono(MailResponseDTO.class)
                                .share()
                                .block();
        }
}
