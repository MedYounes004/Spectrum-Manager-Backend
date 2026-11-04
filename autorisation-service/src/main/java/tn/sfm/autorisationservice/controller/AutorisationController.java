package tn.sfm.autorisationservice.controller;

import tn.sfm.autorisationservice.entity.Autorisation;
import tn.sfm.autorisationservice.entity.StatutAutorisation;
import tn.sfm.autorisationservice.entity.TypeAutorisation;
import tn.sfm.autorisationservice.service.AutorisationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/autorisations")
@RequiredArgsConstructor
@Slf4j
public class AutorisationController {

    private final AutorisationService autorisationService;

    // ===== CRUD =====

    @PostMapping
    public ResponseEntity<Autorisation> create(@RequestBody Autorisation autorisation) {
        log.info("POST /api/autorisations - Création d'une autorisation");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(autorisationService.createAutorisation(autorisation));
    }

    @GetMapping
    public ResponseEntity<List<Autorisation>> getAll() {
        log.info("GET /api/autorisations - Récupération de toutes les autorisations");
        return ResponseEntity.ok(autorisationService.getAllAutorisations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Autorisation> getById(@PathVariable Long id) {
        log.info("GET /api/autorisations/{} - Récupération de l'autorisation", id);
        return ResponseEntity.ok(autorisationService.getAutorisationById(id));
    }

    @GetMapping("/service/{service}")
    public ResponseEntity<List<Autorisation>> getByService(@PathVariable TypeAutorisation service) {
        log.info("GET /api/autorisations/service/{} - Récupération par service", service);
        return ResponseEntity.ok(autorisationService.getAutorisationsByService(service));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Autorisation>> getByClient(@PathVariable Long clientId) {
        log.info("GET /api/autorisations/client/{} - Récupération par client", clientId);
        return ResponseEntity.ok(autorisationService.getAutorisationsByClient(clientId));
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<Autorisation>> getByStatut(@PathVariable StatutAutorisation statut) {
        log.info("GET /api/autorisations/statut/{} - Récupération par statut", statut);
        return ResponseEntity.ok(autorisationService.getAutorisationsByStatut(statut));
    }

    @GetMapping("/expiring-soon")
    public ResponseEntity<List<Autorisation>> getExpiringSoon() {
        log.info("GET /api/autorisations/expiring-soon - Autorisations expirant bientôt");
        return ResponseEntity.ok(autorisationService.getAutorisationsExpiringSoon());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Autorisation> update(@PathVariable Long id, @RequestBody Autorisation autorisation) {
        log.info("PUT /api/autorisations/{} - Mise à jour", id);
        return ResponseEntity.ok(autorisationService.updateAutorisation(id, autorisation));
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<Autorisation> updateStatut(@PathVariable Long id, @RequestParam StatutAutorisation statut) {
        log.info("PATCH /api/autorisations/{}/statut - Changement de statut vers {}", id, statut);
        return ResponseEntity.ok(autorisationService.updateStatut(id, statut));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/autorisations/{} - Suppression", id);
        autorisationService.deleteAutorisation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("GET /api/autorisations/stats - Statistiques");
        return ResponseEntity.ok(autorisationService.getStats());
    }

    // ===== RECHERCHE AVANCÉE (SANS TITULAIRE) =====

    @GetMapping("/search")
    public ResponseEntity<List<Autorisation>> search(
            @RequestParam(required = false) Double frequence,
            @RequestParam(required = false) String zone,
            @RequestParam(required = false) TypeAutorisation service,
            @RequestParam(required = false) StatutAutorisation statut,
            @RequestParam(required = false) Long clientId) {
        log.info("GET /api/autorisations/search - Recherche avancée");
        return ResponseEntity.ok(autorisationService.searchAutorisations(
                frequence, zone, service, statut, clientId));
    }

    @GetMapping("/search/by-frequence")
    public ResponseEntity<List<Autorisation>> searchByFrequence(@RequestParam Double frequence) {
        log.info("GET /api/autorisations/search/by-frequence - Recherche par fréquence: {}", frequence);
        return ResponseEntity.ok(autorisationService.searchByFrequence(frequence));
    }

    @GetMapping("/search/by-zone")
    public ResponseEntity<List<Autorisation>> searchByZone(@RequestParam String zone) {
        log.info("GET /api/autorisations/search/by-zone - Recherche par zone: {}", zone);
        return ResponseEntity.ok(autorisationService.searchByZone(zone));
    }

    @GetMapping("/search/by-frequence-and-zone")
    public ResponseEntity<List<Autorisation>> searchByFrequenceAndZone(
            @RequestParam Double frequence,
            @RequestParam String zone) {
        log.info("GET /api/autorisations/search/by-frequence-and-zone - Recherche par fréquence {} et zone {}", frequence, zone);
        return ResponseEntity.ok(autorisationService.searchByFrequenceAndZone(frequence, zone));
    }
}