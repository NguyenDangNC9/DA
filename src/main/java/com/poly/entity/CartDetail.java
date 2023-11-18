package com.poly.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Cartdetails")
public class CartDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    Double Price;
    Integer Quantity;
    @ManyToOne
    @JoinColumn(name = "Product_id")
    Product product;
    @ManyToOne
    @JoinColumn(name = "Cart_id")
    Cart cart;
}

