package com.volkankaytmaz.ebookcommerce.dto;

public class AddToCartRequest {
    private Long bookId;
    private int quantity;

    // Getters and setters

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}