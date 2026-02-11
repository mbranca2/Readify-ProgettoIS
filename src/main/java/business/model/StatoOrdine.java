package business.model;

public enum StatoOrdine {
    IN_ATTESA("In attesa di pagamento", "in_attesa"),
    PAGATO("Pagato", "pagato"),
    IN_ELABORAZIONE("In elaborazione", "in_elaborazione"),
    SPEDITO("Spedito", "spedito"),
    CONSEGNATO("Consegnato", "consegnato"),
    ANNULLATO("Annullato", "annullato"),
    RIMBORSATO("Rimborsato", "rimborsato");

    private final String descrizione;
    private final String dbValue;

    StatoOrdine(String descrizione, String dbValue) {
        this.descrizione = descrizione;
        this.dbValue = dbValue;
    }

    public static StatoOrdine fromDbValue(String dbValue) {
        if (dbValue == null) return IN_ATTESA;
        String v = dbValue.trim().toLowerCase();
        for (StatoOrdine s : values()) {
            if (s.dbValue.equals(v)) return s;
        }
        return IN_ATTESA;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String toDbValue() {
        return dbValue;
    }

    @Override
    public String toString() {
        return descrizione;
    }
}
