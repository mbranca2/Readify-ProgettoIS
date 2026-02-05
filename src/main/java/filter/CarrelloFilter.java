package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import model.bean.Carrello;
import model.bean.Utente;
import model.dao.CarrelloDAO;

import java.io.IOException;

@WebFilter("/*")
public class CarrelloFilter implements Filter {
    private CarrelloDAO carrelloDAO;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        carrelloDAO = new CarrelloDAO();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(true);

        try {
            if (session.getAttribute("utente") != null) {
                Utente utente = (Utente) session.getAttribute("utente");
                Carrello carrello = carrelloDAO.getCarrelloByUtente(utente.getIdUtente());
                if (carrello == null) {
                    carrello = new Carrello();
                }
                session.setAttribute("carrello", carrello);
            } else if (session.getAttribute("carrello") == null) {
                session.setAttribute("carrello", new Carrello());
            }
            request.setAttribute("carrello", session.getAttribute("carrello"));
        } catch (Exception e) {
            throw new ServletException("Errore durante il caricamento del carrello", e);
        }
        chain.doFilter(request, response);
    }
}