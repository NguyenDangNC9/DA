package com.poly.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.entity.Trademark;

public interface TrademarkDao extends JpaRepository<Trademark, Integer> {

}
