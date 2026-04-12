package com.hackathon.ehr.service;

import com.hackathon.ehr.dto.PrescriptionItemDTO;
import com.hackathon.ehr.dto.PrescriptionTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescriptionTemplateService {

    public List<PrescriptionTemplate> getAllTemplates() {
        return Arrays.asList(
                createMusclePainTemplate(),
                createJointPainTemplate(),
                createInjuryTemplate()
        );
    }

    public PrescriptionTemplate getTemplateByCondition(String condition) {
        return getAllTemplates().stream()
                .filter(t -> t.getCondition().equalsIgnoreCase(condition))
                .findFirst()
                .orElse(null);
    }

    private PrescriptionTemplate createMusclePainTemplate() {
        PrescriptionTemplate template = new PrescriptionTemplate();
        template.setCondition("Muscle Pain");
        template.setDiagnosis("Muscle strain / Mild muscle pain");
        template.setNotes("Patient reports mild muscle pain after physical activity. No swelling or severe injury observed. Recommended rest and basic pain relief.");
        template.setMedicines(Arrays.asList(
                createItem("Paracetamol", "500 mg", "2 times a day", "3–5 days"),
                createItem("Ibuprofen", "400 mg", "After food, 1–2 times a day", "3 days")
        ));
        template.setAdditionalAdvice("Rest the affected area. Apply hot/cold compress. Avoid heavy exercise. Stretch gently.");
        return template;
    }

    private PrescriptionTemplate createJointPainTemplate() {
        PrescriptionTemplate template = new PrescriptionTemplate();
        template.setCondition("Joint Pain");
        template.setDiagnosis("Mild joint inflammation / Arthritis flare-up");
        template.setNotes("Patient reports joint pain and stiffness. Mild inflammation noted. Recommended anti-inflammatory medication and rest.");
        template.setMedicines(Arrays.asList(
                createItem("Ibuprofen", "400 mg", "2–3 times a day after food", "5–7 days"),
                createItem("Paracetamol", "500 mg", "As needed for pain", "5 days")
        ));
        template.setAdditionalAdvice("Rest the joint. Apply ice packs. Gentle range-of-motion exercises. Consult if pain persists.");
        return template;
    }

    private PrescriptionTemplate createInjuryTemplate() {
        PrescriptionTemplate template = new PrescriptionTemplate();
        template.setCondition("Injury");
        template.setDiagnosis("Minor injury / Sprain");
        template.setNotes("Patient reports minor injury with swelling and pain. No fracture suspected. Recommended RICE protocol and pain management.");
        template.setMedicines(Arrays.asList(
                createItem("Paracetamol", "500 mg", "2–3 times a day", "3–5 days"),
                createItem("Diclofenac Gel", "Apply locally", "2–3 times a day", "5 days")
        ));
        template.setAdditionalAdvice("Follow RICE: Rest, Ice, Compression, Elevation. Avoid weight-bearing. Seek medical attention if swelling increases.");
        return template;
    }

    private PrescriptionItemDTO createItem(String name, String dosage, String frequency, String duration) {
        PrescriptionItemDTO item = new PrescriptionItemDTO();
        item.setMedicineName(name);
        item.setDosage(dosage);
        item.setFrequency(frequency);
        item.setDuration(duration);
        return item;
    }
}
