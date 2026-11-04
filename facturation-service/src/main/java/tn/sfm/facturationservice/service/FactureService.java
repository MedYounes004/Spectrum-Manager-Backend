package tn.sfm.facturationservice.service;

import tn.sfm.facturationservice.entity.Facture;
import tn.sfm.facturationservice.entity.StatutFacture;
import tn.sfm.facturationservice.entity.TypeFacture;
import tn.sfm.facturationservice.repository.FactureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FactureService {

    private final FactureRepository factureRepository;

    // ===== CRUD EXISTANT =====

    public Facture createFacture(Facture facture) {
        log.info("Création d'une nouvelle facture");

        if (facture.getClientId() == null) {
            throw new RuntimeException("Le clientId est obligatoire");
        }

        if (facture.getTypeFacture() == TypeFacture.AVOIR) {
            if (facture.getMontant() == null || facture.getMontant() == 0) {
                throw new RuntimeException("Le montant est obligatoire pour un avoir");
            }
        } else {
            if (facture.getMontant() == null || facture.getMontant() <= 0) {
                throw new RuntimeException("Le montant doit être supérieur à 0");
            }
        }

        if (facture.getNumeroFacture() == null || facture.getNumeroFacture().isEmpty()) {
            facture.setNumeroFacture("FACT-" + System.currentTimeMillis());
        }
        if (facture.getDateEmission() == null) {
            facture.setDateEmission(LocalDate.now());
        }
        if (facture.getDateEcheance() == null) {
            facture.setDateEcheance(LocalDate.now().plusMonths(1));
        }
        if (facture.getStatut() == null) {
            facture.setStatut(StatutFacture.EN_ATTENTE);
        }
        if (facture.getType() == null) {
            facture.setType(TypeFacture.REDEVANCE);
        }
        if (facture.getTypeFacture() == null) {
            facture.setTypeFacture(TypeFacture.INDIVIDUELLE);
        }
        if (facture.getDatePaiement() == null) {
            facture.setDatePaiement(LocalDate.now());
        }

        if (facture.getTypeFacture() == TypeFacture.AVOIR) {
            facture.setStatut(StatutFacture.PAYEE);
        }

        return factureRepository.save(facture);
    }

    public List<Facture> getAllFactures() {
        return factureRepository.findAll();
    }

    public Facture getFactureById(Long id) {
        return factureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée avec l'ID: " + id));
    }

    public Facture getFactureByNumero(String numero) {
        return factureRepository.findByNumeroFacture(numero)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée avec le numéro: " + numero));
    }

    public Facture updateFacture(Long id, Facture factureDetails) {
        log.info("Mise à jour de la facture ID: {}", id);
        Facture facture = getFactureById(id);

        if (factureDetails.getClientId() != null) facture.setClientId(factureDetails.getClientId());
        if (factureDetails.getTypeAutorisation() != null) facture.setTypeAutorisation(factureDetails.getTypeAutorisation());
        if (factureDetails.getTypeFacture() != null) facture.setTypeFacture(factureDetails.getTypeFacture());
        if (factureDetails.getDescription() != null) facture.setDescription(factureDetails.getDescription());
        if (factureDetails.getMontant() != null) facture.setMontant(factureDetails.getMontant());
        if (factureDetails.getDateEcheance() != null) facture.setDateEcheance(factureDetails.getDateEcheance());
        if (factureDetails.getStatut() != null) facture.setStatut(factureDetails.getStatut());
        if (factureDetails.getType() != null) facture.setType(factureDetails.getType());
        if (factureDetails.getModePaiement() != null) facture.setModePaiement(factureDetails.getModePaiement());
        if (factureDetails.getNotes() != null) facture.setNotes(factureDetails.getNotes());

        return factureRepository.save(facture);
    }

    public void deleteFacture(Long id) {
        log.info("Suppression de la facture ID: {}", id);
        if (!factureRepository.existsById(id)) {
            throw new RuntimeException("Facture non trouvée avec l'ID: " + id);
        }
        factureRepository.deleteById(id);
    }

    public Facture marquerPayee(Long id) {
        log.info("Marquage de la facture {} comme payée", id);
        Facture facture = getFactureById(id);
        facture.setStatut(StatutFacture.PAYEE);
        facture.setDatePaiement(LocalDate.now());
        return factureRepository.save(facture);
    }

    public List<Facture> getFacturesEnRetard() {
        log.info("Récupération des factures en retard");
        return factureRepository.findByStatutAndDateEcheanceBefore(StatutFacture.EN_ATTENTE, LocalDate.now());
    }

    public List<Facture> getFacturesByClient(Long clientId) {
        log.info("Récupération des factures du client ID: {}", clientId);
        return factureRepository.findByClientId(clientId);
    }

    public List<Facture> getFacturesByStatut(StatutFacture statut) {
        log.info("Récupération des factures par statut: {}", statut);
        return factureRepository.findByStatut(statut);
    }

    public List<Facture> getFacturesByTypeFacture(TypeFacture typeFacture) {
        log.info("Récupération des factures par type: {}", typeFacture);
        return factureRepository.findByTypeFacture(typeFacture);
    }

    public List<Facture> getFacturesByTypeAutorisation(String typeAutorisation) {
        log.info("Récupération des factures par type d'autorisation: {}", typeAutorisation);
        return factureRepository.findByTypeAutorisation(typeAutorisation);
    }

    // ===== FACTURES COLLECTIVES (NOUVEAU) =====

    /**
     * Créer une facture collective pour un client avec plusieurs autorisations
     */
    public Facture createFactureCollective(Long clientId, List<Long> autorisationIds,
                                           String description, LocalDate dateEmission,
                                           LocalDate dateEcheance) {
        log.info("Création d'une facture collective pour le client {}", clientId);

        if (autorisationIds == null || autorisationIds.isEmpty()) {
            throw new RuntimeException("Au moins une autorisation est requise");
        }

        // Calculer le montant total (à adapter selon vos règles)
        double montantTotal = autorisationIds.size() * 10000.0; // Exemple simplifié

        Facture facture = new Facture();
        facture.setClientId(clientId);
        facture.setTypeFacture(TypeFacture.COLLECTIVE);
        facture.setTypeAutorisation("COLLECTIVE");
        facture.setDescription(description != null ? description : "Facture collective");
        facture.setMontant(montantTotal);
        facture.setDateEmission(dateEmission != null ? dateEmission : LocalDate.now());
        facture.setDateEcheance(dateEcheance != null ? dateEcheance : LocalDate.now().plusMonths(1));
        facture.setStatut(StatutFacture.EN_ATTENTE);
        facture.setType(TypeFacture.REDEVANCE);
        facture.setModePaiement("VIREMENT");
        facture.setNumeroFacture("FACT-COL-" + System.currentTimeMillis());
        facture.setNotes("Facture collective regroupant " + autorisationIds.size() + " autorisations");

        return factureRepository.save(facture);
    }

    // ===== FACTURES AVOIR (NOUVEAU) =====

    /**
     * Créer un avoir pour annuler/corriger une facture existante
     */
    public Facture createAvoir(Long factureId, String motif) {
        log.info("Création d'un avoir pour la facture {}", factureId);

        Facture factureOriginale = getFactureById(factureId);

        Facture avoir = new Facture();
        avoir.setClientId(factureOriginale.getClientId());
        avoir.setAutorisationId(factureOriginale.getAutorisationId());
        avoir.setTypeAutorisation(factureOriginale.getTypeAutorisation());
        avoir.setTypeFacture(TypeFacture.AVOIR);
        avoir.setDescription("Avoir - " + (motif != null ? motif : "Correction de facturation"));
        avoir.setMontant(-Math.abs(factureOriginale.getMontant()));
        avoir.setDateEmission(LocalDate.now());
        avoir.setDateEcheance(LocalDate.now().plusDays(15));
        avoir.setStatut(StatutFacture.PAYEE);
        avoir.setType(TypeFacture.AVOIR);
        avoir.setModePaiement(factureOriginale.getModePaiement());
        avoir.setNotes("Avoir pour annulation de la facture " + factureOriginale.getNumeroFacture());
        avoir.setNumeroFacture("AVOIR-" + System.currentTimeMillis());

        return factureRepository.save(avoir);
    }

    /**
     * Récupérer toutes les factures avoir d'un client
     */
    public List<Facture> getAvoirsByClient(Long clientId) {
        return factureRepository.findByClientIdAndTypeFacture(clientId, TypeFacture.AVOIR);
    }

    // ===== STATISTIQUES (NOUVEAU) =====

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", factureRepository.count());
        stats.put("totalMontantPaye", factureRepository.sumMontantPaye());
        stats.put("parStatut", getCountByStatut());
        stats.put("parTypeFacture", getCountByTypeFacture());
        stats.put("parTypeAutorisation", getCountByTypeAutorisation());
        return stats;
    }

    public Map<String, Long> getCountByTypeFacture() {
        Map<String, Long> stats = new HashMap<>();
        List<Object[]> results = factureRepository.countByTypeFacture();
        for (Object[] result : results) {
            stats.put(((TypeFacture) result[0]).getLabel(), (Long) result[1]);
        }
        return stats;
    }

    public Map<String, Long> getCountByTypeAutorisation() {
        Map<String, Long> stats = new HashMap<>();
        List<Object[]> results = factureRepository.countByTypeAutorisation();
        for (Object[] result : results) {
            stats.put((String) result[0], (Long) result[1]);
        }
        return stats;
    }

    public Map<String, Long> getCountByStatut() {
        Map<String, Long> stats = new HashMap<>();
        List<Object[]> results = factureRepository.countByStatut();
        for (Object[] result : results) {
            stats.put(((StatutFacture) result[0]).getLabel(), (Long) result[1]);
        }
        return stats;
    }

    public double getTotalMontant() {
        Double total = factureRepository.sumMontantPaye();
        return total != null ? total : 0.0;
    }
}