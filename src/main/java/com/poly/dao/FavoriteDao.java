package com.poly.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poly.entity.Favorite;

public interface FavoriteDao extends JpaRepository<Favorite, Integer> {
	@Query(value = "Select * from Favorites order by Favorites.Favorite_id desc", nativeQuery = true)
	List<Favorite> findByAllDesc();

	@Transactional
	@Modifying
	@Query(value = "delete from Favorites where Product_id = ?1 and Username like ?2 ", nativeQuery = true)
	void deleteFavaritesAdmin(@Param("product_id") Integer product_id, @Param("username") String username);

	@Query(value = "select *  from Favorites where Product_id = ?1 and Username like ?2 ", nativeQuery = true)
	List<Favorite> checkFavaritesAdmin(@Param("product_id") Integer product_id, @Param("username") String username);

	@Query(value = "select *  from Favorites where Username like ?1 ", nativeQuery = true)
	List<Favorite> getListUserFavorite(@Param("username") String username);
}
