package com.hackathon.ehr.dto;

import lombok.Data;
import java.util.List;

@Data
public class PrescriptionTemplate {
    private String condition;
    private String diagnosis;
    private String notes;
    private List<PrescriptionItemDTO> medicines;
    private String additionalAdvice;
}
