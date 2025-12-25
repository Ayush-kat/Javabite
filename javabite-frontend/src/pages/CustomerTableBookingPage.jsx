import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { bookingApi } from '../api/api.js';

const CustomerTableBookingPage = () => {
    const navigate = useNavigate();
    const { user, isAuthenticated } = useAuth();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [availableTables, setAvailableTables] = useState([]);
    const [selectedTable, setSelectedTable] = useState(null);
    const [showBookingForm, setShowBookingForm] = useState(false);

    const TOTAL_TABLES = 6;

    const [bookingForm, setBookingForm] = useState({
        bookingDate: '',
        bookingTime: '12:00',
        numberOfGuests:'1'
    });

    useEffect(() => {
        if (!isAuthenticated) {
            alert('Please login to book a table');
            navigate('/login');
        }
    }, [isAuthenticated, navigate]);

    useEffect(() => {
        if (bookingForm.bookingDate && bookingForm.bookingTime) {
            fetchAvailableTables();
        }
    }, [bookingForm.bookingDate, bookingForm.bookingTime]);

    const fetchAvailableTables = async () => {
        if (!bookingForm.bookingDate || !bookingForm.bookingTime) {
            // Show all as available if date/time not selected
            const tableStatuses = [];
            for (let i = 1; i <= TOTAL_TABLES; i++) {
                tableStatuses.push({
                    tableNumber: i,
                    status: 'AVAILABLE'
                });
            }
            setAvailableTables(tableStatuses);
            return;
        }

        try {
            setLoading(true);
            // ✅ FIX: Call with both date AND time
            const available = await bookingApi.checkAvailability(
                bookingForm.bookingDate,
                bookingForm.bookingTime
            );

            // Generate table statuses
            const tableStatuses = [];
            for (let i = 1; i <= TOTAL_TABLES; i++) {
                const isAvailable = available.includes(i);
                tableStatuses.push({
                    tableNumber: i,
                    status: isAvailable ? 'AVAILABLE' : 'BOOKED'
                });
            }
            setAvailableTables(tableStatuses);
        } catch (err) {
            console.error('Failed to check availability:', err);
            setError('Failed to check availability');
        } finally {
            setLoading(false);
        }
    };


    const handleFormChange = (e) => {
        setBookingForm({
            ...bookingForm,
            [e.target.name]: e.target.value
        });
    };

    const handleTableSelect = (table) => {
        if (table.status === 'AVAILABLE') {
            setSelectedTable(table.tableNumber);
            setShowBookingForm(true);
        } else {
            alert('This table is already booked for the selected time slot');
        }
    };

    const handleBookingSubmit = async (e) => {
        e.preventDefault();

        if (!selectedTable) {
            setError('Please select a table');
            return;
        }

        if (!bookingForm.bookingDate || !bookingForm.bookingTime) {
            setError('Please select date and time');
            return;
        }

        setLoading(true);
        setError('');

        try {
            const bookingData = {
                tableNumber: selectedTable,
                bookingDate: bookingForm.bookingDate,
                bookingTime: bookingForm.bookingTime,
                numberOfGuests: parseInt(bookingForm.numberOfGuests)
            };

            await bookingApi.createBooking(bookingData);
            setSuccess('Table booked successfully! You can now place your order.');

            setTimeout(() => {
                navigate('/cart');
            }, 2000);
        } catch (err) {
            console.error('Booking failed:', err);
            setError(err.message || 'Failed to book table. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const getMinDate = () => {
        const today = new Date();
        return today.toISOString().split('T')[0];
    };

    const timeSlots = [
        '11:00', '11:30', '12:00', '12:30', '13:00', '13:30',
        '14:00', '14:30', '15:00', '15:30', '16:00', '16:30',
        '17:00', '17:30', '18:00', '18:30', '19:00', '19:30',
        '20:00', '20:30', '21:00'
    ];

    return (
        <div style={{
            minHeight: '100vh',
            background: 'linear-gradient(135deg, #f5f0e8 0%, #e8dcc8 100%)',
            padding: '40px 20px'
        }}>
            <div style={{
                maxWidth: '1200px',
                margin: '0 auto'
            }}>
                {/* Header */}
                <div style={{
                    textAlign: 'center',
                    marginBottom: '48px'
                }}>
                    <h1 style={{
                        fontSize: '48px',
                        fontWeight: '700',
                        color: '#3e2723',
                        marginBottom: '12px'
                    }}>
                        Book Your Table
                    </h1>
                    <p style={{
                        fontSize: '18px',
                        color: '#6d4c41'
                    }}>
                        Select your preferred table and time
                    </p>
                </div>

                {success && (
                    <div style={{
                        background: '#e8f5e9',
                        border: '2px solid #4caf50',
                        color: '#2e7d32',
                        padding: '16px 24px',
                        borderRadius: '12px',
                        marginBottom: '24px',
                        textAlign: 'center',
                        fontSize: '16px',
                        fontWeight: '600'
                    }}>
                        ✓ {success}
                    </div>
                )}

                {error && (
                    <div style={{
                        background: '#ffebee',
                        border: '2px solid #f44336',
                        color: '#c62828',
                        padding: '16px 24px',
                        borderRadius: '12px',
                        marginBottom: '24px',
                        textAlign: 'center'
                    }}>
                        {error}
                    </div>
                )}

                {/* Date and Time Selection */}
                <div style={{
                    background: 'white',
                    borderRadius: '20px',
                    padding: '32px',
                    marginBottom: '32px',
                    boxShadow: '0 4px 20px rgba(0,0,0,0.1)'
                }}>
                    <h2 style={{
                        fontSize: '24px',
                        fontWeight: '700',
                        color: '#3e2723',
                        marginBottom: '24px'
                    }}>
                        Select Date & Time
                    </h2>
                    <div style={{
                        display: 'grid',
                        gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
                        gap: '20px'
                    }}>
                        <div>
                            <label style={{
                                display: 'block',
                                fontSize: '14px',
                                fontWeight: '600',
                                color: '#6d4c41',
                                marginBottom: '8px'
                            }}>
                                Date *
                            </label>
                            <input
                                type="date"
                                name="bookingDate"
                                value={bookingForm.bookingDate}
                                onChange={handleFormChange}
                                min={getMinDate()}
                                required
                                style={{
                                    width: '100%',
                                    padding: '14px',
                                    borderRadius: '12px',
                                    border: '2px solid #d7ccc8',
                                    fontSize: '16px',
                                    fontFamily: 'inherit'
                                }}
                            />
                        </div>
                        <div>
                            <label style={{
                                display: 'block',
                                fontSize: '14px',
                                fontWeight: '600',
                                color: '#6d4c41',
                                marginBottom: '8px'
                            }}>
                                Time *
                            </label>
                            <select
                                name="bookingTime"
                                value={bookingForm.bookingTime}
                                onChange={handleFormChange}
                                required
                                style={{
                                    width: '100%',
                                    padding: '14px',
                                    borderRadius: '12px',
                                    border: '2px solid #d7ccc8',
                                    fontSize: '16px',
                                    fontFamily: 'inherit'
                                }}
                            >
                                {timeSlots.map(time => (
                                    <option key={time} value={time}>{time}</option>
                                ))}
                            </select>
                        </div>
                        <div>
                            <label style={{
                                display: 'block',
                                fontSize: '14px',
                                fontWeight: '600',
                                color: '#6d4c41',
                                marginBottom: '8px'
                            }}>
                                Number of Guests *
                            </label>
                            <input
                                type="number"
                                name="numberOfGuests"
                                value={bookingForm.numberOfGuests}
                                onChange={handleFormChange}
                                min="1"
                                max="8"
                                required
                                style={{
                                    width: '100%',
                                    padding: '14px',
                                    borderRadius: '12px',
                                    border: '2px solid #d7ccc8',
                                    fontSize: '16px',
                                    fontFamily: 'inherit'
                                }}
                            />
                        </div>
                    </div>
                </div>

                {/* Table Grid */}
                <div style={{
                    background: 'white',
                    borderRadius: '20px',
                    padding: '32px',
                    boxShadow: '0 4px 20px rgba(0,0,0,0.1)'
                }}>
                    <h2 style={{
                        fontSize: '24px',
                        fontWeight: '700',
                        color: '#3e2723',
                        marginBottom: '24px'
                    }}>
                        Available Tables
                    </h2>

                    {loading ? (
                        <div style={{
                            display: 'flex',
                            justifyContent: 'center',
                            alignItems: 'center',
                            minHeight: '300px'
                        }}>
                            <div style={{
                                width: '50px',
                                height: '50px',
                                border: '4px solid #d7ccc8',
                                borderTopColor: '#8b6f47',
                                borderRadius: '50%',
                                animation: 'spin 1s linear infinite'
                            }}></div>
                        </div>
                    ) : (
                        <div style={{
                            display: 'grid',
                            gridTemplateColumns: 'repeat(auto-fill, minmax(180px, 1fr))',
                            gap: '20px'
                        }}>
                            {availableTables.map((table) => (
                                <div
                                    key={table.tableNumber}
                                    onClick={() => handleTableSelect(table)}
                                    style={{
                                        background: table.status === 'AVAILABLE'
                                            ? (selectedTable === table.tableNumber ? '#8b6f47' : '#e8f5e9')
                                            : '#ffebee',
                                        borderRadius: '16px',
                                        padding: '32px 24px',
                                        textAlign: 'center',
                                        cursor: table.status === 'AVAILABLE' ? 'pointer' : 'not-allowed',
                                        border: `3px solid ${
                                            selectedTable === table.tableNumber
                                                ? '#6d5739'
                                                : table.status === 'AVAILABLE'
                                                    ? '#4caf50'
                                                    : '#f44336'
                                        }`,
                                        transition: 'all 0.2s ease',
                                        opacity: table.status === 'BOOKED' ? 0.6 : 1
                                    }}
                                    onMouseEnter={(e) => {
                                        if (table.status === 'AVAILABLE') {
                                            e.currentTarget.style.transform = 'translateY(-4px)';
                                            e.currentTarget.style.boxShadow = '0 8px 20px rgba(0,0,0,0.15)';
                                        }
                                    }}
                                    onMouseLeave={(e) => {
                                        e.currentTarget.style.transform = 'translateY(0)';
                                        e.currentTarget.style.boxShadow = 'none';
                                    }}
                                >
                                    <div style={{
                                        fontSize: '48px',
                                        marginBottom: '12px'
                                    }}>
                                        {table.status === 'AVAILABLE'
                                            ? (selectedTable === table.tableNumber ? '✓' : '🪑')
                                            : '🔒'}
                                    </div>
                                    <h3 style={{
                                        fontSize: '24px',
                                        fontWeight: '700',
                                        color: selectedTable === table.tableNumber ? 'white' : '#3e2723',
                                        marginBottom: '8px'
                                    }}>
                                        Table {table.tableNumber}
                                    </h3>
                                    <div style={{
                                        fontSize: '14px',
                                        fontWeight: '600',
                                        color: selectedTable === table.tableNumber
                                            ? 'white'
                                            : table.status === 'AVAILABLE'
                                                ? '#2e7d32'
                                                : '#c62828'
                                    }}>
                                        {selectedTable === table.tableNumber ? 'SELECTED' : table.status}
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}

                    {/* Book Button */}
                    {selectedTable && (
                        <div style={{ marginTop: '32px', textAlign: 'center' }}>
                            <button
                                onClick={handleBookingSubmit}
                                disabled={loading}
                                style={{
                                    padding: '16px 48px',
                                    background: loading ? '#d7ccc8' : '#8b6f47',
                                    color: 'white',
                                    border: 'none',
                                    borderRadius: '30px',
                                    fontSize: '18px',
                                    fontWeight: '700',
                                    cursor: loading ? 'not-allowed' : 'pointer',
                                    transition: 'all 0.3s ease',
                                    boxShadow: '0 4px 15px rgba(139, 111, 71, 0.3)'
                                }}
                                onMouseEnter={(e) => {
                                    if (!loading) {
                                        e.target.style.transform = 'translateY(-2px)';
                                        e.target.style.boxShadow = '0 6px 20px rgba(139, 111, 71, 0.4)';
                                    }
                                }}
                                onMouseLeave={(e) => {
                                    e.target.style.transform = 'translateY(0)';
                                    e.target.style.boxShadow = '0 4px 15px rgba(139, 111, 71, 0.3)';
                                }}
                            >
                                {loading ? 'Booking...' : `Book Table ${selectedTable}`}
                            </button>
                        </div>
                    )}
                </div>

                {/* Legend */}
                <div style={{
                    marginTop: '32px',
                    display: 'flex',
                    justifyContent: 'center',
                    gap: '32px',
                    flexWrap: 'wrap'
                }}>
                </div>
            </div>

            <style>{`
                @keyframes spin {
                    to { transform: rotate(360deg); }
                }
            `}</style>
        </div>
    );
};

export default CustomerTableBookingPage;