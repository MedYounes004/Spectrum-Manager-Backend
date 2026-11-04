package tn.sfm.reportingservice.service;

import tn.sfm.reportingservice.dto.BilanAnnuelDTO;
import tn.sfm.reportingservice.dto.RapportActiviteDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final RestTemplate restTemplate;

    private static final String GATEWAY_URL = "http://localhost:8083/api";
    private static final String AUTORISATION_URL = GATEWAY_URL + "/autorisations";
    private static final String FACTURE_URL = GATEWAY_URL + "/factures";
    private static final String CLIENT_URL = GATEWAY_URL + "/clients";

    // ===== MÉTHODES EXISTANTES =====

    public BilanAnnuelDTO getBilanAnnuel(int annee) {
        log.info("Génération du bilan annuel pour l'année {}", annee);

        List<Map<String, Object>> autorisations = getAutorisations();
        List<Map<String, Object>> factures = getFactures();

        long totalAutorisations = autorisations.size();
        long autorisationsActives = countByStatut(autorisations, "ACTIVE");
        long autorisationsExpirees = countByStatut(autorisations, "EXPIREE");
        long autorisationsEnAttente = countByStatut(autorisations, "EN_ATTENTE");
        long nouvellesAutorisations = countNewAutorisations(autorisations, annee);

        double totalRecettes = factures.stream()
                .filter(f -> "PAYEE".equals(f.get("statut")))
                .mapToDouble(f -> ((Number) f.get("montant")).doubleValue())
                .sum();

        Map<String, Long> autorisationsParType = autorisations.stream()
                .collect(Collectors.groupingBy(
                        a -> (String) a.get("service"),
                        Collectors.counting()
                ));

        Map<String, Double> recettesParType = factures.stream()
                .filter(f -> "PAYEE".equals(f.get("statut")))
                .collect(Collectors.groupingBy(
                        f -> (String) f.get("typeAutorisation"),
                        Collectors.summingDouble(f -> ((Number) f.get("montant")).doubleValue())
                ));

        double tauxCroissance = totalAutorisations > 0 ? ((double) nouvellesAutorisations / totalAutorisations) * 100 : 0;

        return new BilanAnnuelDTO(
                annee,
                totalAutorisations,
                autorisationsActives,
                autorisationsExpirees,
                autorisationsEnAttente,
                nouvellesAutorisations,
                totalRecettes,
                autorisationsParType,
                recettesParType,
                Math.round(tauxCroissance * 100.0) / 100.0
        );
    }

    public RapportActiviteDTO getRapportActivite(LocalDate dateDebut, LocalDate dateFin) {
        log.info("Génération du rapport d'activité du {} au {}", dateDebut, dateFin);

        List<Map<String, Object>> autorisations = getAutorisations();
        List<Map<String, Object>> factures = getFactures();

        long totalDemandes = autorisations.size();
        long demandesApprouvees = countByStatut(autorisations, "ACTIVE");
        long demandesRefusees = countByStatut(autorisations, "ANNULEE");
        long demandesEnAttente = countByStatut(autorisations, "EN_ATTENTE");

        double montantTotal = factures.stream()
                .mapToDouble(f -> ((Number) f.get("montant")).doubleValue())
                .sum();

        double montantPerçu = factures.stream()
                .filter(f -> "PAYEE".equals(f.get("statut")))
                .mapToDouble(f -> ((Number) f.get("montant")).doubleValue())
                .sum();

        Map<String, Long> demandesParType = autorisations.stream()
                .collect(Collectors.groupingBy(
                        a -> (String) a.get("service"),
                        Collectors.counting()
                ));

        Map<String, Double> recettesParMois = new HashMap<>();
        // Simuler des recettes par mois
        recettesParMois.put("Janvier", 5000.0);
        recettesParMois.put("Février", 4500.0);
        recettesParMois.put("Mars", 6000.0);

        return new RapportActiviteDTO(
                dateDebut,
                dateFin,
                totalDemandes,
                demandesApprouvees,
                demandesRefusees,
                demandesEnAttente,
                montantTotal,
                montantPerçu,
                recettesParMois,
                demandesParType
        );
    }

    // ===== RAPPORTS ANNUELS (NOUVEAU) =====

    /**
     * Générer un rapport annuel complet
     */
    public Map<String, Object> generateAnnualReport(int annee, Long clientId, String typeFacture, String statut) {
        log.info("Génération du rapport annuel pour l'année {}", annee);

        List<Map<String, Object>> factures = getFacturesByYear(annee);

        // Filtrer par client
        if (clientId != null) {
            factures = factures.stream()
                    .filter(f -> clientId.equals(((Number) f.get("clientId")).longValue()))
                    .collect(Collectors.toList());
        }

        // Filtrer par type de facture
        if (typeFacture != null && !typeFacture.isEmpty()) {
            factures = factures.stream()
                    .filter(f -> typeFacture.equals(f.get("typeFacture")))
                    .collect(Collectors.toList());
        }

        // Filtrer par statut
        if (statut != null && !statut.isEmpty()) {
            factures = factures.stream()
                    .filter(f -> statut.equals(f.get("statut")))
                    .collect(Collectors.toList());
        }

        Map<String, Object> report = new HashMap<>();
        report.put("annee", annee);
        report.put("totalFactures", factures.size());
        report.put("montantTotal", factures.stream().mapToDouble(f -> ((Number) f.get("montant")).doubleValue()).sum());
        report.put("montantPaye", factures.stream()
                .filter(f -> "PAYEE".equals(f.get("statut")))
                .mapToDouble(f -> ((Number) f.get("montant")).doubleValue())
                .sum());
        report.put("montantImpaye", factures.stream()
                .filter(f -> "EN_ATTENTE".equals(f.get("statut")) || "EN_RETARD".equals(f.get("statut")))
                .mapToDouble(f -> ((Number) f.get("montant")).doubleValue())
                .sum());
        report.put("factures", factures);
        report.put("statistiques", getAnnualStatistics(factures));

        return report;
    }

    /**
     * Générer un résumé annuel
     */
    public Map<String, Object> generateAnnualSummary(int annee) {
        log.info("Génération du résumé annuel {}", annee);

        List<Map<String, Object>> factures = getFacturesByYear(annee);
        List<Map<String, Object>> autorisations = getAutorisations();
        List<Map<String, Object>> clients = getClients();

        Map<String, Object> summary = new HashMap<>();
        summary.put("annee", annee);
        summary.put("totalClients", clients.size());
        summary.put("totalAutorisations", autorisations.size());
        summary.put("nouvellesAutorisations", countNewAutorisations(autorisations, annee));
        summary.put("autorisationsActives", countByStatut(autorisations, "ACTIVE"));
        summary.put("totalFactures", factures.size());
        summary.put("montantTotal", factures.stream().mapToDouble(f -> ((Number) f.get("montant")).doubleValue()).sum());
        summary.put("montantPerçu", factures.stream()
                .filter(f -> "PAYEE".equals(f.get("statut")))
                .mapToDouble(f -> ((Number) f.get("montant")).doubleValue())
                .sum());
        summary.put("tauxRecouvrement", calculateRecouvrement(factures));

        return summary;
    }

    /**
     * Obtenir les statistiques annuelles
     */
    private Map<String, Object> getAnnualStatistics(List<Map<String, Object>> factures) {
        Map<String, Object> stats = new HashMap<>();

        // Par type de facture
        Map<String, Long> byType = factures.stream()
                .collect(Collectors.groupingBy(
                        f -> (String) f.get("typeFacture"),
                        Collectors.counting()
                ));
        stats.put("parType", byType);

        // Par statut
        Map<String, Long> byStatut = factures.stream()
                .collect(Collectors.groupingBy(
                        f -> (String) f.get("statut"),
                        Collectors.counting()
                ));
        stats.put("parStatut", byStatut);

        return stats;
    }

    /**
     * Calculer le taux de recouvrement
     */
    private double calculateRecouvrement(List<Map<String, Object>> factures) {
        double total = factures.stream().mapToDouble(f -> ((Number) f.get("montant")).doubleValue()).sum();
        double paye = factures.stream()
                .filter(f -> "PAYEE".equals(f.get("statut")))
                .mapToDouble(f -> ((Number) f.get("montant")).doubleValue())
                .sum();
        return total > 0 ? (paye / total) * 100 : 0;
    }

    /**
     * Récupérer les factures par année
     */
    private List<Map<String, Object>> getFacturesByYear(int annee) {
        try {
            String url = FACTURE_URL + "/year/" + annee;
            log.info("Appel du service facturation: {}", url);
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Erreur lors de l'appel du service facturation: {}", e.getMessage());
            return List.of();
        }
    }

    // ===== MÉTHODES PRIVÉES =====

    private List<Map<String, Object>> getAutorisations() {
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    AUTORISATION_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Erreur lors de l'appel du service autorisation: {}", e.getMessage());
            return List.of();
        }
    }

    private List<Map<String, Object>> getFactures() {
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    FACTURE_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Erreur lors de l'appel du service facturation: {}", e.getMessage());
            return List.of();
        }
    }

    private List<Map<String, Object>> getClients() {
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    CLIENT_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Erreur lors de l'appel du service client: {}", e.getMessage());
            return List.of();
        }
    }

    private long countByStatut(List<Map<String, Object>> autorisations, String statut) {
        return autorisations.stream()
                .filter(a -> statut.equals(a.get("statut")))
                .count();
    }

    private long countNewAutorisations(List<Map<String, Object>> autorisations, int annee) {
        return autorisations.stream()
                .filter(a -> {
                    String createdAt = (String) a.get("createdAt");
                    return createdAt != null && createdAt.contains(String.valueOf(annee));
                })
                .count();
    }
}