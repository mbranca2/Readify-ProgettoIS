package controller.admin;

import model.Utente;
import model.dao.UtenteDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import utils.HashUtil;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/utenti")
public class GestioneUtentiServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            resp.sendRedirect("../jsp/login.jsp");
            return;
        }

        Utente utente = (Utente) session.getAttribute("utente");
        if (!"admin".equals(utente.getRuolo())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        UtenteDAO utenteDAO = new UtenteDAO();
        List<Utente> listaUtenti = utenteDAO.trovaTuttiUtenti();
        if (listaUtenti == null) {
            System.err.println("GestioneUtentiServlet: Errore critico - la lista utenti è null");
            req.setAttribute("errore", "Errore nel recupero degli utenti");
        } else if (listaUtenti.isEmpty()) {
            System.out.println("GestioneUtentiServlet: La lista utenti è vuota");
        } else {
            System.out.println("GestioneUtentiServlet: Recuperati " + listaUtenti.size() + " utenti");
            listaUtenti.forEach(u -> System.out.println("Utente trovato: " + u.getEmail()));
        }
        req.setAttribute("listaUtenti", listaUtenti);
        req.getRequestDispatcher("../jsp/admin/gestione-utenti.jsp").forward(req, resp);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String azione = request.getParameter("azione");


        //  prend ol'ID utente dal form
        if ("modifica".equals(azione)) {
            String idParam = request.getParameter("idUtente");
            if (idParam == null || idParam.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID utente mancante");
                return;
            }

            try {
                int idUtente = Integer.parseInt(idParam);
                UtenteDAO utenteDAO = new UtenteDAO();
                Utente utente = utenteDAO.trovaUtentePerId(idUtente);

                if (utente == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Utente non trovato");
                    return;
                }

                // Aggiorno i campi dell'utente
                String nome = request.getParameter("nome");
                String cognome = request.getParameter("cognome");
                String email = request.getParameter("email");
                String password = request.getParameter("password");
                String ruolo = request.getParameter("ruolo");

                // validazione campi obbligatori
                if (nome == null || cognome == null || email == null || ruolo == null ||
                        nome.trim().isEmpty() || cognome.trim().isEmpty() || email.trim().isEmpty()) {

                    request.setAttribute("errore", "Tutti i campi sono obbligatori");
                    request.setAttribute("utente", utente);
                    request.getRequestDispatcher("/jsp/admin/modifica-utente.jsp").forward(request, response);
                    return;
                }

                // Aggiorno i campi
                utente.setNome(nome.trim());
                utente.setCognome(cognome.trim());
                utente.setEmail(email.trim().toLowerCase());

                // Aggiorno la password solo se fornita
                if (password != null && !password.trim().isEmpty()) {
                    if (password.length() < 8) {
                        request.setAttribute("errore", "La password deve essere di almeno 8 caratteri");
                        request.setAttribute("utente", utente);
                        request.getRequestDispatcher("/jsp/admin/modifica-utente.jsp").forward(request, response);
                        return;
                    }
                    String passwordCifrata = HashUtil.sha1(password.trim());
                    utente.setPasswordCifrata(passwordCifrata);
                }

                // Imposto ruolo (converto da CLIENTE/AMMINISTRATORE a registrato/admin)
                utente.setRuolo("AMMINISTRATORE".equals(ruolo) ? "admin" : "registrato");

                // salva
                utenteDAO.aggiornaUtente(utente);

                // Reindirizzo alla lista utenti con messaggio di successo
                response.sendRedirect(request.getContextPath() + "/admin/utenti?success=Utente modificato con successo");

            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID utente non valido");
            }
        }

        if ("nuovo".equals(azione)) {

            String nome = request.getParameter("nome");
            String cognome = request.getParameter("cognome");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String ruolo = request.getParameter("ruolo");

            // Validazione
            if (nome == null || cognome == null || email == null || password == null || ruolo == null ||
                    nome.trim().isEmpty() || cognome.trim().isEmpty() ||
                    email.trim().isEmpty() || password.trim().isEmpty()) {

                request.setAttribute("errore", "Tutti i campi sono obbligatori");
                request.getRequestDispatcher("/jsp/admin/nuovoUtente.jsp").forward(request, response);
                return;
            }

            // creo il nuovo utente
            Utente nuovoUtente = new Utente();
            nuovoUtente.setNome(nome.trim());
            nuovoUtente.setCognome(cognome.trim());
            nuovoUtente.setEmail(email.trim().toLowerCase());
            nuovoUtente.setPasswordCifrata(HashUtil.sha1(password.trim()));
            nuovoUtente.setRuolo("AMMINISTRATORE".equals(ruolo) ? "admin" : "registrato");


            // salvo
            UtenteDAO utenteDAO = new UtenteDAO();
            boolean creato = utenteDAO.inserisciUtente(nuovoUtente);

            if (creato) {
                response.sendRedirect(request.getContextPath() +
                            "/admin/utenti?success=Utente creato con successo");
            } else {
                request.setAttribute("errore", "Impossibile creare l'utente. Email già esistente?");
                request.getRequestDispatcher("/jsp/admin/nuovoUtente.jsp").forward(request, response);
            }
        }
    }


}
