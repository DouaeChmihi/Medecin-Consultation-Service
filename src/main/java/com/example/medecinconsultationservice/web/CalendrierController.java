package com.example.medecinconsultationservice.web;

import com.example.medecinconsultationservice.Service.CalendrierService;
import com.example.medecinconsultationservice.dto.CalendrierDTO;
import com.example.medecinconsultationservice.entity.Calendrier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@RestController
@RequestMapping("/calendrier")
public class CalendrierController {

    private final CalendrierService calendrierService;

    @Autowired
    public CalendrierController(CalendrierService calendrierService) {
        this.calendrierService = calendrierService;
    }

    // Point de terminaison pour créer un calendrier de disponibilité du médecin
    @PostMapping("/create")
    public ResponseEntity<?> createCalendrier(@RequestBody CalendrierDTO calendrierDTO) {
        try {
            CalendrierDTO createdCalendrier = calendrierService.createCalendrier(calendrierDTO);
            return new ResponseEntity<>(createdCalendrier, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // En cas d'erreur, retourne un message d'erreur
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Point de terminaison pour obtenir la liste des calendriers d'un médecin
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<CalendrierDTO>> getCalendrierByDoctor(@PathVariable int doctorId) {
        List<CalendrierDTO> calendriers = calendrierService.getCalendrierByDoctor(doctorId);
        return new ResponseEntity<>(calendriers, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CalendrierDTO>> getAllCalendriers() {
        List<CalendrierDTO> calendriers = calendrierService.getAllCalendriers();
        return new ResponseEntity<>(calendriers, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCalendrier(@PathVariable Long id) {
        try {
            calendrierService.deleteCalendrier(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Retourne 204 No Content si tout va bien
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Retourne 404 si le calendrier n'existe pas
        }
    }

    // Point de terminaison pour vérifier si une plage horaire est disponible
    @GetMapping("/checkAvailability")
    public ResponseEntity<Boolean> checkAvailability(@RequestParam int doctorId, @RequestParam String startDate, @RequestParam String endDate) {
        // Définir le format de la date attendu
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        // Convertir les chaînes en LocalDateTime
        LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);

        // Vérifier la disponibilité en appelant le service
        boolean isAvailable = calendrierService.isTimeSlotAvailable(doctorId, startDateTime, endDateTime);

        // Retourner la réponse
        return new ResponseEntity<>(isAvailable, HttpStatus.OK); }
}