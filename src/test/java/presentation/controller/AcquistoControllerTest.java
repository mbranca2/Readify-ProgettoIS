package presentation.controller;

import business.model.Carrello;
import business.model.Ordine;
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

class AcquistoControllerTest {
    private static final String PAGAMENTO_JSP = "/WEB-INF/jsp/pagamento.jsp";
    private static final String CONFERMA_ORDINE_JSP = "/WEB-INF/jsp/conferma-ordine.jsp";

    @Test
    @DisplayName("TC2.1.1 Checkout: CAP non valido")
    void tcIndirizzo_capNonValido() throws Exception {
        ConfermaOrdineServlet servlet = new ConfermaOrdineServlet();
        OrderService orderService = mock(OrderService.class);
        AddressService addressService = mock(AddressService.class);
        AccountService accountService = mock(AccountService.class);

        servlet.setOrderService(orderService);
        servlet.setAddressService(addressService);
        servlet.setAccountService(accountService);

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher rdPagamento = mock(RequestDispatcher.class);

        Utente u = new Utente();
        u.setIdUtente(1);

        Carrello c = mock(Carrello.class);
        when(c.isVuoto()).thenReturn(false);

        stubSession(req, session, u, c);
        stubNewAddressParams(req, "0004223", "RM");
        stubPaymentParams(req, "Roberto Rossi", "5767991234001156", "12/30", "344");

        when(addressService.listByUser(1)).thenReturn(Collections.emptyList());
        when(req.getRequestDispatcher(PAGAMENTO_JSP)).thenReturn(rdPagamento);

        servlet.doPost(req, resp);

        assertErroreForwardPagamentoIndirizzo(req, resp, rdPagamento);
        verify(accountService, never()).addAddress(anyInt(), any());
        verify(orderService, never()).placeOrder(anyInt(), anyInt(), any());
    }

    @Test
    @DisplayName("TC2.1.2 Checkout: provincia non valida")
    void tcIndirizzo_provinciaNonValida() throws Exception {
        ConfermaOrdineServlet servlet = new ConfermaOrdineServlet();
        OrderService orderService = mock(OrderService.class);
        AddressService addressService = mock(AddressService.class);
        AccountService accountService = mock(AccountService.class);

        servlet.setOrderService(orderService);
        servlet.setAddressService(addressService);
        servlet.setAccountService(accountService);

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher rdPagamento = mock(RequestDispatcher.class);

        Utente u = new Utente();
        u.setIdUtente(1);

        Carrello c = mock(Carrello.class);
        when(c.isVuoto()).thenReturn(false);

        stubSession(req, session, u, c);
        stubNewAddressParams(req, "00042", "RMA");
        stubPaymentParams(req, "Roberto Rossi", "5767991234001156", "12/30", "344");

        when(addressService.listByUser(1)).thenReturn(Collections.emptyList());
        when(req.getRequestDispatcher(PAGAMENTO_JSP)).thenReturn(rdPagamento);

        servlet.doPost(req, resp);

        assertErroreForwardPagamentoIndirizzo(req, resp, rdPagamento);
        verify(accountService, never()).addAddress(anyInt(), any());
        verify(orderService, never()).placeOrder(anyInt(), anyInt(), any());
    }

    @Test
    @DisplayName("TC2.1.3 Checkout: indirizzo valido ma pagamento non valido")
    void tcIndirizzo_valido() throws Exception {
        ConfermaOrdineServlet servlet = new ConfermaOrdineServlet();
        OrderService orderService = mock(OrderService.class);
        AddressService addressService = mock(AddressService.class);
        AccountService accountService = mock(AccountService.class);

        servlet.setOrderService(orderService);
        servlet.setAddressService(addressService);
        servlet.setAccountService(accountService);

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher rdPagamento = mock(RequestDispatcher.class);

        Utente u = new Utente();
        u.setIdUtente(1);

        Carrello c = mock(Carrello.class);
        when(c.isVuoto()).thenReturn(false);

        stubSession(req, session, u, c);
        stubNewAddressParams(req, "00042", "RM");
        stubPaymentParams(req, "Roberto Rossi", "576799123400115", "12/30", "344");

        when(accountService.addAddress(eq(1), any())).thenReturn(true);
        when(addressService.listByUser(1)).thenReturn(Collections.emptyList());
        when(req.getRequestDispatcher(PAGAMENTO_JSP)).thenReturn(rdPagamento);

        servlet.doPost(req, resp);

        verify(accountService, times(1)).addAddress(eq(1), any());
        assertErroreForwardPagamentoPagamento(req, resp, rdPagamento);
        verify(orderService, never()).placeOrder(anyInt(), anyInt(), any());
    }

