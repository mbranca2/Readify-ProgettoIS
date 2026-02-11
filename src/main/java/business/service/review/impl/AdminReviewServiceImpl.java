package business.service.review.impl;

import business.service.review.AdminReviewService;
import business.service.review.dto.AdminReviewRow;
import data.dao.RecensioneDAO;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AdminReviewServiceImpl implements AdminReviewService {

    private final RecensioneDAO recensioneDAO;

    public AdminReviewServiceImpl(RecensioneDAO recensioneDAO) {
        this.recensioneDAO = Objects.requireNonNull(recensioneDAO);
    }

    @Override
    public List<AdminReviewRow> listAll() {
        try {
            return recensioneDAO.trovaTutteRecensioniAdmin();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean deleteById(int idRecensione) {
        if (idRecensione <= 0) return false;
        return recensioneDAO.eliminaRecensioneAdmin(idRecensione);
    }
}
