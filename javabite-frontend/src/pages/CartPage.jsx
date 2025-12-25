import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/Cart.css';
import BookingStatusBanner from '../components/BookingStatusBanner';

const CartPage = ({ cart = [], setCart }) => {
    const navigate = useNavigate();
    const [couponCode, setCouponCode] = useState('');
    const [discount, setDiscount] = useState(0);
    const [appliedCoupon, setAppliedCoupon] = useState('');

    // Group items by ID to show quantity
    const groupedCart = cart.reduce((acc, item) => {
        const existingItem = acc.find(i => i.id === item.id);
        if (existingItem) {
            existingItem.quantity += 1;
        } else {
            acc.push({ ...item, quantity: 1 });
        }
        return acc;
    }, []);

    const updateQuantity = (itemId, change) => {
        const currentItem = groupedCart.find(item => item.id === itemId);
        if (!currentItem) return;

        const newQuantity = currentItem.quantity + change;

        if (newQuantity <= 0) {
            // Remove all instances of this item
            setCart(cart.filter(item => item.id !== itemId));
        } else {
            // Adjust the cart
            const newCart = [...cart];
            if (change > 0) {
                // Add one more
                newCart.push(currentItem);
            } else {
                // Remove one
                const indexToRemove = newCart.findIndex(item => item.id === itemId);
                if (indexToRemove !== -1) {
                    newCart.splice(indexToRemove, 1);
                }
            }
            setCart(newCart);
        }
    };

    const removeItem = (itemId) => {
        setCart(cart.filter(item => item.id !== itemId));
    };

    const applyCoupon = () => {
        const validCoupons = {
            'COFFEE10': 10,
            'WELCOME20': 20,
            'SAVE15': 15
        };

        const upperCoupon = couponCode.toUpperCase();
        if (validCoupons[upperCoupon]) {
            setDiscount(validCoupons[upperCoupon]);
            setAppliedCoupon(upperCoupon);
            alert(`Coupon applied! ${validCoupons[upperCoupon]}% off`);
        } else {
            alert('Invalid coupon code');
            setDiscount(0);
            setAppliedCoupon('');
        }
    };

    const removeCoupon = () => {
        setDiscount(0);
        setAppliedCoupon('');
        setCouponCode('');
    };

    const getSubtotal = () => {
        return cart.reduce((total, item) => total + item.price, 0);
    };

    const getTax = () => {
        return getSubtotal() * 0.08; // 8% tax
    };

    const getDiscountAmount = () => {
        return getSubtotal() * (discount / 100);
    };

    const getTotal = () => {
        return getSubtotal() + getTax() - getDiscountAmount();
    };

    const handleCheckout = () => {
        if (cart.length === 0) {
            alert('Your cart is empty!');
            return;
        }
        // Navigate to checkout/payment page
        navigate('/checkout');
    };

    if (cart.length === 0) {
        return (
            <div className="cart-page empty-cart">
                <div className="empty-cart-content">
                    <div className="empty-cart-icon">🛒</div>
                    <h2>Your Cart is Empty</h2>
                    <p>Add some delicious items to get started!</p>
                    <button className="continue-shopping-btn" onClick={() => navigate('/menu')}>
                        Browse Menu
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="cart-page">
            {/* Booking status banner (must be at top) */}
            <BookingStatusBanner />
            <div className="cart-container">
                {/* Cart Items Section */}
                <div className="cart-items-section">
                    <div className="cart-header">
                        <h1>Your Cart</h1>
                        <p className="cart-subtitle">
                            {new Date().toLocaleDateString('en-US', {
                                weekday: 'long',
                                year: 'numeric',
                                month: 'short',
                                day: 'numeric',
                                hour: '2-digit',
                                minute: '2-digit'
                            })}
                        </p>
                    </div>

                    <div className="cart-items-list">
                        {groupedCart.map((item) => (
                            <div key={item.id} className="cart-item">
                                <img
                                    src={item.image}
                                    alt={item.name}
                                    className="cart-item-image"
                                />
                                <div className="cart-item-details">
                                    <h3 className="cart-item-name">{item.name}</h3>
                                    <p className="cart-item-description">{item.description}</p>
                                    <div className="cart-item-footer">
                                        <div className="quantity-controls">
                                            <button
                                                className="quantity-btn"
                                                onClick={() => updateQuantity(item.id, -1)}
                                            >
                                                −
                                            </button>
                                            <span className="quantity-display">{item.quantity}x</span>
                                            <button
                                                className="quantity-btn"
                                                onClick={() => updateQuantity(item.id, 1)}
                                            >
                                                +
                                            </button>
                                        </div>
                                        <div className="cart-item-prices">
                                            <span className="item-unit-price">${item.price.toFixed(2)}</span>
                                            <span className="item-total-price">
                                                ${(item.price * item.quantity).toFixed(2)}
                                            </span>
                                        </div>
                                    </div>
                                </div>
                                <button
                                    className="remove-item-btn"
                                    onClick={() => removeItem(item.id)}
                                    title="Remove item"
                                >
                                    ×
                                </button>
                            </div>
                        ))}
                    </div>

                    <button
                        className="continue-shopping-btn secondary"
                        onClick={() => navigate('/menu')}
                    >
                        ← Continue Shopping
                    </button>
                </div>

                {/* Order Summary Section */}
                <div className="order-summary-section">
                    <div className="order-summary-card">
                        <h2>Order Summary</h2>

                        {/* Coupon Section */}
                        <div className="coupon-section">
                            <label>Have a coupon code?</label>
                            <div className="coupon-input-group">
                                <input
                                    type="text"
                                    value={couponCode}
                                    onChange={(e) => setCouponCode(e.target.value)}
                                    placeholder="Enter code"
                                    className="coupon-input"
                                    disabled={appliedCoupon !== ''}
                                />
                                {appliedCoupon ? (
                                    <button
                                        className="coupon-btn remove"
                                        onClick={removeCoupon}
                                    >
                                        Remove
                                    </button>
                                ) : (
                                    <button
                                        className="coupon-btn"
                                        onClick={applyCoupon}
                                    >
                                        Apply
                                    </button>
                                )}
                            </div>
                            {appliedCoupon && (
                                <div className="coupon-applied">
                                    ✓ Coupon "{appliedCoupon}" applied!
                                </div>
                            )}
                        </div>

                        {/* Price Breakdown */}
                        <div className="price-breakdown">
                            <div className="price-row">
                                <span>Subtotal</span>
                                <span>${getSubtotal().toFixed(2)}</span>
                            </div>
                            <div className="price-row">
                                <span>Tax & Fees (8%)</span>
                                <span>${getTax().toFixed(2)}</span>
                            </div>
                            {discount > 0 && (
                                <div className="price-row discount">
                                    <span>Discount ({discount}%)</span>
                                    <span>-${getDiscountAmount().toFixed(2)}</span>
                                </div>
                            )}
                            <div className="price-row total">
                                <span>Total</span>
                                <span>${getTotal().toFixed(2)}</span>
                            </div>
                        </div>

                        {/* Checkout Button */}
                        <button
                            className="checkout-btn"
                            onClick={handleCheckout}
                        >
                            Proceed to Checkout
                        </button>

                        {/* Available Coupons Info */}
                        <div className="coupon-info">
                            <p className="coupon-info-title">Available Coupons:</p>
                            <ul className="coupon-list">
                                <li><strong>COFFEE10</strong> - 10% off</li>
                                <li><strong>WELCOME20</strong> - 20% off</li>
                                <li><strong>SAVE15</strong> - 15% off</li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default CartPage;