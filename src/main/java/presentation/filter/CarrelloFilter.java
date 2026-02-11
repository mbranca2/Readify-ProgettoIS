package presentation.filter;

import business.model.Carrello;
import business.model.Utente;
import business.service.ServiceFactory;
import business.service.cart.CartFacade;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/*")
public class CarrelloFilter implements Filter {

    private static final String SESSION_CART = "carrello";
    private static final String SESSION_USER = "utente";
    private static final String SESSION_CART_USER_ID = "carrello.userId"; // marker per evitare reload DB
    private static final String REQUEST_CART = "carrello";

    private CartFacade cartFacade;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.cartFacade = ServiceFactory.cartFacade();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = getPath(httpRequest);
        if (isStaticResource(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(true);

        try {
            Utente utente = (Utente) session.getAttribute(SESSION_USER);

            if (utente != null) {
                int userId = utente.getIdUtente();
                Carrello carrelloSession = (Carrello) session.getAttribute(SESSION_CART);
                Integer markerUserId = (Integer) session.getAttribute(SESSION_CART_USER_ID);

                if (carrelloSession == null || markerUserId == null || markerUserId != userId) {
                    Carrello carrelloDb = cartFacade.getCurrentCart(userId, null);
                    session.setAttribute(SESSION_CART, carrelloDb);
                    session.setAttribute(SESSION_CART_USER_ID, userId);
                }

            } else {
                if (session.getAttribute(SESSION_CART) == null) {
                    session.setAttribute(SESSION_CART, new Carrello());
                }
                session.removeAttribute(SESSION_CART_USER_ID);
            }

            request.setAttribute(REQUEST_CART, session.getAttribute(SESSION_CART));

        } catch (Exception e) {
            throw new ServletException("Errore durante il caricamento del carrello", e);
        }

        chain.doFilter(request, response);
    }

    private String getPath(HttpServletRequest req) {
        String uri = req.getRequestURI();          // es: /Readify/css/style.css
        String ctx = req.getContextPath();         // es: /Readify
        return (ctx != null && !ctx.isEmpty() && uri.startsWith(ctx)) ? uri.substring(ctx.length()) : uri;
    }

    private boolean isStaticResource(String path) {
        if (path == null) return false;

        if (path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/img/") ||
                path.startsWith("/images/") ||
                path.startsWith("/fonts/") ||
                path.startsWith("/assets/") ||
                path.startsWith("/webjars/") ||
                path.startsWith("/vendor/")) {
            return true;
        }

        if (path.equals("/favicon.ico") || path.equals("/robots.txt")) {
            return true;
        }

        String lower = path.toLowerCase();
        return lower.endsWith(".css") ||
                lower.endsWith(".js") ||
                lower.endsWith(".png") ||
                lower.endsWith(".jpg") ||
                lower.endsWith(".jpeg") ||
                lower.endsWith(".gif") ||
                lower.endsWith(".svg") ||
                lower.endsWith(".webp") ||
                lower.endsWith(".ico") ||
                lower.endsWith(".woff") ||
                lower.endsWith(".woff2") ||
                lower.endsWith(".ttf") ||
                lower.endsWith(".eot") ||
                lower.endsWith(".map");
    }
}
