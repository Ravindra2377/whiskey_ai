import axios from 'axios';

// Centralized Axios instance for API calls
// Configure base URL via REACT_APP_API_BASE_URL, defaulting to local backend
const api = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL || 'http://localhost:8094',
  timeout: 7000,
});

// Optional: simple response error logging
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (process.env.NODE_ENV !== 'production') {
      // eslint-disable-next-line no-console
      console.warn('[API ERROR]', error?.response?.status, error?.message);
    }
    return Promise.reject(error);
  }
);

export default api;
