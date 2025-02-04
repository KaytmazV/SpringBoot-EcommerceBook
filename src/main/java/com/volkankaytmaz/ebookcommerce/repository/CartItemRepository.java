package com.volkankaytmaz.ebookcommerce.repository;

import com.volkankaytmaz.ebookcommerce.model.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
}

