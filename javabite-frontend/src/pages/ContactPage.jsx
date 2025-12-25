import React, { useState } from 'react';
import '../styles/Contact.css';

const ContactPage = () => {
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        phone: '',
        subject: '',
        message: ''
    });

    const [submitted, setSubmitted] = useState(false);

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        // Here you would normally send the data to your backend
        console.log('Form submitted:', formData);
        setSubmitted(true);

        // Reset form after 3 seconds
        setTimeout(() => {
            setFormData({
                name: '',
                email: '',
                phone: '',
                subject: '',
                message: ''
            });
            setSubmitted(false);
        }, 3000);
    };

    return (
        <div className="contact-page">
            {/* Hero Section */}
            <section className="contact-hero">
                <div className="contact-hero-content">
                    <h1 className="contact-hero-title">Get In Touch</h1>
                    <p className="contact-hero-subtitle">
                        We'd love to hear from you. Drop us a message!
                    </p>
                </div>
            </section>

            {/* Contact Content */}
            <section className="contact-content">
                <div className="contact-container">
                    {/* Contact Info */}
                    <div className="contact-info-section">
                        <h2 className="contact-section-title">Contact Information</h2>

                        <div className="contact-info-cards">
                            <div className="contact-info-card">
                                <div className="contact-info-icon">📍</div>
                                <h3>Visit Us</h3>
                                <p>123 Coffee Street<br/>Downtown, City 12345</p>
                            </div>

                            <div className="contact-info-card">
                                <div className="contact-info-icon">📞</div>
                                <h3>Call Us</h3>
                                <p>(555) 123-4567<br/>Mon-Fri, 7AM-8PM</p>
                            </div>

                            <div className="contact-info-card">
                                <div className="contact-info-icon">✉️</div>
                                <h3>Email Us</h3>
                                <p>hello@javabite.com<br/>support@javabite.com</p>
                            </div>

                            <div className="contact-info-card">
                                <div className="contact-info-icon">🌐</div>
                                <h3>Follow Us</h3>
                                <div className="social-links">
                                    <a href="#" className="social-link">Facebook</a>
                                    <a href="#" className="social-link">Instagram</a>
                                    <a href="#" className="social-link">Twitter</a>
                                </div>
                            </div>
                        </div>

                        {/* Hours */}
                        <div className="hours-section">
                            <h3 className="hours-title">Opening Hours</h3>
                            <div className="hours-list">
                                <div className="hours-item">
                                    <span className="hours-day">Monday - Friday</span>
                                    <span className="hours-time">7:00 AM - 8:00 PM</span>
                                </div>
                                <div className="hours-item">
                                    <span className="hours-day">Saturday</span>
                                    <span className="hours-time">8:00 AM - 9:00 PM</span>
                                </div>
                                <div className="hours-item">
                                    <span className="hours-day">Sunday</span>
                                    <span className="hours-time">8:00 AM - 9:00 PM</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Contact Form */}
                    <div className="contact-form-section">
                        <h2 className="contact-section-title">Send Us a Message</h2>

                        {submitted ? (
                            <div className="success-message">
                                <div className="success-icon">✓</div>
                                <h3>Thank You!</h3>
                                <p>Your message has been sent successfully. We'll get back to you soon.</p>
                            </div>
                        ) : (
                            <form onSubmit={handleSubmit} className="contact-form">
                                <div className="form-row">
                                    <div className="form-group">
                                        <label htmlFor="name">Full Name *</label>
                                        <input
                                            type="text"
                                            id="name"
                                            name="name"
                                            value={formData.name}
                                            onChange={handleChange}
                                            required
                                            placeholder="John Doe"
                                        />
                                    </div>

                                    <div className="form-group">
                                        <label htmlFor="email">Email Address *</label>
                                        <input
                                            type="email"
                                            id="email"
                                            name="email"
                                            value={formData.email}
                                            onChange={handleChange}
                                            required
                                            placeholder="john@example.com"
                                        />
                                    </div>
                                </div>

                                <div className="form-row">
                                    <div className="form-group">
                                        <label htmlFor="phone">Phone Number</label>
                                        <input
                                            type="tel"
                                            id="phone"
                                            name="phone"
                                            value={formData.phone}
                                            onChange={handleChange}
                                            placeholder="(555) 123-4567"
                                        />
                                    </div>

                                    <div className="form-group">
                                        <label htmlFor="subject">Subject *</label>
                                        <select
                                            id="subject"
                                            name="subject"
                                            value={formData.subject}
                                            onChange={handleChange}
                                            required
                                        >
                                            <option value="">Select a subject</option>
                                            <option value="general">General Inquiry</option>
                                            <option value="catering">Catering Services</option>
                                            <option value="feedback">Feedback</option>
                                            <option value="complaint">Complaint</option>
                                            <option value="partnership">Partnership</option>
                                        </select>
                                    </div>
                                </div>

                                <div className="form-group">
                                    <label htmlFor="message">Message *</label>
                                    <textarea
                                        id="message"
                                        name="message"
                                        value={formData.message}
                                        onChange={handleChange}
                                        required
                                        rows="6"
                                        placeholder="Tell us what's on your mind..."
                                    ></textarea>
                                </div>

                                <button type="submit" className="submit-btn">
                                    Send Message
                                </button>
                            </form>
                        )}
                    </div>
                </div>
            </section>

            {/* Map Section */}
            <section className="map-section">
                <div className="map-container">
                    <img
                        src="https://images.unsplash.com/photo-1524661135-423995f22d0b?w=1400&h=500&fit=crop"
                        alt="Location map"
                        className="map-image"
                    />
                    <div className="map-overlay">
                        <h3>Find Us Here</h3>
                        <p>123 Coffee Street, Downtown</p>
                        <button className="directions-btn">Get Directions</button>
                    </div>
                </div>
            </section>
        </div>
    );
};

export default ContactPage;