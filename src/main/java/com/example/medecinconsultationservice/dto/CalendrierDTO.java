package com.example.medecinconsultationservice.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CalendrierDTO {

    private int id;

    private int doctorId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public CalendrierDTO(int id, int doctorId, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.doctorId = doctorId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public CalendrierDTO() {
    }
}
