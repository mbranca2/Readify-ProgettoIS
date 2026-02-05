package model.dao;

import model.bean.Carrello;
import model.bean.Libro;
import utils.DBManager;

import java.sql.*;

public class CarrelloDAO {
    public Carrello getCarrelloByUtente(int idUtente) throws SQLException {
        String query = "SELECT c.id_carrello, c.data_creazione, c.data_aggiornamento, " +
                "dc.id_dettaglio, dc.id_libro, dc.quantita, dc.prezzo_unitario, " +
                "l.titolo, l.autore, l.copertina, l.disponibilita, l.prezzo as prezzo_effettivo " +
                "FROM Carrello c " +
                "LEFT JOIN DettaglioCarrello dc ON c.id_carrello = dc.id_carrello " +
                "LEFT JOIN Libro l ON dc.id_libro = l.id_libro " +
                "WHERE c.id_utente = ?";

        Carrello carrello = new Carrello();

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idUtente);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                if (rs.getInt("id_carrello") == 0) {
                    return null;
                }

                if (rs.getInt("id_dettaglio") != 0) {
                    Libro libro = new Libro();
                    libro.setIdLibro(rs.getInt("id_libro"));
                    libro.setTitolo(rs.getString("titolo"));
                    libro.setAutore(rs.getString("autore"));
                    libro.setCopertina(rs.getString("copertina"));
                    libro.setDisponibilita(rs.getInt("disponibilita"));
                    libro.setPrezzo(rs.getBigDecimal("prezzo_effettivo"));

                    int quantita = rs.getInt("quantita");
                    carrello.aggiungiLibro(libro, quantita);
                }
            }
        }
        return carrello;
    }

    public int creaCarrello(Connection conn, int idUtente) throws SQLException {
        String query = "INSERT INTO Carrello (id_utente) VALUES (?)";

        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, idUtente);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creazione carrello fallita, nessuna riga inserita.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creazione carrello fallita, nessun ID ottenuto.");
                }
            }
        }
    }

    public void salvaCarrello(Connection conn, int idUtente, Carrello carrello) throws SQLException {
        int idCarrello = getCarrelloIdByUtente(conn, idUtente);
        if (idCarrello == 0) {
            idCarrello = creaCarrello(conn, idUtente);
        } else {
            svuotaCarrello(conn, idCarrello);
        }

        for (Carrello.ArticoloCarrello articolo : carrello.getArticoli()) {
            aggiungiArticoloAlCarrello(conn, idCarrello, articolo);
        }
    }

    public void salvaCarrello(int idUtente, Carrello carrello) throws SQLException {
        Connection conn = null;
        try {
            conn = DBManager.getConnection();
            conn.setAutoCommit(false);

            salvaCarrello(conn, idUtente, carrello);

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    private int getCarrelloIdByUtente(Connection conn, int idUtente) throws SQLException {
        String query = "SELECT id_carrello FROM Carrello WHERE id_utente = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idUtente);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_carrello");
            }
            return 0;
        }
    }

    private void svuotaCarrello(Connection conn, int idCarrello) throws SQLException {
        String query = "DELETE FROM DettaglioCarrello WHERE id_carrello = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idCarrello);
            stmt.executeUpdate();
        }
    }

    private void aggiungiArticoloAlCarrello(Connection conn, int idCarrello, Carrello.ArticoloCarrello articolo) throws SQLException {
        String query = "INSERT INTO DettaglioCarrello (id_carrello, id_libro, quantita, prezzo_unitario) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idCarrello);
            stmt.setInt(2, articolo.getLibro().getIdLibro());
            stmt.setInt(3, articolo.getQuantita());
            stmt.setBigDecimal(4, articolo.getLibro().getPrezzo());
            stmt.executeUpdate();
        }
    }

    public void rimuoviArticolo(int idUtente, int idLibro) throws SQLException {
        String query = "DELETE dc FROM DettaglioCarrello dc " +
                "JOIN Carrello c ON dc.id_carrello = c.id_carrello " +
                "WHERE c.id_utente = ? AND dc.id_libro = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idUtente);
            stmt.setInt(2, idLibro);
            stmt.executeUpdate();
        }
    }

    public void aggiornaQuantita(int idUtente, int idLibro, int quantita) throws SQLException {
        if (quantita <= 0) {
            rimuoviArticolo(idUtente, idLibro);
            return;
        }

        String query = "UPDATE DettaglioCarrello dc " +
                "JOIN Carrello c ON dc.id_carrello = c.id_carrello " +
                "SET dc.quantita = ? " +
                "WHERE c.id_utente = ? AND dc.id_libro = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, quantita);
            stmt.setInt(2, idUtente);
            stmt.setInt(3, idLibro);
            stmt.executeUpdate();
        }
    }

    public void svuotaCarrelloUtente(Connection conn, int idUtente) throws SQLException {
        int idCarrello = getCarrelloIdByUtente(conn, idUtente);
        if (idCarrello != 0) {
            svuotaCarrello(conn, idCarrello);
        }
    }
}
