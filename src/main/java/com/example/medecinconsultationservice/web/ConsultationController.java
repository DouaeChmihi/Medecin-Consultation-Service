package com.example.medecinconsultationservice.web;

import com.example.medecinconsultationservice.Service.ConsultationService;
import com.example.medecinconsultationservice.dto.ConsultationDTO;
import com.example.medecinconsultationservice.entity.Consultation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    private final ConsultationService consultationService;

    public ConsultationController(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    @PostMapping
    public ResponseEntity<Object> createConsultation(@RequestBody ConsultationDTO consultationDTO) {
        try {
            ConsultationDTO createdConsultation = consultationService.saveConsultation(consultationDTO);
            return new ResponseEntity<>(createdConsultation, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", errorMessage);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<ConsultationDTO>> getAllConsultations() {
        List<ConsultationDTO> consultations = consultationService.getAllConsultations();
        if (consultations.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Retourne 204 si aucune consultation n'est trouvée
        }
        return new ResponseEntity<>(consultations, HttpStatus.OK); // Retourne 200 avec la liste des consultations
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultationDTO> getConsultationById(@PathVariable Long id) {
        try {
            ConsultationDTO consultationDTO = consultationService.getConsultationById(id);
            return new ResponseEntity<>(consultationDTO, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Si la consultation n'est pas trouvée, renvoie une réponse 404
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<Object> getConsultationsByPatientId(@PathVariable int patientId) {
        try {
            // Récupérer les consultations pour un patient donné
            List<ConsultationDTO> consultations = consultationService.getConsultationsByPatientId(patientId);

            if (consultations.isEmpty()) {
                // Si aucune consultation n'est trouvée, retourner un message personnalisé
                Map<String, String> response = new HashMap<>();
                response.put("message", "La consultation de ce patient ne se trouve pas");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // Si des consultations sont trouvées, les retourner avec un statut 200 OK
            return new ResponseEntity<>(consultations, HttpStatus.OK);

        } catch (RuntimeException ex) {
            // Si le patient n'est pas trouvé, retourner un message personnalisé pour le patient
            Map<String, String> response = new HashMap<>();
            response.put("message", "Patient non trouvé dans le microservice gRPC");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/medecin/{medecinId}")
    public ResponseEntity<Object> getConsultationsByMedecinId(@PathVariable int medecinId) {
        try {
            // Récupérer les consultations pour un médecin donné
            List<ConsultationDTO> consultations = consultationService.getConsultationsByMedecinId(medecinId);

            if (consultations.isEmpty()) {
                // Si aucune consultation n'est trouvée, retourner un message personnalisé
                Map<String, String> response = new HashMap<>();
                response.put("message", "La consultation de ce médecin ne se trouve pas");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // Si des consultations sont trouvées, les retourner avec un statut 200 OK
            return new ResponseEntity<>(consultations, HttpStatus.OK);

        } catch (RuntimeException ex) {
            // Si le médecin n'est pas trouvé, retourner un message personnalisé pour le médecin
            Map<String, String> response = new HashMap<>();
            response.put("message", "Médecin non trouvé dans le microservice User");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteConsultation(@PathVariable Long id) {
        try {
            consultationService.deleteConsultation(id);
            return new ResponseEntity<>("Consultation with ID " + id + " has been deleted.", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultationDTO> updateConsultation(@PathVariable Long id, @RequestBody ConsultationDTO consultationDTO) {
        try {
            ConsultationDTO updatedConsultation = consultationService.updateConsultation(id, consultationDTO);
            return new ResponseEntity<>(updatedConsultation, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }



}