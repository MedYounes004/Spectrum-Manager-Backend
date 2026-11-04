package tn.sfm.autorisationservice.entity;

public enum TechnologieEnum {
    G2("2G"),
    G3("3G"),
    G4("4G"),
    G5("5G"),
    G6("6G");

    private String label;

    TechnologieEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}