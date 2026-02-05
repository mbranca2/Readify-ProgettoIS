package service.cart;

import jakarta.servlet.http.HttpSession;
import model.bean.Carrello;

public interface CartFacade {

    void syncAfterLogin(int idUtente, Carrello carrelloTemporaneo, HttpSession session);

    Carrello getCurrentCart(Integer idUtente, HttpSession session);

    boolean addBook(Integer idUtente, HttpSession session, int idLibro, int quantita);

    boolean updateQuantity(Integer idUtente, HttpSession session, int idLibro, int nuovaQuantita);

    boolean removeBook(Integer idUtente, HttpSession session, int idLibro);
}
