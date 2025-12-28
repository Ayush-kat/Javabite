import React, { useState, useEffect } from 'react';
import { adminApi } from '../api/api';
import OrderDetailsModal from './OrderDetailsModal';
import '../styles/OrdersHistoryPage.css';

const OrdersHistoryPage = () => {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    // Filter states
    const [activeTab, setActiveTab] = useState('all');
    const [searchQuery, setSearchQuery] = useState('');
    const [dateRange, setDateRange] = useState({ start: '', end: '' });
    const [selectedTable, setSelectedTable] = useState('');
    const [selectedChef, setSelectedChef] = useState('');
    const [selectedWaiter, setSelectedWaiter] = useState('');
    const [paymentFilter, setPaymentFilter] = useState('');
    const [sortBy, setSortBy] = useState('date-desc');
    const [showAdvancedFilters, setShowAdvancedFilters] = useState(false);

    // Modal state
    const [selectedOrder, setSelectedOrder] = useState(null);
    const [showDetailsModal, setShowDetailsModal] = useState(false);

    // Staff lists for filters
    const [chefs, setChefs] = useState([]);
    const [waiters, setWaiters] = useState([]);

    // Stats
    const [stats, setStats] = useState({
        pending: 0,
        preparing: 0,
        ready: 0,
        completed: 0,
        cancelled: 0,
        total: 0,
        todaySales: 0,
        avgOrderValue: 0
    });

    useEffect(() => {
        fetchAllData();
        const interval = setInterval(fetchOrders, 30000);
        return () => clearInterval(interval);
    }, []);

    const fetchAllData = async () => {
        await Promise.all([
            fetchOrders(),
            fetchStaff()
        ]);
    };

    const fetchOrders = async () => {
        try {
            setLoading(true);
            setError('');

            const data = await adminApi.getAllOrders();
            const ordersArray = Array.isArray(data) ? data : [];

            setOrders(ordersArray);
            calculateStats(ordersArray);

        } catch (err) {
            console.error('Failed to fetch orders:', err);
            setError(`Failed to load orders: ${err.message}`);
            setOrders([]);
        } finally {
            setLoading(false);
        }
    };

    const fetchStaff = async () => {
        try {
            const [chefsData, waitersData] = await Promise.all([
                adminApi.getAllChefs().catch(() => []),
                adminApi.getAllWaiters().catch(() => [])
            ]);
            setChefs(Array.isArray(chefsData) ? chefsData : []);
            setWaiters(Array.isArray(waitersData) ? waitersData : []);
        } catch (err) {
            console.error('Failed to fetch staff:', err);
        }
    };

    const calculateStats = (ordersArray) => {
        const today = new Date().toDateString();

        const pending = ordersArray.filter(o => o.status === 'PENDING').length;
        const preparing = ordersArray.filter(o => o.status === 'PREPARING').length;
        const ready = ordersArray.filter(o => o.status === 'READY').length;
        const completed = ordersArray.filter(o => o.status === 'COMPLETED').length;
        const cancelled = ordersArray.filter(o => o.status === 'CANCELLED').length;

        const todayOrders = ordersArray.filter(o =>
            new Date(o.createdAt).toDateString() === today
        );

        const todaySales = todayOrders.reduce((sum, o) => sum + (o.total || 0), 0);
        const avgOrderValue = ordersArray.length > 0
            ? ordersArray.reduce((sum, o) => sum + (o.total || 0), 0) / ordersArray.length
            : 0;

        setStats({
            pending,
            preparing,
            ready,
            completed,
            cancelled,
            total: ordersArray.length,
            todaySales,
            avgOrderValue
        });
    };

    const getFilteredOrders = () => {
        let filtered = orders;

        if (activeTab !== 'all') {
            filtered = filtered.filter(o => o.status === activeTab.toUpperCase());
        }

        if (searchQuery) {
            const query = searchQuery.toLowerCase();
            filtered = filtered.filter(o =>
                o.customer?.name?.toLowerCase().includes(query) ||
                o.customer?.email?.toLowerCase().includes(query) ||
                o.id?.toString().includes(query)
            );
        }

        if (dateRange.start) {
            filtered = filtered.filter(o =>
                new Date(o.createdAt) >= new Date(dateRange.start)
            );
        }
        if (dateRange.end) {
            filtered = filtered.filter(o =>
                new Date(o.createdAt) <= new Date(dateRange.end + 'T23:59:59')
            );
        }

        if (selectedTable) {
            filtered = filtered.filter(o =>
                o.tableBooking?.tableNumber?.toString() === selectedTable
            );
        }

        if (selectedChef) {
            filtered = filtered.filter(o =>
                o.chef?.id?.toString() === selectedChef
            );
        }

        if (selectedWaiter) {
            filtered = filtered.filter(o =>
                o.waiter?.id?.toString() === selectedWaiter
            );
        }

        if (paymentFilter) {
            filtered = filtered.filter(o =>
                o.paymentStatus === paymentFilter
            );
        }

        switch (sortBy) {
            case 'date-desc':
                filtered.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
                break;
            case 'date-asc':
                filtered.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
                break;
            case 'total-desc':
                filtered.sort((a, b) => (b.total || 0) - (a.total || 0));
                break;
            case 'total-asc':
                filtered.sort((a, b) => (a.total || 0) - (b.total || 0));
                break;
            case 'status':
                filtered.sort((a, b) => a.status.localeCompare(b.status));
                break;
            default:
                break;
        }

        return filtered;
    };

    const handleViewDetails = (order) => {
        setSelectedOrder(order);
        setShowDetailsModal(true);
    };

    const handleCancelOrder = async (orderId) => {
        if (!window.confirm('Are you sure you want to cancel this order?')) return;

        try {
            await adminApi.cancelOrderAdmin(orderId);
            setSuccess(`Order #${orderId} cancelled successfully`);
            await fetchOrders();
            setTimeout(() => setSuccess(''), 3000);
        } catch (err) {
            setError(`Failed to cancel order: ${err.message}`);
        }
    };

    const handleExportCSV = () => {
        const filtered = getFilteredOrders();
        const csv = convertToCSV(filtered);
        downloadFile(csv, 'orders-export.csv', 'text/csv');
    };

    const convertToCSV = (data) => {
        const headers = [
            'Order ID', 'Customer Name', 'Customer Email', 'Table',
            'Status', 'Total', 'Payment Status', 'Created At',
            'Chef', 'Waiter'
        ];

        const rows = data.map(o => [
            o.id,
            o.customer?.name || '',
            o.customer?.email || '',
            o.tableBooking?.tableNumber || '',
            o.status,
            o.total || 0,
            o.paymentStatus || 'UNPAID',
            new Date(o.createdAt).toLocaleString(),
            o.chef?.name || '',
            o.waiter?.name || ''
        ]);

        return [headers, ...rows].map(row => row.join(',')).join('\n');
    };

    const downloadFile = (content, filename, type) => {
        const blob = new Blob([content], { type });
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = filename;
        link.click();
        URL.revokeObjectURL(url);
    };

    const clearFilters = () => {
        setSearchQuery('');
        setDateRange({ start: '', end: '' });
        setSelectedTable('');
        setSelectedChef('');
        setSelectedWaiter('');
        setPaymentFilter('');
        setSortBy('date-desc');
    };

    const getStatusColor = (status) => {
        switch (status) {
            case 'PENDING':
                return { bg: '#fff3e0', color: '#e65100', border: '#ff9800' };
            case 'PREPARING':
                return { bg: '#e3f2fd', color: '#1565c0', border: '#2196f3' };
            case 'READY':
                return { bg: '#e8f5e9', color: '#2e7d32', border: '#4caf50' };
            case 'COMPLETED':
                return { bg: '#f3e5f5', color: '#6a1b9a', border: '#9c27b0' };
            case 'CANCELLED':
                return { bg: '#ffebee', color: '#c62828', border: '#f44336' };
            default:
                return { bg: '#f5f5f5', color: '#666', border: '#ccc' };
        }
    };

    const filteredOrders = getFilteredOrders();

    if (loading) {
        return (
            <div className="orders-history">
                <div className="loading-container">
                    <div className="loading-spinner"></div>
                    <p>Loading orders...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="orders-history">

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
                    <div className="stat-icon" style={{ background: '#fff3e0', color: '#e65100' }}>⏳</div>
                    <div className="stat-content">
                        <div className="stat-value">{stats.pending}</div>
                        <div className="stat-label">Pending</div>
                    </div>
                </div>

                <div className="stat-card">
                    <div className="stat-icon" style={{ background: '#e3f2fd', color: '#1565c0' }}>👨‍🍳</div>
                    <div className="stat-content">
                        <div className="stat-value">{stats.preparing}</div>
                        <div className="stat-label">Preparing</div>
                    </div>
                </div>

                <div className="stat-card">
                    <div className="stat-icon" style={{ background: '#e8f5e9', color: '#2e7d32' }}>✓</div>
                    <div className="stat-content">
                        <div className="stat-value">{stats.ready}</div>
                        <div className="stat-label">Ready</div>
                    </div>
                </div>

                <div className="stat-card">
                    <div className="stat-icon" style={{ background: '#f3e5f5', color: '#6a1b9a' }}>🎉</div>
                    <div className="stat-content">
                        <div className="stat-value">{stats.completed}</div>
                        <div className="stat-label">Completed</div>
                    </div>
                </div>

                <div className="stat-card">
                    <div className="stat-icon" style={{ background: '#ffebee', color: '#c62828' }}>✗</div>
                    <div className="stat-content">
                        <div className="stat-value">{stats.cancelled}</div>
                        <div className="stat-label">Cancelled</div>
                    </div>
                </div>

                <div className="stat-card">
                    <div className="stat-icon" style={{ background: '#fff9c4', color: '#f57f17' }}>💰</div>
                    <div className="stat-content">
                        <div className="stat-value">${stats.todaySales.toFixed(2)}</div>
                        <div className="stat-label">Today's Sales</div>
                    </div>
                </div>
            </div>

            {/* Filters Bar */}
            <div className="filters-bar">
                <div className="filters-row-main">
                    <input
                        type="text"
                        placeholder="🔍 Search by customer name, email, or order ID..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        className="search-input"
                    />

                    <select
                        value={activeTab}
                        onChange={(e) => setActiveTab(e.target.value)}
                        className="filter-select"
                    >
                        <option value="all">All Status</option>
                        <option value="pending">Pending</option>
                        <option value="preparing">Preparing</option>
                        <option value="ready">Ready</option>
                        <option value="completed">Completed</option>
                        <option value="cancelled">Cancelled</option>
                    </select>

                    <div className="date-range-picker">
                        <input
                            type="date"
                            value={dateRange.start}
                            onChange={(e) => setDateRange({ ...dateRange, start: e.target.value })}
                            className="date-input"
                        />
                        <span className="date-separator">-</span>
                        <input
                            type="date"
                            value={dateRange.end}
                            onChange={(e) => setDateRange({ ...dateRange, end: e.target.value })}
                            className="date-input"
                        />
                    </div>

                    <button onClick={handleExportCSV} className="btn-export">
                        📥 Export
                    </button>
                </div>

                {/* Advanced Filters Toggle */}
                <button
                    className="btn-advanced-filters"
                    onClick={() => setShowAdvancedFilters(!showAdvancedFilters)}
                >
                    {showAdvancedFilters ? '▼' : '▶'} Advanced Filters
                </button>

                {showAdvancedFilters && (
                    <div className="filters-row-advanced">
                        <select
                            value={selectedTable}
                            onChange={(e) => setSelectedTable(e.target.value)}
                            className="filter-select-small"
                        >
                            <option value="">All Tables</option>
                            {Array.from({ length: 20 }, (_, i) => i + 1).map(num => (
                                <option key={num} value={num}>Table {num}</option>
                            ))}
                        </select>

                        <select
                            value={selectedChef}
                            onChange={(e) => setSelectedChef(e.target.value)}
                            className="filter-select-small"
                        >
                            <option value="">All Chefs</option>
                            {chefs.map(chef => (
                                <option key={chef.id} value={chef.id}>{chef.name}</option>
                            ))}
                        </select>

                        <select
                            value={selectedWaiter}
                            onChange={(e) => setSelectedWaiter(e.target.value)}
                            className="filter-select-small"
                        >
                            <option value="">All Waiters</option>
                            {waiters.map(waiter => (
                                <option key={waiter.id} value={waiter.id}>{waiter.name}</option>
                            ))}
                        </select>

                        <select
                            value={paymentFilter}
                            onChange={(e) => setPaymentFilter(e.target.value)}
                            className="filter-select-small"
                        >
                            <option value="">Payment Status</option>
                            <option value="PAID">Paid</option>
                            <option value="UNPAID">Unpaid</option>
                            <option value="REFUNDED">Refunded</option>
                        </select>

                        <select
                            value={sortBy}
                            onChange={(e) => setSortBy(e.target.value)}
                            className="filter-select-small"
                        >
                            <option value="date-desc">Latest First</option>
                            <option value="date-asc">Oldest First</option>
                            <option value="total-desc">Highest Amount</option>
                            <option value="total-asc">Lowest Amount</option>
                            <option value="status">By Status</option>
                        </select>

                        <button onClick={clearFilters} className="btn-clear">
                            ✗ Clear
                        </button>
                    </div>
                )}

                <div className="filter-summary">
                    Showing {filteredOrders.length} of {orders.length} orders
                </div>
            </div>

            {/* Orders Table */}
            <div
                className="orders-table-container"
                style={{
                    display: 'block',
                    background: 'white',
                    borderRadius: '12px',
                    overflow: 'hidden',
                    boxShadow: '0 2px 8px rgba(0,0,0,0.06)'
                }}
            >

                {filteredOrders.length === 0 ? (
                    <div className="empty-state">
                        <div className="empty-icon">🛒</div>
                        <h3>No Orders Found</h3>
                        <p>
                            {searchQuery || dateRange.start || selectedTable || selectedChef || selectedWaiter
                                ? 'No orders match your filters'
                                : 'No orders available at the moment'}
                        </p>
                        {(searchQuery || dateRange.start) && (
                            <button onClick={clearFilters} className="btn-primary" style={{ marginTop: '16px' }}>
                                Clear All Filters
                            </button>
                        )}
                    </div>
                ) : (
                    <table className="orders-table">
                        <thead>
                        <tr>
                            <th style={{ display: 'table-cell', padding: '16px 12px' }}>Order ID</th>
                            <th style={{ display: 'table-cell', padding: '16px 12px' }}>Customer</th>
                            <th style={{ display: 'table-cell', padding: '16px 12px' }}>Table</th>
                            <th style={{ display: 'table-cell', padding: '16px 12px', textAlign:"center" }}>Items</th>
                            <th style={{ display: 'table-cell', padding: '16px 12px', textAlign:"center" }}>Amount</th>
                            <th style={{ display: 'table-cell', padding: '16px 12px' }}>Status</th>
                            <th style={{ display: 'table-cell', padding: '16px 12px' }}>Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        {filteredOrders.map(order => {
                            const statusStyle = getStatusColor(order.status);
                            const orderDate = new Date(order.createdAt);
                            const isToday = orderDate.toDateString() === new Date().toDateString();

                            return (
                                <tr key={order.id}>
                                    <td>
                                        <div className="order-id-cell">
                                            <strong>#{order.id}</strong>
                                            <div className="order-date">
                                                {orderDate.toLocaleDateString('en-US', {
                                                    month: 'short',
                                                    day: 'numeric',
                                                    year: 'numeric'
                                                })}
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <div className="customer-cell">
                                            <div className="customer-name">{order.customer?.name || 'N/A'}</div>
                                            <div className="customer-email">{order.customer?.email || ''}</div>
                                        </div>
                                    </td>
                                    <td>
                                        <div className="table-cell">
                                            {order.tableBooking?.tableNumber
                                                ? `Table ${order.tableBooking.tableNumber}`
                                                : '—'}
                                        </div>
                                    </td>
                                    <td className="items-count">{order.items?.length || 0}</td>
                                    <td>
                                        <div className="amount-cell">
                                            ${(order.total || 0).toFixed(2)}
                                        </div>
                                    </td>
                                    <td>
                                            <span
                                                className="status-badge"
                                                style={{
                                                    background: statusStyle.bg,
                                                    color: statusStyle.color,
                                                    border: `1px solid ${statusStyle.border}`
                                                }}
                                            >
                                                {order.status}
                                            </span>
                                    </td>
                                    <td>
                                        <button
                                            className="btn-action"
                                            onClick={() => handleViewDetails(order)}
                                            title="View Details"
                                        >
                                            👁️
                                        </button>
                                    </td>
                                </tr>
                            );
                        })}
                        </tbody>
                    </table>
                )}
            </div>

            {/* Order Details Modal */}
            {showDetailsModal && selectedOrder && (
                <OrderDetailsModal
                    order={selectedOrder}
                    onClose={() => {
                        setShowDetailsModal(false);
                        setSelectedOrder(null);
                    }}
                    onRefresh={fetchOrders}
                />
            )}
        </div>
    );
};

export default OrdersHistoryPage;