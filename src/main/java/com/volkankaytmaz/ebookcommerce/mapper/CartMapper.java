package com.volkankaytmaz.ebookcommerce.mapper;

import com.volkankaytmaz.ebookcommerce.config.CustomMapperConfig;
import com.volkankaytmaz.ebookcommerce.dto.CartDTO;
import com.volkankaytmaz.ebookcommerce.model.CartEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(config = CustomMapperConfig.class, uses = {CartItemMapper.class})
public interface CartMapper {
    BigDecimal FIXED_BOOK_PRICE = BigDecimal.valueOf(9.99);

    @Mapping(target = "items", source = "items")
    @Mapping(target = "totalPrice", expression = "java(calculateTotalPrice(cart))")
    CartDTO cartToCartDTO(CartEntity cart);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "items", source = "items")
    CartEntity cartDTOToCart(CartDTO cartDTO);

    default BigDecimal calculateTotalPrice(CartEntity cart) {
        BigDecimal totalPrice = cart.getItems().stream()
                .map(item -> FIXED_BOOK_PRICE.multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("Calculated total price: " + totalPrice);
        return totalPrice;
    }
}