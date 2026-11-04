package tn.sfm.facturationservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "formules_tarifaires")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormuleTarifaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String service;

    @Column(length = 50)
    private String pays;

    @Column(name = "montant_base")
    private Double montantBase;

    @Column(name = "coeff_puissance")
    private Double coeffPuissance;

    @Column(name = "coeff_frequence")
    private Double coeffFrequence;

    @Column(name = "coeff_appareils")
    private Double coeffAppareils;

    @Column(name = "coeff_largeur_bande")
    private Double coeffLargeurBande;

    @Column(name = "coeff_gain_antenne")
    private Double coeffGainAntenne;

    private Double tva;

    private Boolean actif;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (actif == null) actif = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}