package com.volkankaytmaz.ebookcommerce.service;

import com.volkankaytmaz.ebookcommerce.dto.CartDTO;
import com.volkankaytmaz.ebookcommerce.model.BookEntity;
import com.volkankaytmaz.ebookcommerce.model.CartEntity;
import com.volkankaytmaz.ebookcommerce.model.CartItemEntity;
import com.volkankaytmaz.ebookcommerce.model.User;
import com.volkankaytmaz.ebookcommerce.repository.BookRepository;
import com.volkankaytmaz.ebookcommerce.repository.CartRepository;
import com.volkankaytmaz.ebookcommerce.repository.UserRepository;
import com.volkankaytmaz.ebookcommerce.mapper.CartMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final CartMapper cartMapper;

    public CartServiceImpl(CartRepository cartRepository, UserRepository userRepository,
                           BookRepository bookRepository, CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.cartMapper = cartMapper;
    }

    @Override
    @Transactional
    public CartDTO getCartByUserId(Long userId) {
        logger.debug("Getting cart for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        logger.debug("User found: {}", user.getUsername());

        CartEntity cart = cartRepository.findByUser(user)
                .orElseGet(() -> createNewCart(user));
        logger.debug("Cart found: {}", cart.getId());
        logger.debug("Cart items count: {}", cart.getItems().size());

        for (CartItemEntity item : cart.getItems()) {
            logger.debug("Cart item: bookId={}, quantity={}", item.getBook().getId(), item.getQuantity());
        }

        CartDTO cartDTO = cartMapper.cartToCartDTO(cart);
        logger.debug("CartDTO items count: {}", cartDTO.getItems().size());
        logger.debug("CartDTO total price: {}", cartDTO.getTotalPrice());

        return cartDTO;
    }

    @Override
    @Transactional
    public CartDTO addItemToCart(Long userId, Long bookId, int quantity) {
        logger.debug("Adding item to cart for user ID: {}, book ID: {}, quantity: {}", userId, bookId, quantity);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        final CartEntity cart = cartRepository.findByUser(user)
                .orElseGet(() -> createNewCart(user));
        final BookEntity finalBook = book;

        CartItemEntity cartItem = cart.getItems().stream()
                .filter(item -> item.getBook().getId().equals(bookId))
                .findFirst()
                .orElseGet(() -> {
                    CartItemEntity newItem = new CartItemEntity();
                    newItem.setCart(cart);
                    newItem.setBook(finalBook);
                    newItem.setQuantity(0);
                    cart.getItems().add(newItem);
                    return newItem;
                });

        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        CartEntity savedCart = cartRepository.save(cart);
        logger.debug("Item added to cart. New cart item count: {}", savedCart.getItems().size());

        return cartMapper.cartToCartDTO(savedCart);
    }

    @Override
    @Transactional
    public CartDTO removeItemFromCart(Long userId, Long bookId) {
        logger.debug("Removing item from cart for user ID: {}, book ID: {}", userId, bookId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        CartEntity cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().removeIf(item -> item.getBook().getId().equals(bookId));
        cart = cartRepository.save(cart);
        logger.debug("Item removed from cart. New cart item count: {}", cart.getItems().size());

        return cartMapper.cartToCartDTO(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        logger.debug("Clearing cart for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        CartEntity cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().clear();
        cartRepository.save(cart);
        logger.debug("Cart cleared");
    }

    @Override
    @Transactional
    public CartDTO updateCartItemQuantity(Long userId, Long bookId, int quantity) {
        logger.debug("Updating cart item quantity for user ID: {}, book ID: {}, new quantity: {}", userId, bookId, quantity);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        CartEntity cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItemEntity cartItem = cart.getItems().stream()
                .filter(item -> item.getBook().getId().equals(bookId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (quantity <= 0) {
            cart.getItems().remove(cartItem);
        } else {
            cartItem.setQuantity(quantity);
        }

        cart = cartRepository.save(cart);
        logger.debug("Cart item quantity updated. New cart item count: {}", cart.getItems().size());

        return cartMapper.cartToCartDTO(cart);
    }

    private CartEntity createNewCart(User user) {
        logger.debug("Creating new cart for user: {}", user.getUsername());
        CartEntity cart = new CartEntity();
        cart.setUser(user);
        CartEntity savedCart = cartRepository.save(cart);
        logger.debug("New cart created with ID: {}", savedCart.getId());
        return savedCart;
    }
}