package com.volkankaytmaz.ebookcommerce.service;

import com.volkankaytmaz.ebookcommerce.dto.CartDTO;

public interface CartService {
    CartDTO getCartByUserId(Long userId);
    CartDTO addItemToCart(Long userId, Long bookId, int quantity);
    CartDTO removeItemFromCart(Long userId, Long bookId);
    void clearCart(Long userId);
    CartDTO updateCartItemQuantity(Long userId, Long bookId, int quantity);
}

