package business.service.address.impl;

import business.model.Indirizzo;
import business.service.address.AddressService;
import data.dao.IndirizzoDAO;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AddressServiceImpl implements AddressService {

    private final IndirizzoDAO indirizzoDAO;

    public AddressServiceImpl(IndirizzoDAO indirizzoDAO) {
        this.indirizzoDAO = Objects.requireNonNull(indirizzoDAO);
    }

    @Override
    public List<Indirizzo> listByUser(int idUtente) {
        if (idUtente <= 0) return Collections.emptyList();
        return indirizzoDAO.trovaIndirizziPerUtente(idUtente);
    }

    @Override
    public Indirizzo getById(int idIndirizzo) {
        if (idIndirizzo <= 0) return null;
        return indirizzoDAO.trovaIndirizzoPerId(idIndirizzo);
    }

    @Override
    public boolean isOwnedByUser(int idIndirizzo, int idUtente) {
        if (idIndirizzo <= 0 || idUtente <= 0) return false;
        Indirizzo indirizzo = indirizzoDAO.trovaIndirizzoPerId(idIndirizzo);
        return indirizzo != null && indirizzo.getIdUtente() == idUtente;
    }
}
