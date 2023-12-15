package com.poly.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import com.poly.entity.Report;
import com.poly.entity.ReportProductbyDay;

public interface ReportDao extends JpaRepository<Report, Serializable> {
	@Query("SELECT new Report(o.category.name, "
			+ " sum(o.Unit_price * o.Quantity), "
			+ " sum(o.Quantity), "
			+ " min(o.Unit_price), "
			+ " max(o.Unit_price), "
			+ " avg(o.Unit_price)) "
			+ " FROM Product o "
			+ " GROUP BY o.category.name")
	List<Report> inventoryByCategory();

	@Query("SELECT new Report(o.product.category.name, "
			+ " sum(o.Price * o.Quantity), "
			+ " sum(o.Quantity), "
			+ " min(o.Price), "
			+ " max(o.Price), "
			+ " avg(o.Price)) "
			+ " FROM OrderDetail o "
			+ " where  o.order.Status =3"
			+ " GROUP BY o.product.category.name")
	List<Report> revenueByCategory();

	@Query("SELECT new Report(o.order.account.Fullname, "
			+ " sum(o.Price * o.Quantity), "
			+ " sum(o.Quantity), "
			+ " min(o.Price), "
			+ " max(o.Price), "
			+ " avg(o.Price)) "
			+ " FROM OrderDetail o "
			+ " where  o.order.Status =3"
			+ " GROUP BY o.order.account.Fullname"
			+ " ORDER BY sum(o.Price * o.Quantity) DESC")
	List<Report> revenueByCustomer();

	@Query("SELECT new Report(month(o.order.CreateDate), "
			+ " sum(o.Price * o.Quantity), "
			+ " sum(o.Quantity), "
			+ " min(o.Price), "
			+ " max(o.Price), "
			+ " avg(o.Price)) "
			+ " FROM OrderDetail o "
			+ " where  o.order.Status =3"
			+ " GROUP BY month(o.order.CreateDate)"
			+ " ORDER BY month(o.order.CreateDate)")
	List<Report> revenueByMonth();

	@Query("SELECT  new Report(o.order.account.Username, "
			+ " sum(o.Price * o.Quantity), "
			+ " sum(o.Quantity), "
			+ " min(o.Price), "
			+ " max(o.Price), "
			+ " avg(o.Price)) "
			+ " FROM OrderDetail o "
			// Số 3 là trạng thái thanh toán trả sau
			+ " where  o.order.Status =3"
			+ " GROUP BY o.order.account.Username"
			+ " ORDER BY sum(o.Price * o.Quantity) DESC")
	List<Report> Top10User(Pageable pageable);

	@Query("SELECT new Report(YEAR(o.order.CreateDate), "
			+ " sum(o.Price * o.Quantity), "
			+ " sum(o.Quantity), "
			+ " min(o.Price), "
			+ " max(o.Price), "
			+ " avg(o.Price)) "
			+ " FROM OrderDetail o "
			+ " where  o.order.Status =3"
			+ " GROUP BY YEAR(o.order.CreateDate)"
			+ " ORDER BY YEAR(o.order.CreateDate)")
	List<Report> revenueByWeek();

	@Query("SELECT new Report(o.trademark.name, "
			+ " sum(o.Unit_price * o.Quantity), "
			+ " sum(o.Quantity), "
			+ " min(o.Unit_price), "
			+ " max(o.Unit_price), "
			+ " avg(o.Unit_price)) "
			+ " FROM Product o "
			+ " GROUP BY o.trademark.name")
	List<Report> inventoryByTrarkmark();

	// Trakmark là thương hiệu
	@Query("SELECT new Report(o.product.trademark.name, "
			+ " sum(o.Price * o.Quantity), "
			+ " sum(o.Quantity), "
			+ " min(o.Price), "
			+ " max(o.Price), "
			+ " avg(o.Price)) "
			+ " FROM OrderDetail o "
			+ " where  o.order.Status =3"
			+ " GROUP BY o.product.trademark.name")
	List<Report> revenueByTrak();

	@Query("SELECT new Report(month(o.order.CreateDate), "
			+ " sum(o.Price * o.Quantity), "
			+ " sum(o.Quantity), "
			+ " min(o.Price), "
			+ " max(o.Price), "
			+ " avg(o.Price)) "
			+ " FROM OrderDetail o "
			+ " where year(o.order.CreateDate) =?1 and o.order.Status =3"
			+ " GROUP BY month(o.order.CreateDate)"
			+ " ORDER BY month(o.order.CreateDate)")
	List<Report> revenueByyear(@Param("year") int year);

	@Query("SELECT new Report(o.order.account.Username, "
			+ " sum(o.Price * o.Quantity), "
			+ " sum(o.Quantity), "
			+ " min(o.Price), "
			+ " max(o.Price), "
			+ " avg(o.Price)) "
			+ " FROM OrderDetail o "
			+ " where  o.order.CreateDate Between ?1 and  ?2 and o.order.Status =3"
			+ " GROUP BY o.order.account.Username"
			+ " ORDER BY sum(o.Price * o.Quantity) DESC")
	List<Report> revenueByCustomerBydate(@DateTimeFormat(pattern = "yyyy-MM-dd") Date MinDay,
			@DateTimeFormat(pattern = "yyyy-MM-dd") Date MaxDay);

}