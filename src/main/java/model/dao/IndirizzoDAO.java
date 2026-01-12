package model.dao;

import model.Indirizzo;
import utils.DBManager;

import java.sql.*;

public class IndirizzoDAO {

    //metodo per l'inserimento di un nuovo indirizzo (mai usato)
    public boolean inserisciIndirizzo(Indirizzo indirizzo) {
        String query = "INSERT INTO Indirizzo (id_utente, via, cap, citta, provincia, paese) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

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
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    indirizzo.setIdIndirizzo(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Indirizzo trovaIndirizzoPerId(int id) {
        String query = "SELECT * FROM Indirizzo WHERE id_indirizzo = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mappaRisultatoAIndirizzo(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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