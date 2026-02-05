package service.account;

public class RegistrationData {
    // dati utente
    public final String email;
    public final String password; // in chiaro (il service la cifra)
    public final String nome;
    public final String cognome;
    public final String telefono;

    // dati indirizzo
    public final String via;
    public final String citta;
    public final String cap;
    public final String provincia;
    public final String paese;

    public RegistrationData(String email, String password, String nome, String cognome, String telefono,
                            String via, String citta, String cap, String provincia, String paese) {
        this.email = email;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.telefono = telefono;
        this.via = via;
        this.citta = citta;
        this.cap = cap;
        this.provincia = provincia;
        this.paese = paese;
    }
}
