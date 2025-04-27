import axios from 'axios';

const BASE_URL = "http://localhost:8080/api";

export const register = (username, password) =>
    axios.post(`${BASE_URL}/auth/register`, { username, password });

export const login = (username, password) =>
    axios.post(`${BASE_URL}/auth/login`, { username, password });