package model.bean;

public class Utente {
    private int idUtente;
    private String email;
    private String passwordCifrata;
    private String nome;
    private String cognome;
    private String ruolo;
    private String telefono;

    public Utente() {
    }

    public Utente(int idUtente, String email, String passwordCifrata,
                  String nome, String cognome, String ruolo, String telefono) {
        this.idUtente = idUtente;
        this.email = email;
        this.passwordCifrata = passwordCifrata;
        this.nome = nome;
        this.cognome = cognome;
        this.ruolo = ruolo;
        this.telefono = telefono;
    }

    public int getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(int idUtente) {
        this.idUtente = idUtente;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordCifrata() {
        return passwordCifrata;
    }

    public void setPasswordCifrata(String passwordCifrata) {
        this.passwordCifrata = passwordCifrata;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isAdmin() {
        return ruolo.equals("admin");
    }
}