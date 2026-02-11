package presentation.controller;

import business.model.Categoria;
import business.model.Libro;
import business.service.ServiceFactory;
import business.service.catalog.CatalogService;
import business.service.catalog.CategoryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/libri")
public class ListaLibriServlet extends HttpServlet {

    private final CatalogService catalogService = ServiceFactory.catalogService();
    private final CategoryService categoryService = ServiceFactory.categoryService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        List<Categoria> categorie = categoryService.listAll();

        String titolo = trimToNull(req.getParameter("titolo"));
        String autore = trimToNull(req.getParameter("autore"));
        String categoria = trimToNull(req.getParameter("categoria"));

        BigDecimal prezzoMin = parseBigDecimalSafe(req.getParameter("prezzoMin"));
        BigDecimal prezzoMax = parseBigDecimalSafe(req.getParameter("prezzoMax"));

        int pagina = parseIntSafe(req.getParameter("pagina"), 1);
        int pageSize = 12;

        List<Libro> libri;
        int totale = 0;
        int totalePagine = 1;

        boolean nessunFiltro = (titolo == null && autore == null && categoria == null && prezzoMin == null && prezzoMax == null);

        if (nessunFiltro) {
            libri = catalogService.listAll();
            totale = (libri != null) ? libri.size() : 0;
        } else {
            libri = catalogService.search(titolo, autore, categoria, prezzoMin, prezzoMax, pagina, pageSize);
            totale = catalogService.count(titolo, autore, categoria, prezzoMin, prezzoMax);
            totalePagine = (int) Math.ceil((double) totale / pageSize);
            if (totalePagine < 1) totalePagine = 1;
        }

        req.setAttribute("categorie", categorie);
        req.setAttribute("libri", libri);

        req.setAttribute("paginaCorrente", pagina);
        req.setAttribute("totalePagine", totalePagine);
        req.setAttribute("totaleElementi", totale);

        req.setAttribute("titolo", titolo);
        req.setAttribute("autore", autore);
        req.setAttribute("categoria", categoria);
        req.setAttribute("prezzoMin", prezzoMin != null ? prezzoMin.toPlainString() : null);
        req.setAttribute("prezzoMax", prezzoMax != null ? prezzoMax.toPlainString() : null);

        req.getRequestDispatcher("/WEB-INF/jsp/catalogo.jsp").forward(req, resp);
    }

    private int parseIntSafe(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String x = s.trim();
        return x.isEmpty() ? null : x;
    }

    private BigDecimal parseBigDecimalSafe(String s) {
        if (s == null) return null;
        String x = s.trim();
        if (x.isEmpty()) return null;
        x = x.replace(",", ".");
        try {
            return new BigDecimal(x);
        } catch (Exception e) {
            return null;
        }
    }
}
