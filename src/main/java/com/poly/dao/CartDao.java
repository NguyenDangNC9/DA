package com.poly.dao;

import com.poly.entity.Cart;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CartDao extends CrudRepository<Cart, Integer> {
    @Query(value="Select * from Carts where Username_id=?1 ;",nativeQuery = true)
    Cart findByName(String username);
}