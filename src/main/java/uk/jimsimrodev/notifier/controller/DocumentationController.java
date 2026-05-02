package uk.jimsimrodev.notifier.controller;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Hidden
@Controller
@RequestMapping("/documentation")
public class DocumentationController {

    @GetMapping
    public void redirectToDocumentation(HttpServletResponse response) {

        try {
            response.sendRedirect("swagger-ui.html");
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder("UNEXPECTED ERROR");
            if(e.getMessage()!=null) {
                sb.append(e.getMessage());
            }
        }
    }
}
