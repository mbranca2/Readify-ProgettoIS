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
import business.service.order.OrderService;

import java.io.IOException;
import java.util.List;

@WebServlet("/profilo")
public class GestioneAccountServlet extends HttpServlet {
    private final AddressService addressService = ServiceFactory.addressService();
    private final AccountService accountService = ServiceFactory.accountService();
    private final OrderService orderService = ServiceFactory.orderService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Utente utente = (Utente) session.getAttribute("utente");
        List<Indirizzo> indirizzi = addressService.listByUser(utente.getIdUtente());
        req.setAttribute("indirizzi", indirizzi);

        if (indirizzi.isEmpty()) {
            session.removeAttribute("indirizzo");
        } else {
            Boolean nuovo = (Boolean) session.getAttribute("indirizzoNuovo");
            if (Boolean.TRUE.equals(nuovo)) {
                session.removeAttribute("indirizzo");
                session.removeAttribute("indirizzoNuovo");
            } else {
                Indirizzo selezionato = (Indirizzo) session.getAttribute("indirizzo");
                boolean valido = false;
                if (selezionato != null) {
                    for (Indirizzo ind : indirizzi) {
                        if (ind.getIdIndirizzo() == selezionato.getIdIndirizzo()) {
                            valido = true;
                            break;
                        }
                    }
                }
                if (!valido) {
                    session.setAttribute("indirizzo", indirizzi.get(0));
                }
            }
        }

        Object flashMessage = session.getAttribute("flashMessage");
        Object flashType = session.getAttribute("flashMessageType");
        Object flashError = session.getAttribute("flashError");
        if (flashMessage != null) {
            req.setAttribute("messaggio", flashMessage);
            req.setAttribute("tipoMessaggio", flashType != null ? flashType : "success");
            session.removeAttribute("flashMessage");
            session.removeAttribute("flashMessageType");
        }
        if (flashError != null) {
            req.setAttribute("errore", flashError);
            session.removeAttribute("flashError");
        }

        req.setAttribute("ordini", orderService.listByUser(utente.getIdUtente()));
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
            req.setAttribute("indirizzi", addressService.listByUser(utente.getIdUtente()));
            req.setAttribute("ordini", orderService.listByUser(utente.getIdUtente()));
            req.getRequestDispatcher("/WEB-INF/jsp/gestioneAccount.jsp").forward(req, resp);
            return;
        }

        utente.setNome(nome.trim());
        utente.setCognome(cognome.trim());
        utente.setEmail(email.trim().toLowerCase());
        utente.setTelefono(telefono != null ? telefono.trim() : null);

        boolean aggiornato = accountService.updateProfile(utente);
        if (aggiornato) {
            session.setAttribute("utente", utente);
            req.setAttribute("messaggio", "Profilo aggiornato con successo");
            req.setAttribute("tipoMessaggio", "success");
        } else {
            req.setAttribute("errore", "Si è verificato un errore durante l'aggiornamento del profilo");
        }

        req.setAttribute("indirizzi", addressService.listByUser(utente.getIdUtente()));
        req.setAttribute("ordini", orderService.listByUser(utente.getIdUtente()));
        req.getRequestDispatcher("/WEB-INF/jsp/gestioneAccount.jsp").forward(req, resp);
    }
}
