package tn.sfm.reportingservice.controller;

import tn.sfm.reportingservice.dto.BilanAnnuelDTO;
import tn.sfm.reportingservice.dto.RapportActiviteDTO;
import tn.sfm.reportingservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportService reportService;

    // ===== BILANS =====

    @GetMapping("/bilan/{annee}")
    public ResponseEntity<BilanAnnuelDTO> getBilanAnnuel(@PathVariable int annee) {
        log.info("Requête de bilan annuel pour l'année {}", annee);
        return ResponseEntity.ok(reportService.getBilanAnnuel(annee));
    }

    @GetMapping("/bilan/courant")
    public ResponseEntity<BilanAnnuelDTO> getBilanAnneeCourante() {
        int anneeCourante = LocalDate.now().getYear();
        log.info("Requête de bilan pour l'année courante {}", anneeCourante);
        return ResponseEntity.ok(reportService.getBilanAnnuel(anneeCourante));
    }

    @GetMapping("/activite")
    public ResponseEntity<RapportActiviteDTO> getRapportActivite(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        log.info("Requête de rapport d'activité du {} au {}", debut, fin);
        return ResponseEntity.ok(reportService.getRapportActivite(debut, fin));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("Requête de statistiques globales");
        return ResponseEntity.ok(Map.of(
                "message", "Service de reporting opérationnel",
                "timestamp", LocalDate.now()
        ));
    }

    @GetMapping("/resume")
    public ResponseEntity<Map<String, Object>> getResume() {
        log.info("Requête de résumé des indicateurs");
        int anneeCourante = LocalDate.now().getYear();
        return ResponseEntity.ok(reportService.generateAnnualSummary(anneeCourante));
    }

    // ===== RAPPORTS ANNUELS (NOUVEAU) =====

    @GetMapping("/annual/{annee}")
    public ResponseEntity<Map<String, Object>> getAnnualReport(@PathVariable int annee) {
        log.info("Requête de rapport annuel pour l'année {}", annee);
        return ResponseEntity.ok(reportService.generateAnnualReport(annee, null, null, null));
    }

    @GetMapping("/annual/{annee}/filtered")
    public ResponseEntity<Map<String, Object>> getAnnualReportFiltered(
            @PathVariable int annee,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) String typeFacture,
            @RequestParam(required = false) String statut) {
        log.info("Requête de rapport annuel filtré pour l'année {}", annee);
        return ResponseEntity.ok(reportService.generateAnnualReport(annee, clientId, typeFacture, statut));
    }

    @GetMapping("/summary/{annee}")
    public ResponseEntity<Map<String, Object>> getAnnualSummary(@PathVariable int annee) {
        log.info("Requête de résumé annuel pour l'année {}", annee);
        return ResponseEntity.ok(reportService.generateAnnualSummary(annee));
    }
}