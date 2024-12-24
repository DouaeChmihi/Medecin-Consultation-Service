package com.example.medecinconsultationservice.Service;
import com.example.medecinconsultationservice.client.PatientGrpcClient.PatientGrpcClient;
import com.example.medecinconsultationservice.client.UserClient.UserServiceProxy;
import com.example.medecinconsultationservice.dto.ConsultationDTO;
import com.example.medecinconsultationservice.entity.Consultation;
import com.example.medecinconsultationservice.repository.ConsultationRepository;
import patient.PatientOuterClass;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConsultationService {
    private final ConsultationRepository consultationRepository;
    private final UserServiceProxy userServiceProxy;
    private final PatientGrpcClient patientGrpcClient;

    public ConsultationService(ConsultationRepository consultationRepository, UserServiceProxy userServiceProxy, PatientGrpcClient patientGrpcClient) {
        this.consultationRepository = consultationRepository;
        this.userServiceProxy = userServiceProxy;
        this.patientGrpcClient = patientGrpcClient;
    }

    public ConsultationDTO saveConsultation(ConsultationDTO consultationDTO) {
        // Validate Medecin ID
        if (!userServiceProxy.validateMedecin(consultationDTO.getMedecinId())) {
            throw new RuntimeException("Invalid Medecin ID");
        }

        // Retrieve patient details via gRPC
        PatientOuterClass.Patient patient = patientGrpcClient.getPatientDetails(consultationDTO.getPatientId());
        if (patient == null) {
            throw new RuntimeException("Patient not found");
        }

        Consultation consultation = new Consultation();
        consultation.setConsultationDate(consultationDTO.getConsultationDate());
        consultation.setPatientId(patient.getId()); // Use the ID from the patient details
        consultation.setMedecinId(consultationDTO.getMedecinId());
        consultation.setDiagnostic(consultationDTO.getDiagnostic());
        consultation.setPrescription(consultationDTO.getPrescription());
        consultation.setRecommandations(consultationDTO.getRecommandations());

        Consultation savedConsultation = consultationRepository.save(consultation);
        System.out.println("Consultation ID before save: " + consultation.getId());
        System.out.println("Consultation ID after save: " + savedConsultation.getId());
        System.out.println("patient de: " + patient.getName());



        // Map the entity to DTO
        return mapToDTO(savedConsultation);
    }

    public List<ConsultationDTO> getAllConsultations() {
        List<Consultation> consultations = consultationRepository.findAll();
        return consultations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ConsultationDTO getConsultationById(Long id) {
        Optional<Consultation> consultationOptional = consultationRepository.findById(id);
        if (consultationOptional.isPresent()) {
            return mapToDTO(consultationOptional.get());
        } else {
            throw new RuntimeException("Consultation not found with ID: " + id);
        }
    }
    public List<ConsultationDTO> getConsultationsByPatientId(int patientId) {
        // Appel gRPC pour vérifier si le patient existe
        PatientOuterClass.Patient patient = patientGrpcClient.getPatientDetails(patientId);

        // Si le patient n'existe pas, lancer une exception ou retourner un message d'erreur
        if (patient == null) {
            throw new RuntimeException("Patient not found in gRPC service");
        }

        // Si le patient existe, récupérez les consultations
        List<Consultation> consultations = consultationRepository.findByPatientId(patientId);

        // Mapper les entités Consultation vers ConsultationDTO
        return consultations.stream().map(this::mapToDTO).collect(Collectors.toList());
    }


    public List<ConsultationDTO> getConsultationsByMedecinId(int medecinId) {
        // Vérifier si le médecin existe via le microservice User
        if (!userServiceProxy.validateMedecin(medecinId)) {
            throw new RuntimeException("Medecin non trouvé dans le microservice User");
        }

        // Si le médecin existe, récupérer les consultations
        List<Consultation> consultations = consultationRepository.findByMedecinId(medecinId);

        // Mapper les entités Consultation vers ConsultationDTO
        return consultations.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public void deleteConsultation(Long id) {
        // Check if the consultation exists
        Optional<Consultation> consultationOptional = consultationRepository.findById(id);
        if (consultationOptional.isEmpty()) {
            throw new RuntimeException("Consultation not found with ID: " + id);
        }

        // Delete the consultation
        consultationRepository.deleteById(id);
        System.out.println("Consultation with ID " + id + " has been deleted.");
    }

    public ConsultationDTO updateConsultation(Long id, ConsultationDTO consultationDTO) {
        // Check if the consultation exists
        Optional<Consultation> consultationOptional = consultationRepository.findById(id);
        if (consultationOptional.isEmpty()) {
            throw new RuntimeException("Consultation not found with ID: " + id);
        }

        // Retrieve the existing consultation
        Consultation consultation = consultationOptional.get();

        // Update the consultation details
        consultation.setConsultationDate(consultationDTO.getConsultationDate());
        consultation.setMedecinId(consultationDTO.getMedecinId());
        consultation.setDiagnostic(consultationDTO.getDiagnostic());
        consultation.setPrescription(consultationDTO.getPrescription());
        consultation.setRecommandations(consultationDTO.getRecommandations());

        // Save the updated consultation
        Consultation updatedConsultation = consultationRepository.save(consultation);
        System.out.println("Consultation updated with ID: " + updatedConsultation.getId());

        // Map the entity to DTO and return
        return mapToDTO(updatedConsultation);
    }


    private ConsultationDTO mapToDTO(Consultation consultation) {
        ConsultationDTO dto = new ConsultationDTO();
        dto.setId(consultation.getId());
        dto.setConsultationDate(consultation.getConsultationDate());
        dto.setPatientId(consultation.getPatientId());
        dto.setMedecinId(consultation.getMedecinId());
        dto.setDiagnostic(consultation.getDiagnostic());
        dto.setPrescription(consultation.getPrescription());
        dto.setRecommandations(consultation.getRecommandations());
        return dto;
    }
}