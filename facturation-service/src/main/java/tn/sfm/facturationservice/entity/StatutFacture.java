package tn.sfm.facturationservice.entity;

public enum StatutFacture {
    EN_ATTENTE("En attente"),
    PAYEE("Payée"),
    EN_RETARD("En retard"),
    ANNULEE("Annulée"),
    EN_COURS("En cours");

    private String label;

    StatutFacture(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}