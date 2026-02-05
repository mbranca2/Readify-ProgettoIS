package service.catalog.impl;

import model.bean.Categoria;
import model.dao.CategoriaDAO;
import service.catalog.CategoryService;

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
