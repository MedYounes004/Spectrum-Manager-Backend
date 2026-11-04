package tn.sfm.autorisationservice.entity;

public enum NormeDiffusionEnum {
    DVB_T("DVB-T"),
    DVB_T2("DVB-T2"),
    FM("FM"),
    AM("AM"),
    DAB("DAB"),
    DRM("DRM");

    private String label;

    NormeDiffusionEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}