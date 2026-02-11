package presentation.controller.admin;

import business.model.Utente;
import business.service.ServiceFactory;
import business.service.account.AdminUserService;
import business.service.order.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/admin/utenti/elimina")
public class EliminaUtenteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final AdminUserService adminUserService = ServiceFactory.adminUserService();
    private final OrderService orderService = ServiceFactory.orderService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/admin/utenti");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.isEmpty()) {
            inviaErrore(response, "ID utente mancante", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int idUtente = Integer.parseInt(idParam);
            Utente utente = adminUserService.getById(idUtente);
            if (utente == null) {
                inviaErrore(response, "Utente non trovato", HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            if (!orderService.listByUser(idUtente).isEmpty()) {
                inviaErrore(response, "Impossibile eliminare l'utente: ordini associati.",
                        HttpServletResponse.SC_CONFLICT);
                return;
            }

            boolean eliminato = adminUserService.delete(idUtente);

            if (eliminato) {
                response.sendRedirect(request.getContextPath() + "/admin/utenti?success=Utente eliminato con successo");
            } else {
                inviaErrore(response, "Impossibile eliminare l'utente", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (NumberFormatException e) {
            inviaErrore(response, "ID utente non valido",
                    HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            inviaErrore(response, "Errore durante l'eliminazione dell'utente: " + e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void inviaErrore(HttpServletResponse response, String messaggio, int statusCode)
            throws IOException {
        response.setStatus(statusCode);
        response.setContentType("text/plain; charset=UTF-8");
        response.getWriter().write(messaggio);
    }
}
