package model.dao;

import model.Recensione;
import utils.DBManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecensioneDAO {

    public List<Recensione> trovaRecensioniPerLibro(int idLibro) {
        List<Recensione> recensioni = new ArrayList<>();

        String query = "SELECT v.*, u.nome, u.cognome FROM Valutazione v " +
                "JOIN Utente u ON v.id_utente = u.id_utente " +
                "WHERE v.id_libro = ? " +
                "ORDER BY v.data_valutazione DESC";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idLibro);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                try {
                    Recensione recensione = new Recensione();
                    recensione.setIdRecensione(rs.getInt("id_valutazione"));
                    recensione.setIdUtente(rs.getInt("id_utente"));
                    recensione.setIdLibro(rs.getInt("id_libro"));
                    recensione.setVoto(rs.getInt("voto"));
                    recensione.setCommento(rs.getString("commento"));
                    recensione.setDataRecensione(rs.getDate("data_valutazione"));
                    
                    // Costruisco  nome utente
                    String nome = rs.getString("nome");
                    String cognome = rs.getString("cognome");
                    if (nome != null && cognome != null) {
                        recensione.setNomeUtente(nome + " " + cognome);
                    } else {
                        recensione.setNomeUtente("Utente Anonimo");
                    }
                    
                    recensioni.add(recensione);
                } catch (SQLException e) {
                    System.err.println("Errore nel mapping della recensione: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero delle recensioni: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Errore durante il recupero delle recensioni", e);
        }

        return recensioni;
    }
}
