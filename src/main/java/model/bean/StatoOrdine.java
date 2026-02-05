package model.bean;

public enum StatoOrdine {
    IN_ATTESA("In attesa di pagamento"),
    PAGATO("Pagato"),
    IN_ELABORAZIONE("In elaborazione"),
    SPEDITO("Spedito"),
    CONSEGNATO("Consegnato"),
    ANNULLATO("Annullato"),
    RIMBORSATO("Rimborsato");

    private final String descrizione;

    StatoOrdine(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getDescrizione() {
        return descrizione;
    }

    @Override
    public String toString() {
        return descrizione;
    }
}
