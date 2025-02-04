package com.volkankaytmaz.ebookcommerce.service;

import com.volkankaytmaz.ebookcommerce.dto.BookDTO;
import com.volkankaytmaz.ebookcommerce.mapper.BookMapper;
import com.volkankaytmaz.ebookcommerce.model.BookEntity;
import com.volkankaytmaz.ebookcommerce.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    public List<BookDTO> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::bookToBookDTO)
                .collect(Collectors.toList());
    }

    public Page<BookDTO> findAllPaginated(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(bookMapper::bookToBookDTO);
    }

    public Optional<BookDTO> findById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::bookToBookDTO);
    }

    @Transactional
    public BookDTO save(BookDTO bookDTO) {
        BookEntity bookEntity = bookMapper.bookDTOToBookEntity(bookDTO);
        BookEntity savedBook = bookRepository.save(bookEntity);
        return bookMapper.bookToBookDTO(savedBook);
    }

    @Transactional
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    public List<BookDTO> findByAuthor(String author) {
        return bookRepository.findByAuthor(author).stream()
                .map(bookMapper::bookToBookDTO)
                .collect(Collectors.toList());
    }

    public List<BookDTO> findByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(bookMapper::bookToBookDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        return bookRepository.findById(id)
                .map(existingBook -> {
                    updateBookFields(existingBook, bookDTO);
                    return bookMapper.bookToBookDTO(bookRepository.save(existingBook));
                })
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }

    private void updateBookFields(BookEntity existingBook, BookDTO bookDTO) {
        existingBook.setIsbn(bookDTO.getIsbn());
        existingBook.setTitle(bookDTO.getTitle());
        existingBook.setAuthor(bookDTO.getAuthor());
        existingBook.setYearOfPublication(bookDTO.getYearOfPublication());
        existingBook.setPublisher(bookDTO.getPublisher());
        existingBook.setImageUrlS(bookDTO.getImageUrlS());
        existingBook.setImageUrlM(bookDTO.getImageUrlM());
        existingBook.setImageUrlL(bookDTO.getImageUrlL());
    }
}

