package com.volkankaytmaz.ebookcommerce.mapper;

import com.volkankaytmaz.ebookcommerce.config.CustomMapperConfig;
import com.volkankaytmaz.ebookcommerce.dto.OrderItemDTO;
import com.volkankaytmaz.ebookcommerce.model.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CustomMapperConfig.class, uses = {BookMapper.class})
public interface OrderItemMapper {
    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "bookId", source = "book.id")
    OrderItemDTO orderItemToOrderItemDTO(OrderItemEntity orderItem);

    @Mapping(target = "order.id", source = "orderId")
    @Mapping(target = "book.id", source = "bookId")
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "book", ignore = true)
    OrderItemEntity orderItemDTOToOrderItemEntity(OrderItemDTO orderItemDTO);
}