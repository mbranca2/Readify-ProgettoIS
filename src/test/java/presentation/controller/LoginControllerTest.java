package presentation.controller;

import business.model.Carrello;
import business.model.Utente;
import business.service.account.AccountService;
import business.service.cart.CartFacade;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

class LoginControllerTest {
    @Test
    @DisplayName("TC1.2.1 Login: email non registrata")
    void tcLogin_emailNonRegistrata() throws Exception {
        LoginServlet servlet = new LoginServlet();
        AccountService accountService = mock(AccountService.class);
        CartFacade cartFacade = mock(CartFacade.class);
        servlet.setAccountService(accountService);
        servlet.setCartFacade(cartFacade);

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        RequestDispatcher rd = mock(RequestDispatcher.class);

        when(req.getParameter("email")).thenReturn("robertorossi100@gmail.com");
        when(req.getParameter("password")).thenReturn("Rosarossa3");
        when(req.getContextPath()).thenReturn("");
        when(req.getRequestDispatcher("/WEB-INF/jsp/login.jsp")).thenReturn(rd);
        when(accountService.login(eq("robertorossi100@gmail.com"), eq("Rosarossa3"))).thenReturn(null);
        servlet.doPost(req, resp);
        verify(rd).forward(req, resp);
    }

    @Test
    @DisplayName("TC1.2.2 Login: email non valida")
    void tcLogin_emailNonValida() throws Exception {
        LoginServlet servlet = new LoginServlet();
        AccountService accountService = mock(AccountService.class);
        CartFacade cartFacade = mock(CartFacade.class);
        servlet.setAccountService(accountService);
        servlet.setCartFacade(cartFacade);

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        RequestDispatcher rd = mock(RequestDispatcher.class);

        when(req.getParameter("email")).thenReturn("robertorossi103gmail.com");
        when(req.getParameter("password")).thenReturn("Rosarossa3");
        when(req.getRequestDispatcher("/WEB-INF/jsp/login.jsp")).thenReturn(rd);
        servlet.doPost(req, resp);
        verify(rd).forward(req, resp);
    }

    @Test
    @DisplayName("TC1.2.3 Login: password errata")
    void tcLogin_passwordErrata() throws Exception {
        LoginServlet servlet = new LoginServlet();
        AccountService accountService = mock(AccountService.class);
        CartFacade cartFacade = mock(CartFacade.class);
        servlet.setAccountService(accountService);
        servlet.setCartFacade(cartFacade);

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        RequestDispatcher rd = mock(RequestDispatcher.class);

        when(req.getParameter("email")).thenReturn("robertorossi103@gmail.com");
        when(req.getParameter("password")).thenReturn("Rosarossa5");
        when(req.getContextPath()).thenReturn("");
        when(req.getRequestDispatcher("/WEB-INF/jsp/login.jsp")).thenReturn(rd);
        when(accountService.login(eq("robertorossi103@gmail.com"), eq("Rosarossa5"))).thenReturn(null);
        servlet.doPost(req, resp);
        verify(rd).forward(req, resp);
    }

    @Test
    @DisplayName("TC1.2.4 Login: successo")
    void tcLogin_successo() throws Exception {
        LoginServlet servlet = new LoginServlet();
        AccountService accountService = mock(AccountService.class);
        CartFacade cartFacade = mock(CartFacade.class);
        servlet.setAccountService(accountService);
        servlet.setCartFacade(cartFacade);

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession newSession = mock(HttpSession.class);

        Utente utente = new Utente();
        utente.setIdUtente(1);
        utente.setRuolo("cliente");

        Carrello carrello = mock(Carrello.class);

        when(req.getParameter("email")).thenReturn("robertorossi103@gmail.com");
        when(req.getParameter("password")).thenReturn("Rosarossa3");
        when(req.getContextPath()).thenReturn("");
        when(req.getSession(false)).thenReturn(null);
        when(req.getSession(true)).thenReturn(newSession);
        when(accountService.login(eq("robertorossi103@gmail.com"), eq("Rosarossa3"))).thenReturn(utente);
        when(cartFacade.syncAfterLogin(eq(1), isNull())).thenReturn(carrello);
        servlet.doPost(req, resp);
        verify(resp).sendRedirect("/home");
    }
}
