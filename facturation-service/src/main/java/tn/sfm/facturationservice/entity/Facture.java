package tn.sfm.facturationservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "factures")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroFacture;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "autorisation_id")
    private Long autorisationId;

    @Column(name = "type_autorisation")
    private String typeAutorisation;

    @Enumerated(EnumType.STRING)
    private TypeFacture typeFacture;

    private String description;

    @Column(nullable = false)
    private Double montant;

    @Column(nullable = false)
    private LocalDate dateEmission;

    private LocalDate dateEcheance;

    private LocalDate datePaiement;

    @Enumerated(EnumType.STRING)
    private StatutFacture statut;

    @Enumerated(EnumType.STRING)
    private TypeFacture type;

    private String modePaiement;

    private String referencePaiement;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (numeroFacture == null || numeroFacture.isEmpty()) {
            numeroFacture = "FACT-" + System.currentTimeMillis();
        }
        if (dateEmission == null) {
            dateEmission = LocalDate.now();
        }
        if (statut == null) {
            statut = StatutFacture.EN_ATTENTE;
        }
        if (type == null) {
            type = TypeFacture.REDEVANCE;
        }
        if (typeFacture == null) {
            typeFacture = TypeFacture.INDIVIDUELLE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}