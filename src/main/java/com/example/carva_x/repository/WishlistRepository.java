package com.example.carva_x.repository;

import com.example.carva_x.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<WishlistItem, Integer> {

    List<WishlistItem> findAllByUser_Id(Integer userId);

    Optional<WishlistItem> findByUser_IdAndProduct_Id(Integer userId, Integer productId);

    boolean existsByUser_IdAndProduct_Id(Integer userId, Integer productId);
}
