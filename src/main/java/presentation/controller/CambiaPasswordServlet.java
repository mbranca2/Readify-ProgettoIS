package presentation.controller;

import business.model.Utente;
import business.service.ServiceFactory;
import business.service.account.AccountService;
import business.service.address.AddressService;
import business.service.order.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/cambia-password")
public class CambiaPasswordServlet extends HttpServlet {

    private AccountService accountService;
    private AddressService addressService;
    private OrderService orderService;

    void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    void setAddressService(AddressService addressService) {
        this.addressService = addressService;
    }

    void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void init() throws ServletException {
        this.accountService = ServiceFactory.accountService();
        this.addressService = ServiceFactory.addressService();
        this.orderService = ServiceFactory.orderService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.setAttribute("indirizzi", addressService.listByUser(utente.getIdUtente()));
        request.setAttribute("ordini", orderService.listByUser(utente.getIdUtente()));
        request.getRequestDispatcher("/WEB-INF/jsp/gestioneAccount.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String oldPassword = firstNonEmpty(
                request.getParameter("oldPassword"),
                request.getParameter("vecchiaPassword")
        );
        String newPassword = firstNonEmpty(
                request.getParameter("newPassword"),
                request.getParameter("nuovaPassword")
        );
        String confirmPassword = firstNonEmpty(
                request.getParameter("confirmPassword"),
                request.getParameter("confermaPassword")
        );

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            request.setAttribute("errore", "Compila tutti i campi.");
            request.setAttribute("indirizzi", addressService.listByUser(utente.getIdUtente()));
            request.setAttribute("ordini", orderService.listByUser(utente.getIdUtente()));
            request.getRequestDispatcher("/WEB-INF/jsp/gestioneAccount.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("errore", "Le nuove password non coincidono.");
            request.setAttribute("indirizzi", addressService.listByUser(utente.getIdUtente()));
            request.setAttribute("ordini", orderService.listByUser(utente.getIdUtente()));
            request.getRequestDispatcher("/WEB-INF/jsp/gestioneAccount.jsp").forward(request, response);
            return;
        }

        boolean ok;
        try {
            ok = accountService.changePassword(utente.getIdUtente(), oldPassword, newPassword);
        } catch (Exception e) {
            request.setAttribute("errore", "Errore interno durante l'aggiornamento della password.");
            request.setAttribute("indirizzi", addressService.listByUser(utente.getIdUtente()));
            request.setAttribute("ordini", orderService.listByUser(utente.getIdUtente()));
            request.getRequestDispatcher("/WEB-INF/jsp/gestioneAccount.jsp").forward(request, response);
            return;
        }

        if (ok) {
            request.setAttribute("messaggio", "Password aggiornata con successo.");
            request.setAttribute("tipoMessaggio", "success");
        } else {
            request.setAttribute("errore", "Impossibile aggiornare la password. Verifica la password attuale.");
        }

        request.setAttribute("indirizzi", addressService.listByUser(utente.getIdUtente()));
        request.setAttribute("ordini", orderService.listByUser(utente.getIdUtente()));
        request.getRequestDispatcher("/WEB-INF/jsp/gestioneAccount.jsp").forward(request, response);
    }

    private String firstNonEmpty(String... values) {
        if (values == null) return "";
        for (String value : values) {
            String trimmed = (value == null) ? "" : value.trim();
            if (!trimmed.isEmpty()) return trimmed;
        }
        return "";
    }
}
