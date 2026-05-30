package uk.jimsimrodev.notifier.service.impl;

import uk.jimsimrodev.notifier.dto.MailDetails;
import uk.jimsimrodev.notifier.dto.MailResponse;

public interface EmailNotifierService {
    MailResponse sendMail(MailDetails mailDetails);
}
