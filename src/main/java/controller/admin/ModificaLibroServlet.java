package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Categoria;
import model.Libro;
import model.dao.CategoriaDAO;
import model.dao.LibroDAO;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/admin/libri/modifica")
public class ModificaLibroServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final LibroDAO libroDAO = new LibroDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Gestisce la visualizzazione del form di modifica
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID libro mancante");
            return;
        }

        try {
            int idLibro = Integer.parseInt(idParam);
            Libro libro = libroDAO.trovaLibroPerId(idLibro);

            if (libro == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Libro non trovato");
                return;
            }

            List<Categoria> categorie = new CategoriaDAO().trovaTutteCategorie();
            request.setAttribute("categorie", categorie);
            request.setAttribute("libro", libro);
            request.getRequestDispatcher("/jsp/admin/modifica-libro.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID libro non valido");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Gestisce l'invio del form di modifica
        String idParam = request.getParameter("idLibro");

        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID libro mancante");
            return;
        }

        try {
            int idLibro = Integer.parseInt(idParam);
            Libro libro = libroDAO.trovaLibroPerId(idLibro);

            if (libro == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Libro non trovato");
                return;
            }

            // Aggiorno i attributi del libro
            libro.setTitolo(request.getParameter("titolo"));
            libro.setAutore(request.getParameter("autore"));
            libro.setPrezzo(new BigDecimal(request.getParameter("prezzo").replace(",", ".")));
            libro.setDisponibilita(Integer.parseInt(request.getParameter("quantita")));
            int idCategoria = Integer.parseInt(request.getParameter("categoria"));
            libro.aggiungiCategoria(idCategoria);
            libro.setDescrizione(request.getParameter("descrizione"));

            // lascio l'immagine attuale
            String copertina = request.getParameter("copertina");
            if (copertina != null && !copertina.trim().isEmpty()) {
                libro.setCopertina(copertina);
            }

            // Aggiorno il libro nel database
            boolean aggiornato = libroDAO.aggiornaLibro(libro);

            if (aggiornato) {
                response.sendRedirect(request.getContextPath() +
                        "/admin/libri?success=Libro aggiornato con successo");
            } else {
                request.setAttribute("errore", "Impossibile aggiornare il libro");
                request.setAttribute("libro", libro);
                request.getRequestDispatcher("/jsp/admin/modifica-libro.jsp").forward(request, response);
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Dati non validi");
        }
    }
}
