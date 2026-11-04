package tn.sfm.facturationservice.entity;

public enum TypeFacture {
    REDEVANCE("Redevance"),
    AMENDE("Amende"),
    RENOUVELLEMENT("Renouvellement"),
    PENALITE("Pénalité"),
    AVOIR("Facture Avoir"),
    INDIVIDUELLE("Facture Individuelle"),
    COLLECTIVE("Facture Collective"),
    AUTRE("Autre");

    private String label;

    TypeFacture(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}