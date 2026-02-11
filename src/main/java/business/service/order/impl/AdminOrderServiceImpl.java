package business.service.order.impl;

import business.model.Indirizzo;
import business.model.Ordine;
import business.model.StatoOrdine;
import business.model.Utente;
import business.service.order.AdminOrderService;
import business.service.order.AdminOrderServiceException;
import business.service.order.dto.AdminOrderDetail;
import data.dao.IndirizzoDAO;
import data.dao.OrdineDAO;
import data.dao.UtenteDAO;

import java.util.List;
import java.util.Objects;

public class AdminOrderServiceImpl implements AdminOrderService {

    private final OrdineDAO ordineDAO;
    private final UtenteDAO utenteDAO;
    private final IndirizzoDAO indirizzoDAO;

    public AdminOrderServiceImpl(OrdineDAO ordineDAO, UtenteDAO utenteDAO, IndirizzoDAO indirizzoDAO) {
        this.ordineDAO = Objects.requireNonNull(ordineDAO);
        this.utenteDAO = Objects.requireNonNull(utenteDAO);
        this.indirizzoDAO = Objects.requireNonNull(indirizzoDAO);
    }

    @Override
    public List<Ordine> listAll() {
        return ordineDAO.trovaTuttiOrdini();
    }

    @Override
    public AdminOrderDetail getOrderDetail(int idOrdine) throws AdminOrderServiceException {
        if (idOrdine <= 0) {
            throw new AdminOrderServiceException("ID ordine non valido.");
        }

        Ordine ordine = ordineDAO.trovaOrdinePerIdConDettagli(idOrdine);
        if (ordine == null) {
            throw new AdminOrderServiceException("Ordine non trovato.");
        }

        Utente utente = utenteDAO.trovaUtentePerId(ordine.getIdUtente());

        Indirizzo indirizzo = null;
        if (ordine.getIdIndirizzo() > 0) {
            indirizzo = indirizzoDAO.trovaIndirizzoPerId(ordine.getIdIndirizzo());
        }

        return new AdminOrderDetail(ordine, utente, indirizzo);
    }

    @Override
    public boolean updateStatus(int idOrdine, StatoOrdine nuovoStato) throws AdminOrderServiceException {
        if (idOrdine <= 0) throw new AdminOrderServiceException("ID ordine non valido.");
        if (nuovoStato == null) throw new AdminOrderServiceException("Stato ordine non valido.");

        // opzionale: valida transizioni (se vuoi lo facciamo dopo).
        boolean ok = ordineDAO.aggiornaStatoOrdine(idOrdine, nuovoStato);
        if (!ok)
            throw new AdminOrderServiceException("Impossibile aggiornare lo stato (ordine non trovato o update fallito).");
        return true;
    }
}
