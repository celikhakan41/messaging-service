import React, { useState } from 'react';
import { login } from './api';
import Register from './Register';

const Login = ({ onLogin }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [showRegister, setShowRegister] = useState(false);

    const handleSubmit = async e => {
        e.preventDefault();
        try {
            const response = await login(username, password);
            onLogin(response.data.token, username);
        } catch (error) {
            alert("Login failed");
        }
    };

    if (showRegister) {
        return <Register onRegisterSuccess={() => setShowRegister(false)} />;
    }

    return (
        <form onSubmit={handleSubmit}>
            <h2>Login</h2>
            <input
                placeholder="Username"
                autoComplete="username"
                onChange={e => setUsername(e.target.value)}
            />
            <input
                placeholder="Password"
                type="password"
                autoComplete="current-password"
                onChange={e => setPassword(e.target.value)}
            />
            <button type="submit">Login</button>

            <p>
                Don't have an account?{' '}
                <button type="button" onClick={() => setShowRegister(true)}>
                    Register
                </button>
            </p>
        </form>
    );
};

export default Login;