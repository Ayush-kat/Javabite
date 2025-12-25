import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { register } from '../api/AuthApi';
import '../styles/Auth.css';

function SignupPage() {
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
        confirmPassword: ''
    });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
        // Clear errors when user starts typing
        if (error) setError('');
    };

    const handleRegister = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        // Validation
        if (formData.password !== formData.confirmPassword) {
            setError('Passwords do not match');
            return;
        }

        if (formData.password.length < 6) {
            setError('Password must be at least 6 characters long');
            return;
        }

        if (formData.username.length < 3) {
            setError('Username must be at least 3 characters long');
            return;
        }

        setIsLoading(true);

        try {
            await register(formData.username, formData.email, formData.password);

            setSuccess('Registration successful! Redirecting to login...');
            setTimeout(() => {
                navigate('/login');
            }, 2000);
        } catch (err) {
            setError(err.message || 'Registration failed. Please try again.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="auth-page">
            <div className="auth-container single-section">
                {/* Right Side - Register Form */}
                <div className="auth-form-section centered">
                    <div className="auth-form-header">
                        <div className="auth-brand-small">☕ JavaBite</div>
                        <h1 className="auth-form-title">Create Account</h1>
                        <p className="auth-form-subtitle">
                            Sign up to start ordering delicious coffee
                        </p>
                    </div>

                    {error && <div className="auth-error">{error}</div>}
                    {success && <div className="auth-success">{success}</div>}

                    <form onSubmit={handleRegister} className="auth-form">
                        <div className="auth-form-group">
                            <label className="auth-form-label">Username</label>
                            <input
                                type="text"
                                name="username"
                                value={formData.username}
                                onChange={handleChange}
                                placeholder="Choose a username"
                                className="auth-form-input"
                                required
                                disabled={isLoading}
                                minLength="3"
                            />
                        </div>

                        <div className="auth-form-group">
                            <label className="auth-form-label">Email Address</label>
                            <input
                                type="email"
                                name="email"
                                value={formData.email}
                                onChange={handleChange}
                                placeholder="you@example.com"
                                className="auth-form-input"
                                required
                                disabled={isLoading}
                            />
                        </div>

                        <div className="auth-form-group">
                            <label className="auth-form-label">Password</label>
                            <input
                                type="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                placeholder="Minimum 6 characters"
                                className="auth-form-input"
                                required
                                disabled={isLoading}
                                minLength="6"
                            />
                        </div>

                        <div className="auth-form-group">
                            <label className="auth-form-label">Confirm Password</label>
                            <input
                                type="password"
                                name="confirmPassword"
                                value={formData.confirmPassword}
                                onChange={handleChange}
                                placeholder="Re-enter password"
                                className="auth-form-input"
                                required
                                disabled={isLoading}
                            />
                        </div>

                        <button
                            type="submit"
                            className="auth-submit-btn"
                            disabled={isLoading}
                        >
                            {isLoading ? 'Creating Account...' : 'Sign Up'}
                        </button>
                    </form>

                    <div className="auth-switch">
                        Already have an account? {' '}
                        <Link to="/login" className="auth-switch-link">
                            Log In
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default SignupPage;