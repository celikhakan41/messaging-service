import React, { useState } from 'react';
import { register } from './api';

const Register = ({ onRegisterSuccess }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');

    const handleSubmit = async e => {
        e.preventDefault();

        if (password !== confirmPassword) {
            alert('Passwords do not match.');
            return;
        }

        try {
            const response = await register(username, password);
            alert('Registration successful! You can now log in.');
            onRegisterSuccess(); // Register sonrası Login sayfasına geçebiliriz
        } catch (error) {
            console.error(error);
            alert('Registration failed. Username may already exist.');
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <h2>Register</h2>
            <input
                placeholder="Username"
                autoComplete="username"
                onChange={e => setUsername(e.target.value)}
            />
            <input
                placeholder="Password"
                type="password"
                autoComplete="new-password"
                onChange={e => setPassword(e.target.value)}
            />
            <input
                placeholder="Confirm Password"
                type="password"
                autoComplete="new-password"
                onChange={e => setConfirmPassword(e.target.value)}
            />
            <button type="submit">Register</button>
        </form>
    );
};

export default Register;