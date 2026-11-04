package tn.sfm.clientservice.controller;

import tn.sfm.clientservice.entity.Client;
import tn.sfm.clientservice.service.ClientDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/client/dashboard")
@RequiredArgsConstructor
@Slf4j
public class ClientDashboardController {

    private final ClientDashboardService clientDashboardService;

    // ✅ Récupérer les infos du client connecté
    @GetMapping("/info/{userId}")
    public ResponseEntity<Client> getClientInfo(@PathVariable Long userId) {
        log.info("GET /api/client/dashboard/info/{} - Récupération des infos du client", userId);
        return ResponseEntity.ok(clientDashboardService.getClientByUserId(userId));
    }

    // ✅ Récupérer les autorisations du client
    @GetMapping("/autorisations/{userId}")
    public ResponseEntity<Object> getClientAutorisations(@PathVariable Long userId) {
        log.info("GET /api/client/dashboard/autorisations/{} - Récupération des autorisations", userId);
        return ResponseEntity.ok(clientDashboardService.getClientAutorisations(userId));
    }

    // ✅ Récupérer les factures du client
    @GetMapping("/factures/{userId}")
    public ResponseEntity<Object> getClientFactures(@PathVariable Long userId) {
        log.info("GET /api/client/dashboard/factures/{} - Récupération des factures", userId);
        return ResponseEntity.ok(clientDashboardService.getClientFactures(userId));
    }

    // ✅ Récupérer les statistiques du client
    @GetMapping("/stats/{userId}")
    public ResponseEntity<Map<String, Object>> getClientStats(@PathVariable Long userId) {
        log.info("GET /api/client/dashboard/stats/{} - Récupération des statistiques", userId);
        return ResponseEntity.ok(clientDashboardService.getClientStats(userId));
    }
}