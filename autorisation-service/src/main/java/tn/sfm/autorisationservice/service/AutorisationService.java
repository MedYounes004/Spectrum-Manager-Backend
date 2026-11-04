package tn.sfm.autorisationservice.service;

import tn.sfm.autorisationservice.entity.Autorisation;
import tn.sfm.autorisationservice.entity.StatutAutorisation;
import tn.sfm.autorisationservice.entity.TypeAutorisation;
import tn.sfm.autorisationservice.repository.AutorisationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AutorisationService {

    private final AutorisationRepository autorisationRepository;

    // ===== CRUD =====

    public Autorisation createAutorisation(Autorisation autorisation) {
        if (autorisationRepository.existsByNumeroAutorisation(autorisation.getNumeroAutorisation())) {
            throw new RuntimeException("Numéro d'autorisation déjà existant");
        }
        if (autorisation.getClientId() == null) {
            throw new RuntimeException("Le clientId est obligatoire");
        }
        if (autorisation.getService() == null) {
            throw new RuntimeException("Le service est obligatoire");
        }
        if (autorisation.getStatut() == null) {
            autorisation.setStatut(StatutAutorisation.EN_ATTENTE);
        }
        return autorisationRepository.save(autorisation);
    }

    public List<Autorisation> getAllAutorisations() {
        return autorisationRepository.findAll();
    }

    public Autorisation getAutorisationById(Long id) {
        return autorisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Autorisation non trouvée"));
    }

    public List<Autorisation> getAutorisationsByService(TypeAutorisation service) {
        return autorisationRepository.findByService(service);
    }

    public List<Autorisation> getAutorisationsByClient(Long clientId) {
        return autorisationRepository.findByClientId(clientId);
    }

    public List<Autorisation> getAutorisationsByStatut(StatutAutorisation statut) {
        return autorisationRepository.findByStatut(statut);
    }

    public Autorisation updateAutorisation(Long id, Autorisation autorisationDetails) {
        Autorisation autorisation = getAutorisationById(id);

        if (autorisationDetails.getClientId() != null) {
            autorisation.setClientId(autorisationDetails.getClientId());
        }
        if (autorisationDetails.getService() != null) {
            autorisation.setService(autorisationDetails.getService());
        }
        if (autorisationDetails.getStatut() != null) {
            autorisation.setStatut(autorisationDetails.getStatut());
        }
        if (autorisationDetails.getDateDebut() != null) {
            autorisation.setDateDebut(autorisationDetails.getDateDebut());
        }
        if (autorisationDetails.getDateFin() != null) {
            autorisation.setDateFin(autorisationDetails.getDateFin());
        }

        updateFieldsByService(autorisation, autorisationDetails);
        return autorisationRepository.save(autorisation);
    }

    public Autorisation updateStatut(Long id, StatutAutorisation statut) {
        Autorisation autorisation = getAutorisationById(id);
        autorisation.setStatut(statut);
        return autorisationRepository.save(autorisation);
    }

    public void deleteAutorisation(Long id) {
        autorisationRepository.deleteById(id);
    }

    public List<Autorisation> getAutorisationsExpiringSoon() {
        LocalDate dateLimite = LocalDate.now().plusMonths(1);
        return autorisationRepository.findByStatutAndDateFinBefore(StatutAutorisation.ACTIVE, dateLimite);
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", autorisationRepository.count());
        stats.put("parService", autorisationRepository.countByService());
        stats.put("parStatut", autorisationRepository.countByStatut());
        return stats;
    }

    // ===== RECHERCHE AVANCÉE (Sans RestTemplate) =====

    /**
     * Recherche avancée avec critères multiples
     * @param frequence - Fréquence en MHz
     * @param zone - Zone de service
     * @param service - Type de service
     * @param statut - Statut de l'autorisation
     * @param clientId - ID du client (utiliser ceci au lieu de titulaire)
     */
    public List<Autorisation> searchAutorisations(
            Double frequence, String zone, TypeAutorisation service,
            StatutAutorisation statut, Long clientId) {

        log.info("Recherche avancée - frequence: {}, zone: {}, service: {}, statut: {}, clientId: {}",
                frequence, zone, service, statut, clientId);

        // Si pas de critères, retourner tout
        if (frequence == null && zone == null && service == null &&
                statut == null && clientId == null) {
            return autorisationRepository.findAll();
        }

        return autorisationRepository.searchAdvanced(frequence, zone, service, statut, clientId);
    }

    /**
     * Recherche par fréquence uniquement
     */
    public List<Autorisation> searchByFrequence(Double frequence) {
        log.info("Recherche par fréquence: {}", frequence);
        return autorisationRepository.findByAnyFrequence(frequence);
    }

    /**
     * Recherche par zone uniquement
     */
    public List<Autorisation> searchByZone(String zone) {
        log.info("Recherche par zone: {}", zone);
        return autorisationRepository.findByAnyZone(zone);
    }

    /**
     * Recherche par fréquence ET zone
     */
    public List<Autorisation> searchByFrequenceAndZone(Double frequence, String zone) {
        log.info("Recherche par fréquence {} et zone {}", frequence, zone);
        return autorisationRepository.findByFrequenceAndZone(frequence, zone);
    }

    // ===== MISE À JOUR DES CHAMPS SPÉCIFIQUES =====

    private void updateFieldsByService(Autorisation target, Autorisation source) {

        // SERVICE MOBILE TERRESTRE
        if (source.getFrequenceCentrale() != null) target.setFrequenceCentrale(source.getFrequenceCentrale());
        if (source.getLargeurCanal() != null) target.setLargeurCanal(source.getLargeurCanal());
        if (source.getPuissanceRayonnee() != null) target.setPuissanceRayonnee(source.getPuissanceRayonnee());
        if (source.getGainAntenne() != null) target.setGainAntenne(source.getGainAntenne());
        if (source.getTypeAntenne() != null) target.setTypeAntenne(source.getTypeAntenne());
        if (source.getMarqueAntenne() != null) target.setMarqueAntenne(source.getMarqueAntenne());
        if (source.getDiagrammeAntenne() != null) target.setDiagrammeAntenne(source.getDiagrammeAntenne());
        if (source.getTechnologie() != null) target.setTechnologie(source.getTechnologie());
        if (source.getNombreEmetteurs() != null) target.setNombreEmetteurs(source.getNombreEmetteurs());
        if (source.getPositionGPS() != null) target.setPositionGPS(source.getPositionGPS());
        if (source.getZoneService() != null) target.setZoneService(source.getZoneService());
        if (source.getHauteurAntenneSol() != null) target.setHauteurAntenneSol(source.getHauteurAntenneSol());

        // SERVICE RADIODIFFUSION
        if (source.getFrequenceRadio() != null) target.setFrequenceRadio(source.getFrequenceRadio());
        if (source.getFrequenceImage() != null) target.setFrequenceImage(source.getFrequenceImage());
        if (source.getPuissanceRadio() != null) target.setPuissanceRadio(source.getPuissanceRadio());
        if (source.getBandeFrequences() != null) target.setBandeFrequences(source.getBandeFrequences());
        if (source.getTypeAntenneRadio() != null) target.setTypeAntenneRadio(source.getTypeAntenneRadio());
        if (source.getMarqueAntenneRadio() != null) target.setMarqueAntenneRadio(source.getMarqueAntenneRadio());
        if (source.getLocalisationZone() != null) target.setLocalisationZone(source.getLocalisationZone());
        if (source.getNormeDiffusion() != null) target.setNormeDiffusion(source.getNormeDiffusion());
        if (source.getLargeurCanalRadio() != null) target.setLargeurCanalRadio(source.getLargeurCanalRadio());

        // SERVICE FIXE
        if (source.getFrequenceEmission() != null) target.setFrequenceEmission(source.getFrequenceEmission());
        if (source.getFrequenceReception() != null) target.setFrequenceReception(source.getFrequenceReception());
        if (source.getLargeurCanalFixe() != null) target.setLargeurCanalFixe(source.getLargeurCanalFixe());
        if (source.getDiversite() != null) target.setDiversite(source.getDiversite());
        if (source.getCoordonneesGPSEmetteur() != null) target.setCoordonneesGPSEmetteur(source.getCoordonneesGPSEmetteur());
        if (source.getCoordonneesGPSRecepteur() != null) target.setCoordonneesGPSRecepteur(source.getCoordonneesGPSRecepteur());
        if (source.getZoneServiceFixe() != null) target.setZoneServiceFixe(source.getZoneServiceFixe());
        if (source.getArrangementCanaux() != null) target.setArrangementCanaux(source.getArrangementCanaux());
        if (source.getDiametreAntenneFixe() != null) target.setDiametreAntenneFixe(source.getDiametreAntenneFixe());
        if (source.getTypeModulation() != null) target.setTypeModulation(source.getTypeModulation());

        // SERVICE SATELLITE
        if (source.getNomSatellite() != null) target.setNomSatellite(source.getNomSatellite());
        if (source.getTypeSatellite() != null) target.setTypeSatellite(source.getTypeSatellite());
        if (source.getPositionGPSStation() != null) target.setPositionGPSStation(source.getPositionGPSStation());
        if (source.getDebit() != null) target.setDebit(source.getDebit());
        if (source.getModulation() != null) target.setModulation(source.getModulation());
        if (source.getPositionOrbitale() != null) target.setPositionOrbitale(source.getPositionOrbitale());
        if (source.getTypeStation() != null) target.setTypeStation(source.getTypeStation());
        if (source.getGainAntenneSatellite() != null) target.setGainAntenneSatellite(source.getGainAntenneSatellite());
        if (source.getDiametreAntenneSat() != null) target.setDiametreAntenneSat(source.getDiametreAntenneSat());
        if (source.getBandeFreqSat() != null) target.setBandeFreqSat(source.getBandeFreqSat());

        // SERVICE MARITIME
        if (source.getTypeNavire() != null) target.setTypeNavire(source.getTypeNavire());
        if (source.getNumMMSI() != null) target.setNumMMSI(source.getNumMMSI());
        if (source.getIndicatifAppel() != null) target.setIndicatifAppel(source.getIndicatifAppel());
        if (source.getFrequenceMaritime() != null) target.setFrequenceMaritime(source.getFrequenceMaritime());
        if (source.getPuissanceMaritime() != null) target.setPuissanceMaritime(source.getPuissanceMaritime());
        if (source.getZoneNavigation() != null) target.setZoneNavigation(source.getZoneNavigation());
        if (source.getEquipementRadio() != null) target.setEquipementRadio(source.getEquipementRadio());
        if (source.getTypeAntenneMaritime() != null) target.setTypeAntenneMaritime(source.getTypeAntenneMaritime());
        if (source.getPositionGPSMaritime() != null) target.setPositionGPSMaritime(source.getPositionGPSMaritime());

        // SERVICE AÉRONAUTIQUE
        if (source.getTypeAeronef() != null) target.setTypeAeronef(source.getTypeAeronef());
        if (source.getImmatriculation() != null) target.setImmatriculation(source.getImmatriculation());
        if (source.getFrequenceAero() != null) target.setFrequenceAero(source.getFrequenceAero());
        if (source.getPuissanceAero() != null) target.setPuissanceAero(source.getPuissanceAero());
        if (source.getModeCommunication() != null) target.setModeCommunication(source.getModeCommunication());
        if (source.getZoneOperation() != null) target.setZoneOperation(source.getZoneOperation());
        if (source.getTypeAntenneAero() != null) target.setTypeAntenneAero(source.getTypeAntenneAero());
        if (source.getPositionGPSAero() != null) target.setPositionGPSAero(source.getPositionGPSAero());

        // SERVICE AMATEUR
        if (source.getClasseAmateur() != null) target.setClasseAmateur(source.getClasseAmateur());
        if (source.getIndicatif() != null) target.setIndicatif(source.getIndicatif());
        if (source.getFrequenceAmateur() != null) target.setFrequenceAmateur(source.getFrequenceAmateur());
        if (source.getPuissanceAmateur() != null) target.setPuissanceAmateur(source.getPuissanceAmateur());
        if (source.getModeEmission() != null) target.setModeEmission(source.getModeEmission());
        if (source.getAntenneType() != null) target.setAntenneType(source.getAntenneType());
        if (source.getMarqueAntenneAmateur() != null) target.setMarqueAntenneAmateur(source.getMarqueAntenneAmateur());
        if (source.getGainAntenneAmateur() != null) target.setGainAntenneAmateur(source.getGainAntenneAmateur());
        if (source.getPositionGPSAmateur() != null) target.setPositionGPSAmateur(source.getPositionGPSAmateur());

        // CHAMPS COMMUNS
        if (source.getTypeEquipement() != null) target.setTypeEquipement(source.getTypeEquipement());
        if (source.getMarqueEquipement() != null) target.setMarqueEquipement(source.getMarqueEquipement());
        if (source.getModeleEquipement() != null) target.setModeleEquipement(source.getModeleEquipement());
        if (source.getNombreAppareils() != null) target.setNombreAppareils(source.getNombreAppareils());
        if (source.getConditions() != null) target.setConditions(source.getConditions());
        if (source.getObservations() != null) target.setObservations(source.getObservations());
    }
}