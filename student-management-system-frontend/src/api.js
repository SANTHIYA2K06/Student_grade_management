import axios from 'axios';
import tokenService from './tokenService';

const api = axios.create({
  baseURL: 'http://localhost:2800',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Flag to prevent multiple refresh token requests
let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

const clearAuthState = () => {
  isRefreshing = false;
  failedQueue = [];
  api.defaults.headers['Authorization'] = null;
  tokenService.clearTokens();
};

export const logout = () => {
  clearAuthState();
  window.dispatchEvent(new CustomEvent('logout'));
};

api.interceptors.request.use(
  (config) => {
    const token = tokenService.getAccessToken();
    if (token) {
      config.headers['Authorization'] = `${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Check if error is due to token expiration
    if ((error.response?.status === 401 || error.response?.data?.code === 401) && !originalRequest._retry) {
      if (isRefreshing) {
        // If token refresh is in progress, add request to queue
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then(token => {
            originalRequest.headers['Authorization'] = token;
            return api(originalRequest);
          })
          .catch(err => Promise.reject(err));
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        const refreshToken = tokenService.getRefreshToken();
        if (!refreshToken) {
          throw new Error('No refresh token available');
        }

        // Use the refresh token endpoint shown in the image
        const response = await axios.get(`${api.defaults.baseURL}/api/auth/refresh`, {
          params: { refreshToken }
        });

        if (response.data?.code === 200 && response.data?.data) {
          const { accessToken, refreshToken: newRefreshToken } = response.data.data;
          tokenService.setTokens(accessToken, newRefreshToken);
          api.defaults.headers['Authorization'] = `${accessToken}`;
          originalRequest.headers['Authorization'] = `${accessToken}`;
          
          processQueue(null, accessToken);
          isRefreshing = false;
          
          return api(originalRequest);
        } else {
          throw new Error('Invalid refresh token response');
        }
      } catch (err) {
        processQueue(err, null);
        isRefreshing = false;
        clearAuthState();
        
        window.dispatchEvent(new CustomEvent('auth-error', { 
          detail: { message: 'Session expired. Please login again.' }
        }));
        
        return Promise.reject(err);
      }
    }
    return Promise.reject(error);
  }
);

export default api;
