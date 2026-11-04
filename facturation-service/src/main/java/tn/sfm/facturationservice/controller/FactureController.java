package tn.sfm.facturationservice.controller;

import tn.sfm.facturationservice.entity.Facture;
import tn.sfm.facturationservice.entity.StatutFacture;
import tn.sfm.facturationservice.entity.TypeFacture;
import tn.sfm.facturationservice.service.FactureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/factures")
@RequiredArgsConstructor
@Slf4j
public class FactureController {

    private final FactureService factureService;

    // ===== CRUD =====

    @PostMapping
    public ResponseEntity<Facture> createFacture(@Valid @RequestBody Facture facture) {
        log.info("POST /api/factures - Création d'une facture");
        return ResponseEntity.status(HttpStatus.CREATED).body(factureService.createFacture(facture));
    }

    @GetMapping
    public ResponseEntity<List<Facture>> getAllFactures() {
        log.info("GET /api/factures - Récupération de toutes les factures");
        return ResponseEntity.ok(factureService.getAllFactures());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Facture> getFactureById(@PathVariable Long id) {
        log.info("GET /api/factures/{} - Récupération de la facture", id);
        return ResponseEntity.ok(factureService.getFactureById(id));
    }

    @GetMapping("/numero/{numero}")
    public ResponseEntity<Facture> getFactureByNumero(@PathVariable String numero) {
        log.info("GET /api/factures/numero/{} - Récupération de la facture par numéro", numero);
        return ResponseEntity.ok(factureService.getFactureByNumero(numero));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Facture> updateFacture(@PathVariable Long id, @RequestBody Facture facture) {
        log.info("PUT /api/factures/{} - Mise à jour de la facture", id);
        return ResponseEntity.ok(factureService.updateFacture(id, facture));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFacture(@PathVariable Long id) {
        log.info("DELETE /api/factures/{} - Suppression de la facture", id);
        factureService.deleteFacture(id);
        return ResponseEntity.noContent().build();
    }

    // ===== FONCTIONS SPÉCIFIQUES =====

    @PatchMapping("/{id}/paiement")
    public ResponseEntity<Facture> marquerPayee(@PathVariable Long id) {
        log.info("PATCH /api/factures/{}/paiement - Marquage comme payée", id);
        return ResponseEntity.ok(factureService.marquerPayee(id));
    }

    @GetMapping("/en-retard")
    public ResponseEntity<List<Facture>> getFacturesEnRetard() {
        log.info("GET /api/factures/en-retard - Récupération des factures en retard");
        return ResponseEntity.ok(factureService.getFacturesEnRetard());
    }

    // ===== RECHERCHES =====

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Facture>> getFacturesByClient(@PathVariable Long clientId) {
        log.info("GET /api/factures/client/{} - Récupération des factures par client", clientId);
        return ResponseEntity.ok(factureService.getFacturesByClient(clientId));
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<Facture>> getFacturesByStatut(@PathVariable StatutFacture statut) {
        log.info("GET /api/factures/statut/{} - Récupération des factures par statut", statut);
        return ResponseEntity.ok(factureService.getFacturesByStatut(statut));
    }

    @GetMapping("/type-facture/{typeFacture}")
    public ResponseEntity<List<Facture>> getFacturesByTypeFacture(@PathVariable TypeFacture typeFacture) {
        log.info("GET /api/factures/type-facture/{} - Récupération des factures par type", typeFacture);
        return ResponseEntity.ok(factureService.getFacturesByTypeFacture(typeFacture));
    }

    @GetMapping("/type-autorisation/{typeAutorisation}")
    public ResponseEntity<List<Facture>> getFacturesByTypeAutorisation(@PathVariable String typeAutorisation) {
        log.info("GET /api/factures/type-autorisation/{} - Récupération des factures par type d'autorisation", typeAutorisation);
        return ResponseEntity.ok(factureService.getFacturesByTypeAutorisation(typeAutorisation));
    }

    // ===== FACTURES COLLECTIVES (NOUVEAU) =====

    @PostMapping("/collective")
    public ResponseEntity<Facture> createFactureCollective(
            @RequestParam Long clientId,
            @RequestParam List<Long> autorisationIds,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) LocalDate dateEmission,
            @RequestParam(required = false) LocalDate dateEcheance) {
        log.info("POST /api/factures/collective - Création d'une facture collective");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(factureService.createFactureCollective(clientId, autorisationIds,
                        description, dateEmission, dateEcheance));
    }

    // ===== FACTURES AVOIR (NOUVEAU) =====

    @PostMapping("/avoir")
    public ResponseEntity<Facture> createAvoir(
            @RequestParam Long factureId,
            @RequestParam(required = false) String motif) {
        log.info("POST /api/factures/avoir - Création d'un avoir pour la facture {}", factureId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(factureService.createAvoir(factureId, motif));
    }

    @GetMapping("/avoir/client/{clientId}")
    public ResponseEntity<List<Facture>> getAvoirsByClient(@PathVariable Long clientId) {
        log.info("GET /api/factures/avoir/client/{} - Récupération des avoirs du client", clientId);
        return ResponseEntity.ok(factureService.getAvoirsByClient(clientId));
    }

    // ===== STATISTIQUES =====

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("GET /api/factures/stats - Récupération des statistiques");
        return ResponseEntity.ok(factureService.getStats());
    }

    @GetMapping("/stats/type-facture")
    public ResponseEntity<Map<String, Long>> getStatsByTypeFacture() {
        return ResponseEntity.ok(factureService.getCountByTypeFacture());
    }

    @GetMapping("/stats/type-autorisation")
    public ResponseEntity<Map<String, Long>> getStatsByTypeAutorisation() {
        return ResponseEntity.ok(factureService.getCountByTypeAutorisation());
    }

    @GetMapping("/stats/statut")
    public ResponseEntity<Map<String, Long>> getStatsByStatut() {
        return ResponseEntity.ok(factureService.getCountByStatut());
    }
}