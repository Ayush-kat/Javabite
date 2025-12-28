import React, { useState } from 'react';
import { adminApi } from '../api/api';
import '../styles/OrderDetailsModal.css';

const OrderDetailsModal = ({ order, onClose, onRefresh }) => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [notes, setNotes] = useState(order.adminNotes || '');

    const handleDownloadReceipt = () => {
        const receiptContent = generateReceiptHTML(order);
        const printWindow = window.open('', '_blank');
        printWindow.document.write(receiptContent);
        printWindow.document.close();
        printWindow.print();
    };

    const handleSaveNotes = async () => {
        try {
            setLoading(true);
            await adminApi.updateOrderNotes(order.id, notes);
            setSuccess('Notes saved successfully');
            setTimeout(() => setSuccess(''), 2000);
        } catch (err) {
            setError('Failed to save notes');
        } finally {
            setLoading(false);
        }
    };

    const handleRefundOrder = async () => {
        if (!window.confirm('Are you sure you want to refund this order?')) return;

        try {
            setLoading(true);
            await adminApi.refundOrder(order.id);
            setSuccess('Order refunded successfully');
            await onRefresh();
            setTimeout(() => {
                setSuccess('');
                onClose();
            }, 2000);
        } catch (err) {
            setError('Failed to refund order');
        } finally {
            setLoading(false);
        }
    };

    const generateReceiptHTML = (order) => {
        return `
            <!DOCTYPE html>
            <html>
            <head>
                <title>Order Receipt #${order.id}</title>
                <style>
                    body {
                        font-family: 'Courier New', monospace;
                        max-width: 400px;
                        margin: 20px auto;
                        padding: 20px;
                    }
                    .header {
                        text-align: center;
                        border-bottom: 2px dashed #000;
                        padding-bottom: 10px;
                        margin-bottom: 20px;
                    }
                    .logo { font-size: 24px; font-weight: bold; }
                    .item { 
                        display: flex; 
                        justify-content: space-between;
                        margin: 5px 0;
                    }
                    .total {
                        border-top: 2px dashed #000;
                        margin-top: 10px;
                        padding-top: 10px;
                        font-weight: bold;
                        font-size: 18px;
                    }
                    .footer {
                        text-align: center;
                        margin-top: 20px;
                        border-top: 2px dashed #000;
                        padding-top: 10px;
                    }
                </style>
            </head>
            <body>
                <div class="header">
                    <div class="logo">☕ JavaBite Coffee</div>
                    <div>Receipt</div>
                </div>
                
                <div><strong>Order #${order.id}</strong></div>
                <div>Date: ${new Date(order.createdAt).toLocaleString()}</div>
                <div>Customer: ${order.customer?.name}</div>
                <div>Table: ${order.tableBooking?.tableNumber || 'N/A'}</div>
                
                <div style="margin: 20px 0; border-bottom: 1px dashed #000; padding-bottom: 10px;">
                    <strong>Items:</strong>
                </div>
                
                ${order.items?.map(item => `
                    <div class="item">
                        <span>${item.menuItem?.name} x${item.quantity}</span>
                        <span>$${((item.priceAtOrder || 0) * item.quantity).toFixed(2)}</span>
                    </div>
                `).join('')}
                
                <div class="total">
                    <div class="item">
                        <span>TOTAL:</span>
                        <span>$${(order.total || 0).toFixed(2)}</span>
                    </div>
                </div>
                
                <div class="footer">
                    <div>Thank you for your visit!</div>
                    <div>Visit us again soon!</div>
                </div>
            </body>
            </html>
        `;
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

    const statusStyle = getStatusColor(order.status);

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-container order-details-modal" onClick={(e) => e.stopPropagation()}>
                <div className="modal-header">
                    <div className="modal-title-section">
                        <h2>Order Details #{order.id}</h2>
                        <div
                            className="status-badge-large"
                            style={{
                                background: statusStyle.bg,
                                color: statusStyle.color,
                                border: `2px solid ${statusStyle.border}`
                            }}
                        >
                            {order.status}
                        </div>
                    </div>
                    <button onClick={onClose} className="modal-close">×</button>
                </div>

                <div className="modal-body">
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

                    {/* Quick Actions Bar */}
                    <div className="quick-actions">
                        <button onClick={handleDownloadReceipt} className="action-btn">
                            🖨️ Print Receipt
                        </button>
                        <button className="action-btn" disabled>
                            📧 Email Receipt
                        </button>
                        {order.status === 'COMPLETED' && (
                            <button onClick={handleRefundOrder} className="action-btn btn-danger">
                                💸 Refund Order
                            </button>
                        )}
                    </div>

                    {/* Main Content Grid */}
                    <div className="details-grid">
                        {/* Customer Information */}
                        <div className="details-section">
                            <h3 className="section-title">👤 Customer Information</h3>
                            <div className="detail-row">
                                <span className="detail-label">Name:</span>
                                <span className="detail-value">{order.customer?.name || 'N/A'}</span>
                            </div>
                            <div className="detail-row">
                                <span className="detail-label">Email:</span>
                                <span className="detail-value">{order.customer?.email || 'N/A'}</span>
                            </div>
                            <div className="detail-row">
                                <span className="detail-label">Customer ID:</span>
                                <span className="detail-value">#{order.customer?.id}</span>
                            </div>
                        </div>

                        {/* Table & Booking Information */}
                        {order.tableBooking && (
                            <div className="details-section">
                                <h3 className="section-title">🪑 Table & Booking</h3>
                                <div className="detail-row">
                                    <span className="detail-label">Table Number:</span>
                                    <span className="detail-value highlighted">
                                        Table {order.tableBooking.tableNumber}
                                    </span>
                                </div>
                                <div className="detail-row">
                                    <span className="detail-label">Booking ID:</span>
                                    <span className="detail-value">#{order.tableBooking.id}</span>
                                </div>
                                <div className="detail-row">
                                    <span className="detail-label">Booking Status:</span>
                                    <span className="detail-value">
                                        {order.tableBooking.status}
                                    </span>
                                </div>
                                <div className="detail-row">
                                    <span className="detail-label">Guests:</span>
                                    <span className="detail-value">
                                        {order.tableBooking.numberOfGuests} people
                                    </span>
                                </div>
                            </div>
                        )}

                        {/* Staff Assigned */}
                        <div className="details-section">
                            <h3 className="section-title">👥 Staff Assigned</h3>
                            <div className="staff-info">
                                <div className="staff-member">
                                    <div className="staff-avatar">👨‍🍳</div>
                                    <div>
                                        <div className="staff-role">Chef</div>
                                        <div className="staff-name">
                                            {order.chef?.name || 'Not assigned'}
                                        </div>
                                        {order.chef && (
                                            <div className="staff-email">{order.chef.email}</div>
                                        )}
                                    </div>
                                </div>

                                <div className="staff-member">
                                    <div className="staff-avatar">🤵</div>
                                    <div>
                                        <div className="staff-role">Waiter</div>
                                        <div className="staff-name">
                                            {order.waiter?.name || 'Not assigned'}
                                        </div>
                                        {order.waiter && (
                                            <div className="staff-email">{order.waiter.email}</div>
                                        )}
                                    </div>
                                </div>
                            </div>
                            {order.autoAssigned && (
                                <div className="auto-assigned-badge">
                                    🤖 Auto-assigned by system
                                </div>
                            )}
                        </div>

                        {/* Order Items - Full Width */}
                        <div className="details-section full-width">
                            <h3 className="section-title">🛒 Order Items</h3>
                            <div className="items-table">
                                <div className="items-header">
                                    <span>Item</span>
                                    <span>Qty</span>
                                    <span>Price</span>
                                    <span>Subtotal</span>
                                </div>
                                {order.items?.map((item, idx) => (
                                    <div key={idx} className="items-row">
                                        <span className="item-name">
                                            {item.menuItem?.name || 'Item'}
                                        </span>
                                        <span>{item.quantity}</span>
                                        <span>${(item.priceAtOrder || 0).toFixed(2)}</span>
                                        <span className="item-subtotal">
                                            ${((item.priceAtOrder || 0) * item.quantity).toFixed(2)}
                                        </span>
                                    </div>
                                ))}
                            </div>
                        </div>

                        {/* Pricing Breakdown */}
                        <div className="details-section">
                            <h3 className="section-title">💰 Pricing</h3>
                            <div className="pricing-breakdown">
                                <div className="price-row">
                                    <span>Subtotal:</span>
                                    <span>${(order.total || 0).toFixed(2)}</span>
                                </div>
                                <div className="price-row">
                                    <span>Tax (0%):</span>
                                    <span>$0.00</span>
                                </div>
                                <div className="price-row total-row">
                                    <span>Total:</span>
                                    <span>${(order.total || 0).toFixed(2)}</span>
                                </div>
                            </div>
                        </div>

                        {/* Payment Information */}
                        <div className="details-section">
                            <h3 className="section-title">💳 Payment Details</h3>
                            <div className="detail-row">
                                <span className="detail-label">Payment Status:</span>
                                <span className={`payment-status-badge ${order.paymentStatus?.toLowerCase() || 'unpaid'}`}>
                                    {order.paymentStatus || 'UNPAID'}
                                </span>
                            </div>
                            {order.paymentMethod && (
                                <div className="detail-row">
                                    <span className="detail-label">Payment Method:</span>
                                    <span className="detail-value">{order.paymentMethod}</span>
                                </div>
                            )}
                            {order.transactionId && (
                                <div className="detail-row">
                                    <span className="detail-label">Transaction ID:</span>
                                    <span className="detail-value mono">{order.transactionId}</span>
                                </div>
                            )}
                            {order.paidAt && (
                                <div className="detail-row">
                                    <span className="detail-label">Paid At:</span>
                                    <span className="detail-value">
                                        {new Date(order.paidAt).toLocaleString()}
                                    </span>
                                </div>
                            )}
                        </div>

                        {/* Order Timeline - Full Width */}
                        <div className="details-section full-width">
                            <h3 className="section-title">📝 Order Timeline</h3>
                            <div className="timeline-visual">
                                <div className="timeline-item">
                                    <div className="timeline-dot active"></div>
                                    <div className="timeline-content">
                                        <div className="timeline-label">Order Created</div>
                                        <div className="timeline-time">
                                            {new Date(order.createdAt).toLocaleString()}
                                        </div>
                                    </div>
                                </div>

                                {order.chefAssignedAt && (
                                    <div className="timeline-item">
                                        <div className="timeline-dot active"></div>
                                        <div className="timeline-content">
                                            <div className="timeline-label">Chef Assigned</div>
                                            <div className="timeline-time">
                                                {new Date(order.chefAssignedAt).toLocaleString()}
                                            </div>
                                        </div>
                                    </div>
                                )}

                                {order.waiterAssignedAt && (
                                    <div className="timeline-item">
                                        <div className="timeline-dot active"></div>
                                        <div className="timeline-content">
                                            <div className="timeline-label">Waiter Assigned</div>
                                            <div className="timeline-time">
                                                {new Date(order.waiterAssignedAt).toLocaleString()}
                                            </div>
                                        </div>
                                    </div>
                                )}

                                {order.preparationStartedAt && (
                                    <div className="timeline-item">
                                        <div className="timeline-dot active"></div>
                                        <div className="timeline-content">
                                            <div className="timeline-label">Preparation Started</div>
                                            <div className="timeline-time">
                                                {new Date(order.preparationStartedAt).toLocaleString()}
                                            </div>
                                        </div>
                                    </div>
                                )}

                                {order.readyAt && (
                                    <div className="timeline-item">
                                        <div className="timeline-dot active"></div>
                                        <div className="timeline-content">
                                            <div className="timeline-label">Order Ready</div>
                                            <div className="timeline-time">
                                                {new Date(order.readyAt).toLocaleString()}
                                            </div>
                                        </div>
                                    </div>
                                )}

                                {order.servedAt && (
                                    <div className="timeline-item">
                                        <div className="timeline-dot active"></div>
                                        <div className="timeline-content">
                                            <div className="timeline-label">Order Served</div>
                                            <div className="timeline-time">
                                                {new Date(order.servedAt).toLocaleString()}
                                            </div>
                                        </div>
                                    </div>
                                )}

                                {order.completedAt && (
                                    <div className="timeline-item">
                                        <div className="timeline-dot completed"></div>
                                        <div className="timeline-content">
                                            <div className="timeline-label">Order Completed</div>
                                            <div className="timeline-time">
                                                {new Date(order.completedAt).toLocaleString()}
                                            </div>
                                        </div>
                                    </div>
                                )}

                                {order.cancelledAt && (
                                    <div className="timeline-item">
                                        <div className="timeline-dot cancelled"></div>
                                        <div className="timeline-content">
                                            <div className="timeline-label">Order Cancelled</div>
                                            <div className="timeline-time">
                                                {new Date(order.cancelledAt).toLocaleString()}
                                            </div>
                                        </div>
                                    </div>
                                )}
                            </div>
                        </div>

                        {/* Admin Notes - Full Width */}
                        <div className="details-section full-width">
                            <h3 className="section-title">📝 Admin Notes</h3>
                            <textarea
                                value={notes}
                                onChange={(e) => setNotes(e.target.value)}
                                placeholder="Add internal notes about this order..."
                                className="notes-textarea"
                                rows="4"
                            />
                            <button
                                onClick={handleSaveNotes}
                                disabled={loading}
                                className="btn-save-notes"
                            >
                                {loading ? 'Saving...' : '💾 Save Notes'}
                            </button>
                        </div>

                        {/* Special Requests */}
                        {order.specialRequests && (
                            <div className="details-section full-width">
                                <h3 className="section-title">📝 Customer Special Requests</h3>
                                <div className="special-requests-box">
                                    {order.specialRequests}
                                </div>
                            </div>
                        )}
                    </div>
                </div>

                <div className="modal-footer">
                    <button onClick={onClose} className="btn-secondary">
                        Close
                    </button>
                </div>
            </div>
        </div>
    );
};

export default OrderDetailsModal;