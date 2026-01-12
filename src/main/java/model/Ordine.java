package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Ordine {
    private int idOrdine;
    private int idUtente;
    private int idIndirizzo;
    private Date dataOrdine;
    private StatoOrdine stato;
    private BigDecimal totale;
    private List<DettaglioOrdine> dettagli;
    public Ordine() {
        this.dettagli = new ArrayList<>();
        this.dataOrdine = new Date(System.currentTimeMillis());
        this.stato = StatoOrdine.IN_ELABORAZIONE;
        this.totale = BigDecimal.ZERO;
    }


    public int getIdOrdine() {
        return idOrdine;
    }
    
    public void setIdOrdine(int idOrdine) {
        this.idOrdine = idOrdine;
    }

    public int getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(int idUtente) {
        this.idUtente = idUtente;
    }

    public int getIdIndirizzo() {
        return idIndirizzo;
    }

    public void setIdIndirizzo(int idIndirizzo) {
        this.idIndirizzo = idIndirizzo;
    }

    public Date getDataOrdine() {
        return dataOrdine;
    }

    public void setDataOrdine(Date dataOrdine) {
        this.dataOrdine = dataOrdine != null ? new Date(dataOrdine.getTime()) : null;
    }

    public StatoOrdine getStato() {
        return stato;
    }

    public void setStato(StatoOrdine stato) {
        this.stato = stato;
    }

    public void setStato(String stato) {
        try {
            this.stato = StatoOrdine.valueOf(stato.toUpperCase());
        } catch (IllegalArgumentException e) {
            this.stato = StatoOrdine.IN_ELABORAZIONE;
        }
    }

    public BigDecimal getTotale() {
        return totale;
    }

    public void setTotale(BigDecimal totale) {
        this.totale = totale;
    }

    public List<DettaglioOrdine> getDettagli() {
        return dettagli;
    }

    public void setDettagli(List<DettaglioOrdine> dettagli) {
        this.dettagli = dettagli;
        calcolaTotale();
    }

    public void aggiungiDettaglio(DettaglioOrdine dettaglio) {
        if (dettagli == null) {
            dettagli = new ArrayList<>();
        }
        dettagli.add(dettaglio);
        calcolaTotale();
    }

    private void calcolaTotale() {
        this.totale = dettagli.stream()
                .map(d -> d.getPrezzoUnitario().multiply(new BigDecimal(d.getQuantita())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}