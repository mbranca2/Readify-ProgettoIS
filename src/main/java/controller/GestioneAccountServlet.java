package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.Indirizzo;
import model.bean.Utente;
import model.dao.IndirizzoDAO;
import model.dao.UtenteDAO;

import java.io.IOException;

@WebServlet("/profilo")
public class GestioneAccountServlet extends HttpServlet {
    private final IndirizzoDAO indirizzoDAO = new IndirizzoDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (session.getAttribute("indirizzo") == null) {
            Utente utente = (Utente) session.getAttribute("utente");
            Indirizzo indirizzo = indirizzoDAO.trovaIndirizzoPerIdUtente(utente.getIdUtente());
            if (indirizzo != null) {
                session.setAttribute("indirizzo", indirizzo);
            }
        }

        req.getRequestDispatcher("/WEB-INF/jsp/gestioneAccount.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Utente utente = (Utente) session.getAttribute("utente");
        String nome = req.getParameter("nome");
        String cognome = req.getParameter("cognome");
        String email = req.getParameter("email");
        String telefono = req.getParameter("telefono");

        if (nome == null || nome.trim().isEmpty() ||
                cognome == null || cognome.trim().isEmpty() ||
                email == null || email.trim().isEmpty()) {
            req.setAttribute("errore", "Tutti i campi obbligatori devono essere compilati");
            req.getRequestDispatcher("/WEB-INF/jsp/gestioneAccount.jsp").forward(req, resp);
            return;
        }

        utente.setNome(nome.trim());
        utente.setCognome(cognome.trim());
        utente.setEmail(email.trim().toLowerCase());
        utente.setTelefono(telefono != null ? telefono.trim() : null);

        UtenteDAO utenteDAO = new UtenteDAO();

        boolean aggiornato = utenteDAO.aggiornaUtente(utente);
        if (aggiornato) {
            session.setAttribute("utente", utente);
            req.setAttribute("successo", "Profilo aggiornato con successo");
        } else {
            req.setAttribute("errore", "Si è verificato un errore durante l'aggiornamento del profilo");
        }

        req.getRequestDispatcher("/WEB-INF/jsp/gestioneAccount.jsp").forward(req, resp);
    }
}
