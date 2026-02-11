package presentation.controller;

import business.model.Utente;
import business.service.account.AccountService;
import business.service.address.AddressService;
import business.service.order.OrderService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ModificaDatiUtenteControllerTest {
    @Test
    @DisplayName("TCS Modifica Password: password attuale errata")
    void tcModificaPassword_passwordAttualeErrata() throws Exception {
        CambiaPasswordServlet servlet = new CambiaPasswordServlet();
        AccountService accountService = mock(AccountService.class);
        AddressService addressService = mock(AddressService.class);
        OrderService orderService = mock(OrderService.class);
        servlet.setAccountService(accountService);
        servlet.setAddressService(addressService);
        servlet.setOrderService(orderService);

        when(addressService.listByUser(1)).thenReturn(Collections.emptyList());
        when(orderService.listByUser(1)).thenReturn(Collections.emptyList());
        when(accountService.changePassword(eq(1), eq("Rosarossa5"), eq("Rosaverde1"))).thenReturn(false);
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher rd = mock(RequestDispatcher.class);

        Utente u = new Utente();
        u.setIdUtente(1);

        when(req.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);
        when(req.getParameter("oldPassword")).thenReturn("Rosarossa5");
        when(req.getParameter("newPassword")).thenReturn("Rosaverde1");
        when(req.getParameter("confirmPassword")).thenReturn("Rosaverde1");
        when(req.getRequestDispatcher("/WEB-INF/jsp/gestioneAccount.jsp")).thenReturn(rd);
        servlet.doPost(req, resp);
        verify(req).setAttribute(eq("errore"), anyString());
        verify(rd).forward(req, resp);
    }

    @Test
    @DisplayName("TCS Modifica Password: conferma non coincide")
    void tcModificaPassword_confermaNonCoincide() throws Exception {
        CambiaPasswordServlet servlet = new CambiaPasswordServlet();
        AccountService accountService = mock(AccountService.class);
        AddressService addressService = mock(AddressService.class);
        OrderService orderService = mock(OrderService.class);
        servlet.setAccountService(accountService);
        servlet.setAddressService(addressService);
        servlet.setOrderService(orderService);

        when(addressService.listByUser(1)).thenReturn(Collections.emptyList());
        when(orderService.listByUser(1)).thenReturn(Collections.emptyList());

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher rd = mock(RequestDispatcher.class);

        Utente u = new Utente();
        u.setIdUtente(1);

        when(req.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);
        when(req.getParameter("oldPassword")).thenReturn("Rosarossa3");
        when(req.getParameter("newPassword")).thenReturn("Rosaverde1");
        when(req.getParameter("confirmPassword")).thenReturn("Rosaverde8");
        when(req.getRequestDispatcher("/WEB-INF/jsp/gestioneAccount.jsp")).thenReturn(rd);
        servlet.doPost(req, resp);
        verify(req).setAttribute(eq("errore"), anyString());
        verify(rd).forward(req, resp);
        verify(accountService, never()).changePassword(anyInt(), anyString(), anyString());
    }

    @Test
    @DisplayName("TCS Modifica Password: successo")
    void tcModificaPassword_successo() throws Exception {
        CambiaPasswordServlet servlet = new CambiaPasswordServlet();
        AccountService accountService = mock(AccountService.class);
        AddressService addressService = mock(AddressService.class);
        OrderService orderService = mock(OrderService.class);
        servlet.setAccountService(accountService);
        servlet.setAddressService(addressService);
        servlet.setOrderService(orderService);

        when(addressService.listByUser(1)).thenReturn(Collections.emptyList());
        when(orderService.listByUser(1)).thenReturn(Collections.emptyList());
        when(accountService.changePassword(eq(1), eq("Rosarossa3"), eq("Rosaverde1"))).thenReturn(true);
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher rd = mock(RequestDispatcher.class);

        Utente u = new Utente();
        u.setIdUtente(1);

        when(req.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);
        when(req.getParameter("oldPassword")).thenReturn("Rosarossa3");
        when(req.getParameter("newPassword")).thenReturn("Rosaverde1");
        when(req.getParameter("confirmPassword")).thenReturn("Rosaverde1");
        when(req.getRequestDispatcher("/WEB-INF/jsp/gestioneAccount.jsp")).thenReturn(rd);
        servlet.doPost(req, resp);
        verify(req).setAttribute(eq("messaggio"), anyString());
        verify(req).setAttribute(eq("tipoMessaggio"), eq("success"));
        verify(rd).forward(req, resp);
    }
}
