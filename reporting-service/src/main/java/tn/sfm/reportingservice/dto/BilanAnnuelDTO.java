package tn.sfm.reportingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BilanAnnuelDTO {
    private int annee;
    private long totalAutorisations;
    private long autorisationsActives;
    private long autorisationsExpirees;
    private long autorisationsEnAttente;
    private long nouvellesAutorisations;
    private double totalRecettes;
    private Map<String, Long> autorisationsParType;
    private Map<String, Double> recettesParType;
    private double tauxCroissance;
}