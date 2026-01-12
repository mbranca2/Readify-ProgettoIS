package model.dao;

import model.Categoria;
import utils.DBManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public boolean inserisciCategoria(Categoria categoria) {
        String query = "INSERT INTO Categoria (nome_categoria) VALUES (?)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, categoria.getNomeCategoria());

            int righeInserite = stmt.executeUpdate();

            if (righeInserite > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    categoria.setIdCategoria(rs.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Categoria> trovaTutteCategorie() {
        List<Categoria> categorie = new ArrayList<>();
        String query = "SELECT * FROM Categoria ORDER BY nome_categoria";

        try (Connection conn = DBManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                categorie.add(mappaRisultatoACategoria(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categorie;
    }

    private Categoria mappaRisultatoACategoria(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(rs.getInt("id_categoria"));
        categoria.setNomeCategoria(rs.getString("nome_categoria"));
        return categoria;
    }

    public Categoria trovaCategoriaPerId(int id) {
        String query = "SELECT * FROM Categoria WHERE id_categoria = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mappaRisultatoACategoria(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Categoria trovaCategoriaPerNome(String nome) {
        String query = "SELECT * FROM Categoria WHERE nome_categoria = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mappaRisultatoACategoria(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}