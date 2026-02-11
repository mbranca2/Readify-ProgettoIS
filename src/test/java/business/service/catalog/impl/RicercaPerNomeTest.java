package business.service.catalog.impl;

import business.model.Libro;
import data.dao.LibroDAO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RicercaPerNomeTest {

    @Test
    @DisplayName("CP Ricerca: titolo valido")
    void searchTitoloValido_ok() {
        LibroDAO dao = mock(LibroDAO.class);
        CatalogServiceImpl service = new CatalogServiceImpl(dao);

        Libro l1 = new Libro();
        Libro l2 = new Libro();
        List<Libro> expected = List.of(l1, l2);

        when(dao.trovaLibriConFiltro(eq("Piccolo Principe"), isNull(), isNull(), isNull(), isNull(), eq(0), eq(10)))
                .thenReturn(expected);

        List<Libro> res = service.search("Piccolo Principe", null, null, null, null, 1, 10);

        assertEquals(expected, res);
    }

    @Test
    @DisplayName("CP Ricerca: titolo con soli spazi")
    void searchTitoloSpazi_normalizzatoNull() {
        LibroDAO dao = mock(LibroDAO.class);
        CatalogServiceImpl service = new CatalogServiceImpl(dao);

        List<Libro> expected = List.of(new Libro());
        when(dao.trovaLibriConFiltro(isNull(), isNull(), isNull(), isNull(), isNull(), eq(0), eq(10)))
                .thenReturn(expected);

        List<Libro> res = service.search("   ", null, null, null, null, 1, 10);

        assertEquals(expected, res);
    }

    @Test
    @DisplayName("CP Ricerca: page/pageSize non valida")
    void searchPageNonValida_safeValues() {
        LibroDAO dao = mock(LibroDAO.class);
        CatalogServiceImpl service = new CatalogServiceImpl(dao);

        List<Libro> expected = List.of(new Libro());
        when(dao.trovaLibriConFiltro(eq("X"), isNull(), isNull(), isNull(), isNull(), eq(0), eq(1)))
                .thenReturn(expected);

        List<Libro> res = service.search("X", null, null, null, null, 0, 0);

        assertEquals(expected, res);
    }

    @Test
    @DisplayName("CP Ricerca: eccezione nel DAO")
    void searchDaoException_emptyList() {
        LibroDAO dao = mock(LibroDAO.class);
        CatalogServiceImpl service = new CatalogServiceImpl(dao);

        when(dao.trovaLibriConFiltro(any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("DB down"));

        List<Libro> res = service.search("Piccolo Principe", null, null, null, null, 1, 10);

        assertTrue(res.isEmpty(), "In caso di eccezione deve tornare lista vuota");
    }
}