package data.dao;

import business.model.Indirizzo;
import data.util.DBManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IndirizzoDAO {

    public boolean inserisciIndirizzo(Indirizzo indirizzo) {
        String query = "INSERT INTO Indirizzo (id_utente, via, cap, citta, provincia, paese) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, indirizzo.getIdUtente());
            stmt.setString(2, indirizzo.getVia());
            stmt.setString(3, indirizzo.getCap());
            stmt.setString(4, indirizzo.getCitta());
            stmt.setString(5, indirizzo.getProvincia());
            stmt.setString(6, indirizzo.getPaese() != null ? indirizzo.getPaese() : "Italia");

            int righeInserite = stmt.executeUpdate();
            if (righeInserite > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        indirizzo.setIdIndirizzo(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Overload per transazioni (stessa insert ma usa conn esterna)
    public boolean inserisciIndirizzo(Connection conn, Indirizzo indirizzo) throws SQLException {
        String query = "INSERT INTO Indirizzo (id_utente, via, cap, citta, provincia, paese) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, indirizzo.getIdUtente());
            stmt.setString(2, indirizzo.getVia());
            stmt.setString(3, indirizzo.getCap());
            stmt.setString(4, indirizzo.getCitta());
            stmt.setString(5, indirizzo.getProvincia());
            stmt.setString(6, indirizzo.getPaese() != null ? indirizzo.getPaese() : "Italia");

            int righeInserite = stmt.executeUpdate();
            if (righeInserite > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        indirizzo.setIdIndirizzo(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public Indirizzo trovaIndirizzoPerId(int id) {
        String query = "SELECT * FROM Indirizzo WHERE id_indirizzo = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mappaRisultatoAIndirizzo(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Indirizzo trovaIndirizzoPerIdUtente(int idUtente) {
        String query = "SELECT * FROM Indirizzo WHERE id_utente = ? ORDER BY id_indirizzo ASC";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idUtente);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mappaRisultatoAIndirizzo(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Indirizzo> trovaIndirizziPerUtente(int idUtente) {
        List<Indirizzo> indirizzi = new ArrayList<>();
        String query = "SELECT * FROM Indirizzo WHERE id_utente = ? ORDER BY id_indirizzo ASC";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idUtente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    indirizzi.add(mappaRisultatoAIndirizzo(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return indirizzi;
    }

    public boolean aggiornaIndirizzo(Indirizzo indirizzo) {
        String query = "UPDATE Indirizzo SET via = ?, cap = ?, citta = ?, provincia = ?, paese = ? " +
                "WHERE id_indirizzo = ? AND id_utente = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, indirizzo.getVia());
            stmt.setString(2, indirizzo.getCap());
            stmt.setString(3, indirizzo.getCitta());
            stmt.setString(4, indirizzo.getProvincia());
            stmt.setString(5, indirizzo.getPaese());
            stmt.setInt(6, indirizzo.getIdIndirizzo());
            stmt.setInt(7, indirizzo.getIdUtente());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminaIndirizzo(int idIndirizzo, int idUtente) {
        if (idIndirizzo <= 0 || idUtente <= 0) return false;

        String query = "DELETE FROM Indirizzo WHERE id_indirizzo = ? AND id_utente = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idIndirizzo);
            stmt.setInt(2, idUtente);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Indirizzo mappaRisultatoAIndirizzo(ResultSet rs) throws SQLException {
        Indirizzo indirizzo = new Indirizzo();
        indirizzo.setIdIndirizzo(rs.getInt("id_indirizzo"));
        indirizzo.setIdUtente(rs.getInt("id_utente"));
        indirizzo.setVia(rs.getString("via"));
        indirizzo.setCap(rs.getString("cap"));
        indirizzo.setCitta(rs.getString("citta"));
        indirizzo.setProvincia(rs.getString("provincia"));
        indirizzo.setPaese(rs.getString("paese"));
        return indirizzo;
    }
}
