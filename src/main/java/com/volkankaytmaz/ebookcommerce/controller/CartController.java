package com.volkankaytmaz.ebookcommerce.controller;

import com.volkankaytmaz.ebookcommerce.dto.CartDTO;
import com.volkankaytmaz.ebookcommerce.service.CartService;
import com.volkankaytmaz.ebookcommerce.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = {"http://localhost:63342", "http://localhost:8082"})
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartDTO> getCart(Authentication authentication) {
        System.out.println("getCart method called");
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            System.out.println("Authentication failed");
            return ResponseEntity.status(401).build();
        }
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        System.out.println("User ID from token: " + userPrincipal.getId());
        CartDTO cartDTO = cartService.getCartByUserId(userPrincipal.getId());
        System.out.println("CartDTO returned: " + cartDTO);
        return ResponseEntity.ok(cartDTO);
    }

    @PostMapping("/add")
    public ResponseEntity<CartDTO> addToCart(
            @RequestParam Long bookId,
            @RequestParam int quantity,
            Authentication authentication) {
        System.out.println("addToCart method called");
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            System.out.println("Authentication failed");
            return ResponseEntity.status(401).build();
        }
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        System.out.println("Adding book ID: " + bookId + " with quantity: " + quantity + " for user ID: " + userPrincipal.getId());
        return ResponseEntity.ok(cartService.addItemToCart(userPrincipal.getId(), bookId, quantity));
    }

    @PostMapping("/remove/{bookId}")
    public ResponseEntity<CartDTO> removeFromCart(
            @PathVariable Long bookId,
            Authentication authentication) {
        System.out.println("removeFromCart method called");
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            System.out.println("Authentication failed");
            return ResponseEntity.status(401).build();
        }
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        System.out.println("Removing book ID: " + bookId + " for user ID: " + userPrincipal.getId());
        return ResponseEntity.ok(cartService.removeItemFromCart(userPrincipal.getId(), bookId));
    }

    @PostMapping("/clear")
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        System.out.println("clearCart method called");
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            System.out.println("Authentication failed");
            return ResponseEntity.status(401).build();
        }
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        System.out.println("Clearing cart for user ID: " + userPrincipal.getId());
        cartService.clearCart(userPrincipal.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update/{bookId}")
    public ResponseEntity<CartDTO> updateCartItemQuantity(
            @PathVariable Long bookId,
            @RequestParam int quantity,
            Authentication authentication) {
        System.out.println("updateCartItemQuantity method called");
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            System.out.println("Authentication failed");
            return ResponseEntity.status(401).build();
        }
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        System.out.println("Updating book ID: " + bookId + " with quantity: " + quantity + " for user ID: " + userPrincipal.getId());
        return ResponseEntity.ok(cartService.updateCartItemQuantity(userPrincipal.getId(), bookId, quantity));
    }
}