package data.dao;

import business.model.Categoria;
import data.util.DBManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {
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
}