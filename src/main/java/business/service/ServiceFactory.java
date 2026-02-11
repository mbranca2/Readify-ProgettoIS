package business.service;

import business.service.account.AccountService;
import business.service.account.AdminUserService;
import business.service.account.impl.AccountServiceImpl;
import business.service.account.impl.AdminUserServiceImpl;
import business.service.address.AddressService;
import business.service.address.impl.AddressServiceImpl;
import business.service.cart.CartFacade;
import business.service.cart.impl.CartFacadeImpl;
import business.service.catalog.AdminCatalogService;
import business.service.catalog.CatalogService;
import business.service.catalog.CategoryService;
import business.service.catalog.impl.AdminCatalogServiceImpl;
import business.service.catalog.impl.CatalogServiceImpl;
import business.service.catalog.impl.CategoryServiceImpl;
import business.service.catalog.observer.CartConsistencyObserver;
import business.service.order.AdminOrderService;
import business.service.order.OrderService;
import business.service.order.impl.AdminOrderServiceImpl;
import business.service.order.impl.OrderServiceImpl;
import business.service.review.AdminReviewService;
import business.service.review.ReviewService;
import business.service.review.impl.AdminReviewServiceImpl;
import business.service.review.impl.ReviewServiceImpl;
import data.dao.*;

public class ServiceFactory {

    private ServiceFactory() {
    }

    public static AccountService accountService() {
        return new AccountServiceImpl(new UtenteDAO(), new IndirizzoDAO());
    }

    public static AdminUserService adminUserService() {
        return new AdminUserServiceImpl(new UtenteDAO());
    }

    public static AddressService addressService() {
        return new AddressServiceImpl(new IndirizzoDAO());
    }

    public static CartFacade cartFacade() {
        return new CartFacadeImpl(new CarrelloDAO(), new LibroDAO());
    }

    public static OrderService orderService() {
        return new OrderServiceImpl(new OrdineDAO(), new CarrelloDAO());
    }

    public static AdminOrderService adminOrderService() {
        return new AdminOrderServiceImpl(new OrdineDAO(), new UtenteDAO(), new IndirizzoDAO());
    }

    public static AdminCatalogService adminCatalogService() {
        AdminCatalogServiceImpl svc = new AdminCatalogServiceImpl(new LibroDAO());
        svc.registerObserver(new CartConsistencyObserver());
        return svc;
    }

    public static CatalogService catalogService() {
        return new CatalogServiceImpl(new LibroDAO());
    }

    public static CategoryService categoryService() {
        return new CategoryServiceImpl(new CategoriaDAO());
    }

    public static ReviewService reviewService() {
        return new ReviewServiceImpl(new RecensioneDAO(), new OrdineDAO());
    }

    public static AdminReviewService adminReviewService() {
        return new AdminReviewServiceImpl(new RecensioneDAO());
    }
}
