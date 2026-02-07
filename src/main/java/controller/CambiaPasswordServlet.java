package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.Utente;
import service.ServiceFactory;
import service.account.AccountService;

import java.io.IOException;

@WebServlet("/cambia-password")
public class CambiaPasswordServlet extends HttpServlet {

    private AccountService accountService;

    @Override
    public void init() throws ServletException {
        this.accountService = ServiceFactory.accountService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Nel progetto non esiste "profilo.jsp": la pagina profilo è "gestioneAccount.jsp"
        request.getRequestDispatcher("/WEB-INF/jsp/gestioneAccount.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String oldPassword = firstNonEmpty(
                request.getParameter("oldPassword"),
                request.getParameter("vecchiaPassword")
        );
        String newPassword = firstNonEmpty(
                request.getParameter("newPassword"),
                request.getParameter("nuovaPassword")
        );
        String confirmPassword = firstNonEmpty(
                request.getParameter("confirmPassword"),
                request.getParameter("confermaPassword")
        );

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            request.setAttribute("errore", "Compila tutti i campi.");
            request.getRequestDispatcher("/WEB-INF/jsp/gestioneAccount.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("errore", "Le nuove password non coincidono.");
            request.getRequestDispatcher("/WEB-INF/jsp/gestioneAccount.jsp").forward(request, response);
            return;
        }

        boolean ok;
        try {
            ok = accountService.changePassword(utente.getIdUtente(), oldPassword, newPassword);
        } catch (Exception e) {
            request.setAttribute("errore", "Errore interno durante l'aggiornamento della password.");
            request.getRequestDispatcher("/WEB-INF/jsp/gestioneAccount.jsp").forward(request, response);
            return;
        }

        if (ok) {
            request.setAttribute("messaggio", "Password aggiornata con successo.");
            request.setAttribute("tipoMessaggio", "success");
        } else {
            request.setAttribute("errore", "Impossibile aggiornare la password. Verifica la password attuale.");
        }

        request.getRequestDispatcher("/WEB-INF/jsp/gestioneAccount.jsp").forward(request, response);
    }

    private String firstNonEmpty(String... values) {
        if (values == null) return "";
        for (String value : values) {
            String trimmed = (value == null) ? "" : value.trim();
            if (!trimmed.isEmpty()) return trimmed;
        }
        return "";
    }
}
