package com.example.medecinconsultationservice.repository;



import com.example.medecinconsultationservice.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    List<Consultation> findByPatientId(int patientId);
    List<Consultation> findByMedecinId(int medecinId);
}
