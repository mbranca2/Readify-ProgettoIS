package model.dao;

import model.Utente;
import utils.DBManager;
import utils.HashUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtenteDAO {

    public boolean inserisciUtente(Utente utente) {
        String query = "INSERT INTO Utente (email, password_cifrata, nome, cognome, ruolo, telefono) " +
                      "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, utente.getEmail());
            stmt.setString(2, utente.getPasswordCifrata());
            stmt.setString(3, utente.getNome());
            stmt.setString(4, utente.getCognome());
            stmt.setString(5, utente.getRuolo());
            stmt.setString(6, utente.getTelefono());

            int righeInserite = stmt.executeUpdate();

            if (righeInserite > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    utente.setIdUtente(rs.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Utente trovaUtentePerId(int id) {
        String query = "SELECT * FROM Utente WHERE id_utente = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mappaRisultatoAUtente(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    //  per mappare un ResultSet a un oggetto Utente
    private Utente mappaRisultatoAUtente(ResultSet rs) throws SQLException {
        Utente utente = new Utente();
        utente.setIdUtente(rs.getInt("id_utente"));
        utente.setEmail(rs.getString("email"));
        utente.setPasswordCifrata(rs.getString("password_cifrata"));
        utente.setNome(rs.getString("nome"));
        utente.setCognome(rs.getString("cognome"));
        utente.setRuolo(rs.getString("ruolo"));
        utente.setTelefono(rs.getString("telefono"));
        return utente;
    }

    // List di tutti gli utenti
    public List<Utente> trovaTuttiUtenti() {
        List<Utente> utenti = new ArrayList<>();
        String query = "SELECT * FROM Utente";

        try (Connection conn = DBManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                utenti.add(mappaRisultatoAUtente(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return utenti;
    }

    public static Utente login(String email, String password) {
        Utente user = null;
        String sql = "SELECT * FROM Utente WHERE email = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_cifrata");
                    String providedHash = HashUtil.sha1(password);

                    if (storedHash != null && storedHash.equals(providedHash)) {
                        user = new Utente(
                                rs.getInt("id_utente"),
                                rs.getString("email"),
                                storedHash,
                                rs.getString("nome"),
                                rs.getString("cognome"),
                                rs.getString("ruolo"),
                                rs.getString("telefono")
                        );
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Errore durante il login:");
            e.printStackTrace();
        }
        return user;
    }

    public boolean aggiornaUtente(Utente utente) {
        String query = "UPDATE Utente SET email = ?, nome = ?, cognome = ?, " +
                "ruolo = ?, telefono = ? WHERE id_utente = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, utente.getEmail());
            stmt.setString(2, utente.getNome());
            stmt.setString(3, utente.getCognome());
            stmt.setString(4, utente.getRuolo());
            stmt.setString(5, utente.getTelefono());
            stmt.setInt(6, utente.getIdUtente());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Eliminazione utente
    public boolean eliminaUtente(int id) {
        String query = "DELETE FROM Utente WHERE id_utente = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}