package business.service.catalog.impl;

import business.model.Categoria;
import business.service.catalog.CategoryService;
import data.dao.CategoriaDAO;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CategoryServiceImpl implements CategoryService {

    private final CategoriaDAO categoriaDAO;

    public CategoryServiceImpl(CategoriaDAO categoriaDAO) {
        this.categoriaDAO = Objects.requireNonNull(categoriaDAO);
    }

    @Override
    public List<Categoria> listAll() {
        try {
            return categoriaDAO.trovaTutteCategorie();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
