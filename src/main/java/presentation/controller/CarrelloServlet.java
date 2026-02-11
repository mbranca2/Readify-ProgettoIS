package presentation.controller;

import business.model.Carrello;
import business.model.Utente;
import business.service.ServiceFactory;
import business.service.cart.CartFacade;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/carrello")
public class CarrelloServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final Gson gson = new Gson();
    private final CartFacade cartFacade = ServiceFactory.cartFacade();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);

        Integer idUtente = resolveUserId(session);

        Carrello carrello = cartFacade.getCurrentCart(idUtente, (Carrello) session.getAttribute("carrello"));
        session.setAttribute("carrello", carrello);

        String azione = request.getParameter("azione");
        String idLibroParam = request.getParameter("idLibro");

        if (azione == null || azione.isBlank() || idLibroParam == null || idLibroParam.isBlank()) {
            handleError(request, response, "Parametri mancanti");
            return;
        }

        try {
            int idLibro = Integer.parseInt(idLibroParam);

            boolean successo;
            String messaggio;

            switch (azione.toLowerCase()) {
                case "aggiungi": {
                    int quantita = 1;
                    String qParam = request.getParameter("quantita");
                    if (qParam != null && !qParam.isBlank()) {
                        quantita = Integer.parseInt(qParam);
                    }

                    successo = cartFacade.addBook(idUtente, carrello, idLibro, quantita);
                    messaggio = successo ? "Libro aggiunto al carrello" : "Quantità non disponibile";
                    break;
                }

                case "rimuovi": {
                    successo = cartFacade.removeBook(idUtente, carrello, idLibro);
                    messaggio = successo ? "Libro rimosso dal carrello" : "Libro non trovato";
                    break;
                }

                case "aggiorna": {
                    String qParam = request.getParameter("quantita");
                    if (qParam == null || qParam.isBlank()) {
                        handleError(request, response, "Quantità mancante");
                        return;
                    }
                    int nuovaQuantita = Integer.parseInt(qParam);

                    successo = cartFacade.updateQuantity(idUtente, carrello, idLibro, nuovaQuantita);
                    messaggio = successo ? "Quantità aggiornata" : "Errore nell'aggiornamento";
                    break;
                }

                default:
                    handleError(request, response, "Azione non valida");
                    return;
            }

            session.setAttribute("carrello", carrello);

            if (isAjaxRequest(request)) {
                inviaRispostaJSON(response, successo, messaggio, carrello);
            } else {
                response.sendRedirect(request.getHeader("referer"));
            }

        } catch (NumberFormatException e) {
            handleError(request, response, "Parametri numerici non validi");
        } catch (Exception e) {
            e.printStackTrace();
            handleError(request, response, "Errore interno");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        Integer idUtente = resolveUserId(session);

        Carrello carrello = cartFacade.getCurrentCart(idUtente, (Carrello) session.getAttribute("carrello"));
        session.setAttribute("carrello", carrello);

        request.setAttribute("carrello", carrello);
        request.getRequestDispatcher("/WEB-INF/jsp/visualizzaCarrello.jsp").forward(request, response);
    }

    private Integer resolveUserId(HttpSession session) {
        Object u = session.getAttribute("utente");
        if (u instanceof Utente) {
            return ((Utente) u).getIdUtente();
        }

        Object legacy = session.getAttribute("idUtente");
        if (legacy instanceof Integer) return (Integer) legacy;

        return null;
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, String messaggio) throws IOException {
        if (isAjaxRequest(request)) {
            inviaErroreJSON(response, messaggio);
        } else {
            response.sendRedirect(request.getHeader("referer"));
        }
    }

    private void inviaErroreJSON(HttpServletResponse response, String messaggio) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", messaggio);

        response.getWriter().write(gson.toJson(errorResponse));
    }

    private void inviaRispostaJSON(HttpServletResponse response, boolean successo, String messaggio, Carrello carrello) throws IOException {
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
