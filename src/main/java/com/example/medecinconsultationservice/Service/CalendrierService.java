package com.example.medecinconsultationservice.Service;

import com.example.medecinconsultationservice.client.UserClient.UserServiceProxy;
import com.example.medecinconsultationservice.dto.CalendrierDTO;
import com.example.medecinconsultationservice.entity.Calendrier;
import com.example.medecinconsultationservice.repository.CalendrierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalendrierService {

    private final CalendrierRepository calendrierRepository;
    private final UserServiceProxy userServiceProxy;

    @Autowired
    public CalendrierService(CalendrierRepository calendrierRepository, UserServiceProxy userServiceProxy) {
        this.calendrierRepository = calendrierRepository;
        this.userServiceProxy = userServiceProxy;
    }

    // Méthode pour créer un calendrier avec validation
    public CalendrierDTO createCalendrier(CalendrierDTO calendrierDTO) {
        if (!userServiceProxy.validateMedecin(calendrierDTO.getDoctorId())) {
            throw new RuntimeException("Invalid Medecin ID");
        }

        if (isTimeSlotAvailable(calendrierDTO.getDoctorId(), calendrierDTO.getStartDate(), calendrierDTO.getEndDate())) {
            // Création d'un calendrier avec le constructeur par défaut
            Calendrier calendrier = new Calendrier();
            // Utilisation des setters pour initialiser les valeurs
            calendrier.setDoctorId(calendrierDTO.getDoctorId());
            calendrier.setStartDate(calendrierDTO.getStartDate());
            calendrier.setEndDate(calendrierDTO.getEndDate());

            // Enregistrement du calendrier
            calendrierRepository.save(calendrier);
            return mapToDTO(calendrier);
        } else {
            throw new RuntimeException("La plage horaire est déjà réservée ou invalide.");
        }
    }


    // Vérifie si la plage horaire est valide
    public boolean isTimeSlotAvailable(int doctorId, LocalDateTime startDate, LocalDateTime endDate) {
        // Recherche des plages horaires existantes pour ce médecin
        List<Calendrier> existingCalendars = calendrierRepository.findByDoctorIdAndStartDateBeforeAndEndDateAfter(
                doctorId, endDate, startDate);
        // Si des plages existent, cela signifie qu'il y a un chevauchement
        return existingCalendars.isEmpty();
    }

    // Récupère tous les calendriers
    public List<CalendrierDTO> getAllCalendriers() {
        List<Calendrier> calendriers = calendrierRepository.findAll();
        return calendriers.stream()
                .map(this::mapToDTO)  // Conversion de l'entité Calendrier en CalendrierDTO
                .collect(Collectors.toList());
    }


    // Méthode pour mapper l'entité Calendrier en CalendrierDTO
    private CalendrierDTO mapToDTO(Calendrier calendrier) {
        CalendrierDTO dto = new CalendrierDTO();
        dto.setId(calendrier.getId());
        dto.setDoctorId(calendrier.getDoctorId());
        dto.setStartDate(calendrier.getStartDate());
        dto.setEndDate(calendrier.getEndDate());
        return dto;
    }

    // Méthode pour obtenir la liste des calendriers d'un médecin
    public List<CalendrierDTO> getCalendrierByDoctor(int doctorId) {
        List<Calendrier> calendriers = calendrierRepository.findByDoctorId(doctorId);
        return calendriers.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void deleteCalendrier(Long id) {
        // Vérification si le calendrier existe avant de le supprimer
        if (!calendrierRepository.existsById(id)) {
            throw new RuntimeException("Calendrier avec ID " + id + " n'existe pas.");
        }

        // Suppression du calendrier
        calendrierRepository.deleteById(id);
    }


}
