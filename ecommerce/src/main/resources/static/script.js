function toggleInputs(val) {
    const dynamicInputs = document.getElementById('dynamic-inputs');

    if (val === 'card') {
        dynamicInputs.innerHTML = `
            <div class="input-group">
                <label for="cardNumber">Card Number *</label>
                <div class="input-wrapper">
                    <i class="fas fa-credit-card"></i>
                    <input type="text" id="cardNumber" placeholder="1234 5678 9012 3456" maxlength="19" required>
                </div>
            </div>
            <div class="input-row">
                <div class="input-group">
                    <label for="expiry">Expiry Date *</label>
                    <div class="input-wrapper">
                        <input type="text" id="expiry" placeholder="MM/YY" maxlength="5" required>
                    </div>
                </div>
                <div class="input-group">
                    <label for="cvv">CVV *</label>
                    <div class="input-wrapper">
                        <i class="fas fa-lock"></i>
                        <input type="text" id="cvv" placeholder="123" maxlength="4" required>
                    </div>
                </div>
            </div>
            <div class="input-group">
                <label for="cardName">Cardholder Name *</label>
                <input type="text" id="cardName" placeholder="JOHN DOE" required>
            </div>`;
        formatCardInputs();
    } else if (val === 'bank') {
        dynamicInputs.innerHTML = `
            <div class="input-group">
                <label for="bankName">Bank Name *</label>
                <input type="text" id="bankName" placeholder="Enter your bank name" required>
            </div>
            <div class="input-group">
                <label for="accountNumber">Account Number *</label>
                <input type="text" id="accountNumber" placeholder="Enter your account number" required>
            </div>`;
    } else if (val === 'qr') {
        dynamicInputs.innerHTML = `
            <div class="input-group">
                <label for="upiId">UPI ID *</label>
                <div class="input-wrapper">
                    <i class="fas fa-mobile"></i>
                    <input type="text" id="upiId" placeholder="yourname@paytm" required>
                </div>
            </div>
            <p style="color: #6b7280; font-size: 0.9rem; margin-top: 1rem;">
                <i class="fas fa-info-circle"></i> Scan the QR code from your UPI app or enter your UPI ID
            </p>`;
    } else if (val === 'gpay') {
        dynamicInputs.innerHTML = `
            <div class="input-group">
                <label for="gpayEmail">Google Pay Email *</label>
                <div class="input-wrapper">
                    <i class="fab fa-google"></i>
                    <input type="email" id="gpayEmail" placeholder="your.email@gmail.com" required>
                </div>
            </div>
            <p style="color: #6b7280; font-size: 0.9rem; margin-top: 1rem;">
                <i class="fas fa-info-circle"></i> A payment link will be sent to your registered phone number
            </p>`;
    } else if (val === 'cod') {
        dynamicInputs.innerHTML = `
            <div style="background: linear-gradient(135deg, rgba(23, 162, 184, 0.1), rgba(91, 108, 246, 0.08)); padding: 1.5rem; border-radius: 10px; border-left: 4px solid #17a2b8;">
                <p style="color: #1f2937; font-weight: 500; margin: 0;">
                    <i class="fas fa-check-circle" style="color: #17a2b8; margin-right: 0.5rem;"></i>
                    Pay with cash when your order is delivered
                </p>
                <p style="color: #6b7280; font-size: 0.9rem; margin: 0.75rem 0 0 0;">
                    No online payment required. Our delivery partner will collect payment at your doorstep.
                </p>
            </div>`;
    }
}

function formatCardInputs() {
    const cardNumberInput = document.getElementById('cardNumber');
    const expiryInput = document.getElementById('expiry');
    const cvvInput = document.getElementById('cvv');

    if (cardNumberInput) {
        cardNumberInput.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\s/g, '');
            let formattedValue = value.replace(/(\d{4})/g, '$1 ').trim();
            e.target.value = formattedValue;
        });
    }

    if (expiryInput) {
        expiryInput.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            if (value.length >= 2) {
                value = value.substring(0, 2) + '/' + value.substring(2, 4);
            }
            e.target.value = value;
        });
    }

    if (cvvInput) {
        cvvInput.addEventListener('input', function(e) {
            e.target.value = e.target.value.replace(/\D/g, '').substring(0, 4);
        });
    }
}

function applyPromo() {
    const promoCode = document.getElementById('promoCode').value.toUpperCase();
    const promoCodes = {
        'SAVE15': 15,
        'WELCOME20': 20,
        'NEWUSER10': 10,
        'SUMMER25': 25
    };

    if (promoCodes[promoCode]) {
        const discount = promoCodes[promoCode];
        alert(`Promo code "${promoCode}" applied! ${discount}% discount added.`);
        // Here you would update the price breakdown
    } else {
        alert('Invalid promo code. Try: SAVE15, WELCOME20, NEWUSER10, or SUMMER25');
    }
}

