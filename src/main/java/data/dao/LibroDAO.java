package data.dao;

import business.model.Libro;
import data.util.DBManager;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {
    public boolean inserisciLibro(Connection conn, Libro libro) throws SQLException {
        String query = "INSERT INTO Libro (titolo, autore, prezzo, isbn, descrizione, disponibilita, copertina) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, libro.getTitolo());
            stmt.setString(2, libro.getAutore());
            stmt.setBigDecimal(3, libro.getPrezzo());
            stmt.setString(4, libro.getIsbn());
            stmt.setString(5, libro.getDescrizione());
            stmt.setInt(6, libro.getDisponibilita());
            stmt.setString(7, libro.getCopertina());

            int righeInserite = stmt.executeUpdate();
            if (righeInserite > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        libro.setIdLibro(rs.getInt(1));
                        inserisciCategorieLibro(conn, libro.getIdLibro(), libro.getCategorie());
                    }
                }
                return true;
            }
            return false;
        }
    }

    private void inserisciCategorieLibro(Connection conn, int idLibro, List<Integer> idCategorie) throws SQLException {
        if (idCategorie == null || idCategorie.isEmpty()) {
            return;
        }

        String query = "INSERT INTO LibroCategoria (id_libro, id_categoria) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (Integer idCategoria : idCategorie) {
                stmt.setInt(1, idLibro);
                stmt.setInt(2, idCategoria);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    public Libro trovaLibroPerId(int id) {
        String query = "SELECT * FROM Libro WHERE id_libro = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Libro libro = mappaRisultatoALibro(rs);
                libro.setCategorie(trovaCategoriePerId(id));
                return libro;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Integer> trovaCategoriePerId(int idLibro) throws SQLException {
        List<Integer> categorie = new ArrayList<>();
        String query = "SELECT id_categoria FROM LibroCategoria WHERE id_libro = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idLibro);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                categorie.add(rs.getInt("id_categoria"));
            }
        }
        return categorie;
    }

    private Libro mappaRisultatoALibro(ResultSet rs) throws SQLException {
        Libro libro = new Libro();
        libro.setIdLibro(rs.getInt("id_libro"));
        libro.setTitolo(rs.getString("titolo"));
        libro.setAutore(rs.getString("autore"));
        libro.setPrezzo(rs.getBigDecimal("prezzo"));
        libro.setIsbn(rs.getString("isbn"));
        libro.setDescrizione(rs.getString("descrizione"));
        libro.setDisponibilita(rs.getInt("disponibilita"));
        libro.setCopertina(rs.getString("copertina"));
        return libro;
    }

    public boolean aggiornaLibro(Connection conn, Libro libro) throws SQLException {
        String query = "UPDATE Libro SET titolo = ?, autore = ?, prezzo = ?, " +
                "isbn = ?, descrizione = ?, disponibilita = ?, copertina = ? " +
                "WHERE id_libro = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, libro.getTitolo());
            stmt.setString(2, libro.getAutore());
            stmt.setBigDecimal(3, libro.getPrezzo());
            stmt.setString(4, libro.getIsbn());
            stmt.setString(5, libro.getDescrizione());
            stmt.setInt(6, libro.getDisponibilita());
            stmt.setString(7, libro.getCopertina());
            stmt.setInt(8, libro.getIdLibro());

            aggiornaCategorie(conn, libro.getIdLibro(), libro.getCategorie());

            return stmt.executeUpdate() > 0;
        }
    }

    private void aggiornaCategorie(Connection conn, int idLibro, List<Integer> nuoveCategorie) throws SQLException {
        String deleteQuery = "DELETE FROM LibroCategoria WHERE id_libro = ?";

        try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
            stmt.setInt(1, idLibro);
            stmt.executeUpdate();
        }

        inserisciCategorieLibro(conn, idLibro, nuoveCategorie);
    }

    public List<Libro> trovaLibriConFiltro(String titolo, String autore, String categoriaId, BigDecimal prezzoMin, BigDecimal prezzoMax, int offset, int limit) {
        List<Libro> libri = new ArrayList<>();
        String query = "SELECT DISTINCT l.* FROM Libro l ";

        if (categoriaId != null && !categoriaId.trim().isEmpty()) {
            query += " INNER JOIN LibroCategoria lc ON l.id_libro = lc.id_libro ";
            query += " WHERE lc.id_categoria = ? ";
        } else {
            query += " WHERE 1=1 ";
        }

        if (titolo != null && !titolo.trim().isEmpty()) {
            query += " AND LOWER(l.titolo) LIKE LOWER(?) ";
        }
        if (autore != null && !autore.trim().isEmpty()) {
            query += " AND LOWER(l.autore) LIKE LOWER(?) ";
        }
        if (prezzoMin != null) {
            query += " AND l.prezzo >= ? ";
        }
        if (prezzoMax != null) {
            query += " AND l.prezzo <= ? ";
        }

        query += " ORDER BY l.titolo LIMIT ? OFFSET ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            int paramIndex = 1;

            if (categoriaId != null && !categoriaId.trim().isEmpty()) {
                try {
                    int idCategoria = Integer.parseInt(categoriaId);
                    stmt.setInt(paramIndex++, idCategoria);
                } catch (NumberFormatException e) {
                    System.err.println("[LibroDAO] Errore nel formato dell'ID categoria: " + categoriaId);
                }
            }

            if (titolo != null && !titolo.trim().isEmpty()) {
                stmt.setString(paramIndex++, "%" + titolo + "%");
            }

            if (autore != null && !autore.trim().isEmpty()) {
                stmt.setString(paramIndex++, "%" + autore + "%");
            }

            if (prezzoMin != null) {
                stmt.setBigDecimal(paramIndex++, prezzoMin);
            }

            if (prezzoMax != null) {
                stmt.setBigDecimal(paramIndex++, prezzoMax);
            }

            stmt.setInt(paramIndex++, limit);
            stmt.setInt(paramIndex, offset);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                libri.add(mappaRisultatoALibro(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return libri;
    }

    public int contaLibriConFiltro(String titolo, String autore, String categoriaId, BigDecimal prezzoMin, BigDecimal prezzoMax) {
        String query = "SELECT COUNT(DISTINCT l.id_libro) FROM Libro l ";

        if (categoriaId != null && !categoriaId.trim().isEmpty()) {
            query += " INNER JOIN LibroCategoria lc ON l.id_libro = lc.id_libro ";
            query += " WHERE lc.id_categoria = ? ";
        } else {
            query += " WHERE 1=1 ";
        }

        if (titolo != null && !titolo.trim().isEmpty()) {
            query += " AND LOWER(l.titolo) LIKE LOWER(?) ";
        }

        if (autore != null && !autore.trim().isEmpty()) {
            query += " AND LOWER(l.autore) LIKE LOWER(?) ";
        }

        if (prezzoMin != null) {
            query += " AND l.prezzo >= ? ";
        }

        if (prezzoMax != null) {
            query += " AND l.prezzo <= ? ";
        }

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            int paramIndex = 1;

            if (categoriaId != null && !categoriaId.trim().isEmpty()) {
                try {
                    int idCategoria = Integer.parseInt(categoriaId);
                    stmt.setInt(paramIndex++, idCategoria);
                } catch (NumberFormatException e) {
                    System.err.println("[LibroDAO] Errore nel formato dell'ID categoria (conta): " + categoriaId);
                }
            }

            if (titolo != null && !titolo.trim().isEmpty()) {
                stmt.setString(paramIndex++, "%" + titolo + "%");
            }

            if (autore != null && !autore.trim().isEmpty()) {
                stmt.setString(paramIndex++, "%" + autore + "%");
            }

            if (prezzoMin != null) {
                stmt.setBigDecimal(paramIndex++, prezzoMin);
            }

            if (prezzoMax != null) {
                stmt.setBigDecimal(paramIndex++, prezzoMax);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<Libro> trovaTutti() {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM Libro";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Libro libro = mappaRisultatoALibro(rs);
                libro.setCategorie(trovaCategoriePerId(libro.getIdLibro()));
                lista.add(libro);
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero dei libri:");
            e.printStackTrace();
        }
        return lista;
    }
}
