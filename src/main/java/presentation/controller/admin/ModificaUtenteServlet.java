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

@WebServlet("/admin/utenti/modifica")
public class ModificaUtenteServlet extends HttpServlet {
    private final AdminUserService adminUserService = ServiceFactory.adminUserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID utente mancante");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            Utente utente = adminUserService.getById(id);

            if (utente == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Utente non trovato");
                return;
            }

            request.setAttribute("utente", utente);
            request.getRequestDispatcher("/WEB-INF/jsp/admin/modifica-utente.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID utente non valido");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("idUtente");

        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID utente mancante");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            Utente utente = adminUserService.getById(id);

            if (utente == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Utente non trovato");
                return;
            }

            String nome = request.getParameter("nome");
            String cognome = request.getParameter("cognome");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String ruolo = request.getParameter("ruolo");

            if (nome == null || cognome == null || email == null || ruolo == null ||
                    nome.trim().isEmpty() || cognome.trim().isEmpty() || email.trim().isEmpty()) {
                request.setAttribute("errore", "Tutti i campi sono obbligatori");
                request.setAttribute("utente", utente);
                request.getRequestDispatcher("/WEB-INF/jsp/admin/modifica-utente.jsp").forward(request, response);
                return;
            }

            utente.setNome(nome);
            utente.setCognome(cognome);
            utente.setEmail(email);

            utente.setRuolo(ruolo.equals("AMMINISTRATORE") ? "admin" : "registrato");

            if (password != null && !password.trim().isEmpty()) {
                if (password.length() < 8) {
                    request.setAttribute("errore", "La password deve essere di almeno 8 caratteri");
                    request.setAttribute("utente", utente);
                    request.getRequestDispatcher("/WEB-INF/jsp/admin/modifica-utente.jsp").forward(request, response);
                    return;
                }
                adminUserService.update(utente, password);
            } else {
                adminUserService.update(utente);
            }
            response.sendRedirect(request.getContextPath() + "/admin/utenti?success=Utente modificato con successo");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID utente non valido");
        }
    }
}
