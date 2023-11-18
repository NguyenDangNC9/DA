package com.poly.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.entity.Account;
import com.poly.entity.Authority;
import com.poly.entity.Category;

public interface CategoryDao extends JpaRepository<Category, Integer>{
    
}
