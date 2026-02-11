package business.service.order.dto;

import business.model.Indirizzo;
import business.model.Ordine;
import business.model.Utente;

public record AdminOrderDetail(Ordine ordine, Utente utente, Indirizzo indirizzo) {

}
