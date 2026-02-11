package presentation.controller.admin;

import business.model.Utente;
import business.service.ServiceFactory;
import business.service.account.AdminUserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/admin/dettaglio-utenti/*")
public class DettaglioUtenteServlet extends HttpServlet {
    private final AdminUserService adminUserService = ServiceFactory.adminUserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/admin/utenti");
            return;
        }

        try {
            String idParam = pathInfo.substring(1);
            int idUtente = Integer.parseInt(idParam);
            Utente utente = adminUserService.getById(idUtente);

            if (utente == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Utente non trovato");
                return;
            }
            request.setAttribute("utente", utente);
            request.getRequestDispatcher("/WEB-INF/jsp/admin/dettaglio-utente.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID utente non valido");
        }
    }
}
