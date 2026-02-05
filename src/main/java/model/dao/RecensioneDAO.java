package model.dao;

import model.bean.Recensione;
import utils.DBManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RecensioneDAO {

    public List<Recensione> trovaRecensioniPerLibro(int idLibro) {
        List<Recensione> recensioni = new ArrayList<>();

        String query = "SELECT r.*, u.nome, u.cognome FROM Recensione r " +
                "JOIN Utente u ON r.id_utente = u.id_utente " +
                "WHERE r.id_libro = ? " +
                "ORDER BY r.data_valutazione DESC";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idLibro);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Recensione recensione = new Recensione();
                    recensione.setIdRecensione(rs.getInt("id_recensione"));
                    recensione.setIdUtente(rs.getInt("id_utente"));
                    recensione.setIdLibro(rs.getInt("id_libro"));
                    recensione.setVoto(rs.getInt("voto"));
                    recensione.setCommento(rs.getString("commento"));
                    recensione.setDataRecensione(rs.getDate("data_valutazione"));

                    String nome = rs.getString("nome");
                    String cognome = rs.getString("cognome");
                    if (nome != null && cognome != null) {
                        recensione.setNomeUtente(nome + " " + cognome);
                    } else {
                        recensione.setNomeUtente("Utente Anonimo");
                    }

                    recensioni.add(recensione);
                }
            }

        } catch (SQLException e) {
            System.err.println("Errore durante il recupero delle recensioni: " + e.getMessage());
            e.printStackTrace();
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
            System.err.println("Errore durante l'inserimento della recensione: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
