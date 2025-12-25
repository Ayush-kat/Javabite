import React, { useState, useEffect } from 'react';
import { waiterApi } from '../api/api';
import '../styles/WaiterDashboard.css';

const WaiterDashboard = () => {
    const [preparingOrders, setPreparingOrders] = useState([]);
    const [readyOrders, setReadyOrders] = useState([]);
    const [activeTab, setActiveTab] = useState('ready'); // Default to ready orders
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchOrders();
        const interval = setInterval(fetchOrders, 5000); // Refresh every 5s
        return () => clearInterval(interval);
    }, []);

    const fetchOrders = async () => {
        try {
            setError('');

            // Fetch preparing orders (PENDING/PREPARING status)
            const preparing = await waiterApi.getPreparingOrders();
            setPreparingOrders(preparing);

            // Fetch ready orders (READY status)
            const ready = await waiterApi.getReadyOrders();
            setReadyOrders(ready);

            setLoading(false);
        } catch (error) {
            console.error('Failed to fetch orders:', error);
            setError('Failed to load orders. Please refresh.');
            setLoading(false);
        }
    };

    const handleServe = async (orderId) => {
        if (!window.confirm('Mark this order as served?')) return;

        try {
            await waiterApi.markAsServed(orderId);
            fetchOrders(); // Refresh orders
            alert('Order marked as served successfully!');
        } catch (error) {
            console.error('Failed to mark order as served:', error);
            alert('Failed to mark order as served. Please try again.');
        }
    };

    if (loading) {
        return (
            <div className="waiter-dashboard">
                <div className="loading-container">
                    <div className="loading-spinner"></div>
                    <p>Loading orders...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="waiter-dashboard">
            {/* Header */}
            <div className="dashboard-header">
                <div className="header-content">
                    <h1>Waiter Dashboard</h1>
                    <p className="header-subtitle">
                        {new Date().toLocaleDateString('en-US', {
                            weekday: 'long',
                            year: 'numeric',
                            month: 'long',
                            day: 'numeric'
                        })}
                    </p>
                </div>
                <button className="refresh-btn" onClick={fetchOrders}>
                    🔄 Refresh
                </button>
            </div>

            {/* Error Message */}
            {error && (
                <div className="error-banner">
                    <span>⚠️ {error}</span>
                    <button onClick={fetchOrders}>Retry</button>
                </div>
            )}

            {/* Stats Cards */}
            <div className="stats-grid">
                <div className="stat-card preparing">
                    <div className="stat-icon">👨‍🍳</div>
                    <div className="stat-content">
                        <h3>{preparingOrders.length}</h3>
                        <p>Being Prepared</p>
                    </div>
                </div>
                <div className="stat-card ready">
                    <div className="stat-icon">✅</div>
                    <div className="stat-content">
                        <h3>{readyOrders.length}</h3>
                        <p>Ready to Serve</p>
                    </div>
                </div>
            </div>

            {/* Tabs */}
            <div className="tabs-container">
                <button
                    className={`tab-btn ${activeTab === 'ready' ? 'active' : ''}`}
                    onClick={() => setActiveTab('ready')}
                >
                    <span className="tab-icon">✅</span>
                    <span className="tab-label">Ready to Serve</span>
                    <span className="tab-count">{readyOrders.length}</span>
                </button>
                <button
                    className={`tab-btn ${activeTab === 'preparing' ? 'active' : ''}`}
                    onClick={() => setActiveTab('preparing')}
                >
                    <span className="tab-icon">⏳</span>
                    <span className="tab-label">Being Prepared</span>
                    <span className="tab-count">{preparingOrders.length}</span>
                </button>
            </div>

            {/* Orders Content */}
            <div className="orders-content">
                {activeTab === 'ready' && (
                    <div className="ready-section">
                        {readyOrders.length === 0 ? (
                            <div className="empty-state">
                                <div className="empty-icon">🍽️</div>
                                <h3>No Orders Ready</h3>
                                <p>Orders ready to serve will appear here</p>
                            </div>
                        ) : (
                            <div className="orders-grid">
                                {readyOrders.map(order => (
                                    <div key={order.id} className="order-card ready-card">
                                        <div className="order-badge ready-badge">
                                            <span className="badge-icon">✅</span>
                                            <span>Ready</span>
                                        </div>

                                        <div className="order-header">
                                            <div className="order-title">
                                                <h3>Order #{order.id}</h3>
                                                <span className="order-time">
                                                    {new Date(order.readyAt).toLocaleTimeString('en-US', {
                                                        hour: '2-digit',
                                                        minute: '2-digit'
                                                    })}
                                                </span>
                                            </div>
                                        </div>

                                        <div className="order-info">
                                            <div className="info-row">
                                                <span className="info-icon">🪑</span>
                                                <span className="info-label">Table:</span>
                                                <span className="info-value">Table {order.tableNumber}</span>
                                            </div>
                                            <div className="info-row">
                                                <span className="info-icon">👤</span>
                                                <span className="info-label">Customer:</span>
                                                <span className="info-value">{order.customerName}</span>
                                            </div>
                                            <div className="info-row">
                                                <span className="info-icon">👨‍🍳</span>
                                                <span className="info-label">Chef:</span>
                                                <span className="info-value">{order.chefName}</span>
                                            </div>
                                        </div>

                                        <div className="order-items">
                                            <h4>Items:</h4>
                                            <ul>
                                                {order.items.map(item => (
                                                    <li key={item.id}>
                                                        <span className="item-quantity">{item.quantity}x</span>
                                                        <span className="item-name">{item.menuItem.name}</span>
                                                        <span className="item-price">
                                                            ${(item.priceAtOrder * item.quantity).toFixed(2)}
                                                        </span>
                                                    </li>
                                                ))}
                                            </ul>
                                        </div>

                                        {order.specialInstructions && (
                                            <div className="special-instructions">
                                                <span className="instructions-icon">📝</span>
                                                <span className="instructions-text">
                                                    {order.specialInstructions}
                                                </span>
                                            </div>
                                        )}

                                        <div className="order-total">
                                            <span>Total:</span>
                                            <span className="total-amount">${order.total.toFixed(2)}</span>
                                        </div>

                                        <button
                                            className="serve-btn"
                                            onClick={() => handleServe(order.id)}
                                        >
                                            <span className="btn-icon">✓</span>
                                            Mark as Served
                                        </button>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                )}

                {activeTab === 'preparing' && (
                    <div className="preparing-section">
                        {preparingOrders.length === 0 ? (
                            <div className="empty-state">
                                <div className="empty-icon">👨‍🍳</div>
                                <h3>No Orders Being Prepared</h3>
                                <p>Orders assigned to you will appear here while the chef is preparing them</p>
                            </div>
                        ) : (
                            <div className="orders-grid">
                                {preparingOrders.map(order => (
                                    <div key={order.id} className="order-card preparing-card">
                                        <div className={`order-badge ${order.status.toLowerCase()}-badge`}>
                                            <span className="badge-icon">
                                                {order.status === 'PENDING' ? '⏳' : '👨‍🍳'}
                                            </span>
                                            <span>
                                                {order.status === 'PENDING' ? 'Waiting for Chef' : 'Preparing'}
                                            </span>
                                        </div>

                                        <div className="order-header">
                                            <div className="order-title">
                                                <h3>Order #{order.id}</h3>
                                                <span className="order-time">
                                                    {new Date(order.createdAt).toLocaleTimeString('en-US', {
                                                        hour: '2-digit',
                                                        minute: '2-digit'
                                                    })}
                                                </span>
                                            </div>
                                        </div>

                                        <div className="order-info">
                                            <div className="info-row">
                                                <span className="info-icon">🪑</span>
                                                <span className="info-label">Table:</span>
                                                <span className="info-value">Table {order.tableNumber}</span>
                                            </div>
                                            <div className="info-row">
                                                <span className="info-icon">👤</span>
                                                <span className="info-label">Customer:</span>
                                                <span className="info-value">{order.customerName}</span>
                                            </div>
                                            <div className="info-row">
                                                <span className="info-icon">👨‍🍳</span>
                                                <span className="info-label">Chef:</span>
                                                <span className="info-value">
                                                    {order.chefName || 'Not assigned yet'}
                                                </span>
                                            </div>
                                        </div>

                                        <div className="order-items">
                                            <h4>Items:</h4>
                                            <ul>
                                                {order.items.map(item => (
                                                    <li key={item.id}>
                                                        <span className="item-quantity">{item.quantity}x</span>
                                                        <span className="item-name">{item.menuItem.name}</span>
                                                        <span className="item-price">
                                                            ${(item.priceAtOrder * item.quantity).toFixed(2)}
                                                        </span>
                                                    </li>
                                                ))}
                                            </ul>
                                        </div>

                                        {order.specialInstructions && (
                                            <div className="special-instructions">
                                                <span className="instructions-icon">📝</span>
                                                <span className="instructions-text">
                                                    {order.specialInstructions}
                                                </span>
                                            </div>
                                        )}

                                        <div className="order-total">
                                            <span>Total:</span>
                                            <span className="total-amount">${order.total.toFixed(2)}</span>
                                        </div>

                                        <div className="preparing-status">
                                            <div className="status-icon">⏰</div>
                                            <p>
                                                {order.status === 'PENDING'
                                                    ? 'Waiting for chef to start preparation...'
                                                    : 'Chef is preparing your order...'
                                                }
                                            </p>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};

export default WaiterDashboard;