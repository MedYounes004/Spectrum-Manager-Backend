package tn.sfm.clientservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code_client", unique = true, nullable = false, length = 20)
    private String codeClient;

    // ✅ NOUVEAU : Lien avec le compte User
    @Column(name = "user_id", nullable = true)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(length = 100)
    private String prenom;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String telephone;

    @Column(length = 255)
    private String adresse;

    @Column(length = 50)
    private String ville;

    @Column(name = "code_postal", length = 10)
    private String codePostal;

    @Column(length = 50)
    private String pays;

    @Enumerated(EnumType.STRING)
    private TypeClient type;

    @Enumerated(EnumType.STRING)
    private StatutClient statut;

    @Column(name = "secteur_activite", length = 50)
    private String secteurActivite;

    @Column(name = "numero_fiscal", length = 20)
    private String numeroFiscal;

    @Column(length = 20)
    private String matricule;

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
        if (codeClient == null) {
            codeClient = "CLT-" + System.currentTimeMillis();
        }
        if (statut == null) {
            statut = StatutClient.ACTIF;
        }
        if (type == null) {
            type = TypeClient.ENTREPRISE;
        }
        if (pays == null) {
            pays = "Tunisie";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}