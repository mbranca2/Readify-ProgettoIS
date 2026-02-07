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

import java.io.IOException;

@WebServlet("/gestione-indirizzo")
public class GestioneIndirizzoServlet extends HttpServlet {
    private final IndirizzoDAO indirizzoDAO = new IndirizzoDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/profilo");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Utente utente = (Utente) session.getAttribute("utente");
        String via = req.getParameter("via");
        String cap = req.getParameter("cap");
        String citta = req.getParameter("citta");
        String provincia = req.getParameter("provincia");
        String paese = req.getParameter("paese");
        if (via == null || via.trim().isEmpty() ||
                cap == null || !cap.matches("\\d{5}") ||
                citta == null || citta.trim().isEmpty() ||
                provincia == null || provincia.trim().length() != 2) {

            req.setAttribute("errore", "Compilare correttamente tutti i campi obbligatori");
            req.getRequestDispatcher("/WEB-INF/jsp/gestioneAccount.jsp").forward(req, resp);
            return;
        }

        Indirizzo indirizzo = (Indirizzo) session.getAttribute("indirizzo");
        if (indirizzo == null) {
            indirizzo = new Indirizzo();
            indirizzo.setIdUtente(utente.getIdUtente());
        }
        indirizzo.setVia(via.trim());
        indirizzo.setCap(cap.trim());
        indirizzo.setCitta(citta.trim());
        indirizzo.setProvincia(provincia.trim().toUpperCase());
        indirizzo.setPaese(paese != null ? paese.trim() : "Italia");

        boolean successo;
        if (indirizzo.getIdIndirizzo() == 0) {
            successo = indirizzoDAO.inserisciIndirizzo(indirizzo);
        } else {
            successo = indirizzoDAO.aggiornaIndirizzo(indirizzo);
        }

        if (successo) {
            session.setAttribute("indirizzo", indirizzo);
            req.setAttribute("messaggio", "Indirizzo aggiornato con successo!");
        } else {
            req.setAttribute("errore", "Si è verificato un errore durante il salvataggio dell'indirizzo");
        }
        req.getRequestDispatcher("/WEB-INF/jsp/gestioneAccount.jsp").forward(req, resp);
    }
}
