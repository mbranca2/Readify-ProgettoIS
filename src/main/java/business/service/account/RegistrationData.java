package business.service.account;

public record RegistrationData(String email, String password, String nome, String cognome, String telefono, String via,
                               String citta, String cap, String provincia, String paese) {
}
