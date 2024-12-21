package com.example.medecinconsultationservice.client.UserClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserServiceProxy {

    private final RestTemplate restTemplate;

    @Value("${user.service.url}") // Set UserService URL in application.properties
    private String userServiceUrl;

    public UserServiceProxy(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean validateMedecin(int medecinId) {
        try {
            String url = userServiceUrl + "/graphql"; // GraphQL endpoint
            String query = "{\"query\":\"query { findMedecinById(id:" + medecinId + ") { id } }\"}";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(query, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            // Check if the response contains valid medecin data
            if (response.getStatusCode().is2xxSuccessful() && response.getBody().contains("\"id\":")) {
                return true;
            }

        } catch (Exception e) {
            System.err.println("Error validating Medecin ID: " + e.getMessage());
        }
        return false;
    }
}
