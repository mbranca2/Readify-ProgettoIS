package business.service.catalog.impl;

import business.model.Libro;
import business.service.catalog.CatalogService;
import data.dao.LibroDAO;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CatalogServiceImpl implements CatalogService {

    private final LibroDAO libroDAO;

    public CatalogServiceImpl(LibroDAO libroDAO) {
        this.libroDAO = Objects.requireNonNull(libroDAO);
    }

    @Override
    public List<Libro> listAll() {
        try {
            return libroDAO.trovaTutti();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Libro> search(String titolo, String autore, String categoriaId, BigDecimal prezzoMin, BigDecimal prezzoMax, int page, int pageSize) {
        try {
            int safePage = Math.max(page, 1);
            int safePageSize = Math.max(pageSize, 1);
            int offset = (safePage - 1) * safePageSize;

            String t = normalize(titolo);
            String a = normalize(autore);
            String c = normalize(categoriaId);

            return libroDAO.trovaLibriConFiltro(t, a, c, prezzoMin, prezzoMax, offset, safePageSize);

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public int count(String titolo, String autore, String categoriaId, BigDecimal prezzoMin, BigDecimal prezzoMax) {
        try {
            String t = normalize(titolo);
            String a = normalize(autore);
            String c = normalize(categoriaId);
            return libroDAO.contaLibriConFiltro(t, a, c, prezzoMin, prezzoMax);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Libro getById(int idLibro) {
        if (idLibro <= 0) return null;
        try {
            return libroDAO.trovaLibroPerId(idLibro);
        } catch (Exception e) {
            return null;
        }
    }

    private String normalize(String s) {
        if (s == null) return null;
        String x = s.trim();
        return x.isEmpty() ? null : x;
    }
}
