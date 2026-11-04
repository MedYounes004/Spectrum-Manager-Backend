package tn.sfm.reportingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RapportActiviteDTO {
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private long totalDemandes;
    private long demandesApprouvees;
    private long demandesRefusees;
    private long demandesEnAttente;
    private double montantTotal;
    private double montantPerçu;
    private Map<String, Double> recettesParMois;
    private Map<String, Long> demandesParType;
}