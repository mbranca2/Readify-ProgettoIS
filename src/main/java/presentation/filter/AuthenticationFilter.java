package presentation.filter;

import business.model.Utente;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(
        filterName = "AuthenticationFilter",
        urlPatterns = {
                "/checkout",
                "/conferma-ordine",
                "/dettaglio-ordine",
                "/salva-ordine",
                "/profilo",
                "/profilo/*",
                "/gestione-indirizzo",
                "/cambia-password",
                "/recensioni/aggiungi",
                "/admin/*"
        }
)
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // no-op
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String contextPath = httpRequest.getContextPath();
        String requestURI = httpRequest.getRequestURI();

        HttpSession session = httpRequest.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        boolean loggedIn = (utente != null);
        boolean isAdminArea = requestURI.startsWith(contextPath + "/admin");

        if (isAdminArea) {
            if (!loggedIn) {
                redirectToLoginWithReturnUrl(httpRequest, httpResponse);
                return;
            }
            if (!utente.isAdmin()) {
                httpResponse.sendRedirect(contextPath + "/home?error=unauthorized");
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        if (loggedIn) {
            chain.doFilter(request, response);
            return;
        }

        redirectToLoginWithReturnUrl(httpRequest, httpResponse);
    }

    private void redirectToLoginWithReturnUrl(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws IOException {

        String contextPath = httpRequest.getContextPath();

        String requestedURL = httpRequest.getRequestURI();
        String queryString = httpRequest.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            requestedURL += "?" + queryString;
        }

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute("redirectAfterLogin", requestedURL);

        httpResponse.sendRedirect(contextPath + "/login?redirect=true");
    }

    @Override
    public void destroy() {
        // no-op
    }
}
