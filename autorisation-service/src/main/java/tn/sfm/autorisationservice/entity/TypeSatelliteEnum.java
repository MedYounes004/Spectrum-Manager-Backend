package tn.sfm.autorisationservice.entity;

public enum TypeSatelliteEnum {
    GEO("GEO"),
    NON_GEO("Non-GEO");

    private String label;

    TypeSatelliteEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}