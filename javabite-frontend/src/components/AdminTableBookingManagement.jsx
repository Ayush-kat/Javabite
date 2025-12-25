import React, { useState, useEffect } from 'react';
import { adminApi } from '../api/api';
import '../styles/AdminTableBookingManagement.css';

const AdminTableBookingManagement = () => {
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [activeTab, setActiveTab] = useState('all');

    useEffect(() => {
        fetchBookings();
        const interval = setInterval(fetchBookings, 30000);
        return () => clearInterval(interval);
    }, []);

    const fetchBookings = async () => {
        console.log('🔄 Starting to fetch bookings...');

        try {
            setLoading(true);
            setError('');

            // Check if adminApi exists
            if (!adminApi) {
                throw new Error('adminApi is undefined');
            }

            console.log('📡 Calling adminApi.getAllBookings()...');
            const data = await adminApi.getAllBookings();

            console.log('✅ Raw data received:', data);
            console.log('📊 Data type:', typeof data);
            console.log('📦 Is array?', Array.isArray(data));

            if (data) {
                console.log('📏 Data length:', data.length);
                if (data.length > 0) {
                    console.log('📝 First booking:', data[0]);
                }
            }

            const bookingsArray = Array.isArray(data) ? data : [];
            console.log('✅ Final bookings array:', bookingsArray);

            setBookings(bookingsArray);

            if (bookingsArray.length === 0) {
                console.log('ℹ️ No bookings found (array is empty)');
            } else {
                console.log(`✅ Successfully loaded ${bookingsArray.length} bookings`);
            }

        } catch (err) {
            console.error('❌ Error fetching bookings:', err);
            console.error('❌ Error name:', err.name);
            console.error('❌ Error message:', err.message);
            console.error('❌ Error stack:', err.stack);

            setError(`Failed to load bookings: ${err.message}`);
            setBookings([]);
        } finally {
            setLoading(false);
            console.log('🏁 Fetch bookings completed');
        }
    };

    const handleUpdateStatus = async (bookingId, newStatus) => {
        try {
            setError('');
            await adminApi.updateBookingStatus(bookingId, newStatus);
            setSuccess(`Booking #${bookingId} status updated to ${newStatus}`);
            await fetchBookings();
            setTimeout(() => setSuccess(''), 3000);
        } catch (err) {
            console.error('Failed to update booking status:', err);
            setError(`Failed to update status: ${err.message}`);
        }
    };

    const handleCancelBooking = async (bookingId) => {
        if (!window.confirm('Are you sure you want to cancel this booking?')) return;

        try {
            setError('');
            await adminApi.cancelBookingAdmin(bookingId);
            setSuccess(`Booking #${bookingId} cancelled successfully`);
            await fetchBookings();
            setTimeout(() => setSuccess(''), 3000);
        } catch (err) {
            console.error('Failed to cancel booking:', err);
            setError(`Failed to cancel booking: ${err.message}`);
        }
    };

    const getFilteredBookings = () => {
        switch (activeTab) {
            case 'confirmed':
                return bookings.filter(b => b.status === 'CONFIRMED');
            case 'active':
                return bookings.filter(b => b.status === 'ACTIVE');
            case 'completed':
                return bookings.filter(b => b.status === 'COMPLETED');
            case 'cancelled':
                return bookings.filter(b => b.status === 'CANCELLED');
            default:
                return bookings;
        }
    };

    const filteredBookings = getFilteredBookings();

    const getStatusColor = (status) => {
        switch (status) {
            case 'CONFIRMED':
                return { bg: '#e8f5e9', color: '#2e7d32', border: '#4caf50' };
            case 'ACTIVE':
                return { bg: '#e3f2fd', color: '#1565c0', border: '#2196f3' };
            case 'COMPLETED':
                return { bg: '#f3e5f5', color: '#6a1b9a', border: '#9c27b0' };
            case 'CANCELLED':
                return { bg: '#ffebee', color: '#c62828', border: '#f44336' };
            default:
                return { bg: '#f5f5f5', color: '#666', border: '#ccc' };
        }
    };

    if (loading) {
        return (
            <div className="booking-management">
                <div className="loading-container">
                    <div className="loading-spinner"></div>
                    <p>Loading bookings...</p>
                    <small style={{ color: '#999', marginTop: '10px' }}>
                        Check browser console for detailed logs
                    </small>
                </div>
            </div>
        );
    }

    return (
        <div className="booking-management">
            {/* Header */}
            <div className="booking-header">
                <div>
                    <h2>Table Booking Management</h2>
                    <p className="subtitle">Manage table bookings and availability</p>
                    <small style={{ color: '#999', display: 'block', marginTop: '8px' }}>
                        Debug: Loaded {bookings.length} bookings
                    </small>
                </div>
                <button className="refresh-btn" onClick={fetchBookings}>
                    🔄 Refresh
                </button>
            </div>

            {/* Alerts */}
            {error && (
                <div className="alert alert-error">
                    <span>⚠️ {error}</span>
                    <button onClick={() => setError('')} className="alert-close">×</button>
                </div>
            )}

            {success && (
                <div className="alert alert-success">
                    <span>✓ {success}</span>
                    <button onClick={() => setSuccess('')} className="alert-close">×</button>
                </div>
            )}

            {/* Stats Cards */}
            <div className="stats-grid">
                <div className="stat-card">
                    <div className="stat-icon" style={{ background: '#e8f5e9', color: '#2e7d32' }}>📅</div>
                    <div className="stat-content">
                        <div className="stat-value">
                            {bookings.filter(b => b.status === 'CONFIRMED').length}
                        </div>
                        <div className="stat-label">Confirmed</div>
                    </div>
                </div>

                <div className="stat-card">
                    <div className="stat-icon" style={{ background: '#e3f2fd', color: '#1565c0' }}>🍽️</div>
                    <div className="stat-content">
                        <div className="stat-value">
                            {bookings.filter(b => b.status === 'ACTIVE').length}
                        </div>
                        <div className="stat-label">Active</div>
                    </div>
                </div>

                <div className="stat-card">
                    <div className="stat-icon" style={{ background: '#f3e5f5', color: '#6a1b9a' }}>✓</div>
                    <div className="stat-content">
                        <div className="stat-value">
                            {bookings.filter(b => b.status === 'COMPLETED').length}
                        </div>
                        <div className="stat-label">Completed</div>
                    </div>
                </div>

                <div className="stat-card">
                    <div className="stat-icon" style={{ background: '#fff3e0', color: '#e65100' }}>📊</div>
                    <div className="stat-content">
                        <div className="stat-value">{bookings.length}</div>
                        <div className="stat-label">Total</div>
                    </div>
                </div>
            </div>

            {/* Tabs */}
            <div className="tabs">
                {[
                    { id: 'all', label: 'All Bookings', count: bookings.length },
                    { id: 'confirmed', label: 'Confirmed', count: bookings.filter(b => b.status === 'CONFIRMED').length },
                    { id: 'active', label: 'Active', count: bookings.filter(b => b.status === 'ACTIVE').length },
                    { id: 'completed', label: 'Completed', count: bookings.filter(b => b.status === 'COMPLETED').length },
                    { id: 'cancelled', label: 'Cancelled', count: bookings.filter(b => b.status === 'CANCELLED').length }
                ].map(tab => (
                    <button
                        key={tab.id}
                        className={`tab ${activeTab === tab.id ? 'active' : ''}`}
                        onClick={() => setActiveTab(tab.id)}
                    >
                        {tab.label} ({tab.count})
                    </button>
                ))}
            </div>

            {/* Bookings List */}
            <div className="bookings-container">
                {filteredBookings.length === 0 ? (
                    <div className="empty-state">
                        <div className="empty-icon">📅</div>
                        <h3>No Bookings Found</h3>
                        <p>
                            {activeTab === 'all'
                                ? 'No bookings available at the moment'
                                : `No ${activeTab} bookings found`}
                        </p>
                        <small style={{ color: '#999', marginTop: '16px', display: 'block' }}>
                            Total bookings in database: {bookings.length}
                        </small>
                    </div>
                ) : (
                    <div className="bookings-grid">
                        {filteredBookings.map(booking => {
                            const statusStyle = getStatusColor(booking.status);
                            const bookingDate = new Date(booking.bookingDate);
                            const isToday = bookingDate.toDateString() === new Date().toDateString();

                            return (
                                <div key={booking.id} className="booking-card">
                                    <div
                                        className="status-badge"
                                        style={{
                                            background: statusStyle.bg,
                                            color: statusStyle.color,
                                            borderColor: statusStyle.border
                                        }}
                                    >
                                        {booking.status}
                                    </div>

                                    <div className="booking-header-info">
                                        <div className="booking-id">Booking #{booking.id}</div>
                                        {isToday && (
                                            <span className="today-badge">Today</span>
                                        )}
                                    </div>

                                    <div className="booking-info">
                                        <div className="info-row">
                                            <span className="info-icon">👤</span>
                                            <span className="info-label">Customer:</span>
                                            <span className="info-value">{booking.customer?.name || 'N/A'}</span>
                                        </div>

                                        <div className="info-row">
                                            <span className="info-icon">📧</span>
                                            <span className="info-label">Email:</span>
                                            <span className="info-value">{booking.customer?.email || 'N/A'}</span>
                                        </div>

                                        <div className="info-row">
                                            <span className="info-icon">🪑</span>
                                            <span className="info-label">Table:</span>
                                            <span className="info-value">
                                                Table {booking.tableNumber || 'Not assigned'}
                                            </span>
                                        </div>

                                        <div className="info-row">
                                            <span className="info-icon">📅</span>
                                            <span className="info-label">Date:</span>
                                            <span className="info-value">
                                                {bookingDate.toLocaleDateString('en-US', {
                                                    weekday: 'short',
                                                    month: 'short',
                                                    day: 'numeric',
                                                    year: 'numeric'
                                                })}
                                            </span>
                                        </div>

                                        <div className="info-row">
                                            <span className="info-icon">🕐</span>
                                            <span className="info-label">Time:</span>
                                            <span className="info-value">{booking.bookingTime}</span>
                                        </div>

                                        <div className="info-row">
                                            <span className="info-icon">👥</span>
                                            <span className="info-label">Guests:</span>
                                            <span className="info-value">{booking.numberOfGuests} people</span>
                                        </div>
                                    </div>

                                    {booking.specialRequests && (
                                        <div className="special-requests">
                                            <span className="requests-icon">📝</span>
                                            <span className="requests-text">{booking.specialRequests}</span>
                                        </div>
                                    )}

                                    <div className="booking-actions">
                                        {booking.status === 'CONFIRMED' && (
                                            <button
                                                className="action-btn btn-primary"
                                                onClick={() => handleUpdateStatus(booking.id, 'ACTIVE')}
                                            >
                                                ✓ Mark Active
                                            </button>
                                        )}

                                        {booking.status === 'ACTIVE' && (
                                            <button
                                                className="action-btn btn-success"
                                                onClick={() => handleUpdateStatus(booking.id, 'COMPLETED')}
                                            >
                                                ✓ Mark Completed
                                            </button>
                                        )}

                                        {(booking.status === 'CONFIRMED' || booking.status === 'ACTIVE') && (
                                            <button
                                                className="action-btn btn-danger"
                                                onClick={() => handleCancelBooking(booking.id)}
                                            >
                                                ✗ Cancel
                                            </button>
                                        )}

                                        {booking.status === 'COMPLETED' && (
                                            <div className="completed-message">
                                                <span className="completed-icon">✓</span>
                                                Booking completed
                                            </div>
                                        )}

                                        {booking.status === 'CANCELLED' && (
                                            <div className="cancelled-message">
                                                <span className="cancelled-icon">✗</span>
                                                Booking cancelled
                                            </div>
                                        )}
                                    </div>

                                    <div className="booking-footer">
                                        <span className="created-date">
                                            Created: {new Date(booking.createdAt).toLocaleDateString()}
                                        </span>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                )}
            </div>
        </div>
    );
};

export default AdminTableBookingManagement;