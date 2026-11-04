package tn.sfm.reportingservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class FacturationClient {

    private final RestTemplate restTemplate;

    @Value("${services.facturation.url}")
    private String facturationUrl;

    public List<Map<String, Object>> getAllFactures() {
        try {
            String url = facturationUrl;
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

    public double getTotalRecettes() {
        try {
            List<Map<String, Object>> factures = getAllFactures();
            return factures.stream()
                    .filter(f -> "PAYEE".equals(f.get("statut")))
                    .mapToDouble(f -> {
                        Object montant = f.get("montant");
                        return montant instanceof Number ? ((Number) montant).doubleValue() : 0.0;
                    })
                    .sum();
        } catch (Exception e) {
            log.error("Erreur lors du calcul des recettes: {}", e.getMessage());
            return 0;
        }
    }

    public Map<String, Double> getRecettesByType() {
        try {
            List<Map<String, Object>> factures = getAllFactures();
            return factures.stream()
                    .filter(f -> "PAYEE".equals(f.get("statut")))
                    .collect(java.util.stream.Collectors.groupingBy(
                            f -> (String) f.get("type"),
                            java.util.stream.Collectors.summingDouble(f -> {
                                Object montant = f.get("montant");
                                return montant instanceof Number ? ((Number) montant).doubleValue() : 0.0;
                            })
                    ));
        } catch (Exception e) {
            log.error("Erreur lors du calcul des recettes par type: {}", e.getMessage());
            return Map.of();
        }
    }
}