# E-Book Store

This project is an e-book store application developed using Spring Boot. Users can view books, add them to their cart, and make purchases.

## About the Dataset

The project uses the "Book Recommendation Dataset" from Kaggle. This dataset:
- Is sourced from Stuttgart City Library
- Contains over 300,000 records
- Is distributed under CC0: Public Domain license
- Contains rich data suitable for book recommendations

Dataset source: [Kaggle Book Recommendation Dataset](https://www.kaggle.com/datasets/arashnic/book-recommendation-dataset)

## Features

- User registration and login (JWT-based authentication)
- Book catalog browsing and search
- Advanced filtering options
- Cart management
- Order tracking
- Book recommendation system

## Technologies

### Backend
- Java 11
- Spring Boot 2.5.x
- Spring Security
- Spring Data JPA
- MySQL
- JWT (JSON Web Token)
- Maven

### Frontend
- HTML5
- CSS3
- JavaScript
- Responsive Design

## API Endpoints

### Authentication
- POST /api/auth/register - Register new user
- POST /api/auth/login - User login

### Books
- GET /api/books - List all books
- GET /api/books/{id} - Get details of a specific book
- GET /api/books/author/{author} - Filter books by author
- GET /api/books/title/{title} - Search books by title

### Cart
- GET /api/cart - View user's cart
- POST /api/cart/add - Add item to cart
- POST /api/cart/remove/{bookId} - Remove item from cart
- POST /api/cart/update/{bookId} - Update item quantity in cart
- POST /api/cart/clear - Clear cart

## Security

The application uses JWT-based authentication. To access protected endpoints:
1. First obtain a token using the /api/auth/login endpoint
2. Use the token in the Authorization header for subsequent requests:


## Installation

1. Clone the project:
   ```bash
   git clone https://github.com/KaytmazV/SpringBoot-EcommerceBook.git
   ```
