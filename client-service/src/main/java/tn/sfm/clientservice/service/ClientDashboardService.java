package tn.sfm.clientservice.service;

import tn.sfm.clientservice.entity.Client;
import tn.sfm.clientservice.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientDashboardService {

    private final ClientRepository clientRepository;
    private final RestTemplate restTemplate;

    private static final String GATEWAY_URL = "http://localhost:8083/api";

    public Client getClientByUserId(Long userId) {
        log.info("Récupération du client pour l'utilisateur {}", userId);
        return clientRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Aucun client associé à cet utilisateur"));
    }

    public List<Map<String, Object>> getClientAutorisations(Long userId) {
        log.info("Récupération des autorisations pour l'utilisateur {}", userId);

        // 1. Récupérer le client
        Client client = getClientByUserId(userId);
        Long clientId = client.getId();

        // 2. Appeler le service d'autorisation
        try {
            String url = GATEWAY_URL + "/autorisations/client/" + clientId;
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

    public List<Map<String, Object>> getClientFactures(Long userId) {
        log.info("Récupération des factures pour l'utilisateur {}", userId);

        // 1. Récupérer le client
        Client client = getClientByUserId(userId);
        Long clientId = client.getId();

        // 2. Appeler le service de facturation
        try {
            String url = GATEWAY_URL + "/factures/client/" + clientId;
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

    public Map<String, Object> getClientStats(Long userId) {
        log.info("Récupération des statistiques pour l'utilisateur {}", userId);

        Client client = getClientByUserId(userId);
        List<Map<String, Object>> autorisations = getClientAutorisations(userId);
        List<Map<String, Object>> factures = getClientFactures(userId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("client", Map.of(
                "id", client.getId(),
                "nom", client.getNom(),
                "prenom", client.getPrenom(),
                "codeClient", client.getCodeClient()
        ));
        stats.put("totalAutorisations", autorisations.size());
        stats.put("totalFactures", factures.size());

        long autorisationsActives = autorisations.stream()
                .filter(a -> "ACTIVE".equals(a.get("statut")))
                .count();
        stats.put("autorisationsActives", autorisationsActives);

        double totalMontant = factures.stream()
                .filter(f -> "PAYEE".equals(f.get("statut")))
                .mapToDouble(f -> (Double) f.get("montant"))
                .sum();
        stats.put("totalMontantPaye", totalMontant);

        return stats;
    }
}