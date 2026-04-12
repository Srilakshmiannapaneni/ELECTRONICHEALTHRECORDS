package com.hackathon.ehr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class PrescriptionRequest {
    @NotNull
    private Long consultationId;

    @NotBlank
    private String digitalSignature;

    @NotEmpty
    private List<PrescriptionItemDTO> items;
}
