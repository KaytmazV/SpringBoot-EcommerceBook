package com.volkankaytmaz.ebookcommerce.mapper;

import com.volkankaytmaz.ebookcommerce.config.CustomMapperConfig;
import com.volkankaytmaz.ebookcommerce.dto.CartItemDTO;
import com.volkankaytmaz.ebookcommerce.model.CartItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CustomMapperConfig.class, uses = {BookMapper.class})
public interface CartItemMapper {
    @Mapping(target = "book", source = "book")
    CartItemDTO cartItemToCartItemDTO(CartItemEntity cartItem);

    @Mapping(target = "book", source = "book")
    CartItemEntity cartItemDTOToCartItemEntity(CartItemDTO cartItemDTO);
}

