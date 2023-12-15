package com.poly.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poly.entity.Account;

public interface AccountDao extends JpaRepository<Account, String> {

	@Query("SELECT DISTINCT ar.account FROM Authority ar WHERE ar.role.id IN ('DIRE','STAF')")
	List<Account> getAdministrators();

	@Query(value = "select * from Accounts where Username like %:kw% or \r\n"
			+ " Fullname like %:kw%  or \r\n" + " Email like %:kw%", nativeQuery = true)
	List<Account> finbyIdOrName(@Param("kw") String keyword);

}
