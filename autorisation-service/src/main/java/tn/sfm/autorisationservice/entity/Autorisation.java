package tn.sfm.autorisationservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "autorisations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Autorisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== INFORMATIONS ADMINISTRATIVES =====
    @Column(nullable = false, unique = true)
    private String numeroAutorisation;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Enumerated(EnumType.STRING)
    private TypeAutorisation service;

    @Enumerated(EnumType.STRING)
    private StatutAutorisation statut;

    @Column(nullable = false)
    private LocalDate dateDebut;

    private LocalDate dateFin;

    // ===== CHAMPS COMMUNS À TOUS LES SERVICES =====
    private String typeEquipement;
    private String marqueEquipement;
    private String modeleEquipement;
    private Integer nombreAppareils;

    // ===== SERVICE MOBILE TERRESTRE =====
    private Double frequenceCentrale;           // Fréquence centrale [MHz]
    private Double largeurCanal;                // Largeur de canal [MHz]
    private Double puissanceRayonnee;           // Puissance rayonnée [W]
    private Double gainAntenne;                 // Gain d'antenne [dBi]
    private String typeAntenne;                 // Type d'antenne
    private String marqueAntenne;               // Marque d'antenne
    private String diagrammeAntenne;            // Diagramme d'antenne (fichier)
    private String technologie;                 // 2G, 3G, 4G, 5G (liste déroulante)
    private Integer nombreEmetteurs;            // Nombre d'émetteurs
    private String positionGPS;                 // Position GPS du site
    private String zoneService;                 // Zone de service
    private Double hauteurAntenneSol;           // Hauteur d'antenne par rapport au sol [m]

    // ===== SERVICE RADIODIFFUSION (TV, radio) =====
    private Double frequenceRadio;              // Fréquence [MHz]
    private Double frequenceImage;              // Fréquence Image [MHz]
    private Double puissanceRadio;              // Puissance rayonnée [W]
    private String bandeFrequences;             // Bande de fréquences
    private String typeAntenneRadio;            // Type d'antenne
    private String marqueAntenneRadio;          // Marque d'antenne
    private String localisationZone;            // Localisation ou zone de service
    private String normeDiffusion;              // Norme de diffusion (liste déroulante)
    private Double largeurCanalRadio;           // Largeur de canal [MHz]

    // ===== SERVICE FIXE =====
    private Double frequenceEmission;           // Fréquence émission [MHz]
    private Double frequenceReception;          // Fréquence réception [MHz]
    private Double largeurCanalFixe;            // Largeur de canal [MHz]
    private String diversite;                   // 1+1, 1+0, 2+0, 2+1 (liste déroulante)
    private String coordonneesGPSEmetteur;      // Coordonnée GPS de l'émetteur
    private String coordonneesGPSRecepteur;     // Coordonnée GPS du récepteur
    private String zoneServiceFixe;             // Zone de service
    private String arrangementCanaux;           // Arrangement de canaux (recommandation ITU)
    private Double diametreAntenneFixe;         // Diamètre antenne [m]
    private String typeModulation;              // Type de modulation

    // ===== SERVICE SATELLITE =====
    private String nomSatellite;                // Nom du satellite
    private String typeSatellite;               // GEO / non-GEO
    private String positionGPSStation;          // Position GPS de la station terrienne
    private Double debit;                       // Débit [Mbit/s]
    private String modulation;                  // Modulation
    private String positionOrbitale;            // Position orbitale
    private String typeStation;                 // Type de station
    private Double gainAntenneSatellite;        // Gain antenne [dBi]
    private Double diametreAntenneSat;          // Diamètre antenne [m]
    private String bandeFreqSat;                // Bande de fréquences
    private Double frequenceSatellite;
    // ===== SERVICE MARITIME =====
    private String typeNavire;                  // Type de navire
    private String numMMSI;                     // Numéro MMSI
    private String indicatifAppel;              // Indicatif d'appel
    private Double frequenceMaritime;           // Fréquence [MHz]
    private Double puissanceMaritime;           // Puissance [W]
    private String zoneNavigation;              // Zone de navigation
    private String equipementRadio;             // Équipement radio
    private String typeAntenneMaritime;         // Type d'antenne
    private String positionGPSMaritime;         // Position GPS

    // ===== SERVICE AÉRONAUTIQUE =====
    private String typeAeronef;                 // Type d'aéronef
    private String immatriculation;             // Immatriculation
    private Double frequenceAero;               // Fréquence [MHz]
    private Double puissanceAero;               // Puissance [W]
    private String modeCommunication;           // Mode de communication (AM, FM, Data)
    private String zoneOperation;               // Zone d'opération
    private String typeAntenneAero;             // Type d'antenne
    private String positionGPSAero;             // Position GPS

    // ===== SERVICE AMATEUR =====
    private String classeAmateur;               // Classe (A, B, C)
    private String indicatif;                   // Indicatif
    private Double frequenceAmateur;            // Fréquence [MHz]
    private Double puissanceAmateur;            // Puissance [W]
    private String modeEmission;                // Mode d'émission (CW, SSB, FM, Digital)
    private String antenneType;                 // Type d'antenne
    private String marqueAntenneAmateur;        // Marque d'antenne
    private Double gainAntenneAmateur;          // Gain d'antenne [dBi]
    private String positionGPSAmateur;          // Position GPS

    // ===== CONDITIONS =====
    @Column(columnDefinition = "TEXT")
    private String conditions;

    @Column(columnDefinition = "TEXT")
    private String observations;

    // ===== TRACABILITÉ =====
    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (numeroAutorisation == null) {
            numeroAutorisation = "AUT-" + System.currentTimeMillis();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}