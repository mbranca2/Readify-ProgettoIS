package model.bean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Carrello implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<Libro, Integer> articoli = new HashMap<>();

    public static class ArticoloCarrello implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private final Libro libro;
        private int quantita;
        
        public ArticoloCarrello(Libro libro, int quantita) {
            this.libro = libro;
            this.quantita = quantita;
        }
        
        public Libro getLibro() { return libro; }
        public int getQuantita() { return quantita; }
        public void setQuantita(int quantita) { this.quantita = quantita; }
        public BigDecimal getTotale() {
            return libro.getPrezzo().multiply(BigDecimal.valueOf(quantita));
        }
    }

    public boolean aggiungiLibro(Libro libro, int quantita) {
        if (libro == null || quantita <= 0 || libro.getDisponibilita() <= 0) {
            return false;
        }
        
        int nuovaQuantita = articoli.getOrDefault(libro, 0) + quantita;

        if (nuovaQuantita > libro.getDisponibilita()) {
            return false;
        }
        articoli.put(libro, nuovaQuantita);
        return true;
    }

    public boolean rimuoviLibro(int idLibro) {
        return articoli.keySet().removeIf(libro -> libro.getIdLibro() == idLibro);
    }

    public boolean aggiornaQuantita(int idLibro, int nuovaQuantita) {
        if (nuovaQuantita <= 0) {
            return rimuoviLibro(idLibro);
        }
        
        for (Map.Entry<Libro, Integer> entry : articoli.entrySet()) {
            if (entry.getKey().getIdLibro() == idLibro) {
                if (nuovaQuantita > entry.getKey().getDisponibilita()) {
                    return false;
                }
                entry.setValue(nuovaQuantita);
                return true;
            }
        }
        return false;
    }

    public int getTotaleArticoli() {
        return articoli.values().stream().mapToInt(Integer::intValue).sum();
    }

    public List<ArticoloCarrello> getArticoli() {
        return articoli.entrySet().stream()
                .map(e -> new ArticoloCarrello(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public BigDecimal getTotale() {
        return articoli.entrySet().stream()
                .map(e -> e.getKey().getPrezzo().multiply(BigDecimal.valueOf(e.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public void svuota() {
        articoli.clear();
    }

    public boolean isVuoto() {
        return articoli.isEmpty();
    }

    public List<DettaglioOrdine> getDettagli() {
        return articoli.entrySet().stream()
                .map(entry -> {
                    DettaglioOrdine dettaglio = new DettaglioOrdine();
                    dettaglio.setIdLibro(entry.getKey().getIdLibro());
                    dettaglio.setQuantita(entry.getValue());
                    dettaglio.setPrezzoUnitario(entry.getKey().getPrezzo());
                    dettaglio.setTitoloLibro(entry.getKey().getTitolo());
                    return dettaglio;
                })
                .collect(Collectors.toList());
    }
}