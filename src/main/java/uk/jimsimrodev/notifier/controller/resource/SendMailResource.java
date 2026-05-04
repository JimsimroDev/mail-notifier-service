package uk.jimsimrodev.notifier.controller.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import uk.jimsimrodev.notifier.dto.ErrorDTO;
import uk.jimsimrodev.notifier.dto.MailDetails;
import uk.jimsimrodev.notifier.dto.MailResponse;

@Tag(name="Send Mail",description="Operaciones relacionadas con el envio de correo")
public interface SendMailResource {
   @Operation(description="Post the information of send mail",responses={
          @ApiResponse(responseCode="200",
                  description="Return the send mail",
                  content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,schema = @Schema(implementation = MailResponse.class))
          ),

           @ApiResponse(responseCode="400",
                   description="Bad Request (@NotBlank, @Email)",
                   content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,schema = @Schema(implementation = ErrorDTO.class))),

           @ApiResponse(responseCode="429",
                   description="Too Many Request (Rate Limiter)",
                   content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,schema = @Schema(implementation = ErrorDTO.class))),

           @ApiResponse(responseCode="500",
                   description="Internel Server Error",
                   content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,schema = @Schema(implementation = ErrorDTO.class))),

           @ApiResponse(responseCode="503",
                   description="Service Unavailable (Circuit Bracker)",
                   content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,schema = @Schema(implementation = ErrorDTO.class))
           ),
   },
           requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(name="SendMail",summary="Example post send mail",value= """
                   {
                     "name":"prueba",
                     "email":"correopreuba@coreo.com",
                     "message":"esto es una prueba"
                   } 
                   """)))
   )

    public ResponseEntity<MailResponse> sendMail(@RequestBody @Valid MailDetails mailDetails);
}
