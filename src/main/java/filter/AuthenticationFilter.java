package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.Utente;

import java.io.IOException;

@WebFilter(
        filterName = "AuthenticationFilter",
        urlPatterns = {
                "/checkout",
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
    public void init(FilterConfig filterConfig) throws ServletException { }

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
    public void destroy() { }
}
