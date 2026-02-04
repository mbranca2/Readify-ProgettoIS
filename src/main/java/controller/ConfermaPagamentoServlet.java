package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.*;
import model.dao.IndirizzoDAO;
import model.dao.OrdineDAO;

import java.io.IOException;

@WebServlet("/conferma-pagamento")
public class ConfermaPagamentoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Carrello carrello = (Carrello) session.getAttribute("carrello");
        if (carrello == null || carrello.isVuoto()) {
            response.sendRedirect(request.getContextPath() + "/carrello");
            return;
        }

        try {
            if (carrello.getArticoli().isEmpty()) {
                throw new Exception("Il carrello è vuoto");
            }

            Utente utente = (Utente) session.getAttribute("utente");
            if (utente == null || utente.getIdUtente() <= 0) {
                throw new Exception("Utente non valido");
            }

            int idIndirizzoSpedizione;
            try {
                idIndirizzoSpedizione = Integer.parseInt(request.getParameter("indirizzoSpedizione"));
            } catch (NumberFormatException e) {
                throw new Exception("Indirizzo di spedizione non valido");
            }

            IndirizzoDAO indirizzoDAO = new IndirizzoDAO();
            Indirizzo indirizzoSpedizione = indirizzoDAO.trovaIndirizzoPerId(idIndirizzoSpedizione);

            if (indirizzoSpedizione == null || indirizzoSpedizione.getIdUtente() != utente.getIdUtente()) {
                throw new Exception("Indirizzo di spedizione non valido");
            }

            try {
                Ordine ordine = new Ordine();
                ordine.setIdUtente(utente.getIdUtente());
                ordine.setStato(StatoOrdine.IN_ELABORAZIONE);
                ordine.setTotale(carrello.getTotale());

                String metodoPagamento = request.getParameter("metodoPagamento");
                if (metodoPagamento == null || metodoPagamento.trim().isEmpty()) {
                    throw new Exception("Metodo di pagamento non specificato");
                }

                ordine.setIdIndirizzo(indirizzoSpedizione.getIdIndirizzo());
                carrello.getArticoli().forEach(articolo -> {
                    DettaglioOrdine dettaglio = new DettaglioOrdine();
                    dettaglio.setIdLibro(articolo.getLibro().getIdLibro());
                    dettaglio.setQuantita(articolo.getQuantita());
                    dettaglio.setPrezzoUnitario(articolo.getLibro().getPrezzo());
                    dettaglio.setTitoloLibro(articolo.getLibro().getTitolo());

                    if (articolo.getLibro().getAutore() != null) {
                        dettaglio.setAutoreLibro(articolo.getLibro().getAutore());
                    }
                    if (articolo.getLibro().getIsbn() != null) {
                        dettaglio.setIsbnLibro(articolo.getLibro().getIsbn());
                    }

                    ordine.getDettagli().add(dettaglio);
                });

                OrdineDAO ordineDAO = new OrdineDAO();
                boolean successo = ordineDAO.salvaOrdine(ordine);

                if (successo) {
                    carrello.svuota();
                    session.setAttribute("carrello", carrello);
                    response.sendRedirect(request.getContextPath() + "/conferma-ordine?id=" + ordine.getIdOrdine());
                } else {
                    throw new Exception("Errore durante il salvataggio dell'ordine nel database");
                }

            } catch (Exception e) {
                System.err.println("Errore durante l'elaborazione del pagamento:");
                e.printStackTrace();
                throw new Exception("Errore durante l'elaborazione del pagamento: " + e.getMessage(), e);
            }
        } catch (Exception e) {
            request.setAttribute("errore", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/errorePagamento.jsp").forward(request, response);
        }
    }
}
