package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.Categoria;
import model.bean.Libro;
import model.bean.Utente;
import model.dao.CategoriaDAO;
import model.dao.LibroDAO;
import utils.DBManager;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin/libri")
public class GestioneLibriServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Utente utente = (Utente) session.getAttribute("utente");
        if (!"admin".equals(utente.getRuolo())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String azione = req.getParameter("azione");
        if ("nuovo".equals(azione)) {
            req.getRequestDispatcher("/WEB-INF/jsp/admin/inserisciLibro.jsp").forward(req, resp);
            return;
        }

        String titolo = req.getParameter("titolo");
        String autore = req.getParameter("autore");
        String categoria = req.getParameter("categoria");
        int pagina = (req.getParameter("pagina") != null) ? Integer.parseInt(req.getParameter("pagina")) : 1;
        int elementiPerPagina = 10;
        int offset = (pagina - 1) * elementiPerPagina;

        LibroDAO libroDAO = new LibroDAO();
        CategoriaDAO categoriaDAO = new CategoriaDAO();

        List<Categoria> categorie = categoriaDAO.trovaTutteCategorie();
        req.setAttribute("categorie", categorie);

        try {
            if (titolo == null && autore == null && categoria == null) {
                System.out.println("[GestioneLibriServlet] Nessun filtro di ricerca, caricamento di tutti i libri");
                List<Libro> libri;
                try {
                    libri = libroDAO.trovaTutti();
                    System.out.println("[GestioneLibriServlet] Libri trovati: " + (libri != null ? libri.size() : "null"));
                    if (libri != null) {
                        for (Libro libro : libri) {
                            System.out.println("[GestioneLibriServlet] Libro: " + libro.getTitolo() + " (ID: " + libro.getIdLibro() + ")");
                        }
                    }
                    req.setAttribute("libri", libri);
                } catch (Exception e) {
                    System.err.println("[GestioneLibriServlet] Errore durante il recupero dei libri: " + e.getMessage());
                    e.printStackTrace();
                    throw e;
                }
            } else {
                List<Libro> libri = libroDAO.trovaLibriConFiltro(titolo, autore, categoria, offset, elementiPerPagina);
                System.out.println("[GestioneLibriServlet] Numero libri filtrati: " + (libri != null ? libri.size() : "null"));
                req.setAttribute("libri", libri);

                int totaleLibri = libroDAO.contaLibriConFiltro(titolo, autore, categoria);
                int totalePagine = (int) Math.ceil((double) totaleLibri / elementiPerPagina);
                req.setAttribute("totalePagine", totalePagine);
                req.setAttribute("paginaCorrente", pagina);
            }

            HttpSession sessione = req.getSession();
            sessione.setAttribute("titolo", titolo);
            sessione.setAttribute("autore", autore);
            sessione.setAttribute("categoria", categoria);

            req.getRequestDispatcher("/WEB-INF/jsp/admin/gestione-libri.jsp").forward(req, resp);

        } catch (Exception e) {
            System.err.println("[GestioneLibriServlet] Errore: " + e.getMessage());
            e.printStackTrace();
            throw new ServletException("Errore nel GestioneLibriServlet: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LibroDAO libroDAO = new LibroDAO();
        String azione = request.getParameter("azione");

        if ("aggiungi".equals(azione)) {
            Libro libro = new Libro();
            setLibroPropertiesFromRequest(libro, request);
            libroDAO.inserisciLibro(libro);

            HttpSession session = request.getSession();
            String titolo = (String) session.getAttribute("titolo");
            String autore = (String) session.getAttribute("autore");
            String categoria = (String) session.getAttribute("categoria");
            StringBuilder url = new StringBuilder("../admin/libri");

            if (titolo != null || autore != null || categoria != null) {
                url.append("?");
                if (titolo != null) url.append("titolo=").append(titolo);
                if (autore != null) url.append("&autore=").append(autore);
                if (categoria != null) url.append("&categoria=").append(categoria);
            }
            response.sendRedirect(url.toString());

        } else if ("modifica".equals(azione)) {
            int id = Integer.parseInt(request.getParameter("id"));
            Libro libro = libroDAO.trovaLibroPerId(id);
            if (libro != null) {
                setLibroPropertiesFromRequest(libro, request);

                Connection conn = null;
                try {
                    conn = DBManager.getConnection();
                    conn.setAutoCommit(false);

                    boolean ok = libroDAO.aggiornaLibro(conn, libro);
                    if (!ok) {
                        conn.rollback();
                        throw new ServletException("Aggiornamento libro fallito");
                    }

                    conn.commit();

                } catch (SQLException e) {
                    try {
                        if (conn != null) conn.rollback();
                    } catch (SQLException ignored) {}
                    throw new ServletException("Errore durante l'aggiornamento del libro", e);

                } finally {
                    try {
                        if (conn != null) {
                            conn.setAutoCommit(true);
                            conn.close();
                        }
                    } catch (SQLException ignored) {}
                }

                HttpSession session = request.getSession();
                String titolo = (String) session.getAttribute("titolo");
                String autore = (String) session.getAttribute("autore");
                String categoria = (String) session.getAttribute("categoria");
                StringBuilder url = new StringBuilder(request.getContextPath() + "/admin/libri");

                if (titolo != null || autore != null || categoria != null) {
                    url.append("?");
                    if (titolo != null) url.append("titolo=").append(titolo);
                    if (autore != null) url.append("&autore=").append(autore);
                    if (categoria != null) url.append("&categoria=").append(categoria);
                }
                response.sendRedirect(url.toString());
            }

        } else if ("elimina".equals(azione)) {
            int id = Integer.parseInt(request.getParameter("id"));
            libroDAO.eliminaLibro(id);

            HttpSession session = request.getSession();
            String titolo = (String) session.getAttribute("titolo");
            String autore = (String) session.getAttribute("autore");
            String categoria = (String) session.getAttribute("categoria");
            StringBuilder url = new StringBuilder(request.getContextPath() + "/admin/libri");

            if (titolo != null || autore != null || categoria != null) {
                url.append("?");
                if (titolo != null) url.append("titolo=").append(titolo);
                if (autore != null) url.append("&autore=").append(autore);
                if (categoria != null) url.append("&categoria=").append(categoria);
            }
            response.sendRedirect(url.toString());
        }
    }

    private void setLibroPropertiesFromRequest(Libro libro, HttpServletRequest request) {
        libro.setTitolo(request.getParameter("titolo"));
        libro.setAutore(request.getParameter("autore"));
        libro.setPrezzo(new BigDecimal(request.getParameter("prezzo")));
        libro.setDisponibilita(Integer.parseInt(request.getParameter("disponibilita")));
    }
}