document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('paymentForm');
    const payButton = document.getElementById('payButton');
    const continueShoppingBtn = document.getElementById('continueShoppingBtn');

    // Initialize card inputs
    formatCardInputs();

    if (form && payButton) {
        form.addEventListener('submit', function(e) {
            // Get selected payment method
            const payMethod = document.querySelector('input[name="payMethod"]:checked').value;
            const amount = document.getElementById('amount').value;
            const email = document.getElementById('email').value;
            const orderId = document.getElementById('orderId').value;

            // Validate form
            if (!validateForm(payMethod)) {
                e.preventDefault();
                return false;
            }

            // Show processing state
            const originalText = payButton.innerHTML;
            payButton.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Processing Payment...';
            payButton.disabled = true;

            // Update modal with current values
            document.getElementById('confirmOrderId').textContent = '#ORD-' + orderId;
            document.getElementById('confirmAmount').textContent = '₹' + parseFloat(amount).toLocaleString('en-IN');
        });
    }

    // Handle Continue Shopping button
    if (continueShoppingBtn) {
        continueShoppingBtn.addEventListener('click', function() {
            window.location.href = '/payment/checkout';
        });
    }

    // Add real-time validation
    const amountInput = document.getElementById('amount');
    if (amountInput) {
        amountInput.addEventListener('input', function(e) {
            const value = parseFloat(e.target.value);
            if (isNaN(value) || value <= 0) {
                e.target.style.borderColor = '#ef4444';
            } else {
                e.target.style.borderColor = '#10b981';
            }
        });
    }
});

window.addEventListener('load', function() {
    // Check if success parameter is in URL or session
    const urlParams = new URLSearchParams(window.location.search);
    const pageSource = document.documentElement.outerHTML;

    // Check if success modal should be shown based on Thymeleaf attribute
    if (pageSource.includes('paymentSuccess') || document.body.getAttribute('data-payment-success') === 'true') {
        const successModal = document.getElementById('successModal');
        if (successModal) {
            successModal.classList.remove('hidden');
        }
    }
});

function validateForm(payMethod) {
    const email = document.getElementById('email').value;
    const amount = document.getElementById('amount').value;
    const orderId = document.getElementById('orderId').value;

    // Basic validation
    if (!email || !email.includes('@')) {
        showErrorAlert('Please enter a valid email address');
        return false;
    }

    if (!amount || parseFloat(amount) <= 0) {
        showErrorAlert('Please enter a valid payment amount');
        return false;
    }

    if (!orderId) {
        showErrorAlert('Please enter an Order ID');
        return false;
    }

    // Payment method specific validation
    if (payMethod === 'card') {
        const cardNumber = document.getElementById('cardNumber');
        const expiry = document.getElementById('expiry');
        const cvv = document.getElementById('cvv');

        if (!cardNumber || cardNumber.value.replace(/\s/g, '').length !== 16) {
            showErrorAlert('Please enter a valid 16-digit card number');
            return false;
        }

        if (!expiry || !expiry.value.match(/^\d{2}\/\d{2}$/)) {
            showErrorAlert('Please enter expiry date in MM/YY format');
            return false;
        }

        if (!cvv || cvv.value.length !== 3) {
            showErrorAlert('Please enter a valid 3-digit CVV');
            return false;
        }
    } else if (payMethod === 'bank') {
        const bankName = document.getElementById('bankName');
        if (!bankName || !bankName.value.trim()) {
            showErrorAlert('Please enter your bank name');
            return false;
        }
    } else if (payMethod === 'gpay' || payMethod === 'qr') {
        const upiOrGpay = payMethod === 'qr' ?
            document.getElementById('upiId') :
            document.getElementById('gpayEmail');
        if (!upiOrGpay || !upiOrGpay.value.trim()) {
            showErrorAlert(`Please enter your ${payMethod === 'qr' ? 'UPI ID' : 'Google Pay email'}`);
            return false;
        }
    }

    return true;
}

function showErrorAlert(message) {
    // Create a simple error notification
    const alert = document.createElement('div');
    alert.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: linear-gradient(135deg, #e74c3c, #c0392b);
        color: #ffffff;
        padding: 1.1rem 1.6rem;
        border-radius: 10px;
        box-shadow: 0 12px 24px -4px rgba(0, 0, 0, 0.15);
        z-index: 2000;
        animation: slideInRight 0.3s ease-out;
        font-weight: 600;
        border-left: 4px solid #e74c3c;
        font-size: 0.95rem;
        letter-spacing: 0.3px;
    `;
    alert.innerHTML = `<i class="fas fa-exclamation-circle"></i> ${message}`;
    document.body.appendChild(alert);

    // Remove after 4 seconds
    setTimeout(() => {
        alert.style.animation = 'slideOutRight 0.3s ease-out';
        setTimeout(() => alert.remove(), 300);
    }, 4000);
}

const style = document.createElement('style');
style.textContent = `
    @keyframes slideInRight {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }

    @keyframes slideOutRight {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(100%);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);
