package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Utente;
import model.dao.UtenteDAO;

import java.io.IOException;

@WebServlet("/admin/dettaglio-utenti/*")
public class DettaglioUtenteServlet extends HttpServlet {
    private final UtenteDAO utenteDAO = new UtenteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/admin/utenti");
            return;
        }

        try {

            String idParam = pathInfo.substring(1);
            int idUtente = Integer.parseInt(idParam);

            // Recupera l'utente dal database
            Utente utente = utenteDAO.trovaUtentePerId(idUtente);

            if (utente == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Utente non trovato");
                return;
            }

            // Imposto l'utente come attributo della richiesta
            request.setAttribute("utente", utente);

            // Inoltro alla JSP di dettaglio
            request.getRequestDispatcher("/jsp/admin/dettaglio-utente.jsp")
                    .forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID utente non valido");
        }
    }
}