    @Test
    @DisplayName("TC2.2.1 Pagamento: numero carta non valido")
    void tcPagamento_numeroCartaNonValido() throws Exception {
        ConfermaOrdineServlet servlet = new ConfermaOrdineServlet();
        OrderService orderService = mock(OrderService.class);
        AddressService addressService = mock(AddressService.class);
        AccountService accountService = mock(AccountService.class);

        servlet.setOrderService(orderService);
        servlet.setAddressService(addressService);
        servlet.setAccountService(accountService);

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher rdPagamento = mock(RequestDispatcher.class);

        Utente u = new Utente();
        u.setIdUtente(1);

        Carrello c = mock(Carrello.class);
        when(c.isVuoto()).thenReturn(false);

        stubSession(req, session, u, c);
        stubExistingAddress(req, addressService, 1);
        stubPaymentParams(req, "Roberto Rossi", "576799123400115", "12/30", "344");

        when(addressService.listByUser(1)).thenReturn(Collections.emptyList());
        when(req.getRequestDispatcher(PAGAMENTO_JSP)).thenReturn(rdPagamento);

        servlet.doPost(req, resp);

        assertErroreForwardPagamentoPagamento(req, resp, rdPagamento);
        verify(orderService, never()).placeOrder(anyInt(), anyInt(), any());
    }

    @Test
    @DisplayName("TC2.2.2 Pagamento: scadenza non valida")
    void tcPagamento_scadenzaNonValida() throws Exception {
        ConfermaOrdineServlet servlet = new ConfermaOrdineServlet();
        OrderService orderService = mock(OrderService.class);
        AddressService addressService = mock(AddressService.class);
        AccountService accountService = mock(AccountService.class);

        servlet.setOrderService(orderService);
        servlet.setAddressService(addressService);
        servlet.setAccountService(accountService);

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher rdPagamento = mock(RequestDispatcher.class);

        Utente u = new Utente();
        u.setIdUtente(1);

        Carrello c = mock(Carrello.class);
        when(c.isVuoto()).thenReturn(false);

        stubSession(req, session, u, c);
        stubExistingAddress(req, addressService, 1);
        stubPaymentParams(req, "Roberto Rossi", "5767991234001156", "15/30", "344");

        when(addressService.listByUser(1)).thenReturn(Collections.emptyList());
        when(req.getRequestDispatcher(PAGAMENTO_JSP)).thenReturn(rdPagamento);

        servlet.doPost(req, resp);

        assertErroreForwardPagamentoPagamento(req, resp, rdPagamento);
        verify(orderService, never()).placeOrder(anyInt(), anyInt(), any());
    }

    @Test
    @DisplayName("TC2.2.3 Pagamento: scadenza passata")
    void tcPagamento_scadenzaPassata() throws Exception {
        ConfermaOrdineServlet servlet = new ConfermaOrdineServlet();
        OrderService orderService = mock(OrderService.class);
        AddressService addressService = mock(AddressService.class);
        AccountService accountService = mock(AccountService.class);

        servlet.setOrderService(orderService);
        servlet.setAddressService(addressService);
        servlet.setAccountService(accountService);

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher rdPagamento = mock(RequestDispatcher.class);

        Utente u = new Utente();
        u.setIdUtente(1);

        Carrello c = mock(Carrello.class);
        when(c.isVuoto()).thenReturn(false);

        stubSession(req, session, u, c);
        stubExistingAddress(req, addressService, 1);
        stubPaymentParams(req, "Roberto Rossi", "5767991234001156", "12/19", "344");

        when(addressService.listByUser(1)).thenReturn(Collections.emptyList());
        when(req.getRequestDispatcher(PAGAMENTO_JSP)).thenReturn(rdPagamento);

        servlet.doPost(req, resp);

        assertErroreForwardPagamentoPagamento(req, resp, rdPagamento);
        verify(orderService, never()).placeOrder(anyInt(), anyInt(), any());
    }

    @Test
    @DisplayName("TC2.2.4 Pagamento: CVV non valido")
    void tcPagamento_cvvNonValido() throws Exception {
        ConfermaOrdineServlet servlet = new ConfermaOrdineServlet();
        OrderService orderService = mock(OrderService.class);
        AddressService addressService = mock(AddressService.class);
        AccountService accountService = mock(AccountService.class);

        servlet.setOrderService(orderService);
        servlet.setAddressService(addressService);
        servlet.setAccountService(accountService);

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher rdPagamento = mock(RequestDispatcher.class);

        Utente u = new Utente();
        u.setIdUtente(1);

        Carrello c = mock(Carrello.class);
        when(c.isVuoto()).thenReturn(false);

        stubSession(req, session, u, c);
        stubExistingAddress(req, addressService, 1);
        stubPaymentParams(req, "Roberto Rossi", "5767991234001156", "12/30", "3445");

        when(addressService.listByUser(1)).thenReturn(Collections.emptyList());
        when(req.getRequestDispatcher(PAGAMENTO_JSP)).thenReturn(rdPagamento);

        servlet.doPost(req, resp);

        assertErroreForwardPagamentoPagamento(req, resp, rdPagamento);
        verify(orderService, never()).placeOrder(anyInt(), anyInt(), any());
    }

