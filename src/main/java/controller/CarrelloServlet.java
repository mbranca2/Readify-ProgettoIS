package controller;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.*;
import model.dao.CarrelloDAO;
import model.dao.LibroDAO;
import com.google.gson.Gson;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/carrello")
public class CarrelloServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true); //creo nuova sessione se non esiste
        Carrello carrello = (Carrello) session.getAttribute("carrello");

        if (carrello == null) {
            carrello = new Carrello();
            session.setAttribute("carrello", carrello);
        }

        String azione = request.getParameter("azione");
        String idLibroParam = request.getParameter("idLibro");

        if (azione == null || idLibroParam == null || idLibroParam.isEmpty()) {
            inviaErrore(response, "Parametri mancanti");
            return;
        }

        try {
            int idLibro = Integer.parseInt(idLibroParam);
            LibroDAO libroDAO = new LibroDAO();
            Libro libro = libroDAO.trovaLibroPerId(idLibro);

            if (libro == null) {
                inviaErrore(response, "Libro non trovato");
                return;
            }

            boolean successo = false;
            String messaggio = "";

            switch (azione.toLowerCase()) {
                case "aggiungi":
                    int quantita = request.getParameter("quantita") != null ?
                            Integer.parseInt(request.getParameter("quantita")) : 1;
                    successo = carrello.aggiungiLibro(libro, quantita);
                    messaggio = successo ? "Libro aggiunto al carrello" : "Quantità non disponibile";
                    break;

                case "rimuovi":
                    successo = carrello.rimuoviLibro(idLibro);
                    messaggio = successo ? "Libro rimosso dal carrello" : "Libro non trovato";
                    break;

                case "aggiorna":
                    int nuovaQuantita = Integer.parseInt(request.getParameter("quantita"));
                    successo = carrello.aggiornaQuantita(idLibro, nuovaQuantita);
                    messaggio = successo ? "Quantità aggiornata" : "Errore nell'aggiornamento";
                    break;

                default:
                    inviaErrore(response, "Azione non valida");
                    return;
            }

            // Se l'utente è loggato, salviamo il carrello nel database
            if (session.getAttribute("utente") != null && successo) {
                CarrelloDAO carrelloDAO = new CarrelloDAO();
                carrelloDAO.salvaCarrello(((Utente)session.getAttribute("utente")).getIdUtente(), carrello);
            }

            if (isAjaxRequest(request)) {
                inviaRispostaJSON(response, successo, messaggio, carrello);
            } else {
                response.sendRedirect(request.getHeader("referer")); // riportato alla pagina precedente
            }

        } catch (NumberFormatException e) {
            inviaErrore(response, "ID libro non valido");
        } catch (SQLException e) {
            throw new ServletException("Errore nel database", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        Carrello carrello = (Carrello) session.getAttribute("carrello");

        if (carrello == null) {
            carrello = new Carrello();
            session.setAttribute("carrello", carrello);
        }

        request.setAttribute("carrello", carrello);
        request.getRequestDispatcher("/jsp/visualizzaCarrello.jsp").forward(request, response);
    }

    private boolean gestisciAggiungiLibro(Carrello carrello, Libro libro, HttpServletRequest request) {
        int quantitaAggiungi = 1;
        try {
            String quantitaParam = request.getParameter("quantita");
            if (quantitaParam != null && !quantitaParam.isEmpty()) {
                quantitaAggiungi = Integer.parseInt(quantitaParam);
            }
            return carrello.aggiungiLibro(libro, quantitaAggiungi);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean gestisciAggiornaQuantita(Carrello carrello, int idLibro, HttpServletRequest request) {
        try {
            String quantitaParam = request.getParameter("quantita");
            if (quantitaParam == null || quantitaParam.isEmpty()) {
                throw new NumberFormatException("Quantità mancante");
            }
            int nuovaQuantita = Integer.parseInt(quantitaParam);
            return carrello.aggiornaQuantita(idLibro, nuovaQuantita);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    private void inviaErrore(HttpServletResponse response, String messaggio) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", messaggio);
        errorResponse.put("carrello", null);

        response.getWriter().write(gson.toJson(errorResponse));
    }
    private void inviaRispostaJSON(HttpServletResponse response, boolean successo,
                                   String messaggio, Carrello carrello) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("success", successo);
        jsonResponse.put("message", messaggio);

        if (carrello != null) {
            int totaleArticoli = carrello.getTotaleArticoli();
            BigDecimal totale = carrello.getTotale();

            jsonResponse.put("totaleArticoli", totaleArticoli);
            jsonResponse.put("totale", totale);

            List<Map<String, Object>> articoli = new ArrayList<>();
            for (Carrello.ArticoloCarrello articolo : carrello.getArticoli()) {
                Map<String, Object> art = new HashMap<>();
                art.put("idLibro", articolo.getLibro().getIdLibro());
                art.put("titolo", articolo.getLibro().getTitolo());
                art.put("quantita", articolo.getQuantita());
                art.put("prezzo", articolo.getLibro().getPrezzo());
                art.put("totale", articolo.getTotale());
                articoli.add(art);
            }
            jsonResponse.put("articoli", articoli);
        } else {
            jsonResponse.put("totaleArticoli", 0);
            jsonResponse.put("totale", BigDecimal.ZERO);
            jsonResponse.put("articoli", new ArrayList<>());
        }
        response.getWriter().write(gson.toJson(jsonResponse));
    }
}
