package service.order;

import model.bean.Carrello;
import model.bean.Ordine;

public interface OrderService {

    /**
     * Crea e salva un ordine a partire dal carrello (con dettagli) e aggiorna la disponibilità dei libri.
     * Ritorna l'Ordine con idOrdine valorizzato se il salvataggio va a buon fine.
     *
     * @param idUtente      id dell'utente che effettua l'ordine
     * @param idIndirizzo   id dell'indirizzo di spedizione selezionato (deve appartenere all'utente)
     * @param carrello      carrello corrente
     */
    Ordine placeOrder(int idUtente, int idIndirizzo, Carrello carrello);
}
