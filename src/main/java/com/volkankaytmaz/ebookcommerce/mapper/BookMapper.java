package com.volkankaytmaz.ebookcommerce.mapper;

import com.volkankaytmaz.ebookcommerce.config.CustomMapperConfig;
import com.volkankaytmaz.ebookcommerce.dto.BookDTO;
import com.volkankaytmaz.ebookcommerce.model.BookEntity;
import org.mapstruct.Mapper;

@Mapper(config = CustomMapperConfig.class)
public interface BookMapper {
    BookDTO bookToBookDTO(BookEntity book);
    BookEntity bookDTOToBookEntity(BookDTO bookDTO);
}