import React, { createContext, useState, useContext, useEffect } from 'react';
import { authApi } from '../api/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [loading, setLoading] = useState(true);
    const [initialCheckDone, setInitialCheckDone] = useState(false);

    // Check authentication only on mount
    useEffect(() => {
        if (!initialCheckDone) {
            checkAuth();
        }
    }, [initialCheckDone]);

    const checkAuth = async () => {
        try {
            setLoading(true);
            const userData = await authApi.getCurrentUser();
            setUser(userData);
            setIsAuthenticated(true);
        } catch (error) {
            // Silent fail - user not authenticated
            setUser(null);
            setIsAuthenticated(false);
        } finally {
            setLoading(false);
            setInitialCheckDone(true);
        }
    };

    const login = async (userData) => {
        // Set user immediately from login response
        setUser(userData);
        setIsAuthenticated(true);
        setLoading(false);
        // Don't re-check - trust the login response
    };

    const logout = async () => {
        try {
            await authApi.logout();
        } catch (error) {
            console.error('Logout error:', error);
        } finally {
            setUser(null);
            setIsAuthenticated(false);
        }
    };

    if (loading && !initialCheckDone) {
        return (
            <div style={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                minHeight: '100vh',
                fontSize: '1.5rem',
                color: '#6d5739'
            }}>
                Loading...
            </div>
        );
    }

    return (
        <AuthContext.Provider value={{ user, isAuthenticated, login, logout, checkAuth }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};