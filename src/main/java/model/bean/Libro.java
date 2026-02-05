package model.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Libro {
    private int idLibro;
    private String titolo;
    private String autore;
    private BigDecimal prezzo;
    private String isbn;
    private String descrizione;
    private int disponibilita;
    private String copertina;
    private List<Integer> categorie;

    public Libro() {
        this.categorie = new ArrayList<>();
    }

    public Libro(int idLibro, String titolo, String autore, BigDecimal prezzo,
                 String isbn, String descrizione, int disponibilita, String copertina) {
        this.idLibro = idLibro;
        this.titolo = titolo;
        this.autore = autore;
        this.prezzo = prezzo;
        this.isbn = isbn;
        this.descrizione = descrizione;
        this.disponibilita = disponibilita;
        this.copertina = copertina;
        this.categorie = new ArrayList<>();
    }

    public int getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getAutore() {
        return autore;
    }

    public void setAutore(String autore) {
        this.autore = autore;
    }

    public BigDecimal getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(BigDecimal prezzo) {
        this.prezzo = prezzo;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public int getDisponibilita() {
        return disponibilita;
    }

    public void setDisponibilita(int disponibilita) {
        this.disponibilita = disponibilita;
    }

    public String getCopertina() {
        return copertina;
    }

    public void setCopertina(String copertina) {
        this.copertina = copertina;
    }

    public List<Integer> getCategorie() {
        return categorie;
    }

    public void setCategorie(List<Integer> categorie) {
        this.categorie = categorie;
    }

    public void aggiungiCategoria(int idCategoria) {
        if (!this.categorie.contains(idCategoria)) {
            this.categorie.add(idCategoria);
        }
    }

    public void rimuoviCategoria(int idCategoria) {
        this.categorie.remove(Integer.valueOf(idCategoria));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Libro libro = (Libro) o;
        return idLibro == libro.idLibro;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idLibro);
    }

    @Override
    public String toString() {
        return "Libro{" +
                "idLibro=" + idLibro +
                ", titolo='" + titolo + '\'' +
                ", autore='" + autore + '\'' +
                ", prezzo=" + prezzo +
                ", isbn='" + isbn + '\'' +
                ", disponibilita=" + disponibilita +
                '}';
    }
}