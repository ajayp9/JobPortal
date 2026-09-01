package com.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.backend.dto.request.RegistrationEmailRequest;
import com.backend.entities.User;
import com.backend.exception.BadRequestException;

@Service
public class RegistrationEmailClient {

    private final RestClient restClient;

    @Value("${registration.email.service.url}")
    private String emailServiceUrl;

    public RegistrationEmailClient(
            RestClient.Builder builder) {

        this.restClient = builder.build();
    }

    public void sendEmail(User user) {

        RegistrationEmailRequest request =
                new RegistrationEmailRequest(
                        user.getName(),
                        user.getEmail(),
                        user.getRole().name()
                );

        try {

            restClient.post()
                    .uri(emailServiceUrl)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();

            System.out.println(
                    "Registration email sent successfully for: "
                            + user.getEmail()
            );

        } catch (Exception exception) {

            System.out.println(
                    "Registration email failed: "
                            + exception.getMessage()
            );

            throw new BadRequestException(
                    "Registration completed, but confirmation email could not be sent"
            );
        }
    }
}