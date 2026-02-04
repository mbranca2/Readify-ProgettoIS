package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.*;
import model.dao.LibroDAO;
import model.dao.OrdineDAO;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/ConfermaOrdine")
public class ConfermaOrdineServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final OrdineDAO ordineDAO = new OrdineDAO();
    private final LibroDAO libroDAO = new LibroDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Utente utente = (Utente) session.getAttribute("utente");
        Carrello carrello = (Carrello) session.getAttribute("carrello");
        if (carrello == null || carrello.isVuoto()) {
            response.sendRedirect(request.getContextPath() + "/carrello");
            return;
        }

        String nomeTitolare = request.getParameter("cardName");
        String numeroCarta = request.getParameter("cardNumber");
        String scadenza = request.getParameter("expiryDate");
        String cvv = request.getParameter("cvv");
        if (!validaDatiPagamento(nomeTitolare, numeroCarta, scadenza, cvv)) {
            request.setAttribute("errore", "Dati di pagamento non validi");
            request.getRequestDispatcher("/WEB-INF/jsp/pagamento.jsp").forward(request, response);
            return;
        }

        try {
            Ordine ordine = new Ordine();
            ordine.setIdUtente(utente.getIdUtente());
            ordine.setStato(StatoOrdine.IN_ELABORAZIONE);
            ordine.setTotale(carrello.getTotale());

            for (Carrello.ArticoloCarrello articolo : carrello.getArticoli()) {
                DettaglioOrdine dettaglio = new DettaglioOrdine();
                dettaglio.setIdLibro(articolo.getLibro().getIdLibro());
                dettaglio.setQuantita(articolo.getQuantita());
                dettaglio.setPrezzoUnitario(articolo.getLibro().getPrezzo());
                dettaglio.setTitoloLibro(articolo.getLibro().getTitolo());
                ordine.aggiungiDettaglio(dettaglio);
            }
            boolean salvato = ordineDAO.salvaOrdine(ordine);
            if (salvato) {
                aggiornaDisponibilitaLibri(carrello);
                carrello.svuota();
                session.setAttribute("carrello", carrello);
                request.setAttribute("idOrdine", ordine.getIdOrdine());
                request.getRequestDispatcher("/WEB-INF/jsp/conferma-ordine.jsp").forward(request, response);
            } else {
                throw new ServletException("Errore durante il salvataggio dell'ordine");
            }
        } catch (Exception e) {
            request.setAttribute("errore", "Si è verificato un errore durante l'elaborazione dell'ordine");
            request.getRequestDispatcher("/WEB-INF/jsp/pagamento.jsp").forward(request, response);
        }
    }

    private boolean validaDatiPagamento(String nomeTitolare, String numeroCarta, String scadenza, String cvv) {
        return nomeTitolare != null && !nomeTitolare.trim().isEmpty() &&
                numeroCarta != null && numeroCarta.replace(" ", "").length() >= 13 &&
                scadenza != null && scadenza.matches("\\d{2}/\\d{2}") &&
                cvv != null && cvv.matches("\\d{3,4}");
    }

    private void aggiornaDisponibilitaLibri(Carrello carrello) throws SQLException {
        for (Carrello.ArticoloCarrello articolo : carrello.getArticoli()) {
            Libro libro = articolo.getLibro();
            int nuovaDisponibilita = libro.getDisponibilita() - articolo.getQuantita();
            libro.setDisponibilita(Math.max(0, nuovaDisponibilita));
            libroDAO.aggiornaLibro(libro);
        }
    }
}
