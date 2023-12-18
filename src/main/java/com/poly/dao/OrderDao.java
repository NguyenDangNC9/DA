package com.poly.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import com.poly.entity.Account;
import com.poly.entity.Authority;
import com.poly.entity.Category;
import com.poly.entity.Order;
import com.poly.entity.Report;

public interface OrderDao extends JpaRepository<Order, Integer> {

	@Query("SELECT o FROM Order o WHERE o.account.Username=?1 order by o.id desc ")
	List<Order> findByUsername(String username);

	@Query(value = "Select * from Orders order by Orders.Order_id desc", nativeQuery = true)
	List<Order> findByAllDesc();

	@Query(value = "Select top (10) * from Orders order by Orders.CreateDate desc", nativeQuery = true)
	List<Order> getTop10();

	@Query(value = "Select  * from Orders where Orders.Status=0  order by Order_id  ", nativeQuery = true)
	List<Order> getStatus();

	@Query(value = "select * from Orders where Order_id like %?1% \r\n"
			+ " order by Order_id  ", nativeQuery = true)
	List<Order> findByOrder_Id(String order_id);

	@Query(value = "select * from Orders where Username like  %?1% \r\n"
			+ "and CreateDate between ?2 and ?3 \r\n"
			+ "and Status like %?4% and Phone like %?5% and Price between ?6 and ?7 \r\n"
			+ "order by Order_id desc", nativeQuery = true)
	List<Order> findByAllKeyWord(@Param("Username") String username,
			@DateTimeFormat(pattern = "yyyy-MM-dd") Date MinDay,
			@DateTimeFormat(pattern = "yyyy-MM-dd") Date MaxDay,
			@Param("Status") String Status, @Param("Phone") String Phone, @Param("MinPrice") Integer unit_price,
			@Param("MaxPrice") Integer unit_price1);

	@Query("SELECT new Report(o.product.Name, sum(o.Price * o.Quantity),  sum(o.Quantity), min(o.Price), max(o.Price), avg(o.Price)) "
			+ " FROM OrderDetail o "
			+ " WHERE o.order.account=:username AND o.order.Status=3"
			+ " GROUP BY o.product.Name")
	List<Report> getPurchaseByUser(@Param("username") String username);

}
