package com.poly.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poly.dao.ReportDao;
import com.poly.entity.Product;
import com.poly.entity.Report;
import com.poly.service.ProductService;

@CrossOrigin("*")
@RestController

public class ReportRestController2 {
	@Autowired
	ReportDao reportDao;

	// Theo khách hàng
	@RequestMapping("/rest/report1")
	public List<Report> getAll() {
		return reportDao.revenueByCustomer();
	}

	// Theo loại sản phẩm
	@RequestMapping("/rest/charcate")
	public List<Report> charcate() {
		return reportDao.revenueByCategory();
	}

	// Theo loại thương hiệu
	@RequestMapping("/rest/chartrak")
	public List<Report> chartrak() {
		return reportDao.revenueByTrak();
	}
}
