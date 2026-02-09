package model.dao;

import model.bean.Utente;
import utils.DBManager;
import utils.HashUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtenteDAO {

    public boolean inserisciUtente(Utente utente) {
        String query = "INSERT INTO Utente (email, password_cifrata, nome, cognome, ruolo, telefono) VALUES (?, ?, ?, ?, ?, ?)";
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

    // ✅ Overload per transazioni (stessa insert ma usa conn esterna)
    public boolean inserisciUtente(Connection conn, Utente utente) throws SQLException {
        String query = "INSERT INTO Utente (email, password_cifrata, nome, cognome, ruolo, telefono) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, utente.getEmail());
            stmt.setString(2, utente.getPasswordCifrata());
            stmt.setString(3, utente.getNome());
            stmt.setString(4, utente.getCognome());
            stmt.setString(5, utente.getRuolo());
            stmt.setString(6, utente.getTelefono());

            int righeInserite = stmt.executeUpdate();
            if (righeInserite > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        utente.setIdUtente(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    // ✅ Check duplicato email (in conn transazionale)
    public boolean emailEsistente(Connection conn, String email) throws SQLException {
        String sql = "SELECT 1 FROM Utente WHERE email = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
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
        String query = "UPDATE Utente SET email = ?, nome = ?, cognome = ?, ruolo = ?, telefono = ? WHERE id_utente = ?";

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

    public boolean changePassword(int idUtente, String oldPassword, String newPassword) {
        if (idUtente <= 0) return false;
        if (oldPassword == null || newPassword == null) return false;

        String select = "SELECT password_cifrata FROM Utente WHERE id_utente = ?";
        String update = "UPDATE Utente SET password_cifrata = ? WHERE id_utente = ?";

        try (Connection conn = DBManager.getConnection()) {
            conn.setAutoCommit(false);

            String storedHash;
            try (PreparedStatement ps = conn.prepareStatement(select)) {
                ps.setInt(1, idUtente);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    storedHash = rs.getString("password_cifrata");
                }
            }

            String providedHash = HashUtil.sha1(oldPassword);
            if (storedHash == null || !storedHash.equals(providedHash)) {
                conn.rollback();
                return false;
            }

            String newHash = HashUtil.sha1(newPassword);
            try (PreparedStatement ps = conn.prepareStatement(update)) {
                ps.setString(1, newHash);
                ps.setInt(2, idUtente);
                boolean ok = ps.executeUpdate() > 0;
                if (!ok) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Utente trovaUtentePerEmail(String email) {
        String query = "SELECT * FROM Utente WHERE email = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mappaRisultatoAUtente(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
