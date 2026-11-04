package tn.sfm.autorisationservice.entity;

public enum DiversiteEnum {
    _1_0("1+0"),
    _1_1("1+1"),
    _2_0("2+0"),
    _2_1("2+1"),
    AUTRE("Autre");

    private String label;

    DiversiteEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}