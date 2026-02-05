package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.Utente;
import model.dao.UtenteDAO;
import utils.HashUtil;

import java.io.IOException;

@WebServlet("/cambia-password")
public class CambiaPasswordServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect("jsp/login.jsp");
            return;
        }

        Utente utente = (Utente) session.getAttribute("utente");
        String vecchiaPassword = request.getParameter("vecchiaPassword");
        String nuovaPassword = request.getParameter("nuovaPassword");
        String confermaPassword = request.getParameter("confermaPassword");

        if (vecchiaPassword == null || nuovaPassword == null || confermaPassword == null ||
                vecchiaPassword.isEmpty() || nuovaPassword.isEmpty() || confermaPassword.isEmpty()) {
            request.setAttribute("errore", "Tutti i campi sono obbligatori");
            request.getRequestDispatcher("jsp/gestioneAccount.jsp").forward(request, response);
            return;
        }

        if (nuovaPassword.length() < 8) {
            request.setAttribute("errore", "La nuova password deve essere di almeno 8 caratteri");
            request.getRequestDispatcher("jsp/gestioneAccount.jsp").forward(request, response);
            return;
        }

        if (!nuovaPassword.equals(confermaPassword)) {
            request.setAttribute("errore", "Le password non coincidono");
            request.getRequestDispatcher("jsp/gestioneAccount.jsp").forward(request, response);
            return;
        }

        String vecchiaPasswordHash = HashUtil.sha1(vecchiaPassword);
        if (!vecchiaPasswordHash.equals(utente.getPasswordCifrata())) {
            request.setAttribute("errore", "La password attuale non è corretta");
            request.getRequestDispatcher("jsp/gestioneAccount.jsp").forward(request, response);
            return;
        }

        String nuovaPasswordHash = HashUtil.sha1(nuovaPassword);
        utente.setPasswordCifrata(nuovaPasswordHash);
        UtenteDAO utenteDAO = new UtenteDAO();
        if (utenteDAO.aggiornaUtente(utente)) {
            request.setAttribute("messaggio", "Password aggiornata con successo");
        } else {
            request.setAttribute("errore", "Errore durante l'aggiornamento della password");
        }
        request.getRequestDispatcher("jsp/gestioneAccount.jsp").forward(request, response);
    }
}
