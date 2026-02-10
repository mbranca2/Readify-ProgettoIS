package presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import business.model.Indirizzo;
import business.model.Utente;
import business.service.ServiceFactory;
import business.service.account.AccountService;
import business.service.address.AddressService;

import java.io.IOException;

@WebServlet("/gestione-indirizzo")
public class GestioneIndirizzoServlet extends HttpServlet {
    private final AccountService accountService = ServiceFactory.accountService();
    private final AddressService addressService = ServiceFactory.addressService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/profilo?tab=address");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Utente utente = (Utente) session.getAttribute("utente");
        String azione = req.getParameter("azione");
        if ("seleziona".equalsIgnoreCase(azione)) {
            handleSelect(req, resp, session, utente);
            return;
        }
        if ("nuovo".equalsIgnoreCase(azione)) {
            session.removeAttribute("indirizzo");
            session.setAttribute("indirizzoNuovo", true);
            resp.sendRedirect(req.getContextPath() + "/profilo?tab=address");
            return;
        }
        if ("elimina".equalsIgnoreCase(azione)) {
            handleDelete(req, resp, session, utente);
            return;
        }

        String via = req.getParameter("via");
        String cap = req.getParameter("cap");
        String citta = req.getParameter("citta");
        String provincia = req.getParameter("provincia");
        String paese = req.getParameter("paese");
        if (via == null || via.trim().isEmpty() ||
                cap == null || !cap.matches("\\d{5}") ||
                citta == null || citta.trim().isEmpty() ||
                provincia == null || provincia.trim().length() != 2) {

            session.setAttribute("flashError", "Compilare correttamente tutti i campi obbligatori");
            resp.sendRedirect(req.getContextPath() + "/profilo?tab=address");
            return;
        }

        String idParam = req.getParameter("idIndirizzo");
        int idIndirizzo = parseIntSafe(idParam);
        Indirizzo indirizzo = new Indirizzo();
        if (idIndirizzo > 0) {
            indirizzo.setIdIndirizzo(idIndirizzo);
        }
        indirizzo.setIdUtente(utente.getIdUtente());
        indirizzo.setVia(via.trim());
        indirizzo.setCap(cap.trim());
        indirizzo.setCitta(citta.trim());
        indirizzo.setProvincia(provincia.trim().toUpperCase());
        indirizzo.setPaese(paese != null ? paese.trim() : "Italia");

        boolean successo;
        if (indirizzo.getIdIndirizzo() == 0) {
            successo = accountService.addAddress(utente.getIdUtente(), indirizzo);
        } else {
            successo = accountService.updateAddress(utente.getIdUtente(), indirizzo);
        }

        if (successo) {
            session.setAttribute("indirizzo", indirizzo);
            session.removeAttribute("indirizzoNuovo");
            session.setAttribute("flashMessage", "Indirizzo aggiornato con successo!");
            session.setAttribute("flashMessageType", "success");
        } else {
            session.setAttribute("flashError", "Si e' verificato un errore durante il salvataggio dell'indirizzo");
        }
        resp.sendRedirect(req.getContextPath() + "/profilo?tab=address");
    }

    private void handleSelect(HttpServletRequest req, HttpServletResponse resp, HttpSession session, Utente utente)
            throws IOException {
        int idIndirizzo = parseIntSafe(req.getParameter("idIndirizzo"));
        if (idIndirizzo <= 0 || !addressService.isOwnedByUser(idIndirizzo, utente.getIdUtente())) {
            session.setAttribute("flashError", "Indirizzo non valido");
            resp.sendRedirect(req.getContextPath() + "/profilo?tab=address");
            return;
        }

        Indirizzo indirizzo = addressService.getById(idIndirizzo);
        if (indirizzo == null) {
            session.setAttribute("flashError", "Indirizzo non trovato");
        } else {
            session.setAttribute("indirizzo", indirizzo);
            session.removeAttribute("indirizzoNuovo");
            session.setAttribute("flashMessage", "Indirizzo selezionato per la modifica");
            session.setAttribute("flashMessageType", "success");
        }
        resp.sendRedirect(req.getContextPath() + "/profilo?tab=address");
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp, HttpSession session, Utente utente)
            throws IOException {
        int idIndirizzo = parseIntSafe(req.getParameter("idIndirizzo"));
        if (idIndirizzo <= 0 || !addressService.isOwnedByUser(idIndirizzo, utente.getIdUtente())) {
            session.setAttribute("flashError", "Indirizzo non valido");
            resp.sendRedirect(req.getContextPath() + "/profilo?tab=address");
            return;
        }

        boolean eliminato = accountService.deleteAddress(utente.getIdUtente(), idIndirizzo);
        if (eliminato) {
            Indirizzo selezionato = (Indirizzo) session.getAttribute("indirizzo");
            if (selezionato != null && selezionato.getIdIndirizzo() == idIndirizzo) {
                session.removeAttribute("indirizzo");
            }
            session.setAttribute("flashMessage", "Indirizzo eliminato con successo");
            session.setAttribute("flashMessageType", "success");
        } else {
            session.setAttribute("flashError", "Impossibile eliminare l'indirizzo");
        }

        resp.sendRedirect(req.getContextPath() + "/profilo?tab=address");
    }

    private int parseIntSafe(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return -1;
        }
    }
}
