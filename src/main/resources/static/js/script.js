// Sabitler
const API_BASE_URL = "http://localhost:8082"
let token = localStorage.getItem("token")
const PLACEHOLDER_IMAGE = "https://via.placeholder.com/133x200.png?text=No+Image"

// Token kontrolü
function getToken() {
    return localStorage.getItem("token")
}

// Token yenileme
async function refreshToken() {
    try {
        const refreshToken = localStorage.getItem("refreshToken")
        if (!refreshToken) {
            throw new Error("Refresh token not found")
        }

        const response = await fetch(`${API_BASE_URL}/api/auth/refresh`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ refreshToken }),
        })

        if (!response.ok) {
            throw new Error("Token refresh failed")
        }

        const data = await response.json()
        localStorage.setItem("token", data.token)
        localStorage.setItem("refreshToken", data.refreshToken)
        token = data.token

        return data.token
    } catch (error) {
        console.error("Token refresh error:", error)
        logout()
        throw error
    }
}

// API isteği gönderme
async function fetchWithAuth(url, options = {}) {
    let token = getToken()
    if (!token) {
        throw new Error("No token available")
    }

    const authOptions = {
        ...options,
        headers: {
            ...options.headers,
            Authorization: `Bearer ${token}`,
        },
    }

    try {
        let response = await fetch(url, authOptions)

        if (response.status === 401) {
            // Token expired, try to refresh
            token = await refreshToken()
            authOptions.headers.Authorization = `Bearer ${token}`
            response = await fetch(url, authOptions)
        }

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`)
        }

        return response
    } catch (error) {
        console.error("Fetch error:", error)
        throw error
    }
}

// Toast Bildirimleri
const createToast = (message, type = "success") => {
    const toastContainer = document.getElementById("toastContainer")
    if (toastContainer) {
        const toast = document.createElement("div")
        toast.className = `toast toast-${type}`
        toast.textContent = message
        toastContainer.appendChild(toast)
        setTimeout(() => {
            toast.remove()
        }, 3000)
    }
}

// Kimlik Doğrulama UI Güncelleme
const updateAuthUI = () => {
    const authNavItem = document.getElementById("authNavItem")
    const authBtn = document.getElementById("authBtn")
    const loginForm = document.getElementById("loginForm")
    const registerForm = document.getElementById("registerForm")

    if (token) {
        if (authBtn) {
            authBtn.textContent = "Çıkış Yap"
            authBtn.onclick = logout
        }
        if (loginForm) loginForm.style.display = "none"
        if (registerForm) registerForm.style.display = "none"
    } else {
        if (authBtn) {
            authBtn.textContent = "Giriş Yap"
            authBtn.onclick = () => {
                if (loginForm) loginForm.style.display = "block"
                if (registerForm) registerForm.style.display = "none"
            }
        }
    }
}

// Giriş İşlemi
async function login(username, password) {
    try {
        const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ username, password }),
        })

        if (!response.ok) {
            throw new Error("Giriş başarısız")
        }

        const data = await response.json()
        localStorage.setItem("token", data.token)
        localStorage.setItem("refreshToken", data.refreshToken)
        token = data.token
        createToast("Giriş başarılı!")
        updateAuthUI()
        await fetchBooks()
        await updateCartCount()
    } catch (error) {
        console.error("Login error:", error)
        createToast(error.message, "error")
    }
}

// Kayıt İşlemi
async function register(username, email, password) {
    try {
        const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ username, email, password }),
        })

        if (!response.ok) {
            throw new Error("Kayıt başarısız")
        }

        createToast("Kayıt başarılı! Lütfen giriş yapın.")
        const loginForm = document.getElementById("loginForm")
        const registerForm = document.getElementById("registerForm")
        if (loginForm) loginForm.style.display = "block"
        if (registerForm) registerForm.style.display = "none"
    } catch (error) {
        console.error("Register error:", error)
        createToast(error.message, "error")
    }
}

// Çıkış İşlemi
function logout() {
    localStorage.removeItem("token")
    localStorage.removeItem("refreshToken")
    token = null
    updateAuthUI()
    createToast("Çıkış yapıldı. Lütfen tekrar giriş yapın.")
    const bookList = document.getElementById("bookList")
    if (bookList) bookList.innerHTML = ""
    const cartCount = document.getElementById("cartCount")
    if (cartCount) cartCount.textContent = "(0)"
    showLoginForm()
}

// Kitapları Görüntüleme
const displayBooks = (books) => {
    const bookList = document.getElementById("bookList")
    if (!bookList) return

    if (!Array.isArray(books) || books.length === 0) {
        bookList.innerHTML = `
            <div class="empty-state">
                <p>Henüz kitap bulunmamaktadır.</p>
            </div>
        `
        return
    }

    const booksHTML = books
        .map(
            (book) => `
        <article class="book-item">
            <div class="book-image">
                <img src="${book.imageUrlM || PLACEHOLDER_IMAGE}" 
                     alt="${book.title}" 
                     loading="lazy">
            </div>
            <div class="book-content">
                <h3 class="book-title">${book.title}</h3>
                <p class="book-author">${book.author}</p>
                <p class="book-price">₺${(book.price || 9.99).toFixed(2)}</p>
                <button class="add-to-cart-btn" onclick="addToCart(${book.id})">
                    Sepete Ekle
                </button>
            </div>
        </article>
    `,
        )
        .join("")

    bookList.innerHTML = booksHTML
}

// Kitapları Getirme
async function fetchBooks() {
    const bookList = document.getElementById("bookList")
    if (!bookList) return

    bookList.innerHTML = `<div class="loading">Kitaplar yükleniyor...</div>`

    try {
        const response = await fetchWithAuth(`${API_BASE_URL}/api/books`)
        const books = await response.json()
        displayBooks(books.content)
    } catch (error) {
        console.error("Error fetching books:", error)
        bookList.innerHTML = `
            <div class="error-message">
                <p>${error.message}</p>
                <button onclick="fetchBooks()" class="retry-btn">Tekrar Dene</button>
            </div>
        `
    }
}

// Sepete Ekleme
async function addToCart(bookId) {
    try {
        const response = await fetchWithAuth(`${API_BASE_URL}/api/cart/add`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ bookId, quantity: 1 }),
        })

        createToast("Kitap sepete eklendi!")
        await updateCartCount()
    } catch (error) {
        console.error("Add to cart error:", error)
        createToast(error.message, "error")
    }
}

// Sepet Sayısını Güncelleme
async function updateCartCount() {
    const cartCount = document.getElementById("cartCount")
    if (!cartCount) return

    const token = getToken()

    if (!token) {
        cartCount.textContent = "(0)"
        return
    }

    try {
        const response = await fetchWithAuth(`${API_BASE_URL}/api/cart`)
        const cartData = await response.json()
        const totalItems = cartData.items.reduce((total, item) => total + item.quantity, 0)
        cartCount.textContent = `(${totalItems})`
    } catch (error) {
        console.error("Update cart count error:", error)
        cartCount.textContent = "(0)"
    }
}

// Sepet Sayfasını Açma
function openCartPage() {
    window.open("cart.html", "_blank")
}

// Giriş formunu gösterme fonksiyonu
function showLoginForm() {
    const loginForm = document.getElementById("loginForm")
    const registerForm = document.getElementById("registerForm")
    if (loginForm) loginForm.style.display = "block"
    if (registerForm) registerForm.style.display = "none"
}

// Sayfa Yüklendiğinde
document.addEventListener("DOMContentLoaded", () => {
    console.log("Sayfa yüklendi, kimlik doğrulama kontrol ediliyor...")
    updateAuthUI()

    const token = getToken()
    if (token) {
        updateCartCount()
        fetchBooks()
    } else {
        const cartCount = document.getElementById("cartCount")
        if (cartCount) cartCount.textContent = "(0)"
        const bookList = document.getElementById("bookList")
        if (bookList) {
            bookList.innerHTML = `
        <div class="error-message">
          <p>Kitapları görüntülemek için giriş yapmanız gerekmektedir.</p>
          <button onclick="showLoginForm()" class="retry-btn">Giriş Yap</button>
        </div>
      `
        }
    }

    const loginFormElement = document.getElementById("loginFormElement")
    if (loginFormElement) {
        loginFormElement.addEventListener("submit", (e) => {
            e.preventDefault()
            const username = document.getElementById("username")
            const password = document.getElementById("password")
            if (username && password) {
                login(username.value, password.value)
            }
        })
    }

    const registerFormElement = document.getElementById("registerFormElement")
    if (registerFormElement) {
        registerFormElement.addEventListener("submit", (e) => {
            e.preventDefault()
            const username = document.getElementById("registerUsername")
            const email = document.getElementById("registerEmail")
            const password = document.getElementById("registerPassword")
            if (username && email && password) {
                register(username.value, email.value, password.value)
            }
        })
    }

    const showRegisterForm = document.getElementById("showRegisterForm")
    if (showRegisterForm) {
        showRegisterForm.addEventListener("click", (e) => {
            e.preventDefault()
            const loginForm = document.getElementById("loginForm")
            const registerForm = document.getElementById("registerForm")
            if (loginForm) loginForm.style.display = "none"
            if (registerForm) registerForm.style.display = "block"
        })
    }

    const showLoginForm = document.getElementById("showLoginForm")
    if (showLoginForm) {
        showLoginForm.addEventListener("click", (e) => {
            e.preventDefault()
            const registerForm = document.getElementById("registerForm")
            const loginForm = document.getElementById("loginForm")
            if (registerForm) registerForm.style.display = "none"
            if (loginForm) loginForm.style.display = "block"
        })
    }

    const cartBtn = document.getElementById("cartBtn")
    if (cartBtn) {
        cartBtn.addEventListener("click", openCartPage)
    }
})

