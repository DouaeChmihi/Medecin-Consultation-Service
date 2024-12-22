package com.example.medecinconsultationservice.repository;

import com.example.medecinconsultationservice.entity.Calendrier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendrierRepository extends JpaRepository<Calendrier, Long> {
    // Rechercher les plages horaires disponibles par médecin (doctorId)
    List<Calendrier> findByDoctorId(int doctorId);
    // Rechercher les calendriers existants pour un médecin, où la plage horaire se chevauche
    List<Calendrier> findByDoctorIdAndStartDateBeforeAndEndDateAfter(
            int doctorId, LocalDateTime endDate, LocalDateTime startDate);
}
