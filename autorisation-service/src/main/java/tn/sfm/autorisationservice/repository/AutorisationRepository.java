package tn.sfm.autorisationservice.repository;

import tn.sfm.autorisationservice.entity.Autorisation;
import tn.sfm.autorisationservice.entity.StatutAutorisation;
import tn.sfm.autorisationservice.entity.TypeAutorisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AutorisationRepository extends JpaRepository<Autorisation, Long> {

    // ===== RECHERCHES EXISTANTES =====

    Optional<Autorisation> findByNumeroAutorisation(String numeroAutorisation);
    boolean existsByNumeroAutorisation(String numeroAutorisation);
    List<Autorisation> findByService(TypeAutorisation service);
    List<Autorisation> findByClientId(Long clientId);
    List<Autorisation> findByStatut(StatutAutorisation statut);
    List<Autorisation> findByStatutAndDateFinBefore(StatutAutorisation statut, LocalDate date);

    @Query("SELECT a.service, COUNT(a) FROM Autorisation a GROUP BY a.service")
    List<Object[]> countByService();

    @Query("SELECT a.statut, COUNT(a) FROM Autorisation a GROUP BY a.statut")
    List<Object[]> countByStatut();

    // ===== RECHERCHE AVANCÉE =====

    /**
     * Recherche par fréquence dans tous les champs de fréquence
     */
    @Query("SELECT a FROM Autorisation a WHERE " +
            "a.frequenceCentrale = :frequence OR " +
            "a.frequenceRadio = :frequence OR " +
            "a.frequenceEmission = :frequence OR " +
            "a.frequenceReception = :frequence OR " +
            "a.frequenceMaritime = :frequence OR " +
            "a.frequenceAero = :frequence OR " +
            "a.frequenceAmateur = :frequence OR " +
            "a.frequenceSatellite = :frequence")
    List<Autorisation> findByAnyFrequence(@Param("frequence") Double frequence);

    /**
     * Recherche par zone de service
     */
    @Query("SELECT a FROM Autorisation a WHERE " +
            "LOWER(a.zoneService) LIKE LOWER(CONCAT('%', :zone, '%')) OR " +
            "LOWER(a.zoneServiceFixe) LIKE LOWER(CONCAT('%', :zone, '%')) OR " +
            "LOWER(a.localisationZone) LIKE LOWER(CONCAT('%', :zone, '%')) OR " +
            "LOWER(a.zoneNavigation) LIKE LOWER(CONCAT('%', :zone, '%')) OR " +
            "LOWER(a.zoneOperation) LIKE LOWER(CONCAT('%', :zone, '%')) OR " +
            "LOWER(a.positionGPS) LIKE LOWER(CONCAT('%', :zone, '%')) OR " +
            "LOWER(a.positionGPSStation) LIKE LOWER(CONCAT('%', :zone, '%')) OR " +
            "LOWER(a.positionGPSMaritime) LIKE LOWER(CONCAT('%', :zone, '%')) OR " +
            "LOWER(a.positionGPSAmateur) LIKE LOWER(CONCAT('%', :zone, '%'))")
    List<Autorisation> findByAnyZone(@Param("zone") String zone);

    /**
     * Recherche par fréquence ET zone
     */
    @Query("SELECT a FROM Autorisation a WHERE " +
            "(a.frequenceCentrale = :frequence OR " +
            "a.frequenceRadio = :frequence OR " +
            "a.frequenceEmission = :frequence OR " +
            "a.frequenceReception = :frequence OR " +
            "a.frequenceMaritime = :frequence OR " +
            "a.frequenceAero = :frequence OR " +
            "a.frequenceAmateur = :frequence OR " +
            "a.frequenceSatellite = :frequence) AND " +
            "(LOWER(a.zoneService) LIKE LOWER(CONCAT('%', :zone, '%')) OR " +
            "LOWER(a.zoneServiceFixe) LIKE LOWER(CONCAT('%', :zone, '%')) OR " +
            "LOWER(a.localisationZone) LIKE LOWER(CONCAT('%', :zone, '%')) OR " +
            "LOWER(a.zoneNavigation) LIKE LOWER(CONCAT('%', :zone, '%')) OR " +
            "LOWER(a.zoneOperation) LIKE LOWER(CONCAT('%', :zone, '%')))")
    List<Autorisation> findByFrequenceAndZone(@Param("frequence") Double frequence, @Param("zone") String zone);

    /**
     * Recherche combinée avec tous les critères
     */
    @Query("SELECT a FROM Autorisation a WHERE " +
            "(:frequence IS NULL OR " +
            "a.frequenceCentrale = :frequence OR " +
            "a.frequenceRadio = :frequence OR " +
            "a.frequenceEmission = :frequence OR " +
            "a.frequenceReception = :frequence OR " +
            "a.frequenceMaritime = :frequence OR " +
            "a.frequenceAero = :frequence OR " +
            "a.frequenceAmateur = :frequence OR " +
            "a.frequenceSatellite = :frequence) AND " +
            "(:zone IS NULL OR " +
            "LOWER(a.zoneService) LIKE LOWER(CONCAT('%', :zone, '%')) OR " +
            "LOWER(a.zoneServiceFixe) LIKE LOWER(CONCAT('%', :zone, '%')) OR " +
            "LOWER(a.localisationZone) LIKE LOWER(CONCAT('%', :zone, '%')) OR " +
            "LOWER(a.zoneNavigation) LIKE LOWER(CONCAT('%', :zone, '%')) OR " +
            "LOWER(a.zoneOperation) LIKE LOWER(CONCAT('%', :zone, '%'))) AND " +
            "(:service IS NULL OR a.service = :service) AND " +
            "(:statut IS NULL OR a.statut = :statut) AND " +
            "(:clientId IS NULL OR a.clientId = :clientId)")
    List<Autorisation> searchAdvanced(
            @Param("frequence") Double frequence,
            @Param("zone") String zone,
            @Param("service") TypeAutorisation service,
            @Param("statut") StatutAutorisation statut,
            @Param("clientId") Long clientId);
}