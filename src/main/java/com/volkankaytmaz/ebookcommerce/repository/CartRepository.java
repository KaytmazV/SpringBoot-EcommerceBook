package com.volkankaytmaz.ebookcommerce.repository;

import com.volkankaytmaz.ebookcommerce.model.CartEntity;
import com.volkankaytmaz.ebookcommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {
    Optional<CartEntity> findByUser(User user);
}

