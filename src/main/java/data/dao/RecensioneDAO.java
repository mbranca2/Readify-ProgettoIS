package data.dao;

import business.model.Recensione;
import business.service.review.dto.AdminReviewRow;
import data.util.DBManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecensioneDAO {
    public List<Recensione> trovaRecensioniPerLibro(int idLibro) {
        List<Recensione> recensioni = new ArrayList<>();

        String query = "SELECT r.*, u.nome, u.cognome " +
                "FROM Recensione r " +
                "JOIN Utente u ON r.id_utente = u.id_utente " +
                "WHERE r.id_libro = ? " +
                "ORDER BY r.data_valutazione DESC";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idLibro);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    recensioni.add(mapRecensioneBase(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il recupero delle recensioni", e);
        }

        return recensioni;
    }

    public boolean inserisciRecensione(Recensione recensione) {
        String query = "INSERT INTO Recensione (id_utente, id_libro, voto, commento) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, recensione.getIdUtente());
            stmt.setInt(2, recensione.getIdLibro());
            stmt.setInt(3, recensione.getVoto());
            stmt.setString(4, recensione.getCommento());

            int rows = stmt.executeUpdate();
            if (rows <= 0) return false;

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    recensione.setIdRecensione(rs.getInt(1));
                }
            }

            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    public Recensione trovaRecensionePerId(int idRecensione) {
        String sql = "SELECT r.*, u.nome, u.cognome " +
                "FROM Recensione r " +
                "JOIN Utente u ON r.id_utente = u.id_utente " +
                "WHERE r.id_recensione = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idRecensione);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRecensioneBase(rs);
            }

        } catch (SQLException e) {
            return null;
        }
    }

    public Recensione trovaRecensionePerUtenteELibro(int idUtente, int idLibro) {
        String sql = "SELECT r.*, u.nome, u.cognome " +
                "FROM Recensione r " +
                "JOIN Utente u ON r.id_utente = u.id_utente " +
                "WHERE r.id_utente = ? AND r.id_libro = ? " +
                "ORDER BY r.data_valutazione DESC " +
                "LIMIT 1";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUtente);
            ps.setInt(2, idLibro);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRecensioneBase(rs);
            }

        } catch (SQLException e) {
            return null;
        }
    }

    public boolean aggiornaRecensione(int idRecensione, int idUtente, int voto, String commento) {
        String sql = "UPDATE Recensione SET voto = ?, commento = ? " +
                "WHERE id_recensione = ? AND id_utente = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, voto);
            ps.setString(2, commento);
            ps.setInt(3, idRecensione);
            ps.setInt(4, idUtente);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            return false;
        }
    }

    public boolean eliminaRecensione(int idRecensione, int idUtente) {
        String sql = "DELETE FROM Recensione WHERE id_recensione = ? AND id_utente = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idRecensione);
            ps.setInt(2, idUtente);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            return false;
        }
    }


    public List<AdminReviewRow> trovaTutteRecensioniAdmin() {
        List<AdminReviewRow> rows = new ArrayList<>();

        String sql = "SELECT r.id_recensione, r.id_libro, l.titolo AS titolo_libro, " +
                "r.id_utente, u.nome, u.cognome, u.email, " +
                "r.voto, r.commento, r.data_valutazione " +
                "FROM Recensione r " +
                "JOIN Utente u ON r.id_utente = u.id_utente " +
                "JOIN Libro l ON r.id_libro = l.id_libro " +
                "ORDER BY r.data_valutazione DESC";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                AdminReviewRow row = new AdminReviewRow();
                row.setIdRecensione(rs.getInt("id_recensione"));
                row.setIdLibro(rs.getInt("id_libro"));
                row.setTitoloLibro(rs.getString("titolo_libro"));
                row.setIdUtente(rs.getInt("id_utente"));

                String nome = rs.getString("nome");
                String cognome = rs.getString("cognome");
                row.setNomeUtente((nome != null ? nome : "") + " " + (cognome != null ? cognome : ""));
                row.setEmailUtente(rs.getString("email"));

                row.setVoto(rs.getInt("voto"));
                row.setCommento(rs.getString("commento"));
                row.setDataValutazione(rs.getDate("data_valutazione"));
                rows.add(row);
            }

        } catch (SQLException e) {
            return rows;
        }

        return rows;
    }

    public boolean eliminaRecensioneAdmin(int idRecensione) {
        String sql = "DELETE FROM Recensione WHERE id_recensione = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idRecensione);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            return false;
        }
    }

    private Recensione mapRecensioneBase(ResultSet rs) throws SQLException {
        Recensione recensione = new Recensione();
        recensione.setIdRecensione(rs.getInt("id_recensione"));
        recensione.setIdUtente(rs.getInt("id_utente"));
        recensione.setIdLibro(rs.getInt("id_libro"));
        recensione.setVoto(rs.getInt("voto"));
        recensione.setCommento(rs.getString("commento"));
        Date d = rs.getDate("data_valutazione");
        recensione.setDataRecensione(d);

        String nome = rs.getString("nome");
        String cognome = rs.getString("cognome");
        if (nome != null && cognome != null) {
            recensione.setNomeUtente(nome + " " + cognome);
        } else {
            recensione.setNomeUtente("Utente Anonimo");
        }
        return recensione;
    }
}
