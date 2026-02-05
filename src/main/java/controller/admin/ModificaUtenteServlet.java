package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.bean.Utente;
import model.dao.UtenteDAO;
import utils.HashUtil;

import java.io.IOException;

@WebServlet("/admin/utenti/modifica")
public class ModificaUtenteServlet extends HttpServlet {
    private final UtenteDAO utenteDAO = new UtenteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID utente mancante");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            Utente utente = utenteDAO.trovaUtentePerId(id);

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
            Utente utente = utenteDAO.trovaUtentePerId(id);

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

            if (password != null && !password.trim().isEmpty()) {
                if (password.length() < 8) {
                    request.setAttribute("errore", "La password deve essere di almeno 8 caratteri");
                    request.setAttribute("utente", utente);
                    request.getRequestDispatcher("/WEB-INF/jsp/admin/modifica-utente.jsp").forward(request, response);
                    return;
                }
                String passwordCifrata = HashUtil.sha1(password);
                utente.setPasswordCifrata(passwordCifrata);
            }

            utente.setRuolo(ruolo.equals("AMMINISTRATORE") ? "admin" : "registrato");
            utenteDAO.aggiornaUtente(utente);
            response.sendRedirect(request.getContextPath() + "/admin/utenti?success=Utente modificato con successo");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID utente non valido");
        }
    }
}
