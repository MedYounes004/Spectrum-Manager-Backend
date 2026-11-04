package tn.sfm.facturationservice.repository;

import tn.sfm.facturationservice.entity.Facture;
import tn.sfm.facturationservice.entity.StatutFacture;
import tn.sfm.facturationservice.entity.TypeFacture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {

    // ===== RECHERCHES EXISTANTES =====

    List<Facture> findByTypeFacture(TypeFacture typeFacture);
    List<Facture> findByTypeAutorisation(String typeAutorisation);
    List<Facture> findByClientId(Long clientId);
    List<Facture> findByStatut(StatutFacture statut);
    List<Facture> findByStatutAndDateEcheanceBefore(StatutFacture statut, LocalDate date);
    Optional<Facture> findByNumeroFacture(String numeroFacture);
    boolean existsByNumeroFacture(String numeroFacture);
    List<Facture> findByDateEmissionBetween(LocalDate dateDebut, LocalDate dateFin);
    List<Facture> findByDateEcheanceBetween(LocalDate dateDebut, LocalDate dateFin);
    List<Facture> findByMontantGreaterThan(Double montant);
    List<Facture> findByMontantLessThan(Double montant);

    @Query("SELECT SUM(f.montant) FROM Facture f WHERE f.statut = 'PAYEE'")
    Double sumMontantPaye();

    @Query("SELECT f.typeFacture, COUNT(f) FROM Facture f GROUP BY f.typeFacture")
    List<Object[]> countByTypeFacture();

    @Query("SELECT f.typeAutorisation, COUNT(f) FROM Facture f GROUP BY f.typeAutorisation")
    List<Object[]> countByTypeAutorisation();

    @Query("SELECT f.statut, COUNT(f) FROM Facture f GROUP BY f.statut")
    List<Object[]> countByStatut();

    // ===== NOUVELLES MÉTHODES =====

    /**
     * Récupérer les factures par client et type
     */
    List<Facture> findByClientIdAndTypeFacture(Long clientId, TypeFacture typeFacture);

    /**
     * Récupérer les factures par année
     */
    @Query("SELECT f FROM Facture f WHERE YEAR(f.dateEmission) = :annee")
    List<Facture> findByYear(@org.springframework.data.repository.query.Param("annee") int annee);

    /**
     * Récupérer les factures par année et client
     */
    @Query("SELECT f FROM Facture f WHERE YEAR(f.dateEmission) = :annee AND f.clientId = :clientId")
    List<Facture> findByYearAndClient(@org.springframework.data.repository.query.Param("annee") int annee,
                                      @org.springframework.data.repository.query.Param("clientId") Long clientId);

    /**
     * Récupérer les factures par année et type
     */
    @Query("SELECT f FROM Facture f WHERE YEAR(f.dateEmission) = :annee AND f.typeFacture = :typeFacture")
    List<Facture> findByYearAndTypeFacture(@org.springframework.data.repository.query.Param("annee") int annee,
                                           @org.springframework.data.repository.query.Param("typeFacture") TypeFacture typeFacture);

    /**
     * Récupérer les factures par année et statut
     */
    @Query("SELECT f FROM Facture f WHERE YEAR(f.dateEmission) = :annee AND f.statut = :statut")
    List<Facture> findByYearAndStatut(@org.springframework.data.repository.query.Param("annee") int annee,
                                      @org.springframework.data.repository.query.Param("statut") StatutFacture statut);

    /**
     * Statistiques annuelles
     */
    @Query("SELECT YEAR(f.dateEmission), COUNT(f), SUM(f.montant) FROM Facture f GROUP BY YEAR(f.dateEmission)")
    List<Object[]> getAnnualStatistics();
}