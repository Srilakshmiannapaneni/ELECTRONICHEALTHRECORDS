package com.hackathon.ehr.controller;

import com.hackathon.ehr.dto.PrescriptionTemplate;
import com.hackathon.ehr.service.PrescriptionTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescription-templates")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PrescriptionTemplateController {

    private final PrescriptionTemplateService templateService;

    @GetMapping
    public ResponseEntity<List<PrescriptionTemplate>> getAllTemplates() {
        return ResponseEntity.ok(templateService.getAllTemplates());
    }

    @GetMapping("/{condition}")
    public ResponseEntity<PrescriptionTemplate> getTemplateByCondition(@PathVariable String condition) {
        PrescriptionTemplate template = templateService.getTemplateByCondition(condition);
        if (template != null) {
            return ResponseEntity.ok(template);
        }
        return ResponseEntity.notFound().build();
    }
}
