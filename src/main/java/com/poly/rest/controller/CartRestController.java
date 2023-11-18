package com.poly.rest.controller;

import com.poly.dao.AccountDao;
import com.poly.dao.CartDao;
import com.poly.dao.CartDetailDao;
import com.poly.entity.Account;
import com.poly.entity.Cart;
import com.poly.entity.CartDetail;
import com.poly.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/rest/carts")
public class CartRestController {
    @Autowired
    CartDao cartDao;

    @Autowired
    CartDetailDao cartDetailDao;

    @Autowired
    AccountDao accountDao;

    @GetMapping()
    public ResponseEntity<List<Product>> getAllProductCart() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Product> products = new ArrayList<>();
        if (principal instanceof UserDetails) {
            Cart cart = cartDao.findByName(((UserDetails) principal).getUsername());
            if (cart != null) {
                List<CartDetail> cartDetails = cartDetailDao.getAllByCart_Id(cart.getId());
                for (CartDetail c : cartDetails) {
                    Product product = c.getProduct();
                    product.setQuantity(c.getQuantity());
                    products.add(product);
                }
            }
        }

        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Transactional
    @PostMapping
    public ResponseEntity<?> addCart(@RequestBody Product[] products) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            Account account = accountDao.getById(((UserDetails) principal).getUsername());
            Cart cart = cartDao.findByName(((UserDetails) principal).getUsername());
            if (cart == null) {
                cart = new Cart();
                cart.setAccount(account);
                cartDao.save(cart);
            }
            cartDetailDao.deleteAllByCart_Id(cart.getId());
            for (Product p : products) {
                CartDetail cartDetail = new CartDetail();
                cartDetail.setCart(cart);
                cartDetail.setProduct(p);
                cartDetail.setPrice(p.getUnit_price());
                cartDetail.setQuantity(p.getQuantity());
                cartDetailDao.save(cartDetail);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);

    }

}
