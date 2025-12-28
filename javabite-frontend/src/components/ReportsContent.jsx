import React, { useState, useEffect } from 'react';
import { adminApi } from '../api/api';

const ReportsContent = () => {
    const [loading, setLoading] = useState(true);
    const [dateRange, setDateRange] = useState({
        start: new Date(new Date().setDate(new Date().getDate() - 30)).toISOString().split('T')[0],
        end: new Date().toISOString().split('T')[0]
    });

    const [reportData, setReportData] = useState({
        totalRevenue: 0,
        totalOrders: 0,
        avgOrderValue: 0,
        topItems: [],
        revenueByDay: [],
        ordersByStatus: {},
        topCustomers: []
    });

    useEffect(() => {
        fetchReportData();
    }, [dateRange]);

    const fetchReportData = async () => {
        try {
            setLoading(true);

            // Fetch all orders
            const orders = await adminApi.getAllOrders();

            // Filter by date range
            const filteredOrders = orders.filter(order => {
                const orderDate = new Date(order.createdAt);
                return orderDate >= new Date(dateRange.start) &&
                    orderDate <= new Date(dateRange.end + 'T23:59:59');
            });

            // Calculate metrics
            const totalRevenue = filteredOrders.reduce((sum, o) => sum + (o.total || 0), 0);
            const totalOrders = filteredOrders.length;
            const avgOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0;

            // Orders by status
            const ordersByStatus = {
                PENDING: filteredOrders.filter(o => o.status === 'PENDING').length,
                PREPARING: filteredOrders.filter(o => o.status === 'PREPARING').length,
                READY: filteredOrders.filter(o => o.status === 'READY').length,
                COMPLETED: filteredOrders.filter(o => o.status === 'COMPLETED').length,
                CANCELLED: filteredOrders.filter(o => o.status === 'CANCELLED').length,
            };

            // Top items
            const itemCount = {};
            filteredOrders.forEach(order => {
                order.items?.forEach(item => {
                    const name = item.menuItem?.name || 'Unknown Item';
                    itemCount[name] = (itemCount[name] || 0) + item.quantity;
                });
            });

            const topItems = Object.entries(itemCount)
                .sort(([, a], [, b]) => b - a)
                .slice(0, 5)
                .map(([name, count]) => ({ name, count }));

            // Revenue by day
            const revenueByDay = {};
            filteredOrders.forEach(order => {
                const date = new Date(order.createdAt).toLocaleDateString();
                revenueByDay[date] = (revenueByDay[date] || 0) + (order.total || 0);
            });

            const revenueChart = Object.entries(revenueByDay)
                .map(([date, revenue]) => ({ date, revenue }))
                .sort((a, b) => new Date(a.date) - new Date(b.date));

            // Top customers
            const customerRevenue = {};
            filteredOrders.forEach(order => {
                const customerId = order.customer?.id;
                const customerName = order.customer?.name || 'Unknown';
                if (customerId) {
                    if (!customerRevenue[customerId]) {
                        customerRevenue[customerId] = {
                            name: customerName,
                            email: order.customer.email,
                            revenue: 0,
                            orders: 0
                        };
                    }
                    customerRevenue[customerId].revenue += order.total || 0;
                    customerRevenue[customerId].orders += 1;
                }
            });

            const topCustomers = Object.values(customerRevenue)
                .sort((a, b) => b.revenue - a.revenue)
                .slice(0, 5);

            setReportData({
                totalRevenue,
                totalOrders,
                avgOrderValue,
                topItems,
                revenueByDay: revenueChart,
                ordersByStatus,
                topCustomers
            });

        } catch (err) {
            console.error('Failed to fetch report data:', err);
        } finally {
            setLoading(false);
        }
    };

    const exportReport = () => {
        const csv = generateReportCSV();
        const blob = new Blob([csv], { type: 'text/csv' });
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `report-${dateRange.start}-to-${dateRange.end}.csv`;
        link.click();
        URL.revokeObjectURL(url);
    };

    const generateReportCSV = () => {
        const headers = ['Metric', 'Value'];
        const rows = [
            ['Total Revenue', `$${reportData.totalRevenue.toFixed(2)}`],
            ['Total Orders', reportData.totalOrders],
            ['Average Order Value', `$${reportData.avgOrderValue.toFixed(2)}`],
            ['Pending Orders', reportData.ordersByStatus.PENDING],
            ['Completed Orders', reportData.ordersByStatus.COMPLETED],
            ['Cancelled Orders', reportData.ordersByStatus.CANCELLED]
        ];
        return [headers, ...rows].map(row => row.join(',')).join('\n');
    };

    if (loading) {
        return (
            <div className="reports-content">
                <div className="loading-container">
                    <div className="loading-spinner"></div>
                    <p>Loading reports...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="reports-content">
            {/* Header */}
            <div className="reports-header">
                <div>
                    <h2>Reports & Analytics</h2>
                    <p className="subtitle">Business insights and performance metrics</p>
                </div>
                <div className="header-actions">
                    <input
                        type="date"
                        value={dateRange.start}
                        onChange={(e) => setDateRange({ ...dateRange, start: e.target.value })}
                        className="date-input"
                    />
                    <span style={{ margin: '0 8px' }}>to</span>
                    <input
                        type="date"
                        value={dateRange.end}
                        onChange={(e) => setDateRange({ ...dateRange, end: e.target.value })}
                        className="date-input"
                    />
                    <button onClick={exportReport} className="btn-export">
                        📥 Export Report
                    </button>
                </div>
            </div>

            {/* Summary Cards */}
            <div className="reports-summary">
                <div className="summary-card">
                    <div className="summary-icon" style={{ background: '#e8f5e9', color: '#2e7d32' }}>
                        💰
                    </div>
                    <div className="summary-content">
                        <div className="summary-label">Total Revenue</div>
                        <div className="summary-value">${reportData.totalRevenue.toFixed(2)}</div>
                    </div>
                </div>

                <div className="summary-card">
                    <div className="summary-icon" style={{ background: '#e3f2fd', color: '#1565c0' }}>
                        📦
                    </div>
                    <div className="summary-content">
                        <div className="summary-label">Total Orders</div>
                        <div className="summary-value">{reportData.totalOrders}</div>
                    </div>
                </div>

                <div className="summary-card">
                    <div className="summary-icon" style={{ background: '#f3e5f5', color: '#6a1b9a' }}>
                        📊
                    </div>
                    <div className="summary-content">
                        <div className="summary-label">Average Order Value</div>
                        <div className="summary-value">${reportData.avgOrderValue.toFixed(2)}</div>
                    </div>
                </div>

                <div className="summary-card">
                    <div className="summary-icon" style={{ background: '#fff9c4', color: '#f57f17' }}>
                        ✅
                    </div>
                    <div className="summary-content">
                        <div className="summary-label">Completed Orders</div>
                        <div className="summary-value">{reportData.ordersByStatus.COMPLETED}</div>
                    </div>
                </div>
            </div>

            {/* Charts Grid */}
            <div className="reports-grid">
                {/* Orders by Status */}
                <div className="report-card">
                    <h3>Orders by Status</h3>
                    <div className="status-chart">
                        {Object.entries(reportData.ordersByStatus).map(([status, count]) => {
                            const total = Object.values(reportData.ordersByStatus).reduce((a, b) => a + b, 0);
                            const percentage = total > 0 ? (count / total * 100).toFixed(1) : 0;
                            const colors = {
                                PENDING: '#ff9800',
                                PREPARING: '#2196f3',
                                READY: '#4caf50',
                                COMPLETED: '#9c27b0',
                                CANCELLED: '#f44336'
                            };

                            return (
                                <div key={status} className="status-bar-item">
                                    <div className="status-bar-label">
                                        <span>{status}</span>
                                        <span className="status-bar-count">{count}</span>
                                    </div>
                                    <div className="status-bar">
                                        <div
                                            className="status-bar-fill"
                                            style={{
                                                width: `${percentage}%`,
                                                background: colors[status]
                                            }}
                                        ></div>
                                    </div>
                                    <div className="status-bar-percentage">{percentage}%</div>
                                </div>
                            );
                        })}
                    </div>
                </div>

                {/* Top Selling Items */}
                <div className="report-card">
                    <h3>Top Selling Items</h3>
                    <div className="top-items-list">
                        {reportData.topItems.map((item, index) => (
                            <div key={index} className="top-item">
                                <div className="item-rank">#{index + 1}</div>
                                <div className="item-details">
                                    <div className="item-name">{item.name}</div>
                                    <div className="item-count">{item.count} sold</div>
                                </div>
                            </div>
                        ))}
                        {reportData.topItems.length === 0 && (
                            <div className="empty-message">No items sold in this period</div>
                        )}
                    </div>
                </div>

                {/* Revenue Trend */}
                <div className="report-card full-width">
                    <h3>Revenue Trend</h3>
                    <div className="revenue-chart">
                        {reportData.revenueByDay.length > 0 ? (
                            <div className="simple-line-chart">
                                {reportData.revenueByDay.map((item, index) => {
                                    const maxRevenue = Math.max(...reportData.revenueByDay.map(d => d.revenue));
                                    const height = maxRevenue > 0 ? (item.revenue / maxRevenue * 100) : 0;

                                    return (
                                        <div key={index} className="chart-bar">
                                            <div className="chart-bar-value">${item.revenue.toFixed(0)}</div>
                                            <div
                                                className="chart-bar-fill"
                                                style={{ height: `${height}%` }}
                                            ></div>
                                            <div className="chart-bar-label">
                                                {new Date(item.date).toLocaleDateString('en-US', {
                                                    month: 'short',
                                                    day: 'numeric'
                                                })}
                                            </div>
                                        </div>
                                    );
                                })}
                            </div>
                        ) : (
                            <div className="empty-message">No revenue data for this period</div>
                        )}
                    </div>
                </div>

                {/* Top Customers */}
                <div className="report-card full-width">
                    <h3>Top Customers</h3>
                    <table className="customers-table">
                        <thead>
                        <tr>
                            <th>Rank</th>
                            <th>Customer</th>
                            <th>Email</th>
                            <th>Orders</th>
                            <th>Total Spent</th>
                        </tr>
                        </thead>
                        <tbody>
                        {reportData.topCustomers.map((customer, index) => (
                            <tr key={index}>
                                <td className="rank-cell">#{index + 1}</td>
                                <td className="name-cell">{customer.name}</td>
                                <td className="email-cell">{customer.email}</td>
                                <td className="orders-cell">{customer.orders}</td>
                                <td className="revenue-cell">${customer.revenue.toFixed(2)}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                    {reportData.topCustomers.length === 0 && (
                        <div className="empty-message">No customer data for this period</div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ReportsContent;