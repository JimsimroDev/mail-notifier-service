package uk.jimsimrodev.notifier.connector.request;

import java.util.List;

public record MailRequestDTO(String htmlContent, SenderDTO sender, String subject, List<ToDTO> to) {
}
