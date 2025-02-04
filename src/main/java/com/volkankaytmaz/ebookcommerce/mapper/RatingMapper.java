package com.volkankaytmaz.ebookcommerce.mapper;

import com.volkankaytmaz.ebookcommerce.config.CustomMapperConfig;
import com.volkankaytmaz.ebookcommerce.dto.RatingDTO;
import com.volkankaytmaz.ebookcommerce.model.RatingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CustomMapperConfig.class)
public interface RatingMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "bookId", source = "book.id")
    RatingDTO ratingToRatingDTO(RatingEntity rating);

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "book.id", source = "bookId")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "book", ignore = true)
    RatingEntity ratingDTOToRatingEntity(RatingDTO ratingDTO);
}