import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { bookingApi } from '../api/api';

const BookingStatusBanner = () => {
    const navigate = useNavigate();
    const { isAuthenticated } = useAuth();
    const [activeBooking, setActiveBooking] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        checkBooking();
    }, [isAuthenticated]);

    const checkBooking = async () => {
        if (!isAuthenticated) {
            setLoading(false);
            return;
        }

        try {
            const bookings = await bookingApi.getMyBookings();
            const now = new Date();
            const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());

            const active = bookings.find(b => {
                const bookingDate = new Date(b.bookingDate);
                return (b.status === 'CONFIRMED' || b.status === 'ACTIVE') && bookingDate >= today;
            });

            setActiveBooking(active);
        } catch (err) {
            console.error('Booking check failed:', err);
        } finally {
            setLoading(false);
        }
    };

    if (loading || !isAuthenticated) return null;

    if (!activeBooking) {
        return (
            <div style={{
                background: '#fff3e0',
                padding: '16px 24px',
                borderRadius: '12px',
                border: '2px solid #ff9800',
                marginBottom: '24px',
                display: 'flex',
                alignItems: 'center',
                gap: '16px'
            }}>
                <span style={{ fontSize: '28px' }}>📅</span>
                <div style={{ flex: 1 }}>
                    <strong style={{ color: '#e65100' }}>Book a table to start ordering</strong>
                    <p style={{ margin: '4px 0 0 0', color: '#f57c00', fontSize: '14px' }}>
                        Table booking required for all orders
                    </p>
                </div>
                <button
                    onClick={() => navigate('/book-table')}
                    style={{
                        padding: '10px 24px',
                        background: '#ff9800',
                        color: 'white',
                        border: 'none',
                        borderRadius: '8px',
                        fontWeight: '600',
                        cursor: 'pointer'
                    }}
                >
                    Book Now
                </button>
            </div>
        );
    }

    return (
        <div style={{
            background: '#e8f5e9',
            padding: '16px 24px',
            borderRadius: '12px',
            border: '2px solid #4caf50',
            marginBottom: '24px',
            display: 'flex',
            alignItems: 'center',
            gap: '16px'
        }}>
            <span style={{ fontSize: '28px' }}>✅</span>
            <div style={{ flex: 1 }}>
                <strong style={{ color: '#1b5e20' }}>Table {activeBooking.tableNumber} Reserved</strong>
                <p style={{ margin: '4px 0 0 0', color: '#2e7d32', fontSize: '14px' }}>
                    {new Date(activeBooking.bookingDate).toLocaleDateString()} at {activeBooking.bookingTime}
                </p>
            </div>
            <button
                onClick={() => navigate('/customer/orders')}
                style={{
                    padding: '10px 24px',
                    background: 'white',
                    color: '#4caf50',
                    border: '2px solid #4caf50',
                    borderRadius: '8px',
                    fontWeight: '600',
                    cursor: 'pointer'
                }}
            >
                View Orders
            </button>
        </div>
    );
};

export default BookingStatusBanner;