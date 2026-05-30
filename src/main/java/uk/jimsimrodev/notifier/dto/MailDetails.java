package uk.jimsimrodev.notifier.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MailDetails(
        @NotBlank(message = "Debe llenar todos los campos") 
        String name,

        @Email(message = "Formato de email invalido favor validar")
        @NotBlank(message = "Debe llenar todos los campos")
        String email,

        @NotBlank(message = "Debes llenar todos los campos")
        String message) {
}
