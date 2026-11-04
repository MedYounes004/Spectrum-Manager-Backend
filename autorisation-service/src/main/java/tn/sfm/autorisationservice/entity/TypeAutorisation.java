package tn.sfm.autorisationservice.entity;

public enum TypeAutorisation {
    MOBILE_TERRESTRE("Service mobile terrestre", "mobile.png"),
    RADIODIFFUSION("Service de radiodiffusion (TV, radio)", "radio.png"),
    AMATEUR("Service des amateurs", "amateur.png"),
    MOBILE_AERONAUTIQUE("Services mobiles aéronautique", "aeronautique.png"),
    MARITIME("Service maritime", "maritime.png"),
    FIXE("Service fixe (faisceaux hertziens)", "fixe.png"),
    SATELLITE("Services par satellites (GSO et non-GSO)", "satellite.png");

    private String label;
    private String logo;

    TypeAutorisation(String label, String logo) {
        this.label = label;
        this.logo = logo;
    }

    public String getLabel() {
        return label;
    }

    public String getLogo() {
        return logo;
    }
}