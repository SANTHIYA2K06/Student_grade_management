class TokenService {
    getAccessToken() {
      return localStorage.getItem('accessToken');
    }
  
    getRefreshToken() {
      return localStorage.getItem('refreshToken');
    }
  
    setTokens(accessToken, refreshToken) {
      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);
    }
  
    clearTokens() {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('username');
    }
  }
  
const tokenService = new TokenService();
export default tokenService;
