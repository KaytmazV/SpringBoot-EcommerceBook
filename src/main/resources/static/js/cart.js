// Sabitler
const API_BASE_URL = "http://localhost:8082"
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
            const errorData = await response.json()
            throw new Error(`Token refresh failed: ${errorData.message || response.statusText}`)
        }

        const data = await response.json()
        localStorage.setItem("token", data.token)
        localStorage.setItem("refreshToken", data.refreshToken)

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
            try {
                token = await refreshToken()
                authOptions.headers.Authorization = `Bearer ${token}`
                response = await fetch(url, authOptions)
            } catch (refreshError) {
                console.error("Token refresh failed:", refreshError)
                logout()
                throw new Error("Authentication failed. Please log in again.")
            }
        }

        if (!response.ok) {
            const errorData = await response.json()
            throw new Error(`HTTP error! status: ${response.status}, message: ${errorData.message || response.statusText}`)
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
    const toast = document.createElement("div")
    toast.className = `toast toast-${type}`
    toast.textContent = message
    toastContainer.appendChild(toast)
    setTimeout(() => {
        toast.remove()
    }, 3000)
}

// Sepeti Görüntüleme
async function displayCart() {
    const cartItems = document.getElementById("cartItems")
    const cartTotal = document.getElementById("cartTotal")
    const checkoutBtn = document.getElementById("checkoutBtn")

    const token = getToken()
    if (!token) {
        cartItems.innerHTML = `
      <div class="error-message">
        <p>Sepeti görüntülemek için giriş yapmanız gerekmektedir.</p>
        <button onclick="redirectToLogin()" class="retry-btn">Giriş Yap</button>
      </div>
    `
        cartTotal.textContent = "Toplam: ₺0.00"
        checkoutBtn.style.display = "none"
        return
    }

    try {
        const response = await fetchWithAuth(`${API_BASE_URL}/api/cart`)
        const cartData = await response.json()

        if (cartData.items.length === 0) {
            cartItems.innerHTML = "<p>Sepetiniz boş.</p>"
            cartTotal.textContent = "Toplam: ₺0.00"
            checkoutBtn.style.display = "none"
            return
        }

        const itemsHTML = cartData.items
            .map(
                (item) => `
        <div class="cart-item">
            <img src="${item.book.imageUrlS || PLACEHOLDER_IMAGE}" alt="${item.book.title}" class="cart-item-image">
            <div class="cart-item-details">
                <h3>${item.book.title}</h3>
                <p>${item.book.author}</p>
                <p>${item.quantity} x ₺${(item.book.price || 0).toFixed(2)}</p>
            </div>
            <div class="cart-item-actions">
                <button onclick="changeQuantity(${item.book.id}, -1)" class="btn btn-small">-</button>
                <span>${item.quantity}</span>
                <button onclick="changeQuantity(${item.book.id}, 1)" class="btn btn-small">+</button>
                <button onclick="removeFromCart(${item.book.id})" class="btn btn-small btn-danger">Kaldır</button>
            </div>
        </div>
    `,
            )
            .join("")

        cartItems.innerHTML = itemsHTML

        const total = cartData.items.reduce((sum, item) => sum + (item.book.price || 0) * item.quantity, 0)
        cartTotal.textContent = `Toplam: ₺${total.toFixed(2)}`
        checkoutBtn.style.display = "block"
    } catch (error) {
        console.error("Display cart error:", error)
        cartItems.innerHTML = `
      <div class="error-message">
        <p>Sepet bilgileri alınamadı. Lütfen daha sonra tekrar deneyin.</p>
        <button onclick="displayCart()" class="retry-btn">Tekrar Dene</button>
      </div>
    `
        cartTotal.textContent = "Toplam: ₺0.00"
        checkoutBtn.style.display = "none"
    }
}

// Ürün Miktarını Değiştirme
async function changeQuantity(bookId, change) {
    try {
        const response = await fetchWithAuth(`${API_BASE_URL}/api/cart/update`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ bookId, quantity: change }),
        })

        await displayCart()
    } catch (error) {
        console.error("Change quantity error:", error)
        createToast("Miktar güncellenirken bir hata oluştu. Lütfen tekrar deneyin.", "error")
    }
}

// Sepetten Ürün Kaldırma
async function removeFromCart(bookId) {
    try {
        const response = await fetchWithAuth(`${API_BASE_URL}/api/cart/remove`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ bookId }),
        })

        createToast("Ürün sepetten kaldırıldı.")
        await displayCart()
    } catch (error) {
        console.error("Remove from cart error:", error)
        createToast("Ürün sepetten kaldırılırken bir hata oluştu. Lütfen tekrar deneyin.", "error")
    }
}

// Çıkış İşlemi
function logout() {
    localStorage.removeItem("token")
    localStorage.removeItem("refreshToken")
    createToast("Çıkış yapıldı. Ana sayfaya yönlendiriliyorsunuz.")
    setTimeout(() => {
        window.location.href = "index.html"
    }, 2000)
}

// Giriş sayfasına yönlendirme
function redirectToLogin() {
    window.location.href = "index.html#loginForm"
}

// Sayfa Yüklendiğinde
document.addEventListener("DOMContentLoaded", () => {
    displayCart()

    const checkoutBtn = document.getElementById("checkoutBtn")
    checkoutBtn.addEventListener("click", () => {
        // Ödeme işlemi burada gerçekleştirilecek
        createToast("Ödeme işlemi başlatıldı.")
    })

    const logoutBtn = document.getElementById("logoutBtn")
    if (logoutBtn) {
        logoutBtn.addEventListener("click", logout)
    }
})

