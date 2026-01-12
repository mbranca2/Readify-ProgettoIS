package model;

import java.sql.Date;

public class Recensione {
    private int idRecensione;
    private int idUtente;
    private int idLibro;
    private int voto;
    private String commento;
    private Date dataRecensione;
    private String nomeUtente;

    public Recensione() {} //costruttore vuoto dato che non faccio inserire recensioni sul sito


    public void setIdRecensione(int idRecensione) {
        this.idRecensione = idRecensione;
    }

    public void setIdUtente(int idUtente) {
        this.idUtente = idUtente;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    public void setVoto(int voto) {
        this.voto = voto;
    }

    public void setCommento(String commento) {
        this.commento = commento;
    }

    public Date getDataRecensione() {
        return dataRecensione;
    }

    public void setDataRecensione(Date dataRecensione) {
        this.dataRecensione = dataRecensione;
    }
    
    public int getVoto() {
        return voto;
    }
    
    public String getCommento() {
        return commento;
    }

    public String getNomeUtente() {
        return nomeUtente;
    }

    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }

}
