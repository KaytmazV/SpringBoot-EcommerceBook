package com.volkankaytmaz.ebookcommerce.service;

import com.volkankaytmaz.ebookcommerce.model.BookEntity;
import com.volkankaytmaz.ebookcommerce.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;

@Service
public class CSVLoaderService {

    private final BookRepository bookRepository;
    private static final Logger logger = LoggerFactory.getLogger(CSVLoaderService.class);

    public CSVLoaderService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public void loadCSVIfDatabaseEmpty() {
        if (bookRepository.count() == 0) {
            loadCSV();
        } else {
            logger.info("Veritabanı boş değil. CSV yükleme işlemi atlanıyor.");
        }
    }

    private void loadCSV() {
        String filePath = "src/main/resources/data/books.csv";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // Başlığı atla
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                try {
                    BookEntity bookEntity = new BookEntity();
                    bookEntity.setIsbn(fields[0].trim());
                    bookEntity.setTitle(fields[1].trim());
                    bookEntity.setAuthor(fields[2].trim());
                    bookEntity.setYearOfPublication(Integer.parseInt(fields[3].trim()));
                    bookEntity.setPublisher(fields[4].trim());
                    bookEntity.setImageUrlS(fields[5].trim());
                    bookEntity.setImageUrlM(fields[6].trim());
                    bookEntity.setImageUrlL(fields[7].trim());
                    // Varsayılan bir fiyat belirle


                    BookEntity savedBook = bookRepository.save(bookEntity);
                    logger.info("Kitap kaydedildi: {} - Yazar: {}", savedBook.getTitle(), savedBook.getAuthor());

                } catch (Exception e) {
                    logger.error("Satır işlenirken hata oluştu: " + line, e);
                }
            }
        } catch (IOException e) {
            logger.error("CSV dosyası okunurken hata oluştu", e);
        }
    }
}