    @Test
    @DisplayName("TC2.2.5 Pagamento: completato")
    void tcPagamento_successo() throws Exception {
        ConfermaOrdineServlet servlet = new ConfermaOrdineServlet();
        OrderService orderService = mock(OrderService.class);
        AddressService addressService = mock(AddressService.class);
        AccountService accountService = mock(AccountService.class);

        servlet.setOrderService(orderService);
        servlet.setAddressService(addressService);
        servlet.setAccountService(accountService);

        Ordine ordine = new Ordine();
        ordine.setIdOrdine(99);

        when(orderService.placeOrder(eq(1), eq(10), any())).thenReturn(ordine);

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher rdConferma = mock(RequestDispatcher.class);

        Utente u = new Utente();
        u.setIdUtente(1);

        Carrello c = mock(Carrello.class);
        when(c.isVuoto()).thenReturn(false);

        stubSession(req, session, u, c);
        stubExistingAddress(req, addressService, 1);
        stubPaymentParams(req, "Roberto Rossi", "5767991234001156", "12/30", "344");

        when(req.getRequestDispatcher(CONFERMA_ORDINE_JSP)).thenReturn(rdConferma);
        when(addressService.isOwnedByUser(10, 1)).thenReturn(true);

        servlet.doPost(req, resp);

        verify(orderService, times(1)).placeOrder(eq(1), eq(10), any());
        verify(c, times(1)).svuota();
        verify(session).setAttribute(eq("carrello"), eq(c));
        verify(req).setAttribute(eq("ordine"), eq(ordine));
        verify(req).setAttribute(eq("indirizzoOrdine"), any());
        verify(req).setAttribute(eq("idOrdine"), eq(99));
        verify(rdConferma).forward(req, resp);
    }

    private void stubSession(HttpServletRequest req, HttpSession session, Utente u, Carrello c) {
        when(req.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);
        when(session.getAttribute("carrello")).thenReturn(c);
        when(req.getContextPath()).thenReturn("");
    }

    private void stubExistingAddress(HttpServletRequest req, AddressService addressService, int userId) {
        when(req.getParameter("indirizzoSpedizione")).thenReturn("10");
        when(addressService.isOwnedByUser(10, userId)).thenReturn(true);
    }

    private void stubNewAddressParams(HttpServletRequest req, String cap, String provincia) {
        when(req.getParameter("indirizzoSpedizione")).thenReturn("new");
        when(req.getParameter("via")).thenReturn("Via Patria Maggiore 96");
        when(req.getParameter("citta")).thenReturn("Roma");
        when(req.getParameter("cap")).thenReturn(cap);
        when(req.getParameter("provincia")).thenReturn(provincia);
        when(req.getParameter("paese")).thenReturn("Italia");
    }

    private void stubPaymentParams(HttpServletRequest req, String cardName, String cardNumber, String expiryDate, String cvv) {
        when(req.getParameter("cardName")).thenReturn(cardName);
        when(req.getParameter("cardNumber")).thenReturn(cardNumber);
        when(req.getParameter("expiryDate")).thenReturn(expiryDate);
        when(req.getParameter("cvv")).thenReturn(cvv);
    }

    private void assertErroreForwardPagamentoIndirizzo(HttpServletRequest req, HttpServletResponse resp, RequestDispatcher rdPagamento) throws Exception {
        verify(req).setAttribute(eq("errore"), anyString());
        verify(req).setAttribute(eq("indirizzi"), any());
        verify(rdPagamento).forward(req, resp);
        verify(req, never()).setAttribute(eq("erroriPagamento"), any());
    }

    private void assertErroreForwardPagamentoPagamento(HttpServletRequest req, HttpServletResponse resp, RequestDispatcher rdPagamento) throws Exception {
        verify(req).setAttribute(eq("errore"), anyString());
        verify(req).setAttribute(eq("erroriPagamento"), any());
        verify(req).setAttribute(eq("indirizzi"), any());
        verify(rdPagamento).forward(req, resp);
    }
}
