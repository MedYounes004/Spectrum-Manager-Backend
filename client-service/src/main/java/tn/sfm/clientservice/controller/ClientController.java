package tn.sfm.clientservice.controller;

import tn.sfm.clientservice.entity.Client;
import tn.sfm.clientservice.entity.StatutClient;
import tn.sfm.clientservice.entity.TypeClient;
import tn.sfm.clientservice.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Slf4j
public class ClientController {

    private final ClientService clientService;

    // ========== CRUD ==========

    @PostMapping
    public ResponseEntity<Client> createClient(@Valid @RequestBody Client client) {
        log.info("POST /api/clients - Création d'un client");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clientService.createClient(client));
    }

    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        log.info("GET /api/clients - Récupération de tous les clients");
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        log.info("GET /api/clients/{} - Récupération du client", id);
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    // ✅ NOUVEAU : Récupérer un client par userId
    @GetMapping("/user/{userId}")
    public ResponseEntity<Client> getClientByUserId(@PathVariable Long userId) {
        log.info("GET /api/clients/user/{} - Récupération du client par userId", userId);
        return ResponseEntity.ok(clientService.getClientByUserId(userId));
    }

    @GetMapping("/code/{codeClient}")
    public ResponseEntity<Client> getClientByCode(@PathVariable String codeClient) {
        log.info("GET /api/clients/code/{} - Récupération du client par code", codeClient);
        return ResponseEntity.ok(clientService.getClientByCode(codeClient));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Client> getClientByEmail(@PathVariable String email) {
        log.info("GET /api/clients/email/{} - Récupération du client par email", email);
        return ResponseEntity.ok(clientService.getClientByEmail(email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody Client client) {
        log.info("PUT /api/clients/{} - Mise à jour du client", id);
        return ResponseEntity.ok(clientService.updateClient(id, client));
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<Client> updateClientStatus(
            @PathVariable Long id,
            @RequestParam StatutClient statut) {
        log.info("PATCH /api/clients/{}/statut?statut={} - Mise à jour du statut", id, statut);
        return ResponseEntity.ok(clientService.updateClientStatus(id, statut));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        log.info("DELETE /api/clients/{} - Suppression du client", id);
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    // ========== RECHERCHES ==========

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<Client>> getClientsByStatut(@PathVariable StatutClient statut) {
        log.info("GET /api/clients/statut/{} - Récupération des clients par statut", statut);
        return ResponseEntity.ok(clientService.getClientsByStatut(statut));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Client>> getClientsByType(@PathVariable TypeClient type) {
        log.info("GET /api/clients/type/{} - Récupération des clients par type", type);
        return ResponseEntity.ok(clientService.getClientsByType(type));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Client>> searchClientsByName(@RequestParam String nom) {
        log.info("GET /api/clients/search?nom={} - Recherche de clients", nom);
        return ResponseEntity.ok(clientService.searchClientsByName(nom));
    }

    // ========== STATISTIQUES ==========

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("GET /api/clients/stats - Récupération des statistiques");
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalClients", clientService.countClients());
        stats.put("activeClients", clientService.countActiveClients());
        stats.put("entreprises", clientService.countByType(TypeClient.ENTREPRISE));
        stats.put("personnes", clientService.countByType(TypeClient.PERSONNE));
        return ResponseEntity.ok(stats);
    }
}