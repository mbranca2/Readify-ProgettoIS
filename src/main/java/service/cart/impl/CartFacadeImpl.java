package service.cart.impl;

import jakarta.servlet.http.HttpSession;
import model.bean.Carrello;
import model.bean.Libro;
import model.dao.CarrelloDAO;
import model.dao.LibroDAO;
import service.cart.CartFacade;

import java.sql.SQLException;
import java.util.Objects;

public class CartFacadeImpl implements CartFacade {

    private final CarrelloDAO carrelloDAO;
    private final LibroDAO libroDAO;

    public CartFacadeImpl(CarrelloDAO carrelloDAO, LibroDAO libroDAO) {
        this.carrelloDAO = Objects.requireNonNull(carrelloDAO);
        this.libroDAO = Objects.requireNonNull(libroDAO);
    }

    @Override
    public void syncAfterLogin(int idUtente, Carrello carrelloTemporaneo, HttpSession session) {
        // Caso 1: c'è un carrello guest con articoli => prova a salvarlo e lo metti in sessione
        if (carrelloTemporaneo != null
                && carrelloTemporaneo.getArticoli() != null
                && !carrelloTemporaneo.getArticoli().isEmpty()) {

            try {
                carrelloDAO.salvaCarrello(idUtente, carrelloTemporaneo);
                session.setAttribute("carrello", carrelloTemporaneo);
            } catch (SQLException e) {
                System.err.println("Errore durante il salvataggio del carrello: " + e.getMessage());
                // fallback: mantieni quello temporaneo in sessione
                session.setAttribute("carrello", carrelloTemporaneo);
            }
            return;
        }

        // Caso 2: niente carrello guest => carica da DB (o vuoto)
        try {
            Carrello carrelloDB = carrelloDAO.getCarrelloByUtente(idUtente);
            if (carrelloDB != null) {
                session.setAttribute("carrello", carrelloDB);
            } else {
                session.setAttribute("carrello", new Carrello());
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il caricamento del carrello: " + e.getMessage());
            session.setAttribute("carrello", new Carrello());
        }
    }

    @Override
    public Carrello getCurrentCart(Integer idUtente, HttpSession session) {
        Carrello cart = (Carrello) session.getAttribute("carrello");
        if (cart != null) return cart;

        if (idUtente != null) {
            try {
                Carrello carrelloDB = carrelloDAO.getCarrelloByUtente(idUtente);
                cart = (carrelloDB != null) ? carrelloDB : new Carrello();
            } catch (SQLException e) {
                System.err.println("Errore durante il caricamento del carrello: " + e.getMessage());
                cart = new Carrello();
            }
        } else {
            cart = new Carrello();
        }

        session.setAttribute("carrello", cart);
        return cart;
    }

    @Override
    public boolean addBook(Integer idUtente, HttpSession session, int idLibro, int quantita) {
        Carrello cart = getCurrentCart(idUtente, session);

        Libro libro = libroDAO.trovaLibroPerId(idLibro);
        if (libro == null) return false;

        boolean ok = cart.aggiungiLibro(libro, quantita);
        if (!ok) return false;

        session.setAttribute("carrello", cart);

        if (idUtente != null) {
            try {
                carrelloDAO.salvaCarrello(idUtente, cart);
            } catch (SQLException e) {
                System.err.println("Errore durante il salvataggio del carrello: " + e.getMessage());
                // manteniamo il cart in sessione comunque
            }
        }
        return true;
    }

    @Override
    public boolean updateQuantity(Integer idUtente, HttpSession session, int idLibro, int nuovaQuantita) {
        Carrello cart = getCurrentCart(idUtente, session);

        boolean ok = cart.aggiornaQuantita(idLibro, nuovaQuantita);
        if (!ok) return false;

        session.setAttribute("carrello", cart);

        if (idUtente != null) {
            try {
                carrelloDAO.salvaCarrello(idUtente, cart);
            } catch (SQLException e) {
                System.err.println("Errore durante il salvataggio del carrello: " + e.getMessage());
            }
        }
        return true;
    }

    @Override
    public boolean removeBook(Integer idUtente, HttpSession session, int idLibro) {
        Carrello cart = getCurrentCart(idUtente, session);

        boolean ok = cart.rimuoviLibro(idLibro);
        if (!ok) return false;

        session.setAttribute("carrello", cart);

        if (idUtente != null) {
            try {
                carrelloDAO.salvaCarrello(idUtente, cart);
            } catch (SQLException e) {
                System.err.println("Errore durante il salvataggio del carrello: " + e.getMessage());
            }
        }
        return true;
    }
}
