package presentation.controller.admin;

import business.model.Categoria;
import business.model.Libro;
import business.model.Utente;
import business.service.ServiceFactory;
import business.service.catalog.AdminCatalogService;
import business.service.catalog.CatalogService;
import business.service.catalog.CategoryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/libri")
public class GestioneLibriServlet extends HttpServlet {

    private CatalogService catalogService;
    private CategoryService categoryService;
    private AdminCatalogService adminCatalogService;

    @Override
    public void init() throws ServletException {
        this.catalogService = ServiceFactory.catalogService();
        this.categoryService = ServiceFactory.categoryService();
        this.adminCatalogService = ServiceFactory.adminCatalogService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;
        if (utente == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (!"admin".equals(utente.getRuolo())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        List<Categoria> categorie = categoryService.listAll();
        req.setAttribute("categorie", categorie);

        String azione = req.getParameter("azione");
        if ("nuovo".equals(azione)) {
            req.getRequestDispatcher("/WEB-INF/jsp/admin/inserisciLibro.jsp").forward(req, resp);
            return;
        }

        String titolo = trimToNull(req.getParameter("titolo"));
        String autore = trimToNull(req.getParameter("autore"));
        String categoria = trimToNull(req.getParameter("categoria"));

        BigDecimal prezzoMin = parseBigDecimalSafe(req.getParameter("prezzoMin"));
        BigDecimal prezzoMax = parseBigDecimalSafe(req.getParameter("prezzoMax"));

        int pagina = parseIntSafe(req.getParameter("pagina"), 1);
        int elementiPerPagina = 10;

        List<Libro> libri;
        int totalePagine = 1;

        boolean nessunFiltro = (titolo == null && autore == null && categoria == null && prezzoMin == null && prezzoMax == null);

        if (nessunFiltro) {
            libri = catalogService.listAll();
        } else {
            libri = catalogService.search(titolo, autore, categoria, prezzoMin, prezzoMax, pagina, elementiPerPagina);
            int totale = catalogService.count(titolo, autore, categoria, prezzoMin, prezzoMax);
            totalePagine = (int) Math.ceil((double) totale / elementiPerPagina);

            req.setAttribute("totalePagine", totalePagine);
            req.setAttribute("paginaCorrente", pagina);
        }

        req.setAttribute("libri", libri);

        if (session != null) {
            session.setAttribute("titolo", titolo);
            session.setAttribute("autore", autore);
            session.setAttribute("categoria", categoria);
            session.setAttribute("prezzoMin", (prezzoMin != null) ? prezzoMin.toPlainString() : null);
            session.setAttribute("prezzoMax", (prezzoMax != null) ? prezzoMax.toPlainString() : null);
        }

        req.getRequestDispatcher("/WEB-INF/jsp/admin/gestione-libri.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;
        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!"admin".equals(utente.getRuolo())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String azione = request.getParameter("azione");

        if ("aggiungi".equals(azione)) {
            Libro libro = new Libro();
            setLibroPropertiesFromRequestForInsert(libro, request);
            applyCategorieFromRequest(libro, request);

            boolean ok = adminCatalogService.addBook(libro);

            if (!ok) {
                request.setAttribute("errore", "Impossibile inserire il libro. Verifica i dati.");
                request.setAttribute("categorie", categoryService.listAll());
                request.getRequestDispatcher("/WEB-INF/jsp/admin/inserisciLibro.jsp").forward(request, response);
                return;
            }

            response.sendRedirect(buildRedirectToAdminLibriWithFilters(request));
            return;
        }

        if ("modifica".equals(azione)) {
            int id = parseIntSafe(request.getParameter("id"), -1);
            if (id <= 0) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID libro mancante o non valido");
                return;
            }

            Libro libro = adminCatalogService.getBookById(id);
            if (libro == null) {
                response.sendRedirect(request.getContextPath() + "/admin/libri?errore=Libro non trovato");
                return;
            }

            applyLibroUpdateFromRequest(libro, request);
            applyCategorieFromRequest(libro, request);

            boolean ok = adminCatalogService.updateBook(libro);
            if (!ok) {
                response.sendRedirect(request.getContextPath() + "/admin/libri?errore=Impossibile aggiornare il libro");
                return;
            }

            response.sendRedirect(buildRedirectToAdminLibriWithFilters(request));
            return;
        }

        if ("elimina".equals(azione)) {
            int id = parseIntSafe(request.getParameter("id"), -1);
            if (id <= 0) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID libro mancante o non valido");
                return;
            }

            boolean ok = adminCatalogService.removeBook(id);
            if (!ok) {
                response.sendRedirect(request.getContextPath() + "/admin/libri?errore=Impossibile eliminare il libro");
                return;
            }

            response.sendRedirect(buildRedirectToAdminLibriWithFilters(request));
            return;
        }

        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Azione non valida");
    }

    private void setLibroPropertiesFromRequestForInsert(Libro libro, HttpServletRequest request) {
        libro.setTitolo(trimToEmpty(request.getParameter("titolo")));
        libro.setAutore(trimToEmpty(request.getParameter("autore")));

        String prezzoStr = request.getParameter("prezzo");
        if (prezzoStr != null) prezzoStr = prezzoStr.replace(",", ".");
        try {
            libro.setPrezzo(new BigDecimal(prezzoStr));
        } catch (Exception e) {
            libro.setPrezzo(BigDecimal.ZERO);
        }

        libro.setIsbn(trimToEmpty(request.getParameter("isbn")));
        libro.setDescrizione(trimToEmpty(request.getParameter("descrizione")));
        libro.setDisponibilita(parseIntSafe(request.getParameter("disponibilita"), 0));
        libro.setCopertina(trimToEmpty(request.getParameter("copertina")));
    }

    private void applyLibroUpdateFromRequest(Libro libro, HttpServletRequest request) {
        String titolo = trimToNull(request.getParameter("titolo"));
        if (titolo != null) libro.setTitolo(titolo);

        String autore = trimToNull(request.getParameter("autore"));
        if (autore != null) libro.setAutore(autore);

        String prezzoStr = trimToNull(request.getParameter("prezzo"));
        if (prezzoStr != null) {
            prezzoStr = prezzoStr.replace(",", ".");
            try {
                libro.setPrezzo(new BigDecimal(prezzoStr));
            } catch (Exception ignored) {
            }
        }

        String disp = trimToNull(request.getParameter("disponibilita"));
        if (disp != null) {
            int d = parseIntSafe(disp, -1);
            if (d >= 0) libro.setDisponibilita(d);
        }

        String isbn = trimToNull(request.getParameter("isbn"));
        if (isbn != null) libro.setIsbn(isbn);

        String descr = trimToNull(request.getParameter("descrizione"));
        if (descr != null) libro.setDescrizione(descr);

        String cop = trimToNull(request.getParameter("copertina"));
        if (cop != null) libro.setCopertina(cop);
    }

    private void applyCategorieFromRequest(Libro libro, HttpServletRequest request) {
        String[] categorieMultiple = request.getParameterValues("categorie");
        if (categorieMultiple != null && categorieMultiple.length > 0) {
            libro.setCategorie(new ArrayList<>());
            for (String c : categorieMultiple) {
                int idCat = parseIntSafe(c, -1);
                if (idCat > 0) libro.aggiungiCategoria(idCat);
            }
            return;
        }

        String categoriaSingola = trimToNull(request.getParameter("categoria"));
        if (categoriaSingola != null) {
            int idCat = parseIntSafe(categoriaSingola, -1);
            if (idCat > 0) {
                libro.setCategorie(new ArrayList<>());
                libro.aggiungiCategoria(idCat);
            }
        }
    }

    private String buildRedirectToAdminLibriWithFilters(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String titolo = (session != null) ? (String) session.getAttribute("titolo") : null;
        String autore = (session != null) ? (String) session.getAttribute("autore") : null;
        String categoria = (session != null) ? (String) session.getAttribute("categoria") : null;
        String prezzoMin = (session != null) ? (String) session.getAttribute("prezzoMin") : null;
        String prezzoMax = (session != null) ? (String) session.getAttribute("prezzoMax") : null;

        StringBuilder url = new StringBuilder(request.getContextPath() + "/admin/libri");

        if (titolo != null || autore != null || categoria != null || prezzoMin != null || prezzoMax != null) {
            url.append("?");
            boolean first = true;

            if (titolo != null) {
                url.append("titolo=").append(encode(titolo));
                first = false;
            }
            if (autore != null) {
                if (!first) url.append("&");
                url.append("autore=").append(encode(autore));
                first = false;
            }
            if (categoria != null) {
                if (!first) url.append("&");
                url.append("categoria=").append(encode(categoria));
                first = false;
            }
            if (prezzoMin != null) {
                if (!first) url.append("&");
                url.append("prezzoMin=").append(encode(prezzoMin));
                first = false;
            }
            if (prezzoMax != null) {
                if (!first) url.append("&");
                url.append("prezzoMax=").append(encode(prezzoMax));
            }
        }

        return url.toString();
    }

    private String encode(String s) {
        return s.replace(" ", "%20");
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
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    private String trimToEmpty(String s) {
        return (s == null) ? "" : s.trim();
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
