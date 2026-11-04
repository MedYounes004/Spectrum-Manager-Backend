package tn.sfm.reportingservice.client;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AutorisationClient {

    private final RestTemplate restTemplate;

    @Value("${services.autorisation.url}")
    private String autorisationUrl;

    public List<Map<String, Object>> getAllAutorisations() {
        try {
            String url = autorisationUrl;
            log.info("Appel du service autorisation: {}", url);

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
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

    public long countAutorisationsByStatut(String statut) {
        try {
            List<Map<String, Object>> autorisations = getAllAutorisations();
            return autorisations.stream()
                    .filter(a -> statut.equals(a.get("statut")))
                    .count();
        } catch (Exception e) {
            log.error("Erreur lors du comptage des autorisations: {}", e.getMessage());
            return 0;
        }
    }

    public long countNouvellesAutorisations(int annee) {
        try {
            List<Map<String, Object>> autorisations = getAllAutorisations();
            return autorisations.stream()
                    .filter(a -> {
                        String createdAt = (String) a.get("createdAt");
                        return createdAt != null && createdAt.contains(String.valueOf(annee));
                    })
                    .count();
        } catch (Exception e) {
            log.error("Erreur lors du comptage des nouvelles autorisations: {}", e.getMessage());
            return 0;
        }
    }

    public Map<String, Long> countAutorisationsByType() {
        try {
            List<Map<String, Object>> autorisations = getAllAutorisations();
            return autorisations.stream()
                    .collect(java.util.stream.Collectors.groupingBy(
                            a -> (String) a.get("type"),
                            java.util.stream.Collectors.counting()
                    ));
        } catch (Exception e) {
            log.error("Erreur lors du comptage des autorisations par type: {}", e.getMessage());
            return Map.of();
        }
    }
}