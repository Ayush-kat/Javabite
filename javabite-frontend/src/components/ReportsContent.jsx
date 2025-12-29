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
            const orders = await adminApi.getAllOrders();
            const filteredOrders = orders.filter(order => {
                const orderDate = new Date(order.createdAt);
                return orderDate >= new Date(dateRange.start) &&
                    orderDate <= new Date(dateRange.end + 'T23:59:59');
            });

            const totalRevenue = filteredOrders.reduce((sum, o) => sum + (o.total || 0), 0);
            const totalOrders = filteredOrders.length;
            const avgOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0;

            const ordersByStatus = {
                PENDING: filteredOrders.filter(o => o.status === 'PENDING').length,
                PREPARING: filteredOrders.filter(o => o.status === 'PREPARING').length,
                READY: filteredOrders.filter(o => o.status === 'READY').length,
                COMPLETED: filteredOrders.filter(o => o.status === 'COMPLETED').length,
                CANCELLED: filteredOrders.filter(o => o.status === 'CANCELLED').length,
            };

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

            const revenueByDay = {};
            filteredOrders.forEach(order => {
                const date = new Date(order.createdAt).toLocaleDateString();
                revenueByDay[date] = (revenueByDay[date] || 0) + (order.total || 0);
            });

            const revenueChart = Object.entries(revenueByDay)
                .map(([date, revenue]) => ({ date, revenue }))
                .sort((a, b) => new Date(a.date) - new Date(b.date));

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

    const styles = {
        container: {
            padding: 0,
            margin: 0,
            width: '100%',
            fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif'
        },
        header: {
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            marginBottom: '28px',
            flexWrap: 'wrap',
            gap: '20px',
            padding: '24px',
            background: 'linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%)',
            borderRadius: '16px',
            boxShadow: '0 4px 12px rgba(0,0,0,0.06)'
        },
        headerTitle: {
            fontSize: '32px',
            fontWeight: '700',
            color: '#2c3e50',
            margin: 0,
            letterSpacing: '-0.5px'
        },
        subtitle: {
            color: '#7f8c8d',
            fontSize: '15px',
            margin: '4px 0 0 0',
            fontWeight: '500'
        },
        headerActions: {
            display: 'flex',
            alignItems: 'center',
            gap: '12px',
            flexWrap: 'wrap'
        },
        dateInput: {
            padding: '12px 16px',
            border: '2px solid #e0e0e0',
            borderRadius: '10px',
            fontSize: '14px',
            minWidth: '160px',
            transition: 'all 0.3s ease',
            fontWeight: '500'
        },
        btnExport: {
            padding: '12px 24px',
            background: 'linear-gradient(135deg, #45af27 0%, #1c6108 100%)',
            color: 'white',
            border: 'none',
            borderRadius: '10px',
            fontSize: '14px',
            fontWeight: '700',
            cursor: 'pointer',
            transition: 'all 0.3s ease',
            boxShadow: '0 4px 12px rgba(69, 175, 39, 0.3)'
        },
        summaryGrid: {
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(260px, 1fr))',
            gap: '24px',
            marginBottom: '32px'
        },
        summaryCard: {
            background: 'white',
            borderRadius: '16px',
            padding: '28px',
            display: 'flex',
            alignItems: 'center',
            gap: '20px',
            boxShadow: '0 4px 16px rgba(0,0,0,0.08)',
            transition: 'all 0.3s ease',
            border: '1px solid #f0f0f0'
        },
        summaryIcon: (bg, color) => ({
            width: '72px',
            height: '72px',
            borderRadius: '16px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontSize: '32px',
            background: bg,
            color: color,
            flexShrink: 0,
            boxShadow: '0 4px 12px rgba(0,0,0,0.1)'
        }),
        summaryLabel: {
            fontSize: '14px',
            color: '#7f8c8d',
            fontWeight: '700',
            marginBottom: '8px',
            textTransform: 'uppercase',
            letterSpacing: '0.5px'
        },
        summaryValue: {
            fontSize: '32px',
            fontWeight: '700',
            color: '#2c3e50',
            lineHeight: '1'
        },
        reportsGrid: {
            display: 'grid',
            gridTemplateColumns: 'repeat(2, 1fr)',
            gap: '24px'
        },
        reportCard: {
            background: 'white',
            borderRadius: '16px',
            padding: '28px',
            boxShadow: '0 4px 16px rgba(0,0,0,0.08)',
            transition: 'all 0.3s ease',
            border: '1px solid #f0f0f0'
        },
        reportCardFullWidth: {
            gridColumn: '1 / -1'
        },
        cardTitle: {
            fontSize: '20px',
            fontWeight: '700',
            color: '#2c3e50',
            marginBottom: '24px',
            paddingBottom: '16px',
            borderBottom: '2px solid #f0f0f0'
        },
        statusChart: {
            display: 'flex',
            flexDirection: 'column',
            gap: '20px'
        },
        statusBarItem: {
            display: 'grid',
            gridTemplateColumns: '160px 1fr 70px',
            gap: '16px',
            alignItems: 'center'
        },
        statusBarLabel: {
            display: 'flex',
            justifyContent: 'space-between',
            fontSize: '14px',
            fontWeight: '700',
            color: '#2c3e50'
        },
        statusBar: {
            height: '28px',
            background: '#f5f5f5',
            borderRadius: '14px',
            overflow: 'hidden',
            boxShadow: 'inset 0 2px 4px rgba(0,0,0,0.06)'
        },
        statusBarFill: (percentage, color) => ({
            height: '100%',
            width: `${percentage}%`,
            background: `linear-gradient(135deg, ${color} 0%, ${color}dd 100%)`,
            transition: 'width 0.8s cubic-bezier(0.4, 0, 0.2, 1)',
            boxShadow: `0 2px 8px ${color}44`
        }),
        topItemsList: {
            display: 'flex',
            flexDirection: 'column',
            gap: '14px'
        },
        topItem: {
            display: 'flex',
            alignItems: 'center',
            gap: '14px',
            padding: '16px',
            background: 'linear-gradient(135deg, #f8f9fa 0%, #f0f0f0 100%)',
            borderRadius: '12px',
            transition: 'all 0.3s ease',
            border: '1px solid #e8e8e8'
        },
        itemRank: {
            width: '40px',
            height: '40px',
            background: 'linear-gradient(135deg, #8b6f47 0%, #6d5635 100%)',
            color: 'white',
            borderRadius: '50%',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontWeight: '700',
            fontSize: '16px',
            flexShrink: 0,
            boxShadow: '0 4px 12px rgba(139, 111, 71, 0.3)'
        },
        itemName: {
            fontWeight: '700',
            color: '#2c3e50',
            flex: 1,
            fontSize: '15px'
        },
        itemCount: {
            fontSize: '13px',
            color: '#7f8c8d',
            fontWeight: '600'
        },
        revenueChart: {
            display: 'flex',
            alignItems: 'flex-end',
            justifyContent: 'space-between',
            gap: '10px',
            minHeight: '280px',
            height: '280px',
            padding: '20px 10px',
            overflowX: 'auto',
            background: 'linear-gradient(180deg, #fafafa 0%, #ffffff 100%)',
            borderRadius: '12px',
            border: '1px solid #f0f0f0'
        },
        chartBar: {
            flex: 1,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'flex-end',
            gap: '10px',
            minWidth: '50px',
            transition: 'all 0.3s ease'
        },
        chartBarValue: {
            fontSize: '12px',
            fontWeight: '700',
            color: '#2c3e50',
            minHeight: '18px',
            textAlign: 'center'
        },
        chartBarFill: (height) => ({
            width: '100%',
            height: `${height}%`,
            background: 'linear-gradient(180deg, #8b6f47 0%, #6d5635 100%)',
            borderRadius: '6px 6px 0 0',
            minHeight: '6px',
            transition: 'height 0.8s cubic-bezier(0.4, 0, 0.2, 1)',
            boxShadow: '0 -4px 12px rgba(139, 111, 71, 0.3)'
        }),
        chartBarLabel: {
            fontSize: '11px',
            color: '#7f8c8d',
            fontWeight: '600',
            transform: 'rotate(-45deg)',
            whiteSpace: 'nowrap',
            marginTop: '24px',
            textAlign: 'center'
        },
        customersTable: {
            width: '100%',
            borderCollapse: 'collapse'
        },
        tableHeader: {
            background: 'linear-gradient(135deg, #f8f9fa 0%, #f0f0f0 100%)',
            padding: '16px 20px',
            textAlign: 'left',
            fontSize: '13px',
            fontWeight: '700',
            color: '#7f8c8d',
            textTransform: 'uppercase',
            borderBottom: '2px solid #e0e0e0',
            letterSpacing: '0.5px'
        },
        tableRow: {
            borderBottom: '1px solid #f5f5f5',
            transition: 'all 0.2s ease'
        },
        tableCell: {
            padding: '16px 20px',
            fontSize: '15px'
        },
        emptyMessage: {
            textAlign: 'center',
            padding: '60px 20px',
            color: '#95a5a6',
            fontSize: '15px',
            fontWeight: '500'
        },
        loadingContainer: {
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '100px 20px',
            background: 'white',
            borderRadius: '16px',
            boxShadow: '0 4px 16px rgba(0,0,0,0.06)'
        }
    };

    if (loading) {
        return (
            <div style={styles.container}>
                <div style={styles.loadingContainer}>
                    <div style={{
                        width: '60px',
                        height: '60px',
                        border: '5px solid #f3f3f3',
                        borderTop: '5px solid #8b6f47',
                        borderRadius: '50%',
                        animation: 'spin 1s linear infinite'
                    }}></div>
                    <p style={{margin: '20px 0 0 0', color: '#7f8c8d', fontSize: '16px', fontWeight: '600'}}>
                        Loading reports...
                    </p>
                </div>
            </div>
        );
    }

    return (
        <div style={styles.container}>
            <div style={styles.header}>
                <div>
                    <h2 style={styles.headerTitle}>Reports & Analytics</h2>
                    <p style={styles.subtitle}>Business insights and performance metrics</p>
                </div>
                <div style={styles.headerActions}>
                    <input
                        type="date"
                        value={dateRange.start}
                        onChange={(e) => setDateRange({ ...dateRange, start: e.target.value })}
                        style={styles.dateInput}
                        onFocus={(e) => e.target.style.borderColor = '#8b6f47'}
                        onBlur={(e) => e.target.style.borderColor = '#e0e0e0'}
                    />
                    <span style={{margin: '0 4px', color: '#7f8c8d', fontWeight: '600'}}>to</span>
                    <input
                        type="date"
                        value={dateRange.end}
                        onChange={(e) => setDateRange({ ...dateRange, end: e.target.value })}
                        style={styles.dateInput}
                        onFocus={(e) => e.target.style.borderColor = '#8b6f47'}
                        onBlur={(e) => e.target.style.borderColor = '#e0e0e0'}
                    />
                    <button
                        onClick={exportReport}
                        style={styles.btnExport}
                        onMouseEnter={(e) => {
                            e.target.style.transform = 'translateY(-2px)';
                            e.target.style.boxShadow = '0 8px 20px rgba(69, 175, 39, 0.4)';
                        }}
                        onMouseLeave={(e) => {
                            e.target.style.transform = 'translateY(0)';
                            e.target.style.boxShadow = '0 4px 12px rgba(69, 175, 39, 0.3)';
                        }}
                    >
                        📥 Export Report
                    </button>
                </div>
            </div>

            <div style={styles.summaryGrid}>
                {[
                    { icon: '💰', label: 'Total Revenue', value: `$${reportData.totalRevenue.toFixed(2)}`, bg: '#e8f5e9', color: '#2e7d32' },
                    { icon: '📦', label: 'Total Orders', value: reportData.totalOrders, bg: '#e3f2fd', color: '#1565c0' },
                    { icon: '📊', label: 'Average Order Value', value: `$${reportData.avgOrderValue.toFixed(2)}`, bg: '#f3e5f5', color: '#6a1b9a' },
                    { icon: '✅', label: 'Completed Orders', value: reportData.ordersByStatus.COMPLETED, bg: '#fff9c4', color: '#f57f17' }
                ].map((card, idx) => (
                    <div
                        key={idx}
                        style={styles.summaryCard}
                        onMouseEnter={(e) => {
                            e.currentTarget.style.transform = 'translateY(-6px)';
                            e.currentTarget.style.boxShadow = '0 8px 24px rgba(0,0,0,0.12)';
                        }}
                        onMouseLeave={(e) => {
                            e.currentTarget.style.transform = 'translateY(0)';
                            e.currentTarget.style.boxShadow = '0 4px 16px rgba(0,0,0,0.08)';
                        }}
                    >
                        <div style={styles.summaryIcon(card.bg, card.color)}>{card.icon}</div>
                        <div>
                            <div style={styles.summaryLabel}>{card.label}</div>
                            <div style={styles.summaryValue}>{card.value}</div>
                        </div>
                    </div>
                ))}
            </div>

            <div style={styles.reportsGrid}>
                <div
                    style={styles.reportCard}
                    onMouseEnter={(e) => {
                        e.currentTarget.style.transform = 'translateY(-4px)';
                        e.currentTarget.style.boxShadow = '0 8px 24px rgba(0,0,0,0.12)';
                    }}
                    onMouseLeave={(e) => {
                        e.currentTarget.style.transform = 'translateY(0)';
                        e.currentTarget.style.boxShadow = '0 4px 16px rgba(0,0,0,0.08)';
                    }}
                >
                    <h3 style={styles.cardTitle}>Orders by Status</h3>
                    <div style={styles.statusChart}>
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
                                <div key={status} style={styles.statusBarItem}>
                                    <div style={styles.statusBarLabel}>
                                        <span>{status}</span>
                                        <span style={{color: '#7f8c8d', fontSize: '13px'}}>{count}</span>
                                    </div>
                                    <div style={styles.statusBar}>
                                        <div style={styles.statusBarFill(percentage, colors[status])}></div>
                                    </div>
                                    <div style={{textAlign: 'right', fontSize: '14px', fontWeight: '700', color: '#7f8c8d'}}>
                                        {percentage}%
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                </div>

                <div
                    style={styles.reportCard}
                    onMouseEnter={(e) => {
                        e.currentTarget.style.transform = 'translateY(-4px)';
                        e.currentTarget.style.boxShadow = '0 8px 24px rgba(0,0,0,0.12)';
                    }}
                    onMouseLeave={(e) => {
                        e.currentTarget.style.transform = 'translateY(0)';
                        e.currentTarget.style.boxShadow = '0 4px 16px rgba(0,0,0,0.08)';
                    }}
                >
                    <h3 style={styles.cardTitle}>Top Selling Items</h3>
                    <div style={styles.topItemsList}>
                        {reportData.topItems.length > 0 ? reportData.topItems.map((item, index) => (
                            <div
                                key={index}
                                style={styles.topItem}
                                onMouseEnter={(e) => {
                                    e.currentTarget.style.transform = 'translateX(8px)';
                                    e.currentTarget.style.background = 'linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%)';
                                }}
                                onMouseLeave={(e) => {
                                    e.currentTarget.style.transform = 'translateX(0)';
                                    e.currentTarget.style.background = 'linear-gradient(135deg, #f8f9fa 0%, #f0f0f0 100%)';
                                }}
                            >
                                <div style={styles.itemRank}>#{index + 1}</div>
                                <div style={{flex: 1}}>
                                    <div style={styles.itemName}>{item.name}</div>
                                    <div style={styles.itemCount}>{item.count} sold</div>
                                </div>
                            </div>
                        )) : <div style={styles.emptyMessage}>No items sold in this period</div>}
                    </div>
                </div>

                <div style={{...styles.reportCard, ...styles.reportCardFullWidth}}>
                    <h3 style={styles.cardTitle}>Revenue Trend</h3>
                    {reportData.revenueByDay.length > 0 ? (
                        <div style={styles.revenueChart}>
                            {reportData.revenueByDay.map((item, index) => {
                                const maxRevenue = Math.max(...reportData.revenueByDay.map(d => d.revenue));
                                const height = maxRevenue > 0 ? (item.revenue / maxRevenue * 100) : 0;

                                return (
                                    <div
                                        key={index}
                                        style={styles.chartBar}
                                        onMouseEnter={(e) => e.currentTarget.style.transform = 'scale(1.05)'}
                                        onMouseLeave={(e) => e.currentTarget.style.transform = 'scale(1)'}
                                    >
                                        <div style={styles.chartBarValue}>${item.revenue.toFixed(0)}</div>
                                        <div style={styles.chartBarFill(height)}></div>
                                        <div style={styles.chartBarLabel}>
                                            {new Date(item.date).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    ) : <div style={styles.emptyMessage}>No revenue data for this period</div>}
                </div>

                <div style={{...styles.reportCard, ...styles.reportCardFullWidth}}>
                    <h3 style={styles.cardTitle}>Top Customers</h3>
                    {reportData.topCustomers.length > 0 ? (
                        <table style={styles.customersTable}>
                            <thead>
                            <tr>
                                <th style={styles.tableHeader}>Rank</th>
                                <th style={styles.tableHeader}>Customer</th>
                                <th style={styles.tableHeader}>Email</th>
                                <th style={styles.tableHeader}>Orders</th>
                                <th style={{...styles.tableHeader, textAlign: 'right'}}>Total Spent</th>
                            </tr>
                            </thead>
                            <tbody>
                            {reportData.topCustomers.map((customer, index) => (
                                <tr
                                    key={index}
                                    style={styles.tableRow}
                                    onMouseEnter={(e) => e.currentTarget.style.background = '#f8f9fa'}
                                    onMouseLeave={(e) => e.currentTarget.style.background = 'transparent'}
                                >
                                    <td style={{...styles.tableCell, fontWeight: '700', color: '#8b6f47'}}>
                                        #{index + 1}
                                    </td>
                                    <td style={{...styles.tableCell, fontWeight: '700', color: '#2c3e50'}}>
                                        {customer.name}
                                    </td>
                                    <td style={{...styles.tableCell, color: '#7f8c8d'}}>
                                        {customer.email}
                                    </td>
                                    <td style={{...styles.tableCell, textAlign: 'center', fontWeight: '700'}}>
                                        {customer.orders}
                                    </td>
                                    <td style={{...styles.tableCell, fontWeight: '700', color: '#27ae60', textAlign: 'right', fontSize: '16px'}}>
                                        ${customer.revenue.toFixed(2)}
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    ) : <div style={styles.emptyMessage}>No customer data for this period</div>}
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

export default ReportsContent;