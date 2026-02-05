package service.catalog;

import model.bean.Libro;

import java.util.List;

public interface CatalogService {
    List<Libro> listAll();

    /**
     * Ricerca con filtri + paginazione.
     *
     * @param titolo      filtro titolo (LIKE), può essere null/vuoto
     * @param autore      filtro autore (LIKE), può essere null/vuoto
     * @param categoriaId id categoria come stringa (coerente con DAO), può essere null/vuoto
     * @param page        pagina 1-based
     * @param pageSize    elementi per pagina
     */
    List<Libro> search(String titolo, String autore, String categoriaId, int page, int pageSize);

    /**
     * Conteggio risultati della ricerca (per paginazione).
     */
    int count(String titolo, String autore, String categoriaId);

    /**
     * Dettaglio libro.
     */
    Libro getById(int idLibro);
}
