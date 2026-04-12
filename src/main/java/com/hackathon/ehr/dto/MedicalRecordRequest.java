package com.hackathon.ehr.dto;

import com.hackathon.ehr.enums.RecordType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MedicalRecordRequest {
    @NotNull
    private Long patientId;

    @NotNull
    private RecordType recordType;

    @NotBlank
    private String title;

    private String description;
}
