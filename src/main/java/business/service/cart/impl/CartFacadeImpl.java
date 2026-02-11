package business.service.cart.impl;

import business.model.Carrello;
import business.model.Libro;
import business.service.cart.CartFacade;
import data.dao.CarrelloDAO;
import data.dao.LibroDAO;

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
    public Carrello syncAfterLogin(int idUtente, Carrello carrelloTemporaneo) {
        if (carrelloTemporaneo != null
                && carrelloTemporaneo.getArticoli() != null
                && !carrelloTemporaneo.getArticoli().isEmpty()) {

            try {
                carrelloDAO.salvaCarrello(idUtente, carrelloTemporaneo);
            } catch (SQLException e) {
                System.err.println("Errore durante il salvataggio del carrello: " + e.getMessage());
            }
            return carrelloTemporaneo;
        }

        try {
            Carrello carrelloDB = carrelloDAO.getCarrelloByUtente(idUtente);
            return (carrelloDB != null) ? carrelloDB : new Carrello();
        } catch (SQLException e) {
            System.err.println("Errore durante il caricamento del carrello: " + e.getMessage());
            return new Carrello();
        }
    }

    @Override
    public Carrello getCurrentCart(Integer idUtente, Carrello sessionCart) {
        if (sessionCart != null) return sessionCart;

        if (idUtente != null) {
            try {
                Carrello carrelloDB = carrelloDAO.getCarrelloByUtente(idUtente);
                return (carrelloDB != null) ? carrelloDB : new Carrello();
            } catch (SQLException e) {
                System.err.println("Errore durante il caricamento del carrello: " + e.getMessage());
                return new Carrello();
            }
        }
        return new Carrello();
    }

    @Override
    public boolean addBook(Integer idUtente, Carrello cart, int idLibro, int quantita) {
        if (cart == null) return false;

        Libro libro = libroDAO.trovaLibroPerId(idLibro);
        if (libro == null) return false;

        boolean ok = cart.aggiungiLibro(libro, quantita);
        if (!ok) return false;

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
    public boolean updateQuantity(Integer idUtente, Carrello cart, int idLibro, int nuovaQuantita) {
        if (cart == null) return false;

        boolean ok = cart.aggiornaQuantita(idLibro, nuovaQuantita);
        if (!ok) return false;

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
    public boolean removeBook(Integer idUtente, Carrello cart, int idLibro) {
        if (cart == null) return false;

        boolean ok = cart.rimuoviLibro(idLibro);
        if (!ok) return false;

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
