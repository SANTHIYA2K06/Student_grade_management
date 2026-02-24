import React from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import LoginPage from './LoginPage'; 
import AdminHome from './Staff/AdminHome';
import StudentHome from './Student/StudentHome';
import StaffHome from './Staff/StaffHome';
import './index.css';

// Helper component to check if the user is logged in and has the correct role
const RequireAuth = ({ children, allowedRoles }) => {
  const isLoggedIn = !!localStorage.getItem('accessToken');
  const userRole = localStorage.getItem('userRole');

  if (!isLoggedIn) {
    return <Navigate to="/" replace />;
  }

  // Redirect to appropriate home page if user tries to access unauthorized route
  if (!allowedRoles.includes(userRole)) {
    switch (userRole) {
      case 'Admin':
        return <Navigate to="/admin-home" replace />;
      case 'Staff':
        return <Navigate to="/staff-home" replace />;
      case 'Student':
        return <Navigate to="/student-home" replace />;
      default:
        return <Navigate to="/" replace />;
    }
  }

  return children;
};

// Helper component to redirect logged-in users from login page
const RequireNoAuth = ({ children }) => {
  const isLoggedIn = !!localStorage.getItem('accessToken');
  const userRole = localStorage.getItem('userRole');

  if (isLoggedIn) {
    switch (userRole) {
      case 'Admin':
        return <Navigate to="/admin-home" replace />;
      case 'Staff':
        return <Navigate to="/staff-home" replace />;
      case 'Student':
        return <Navigate to="/student-home" replace />;
      default:
        return <Navigate to="/" replace />;
    }
  }
  return children;
};

// Main app routing
const Root = () => (
  <Router>
    <Routes>
      {/* Login routes */}
      <Route path="/" element={
        <RequireNoAuth>
          <LoginPage />
        </RequireNoAuth>
      } />
      <Route path="/login-page" element={
        <RequireNoAuth>
          <LoginPage />
        </RequireNoAuth>
      } />

      {/* Protected routes with role-based access */}
      <Route
        path="/staff-home"
        element={
          <RequireAuth allowedRoles={['Staff']}>
            <StaffHome />
          </RequireAuth>
        }
      />
      <Route
        path="/student-home"
        element={
          <RequireAuth allowedRoles={['Student']}>
            <StudentHome />
          </RequireAuth>
        }
      />
      <Route
        path="/admin-home"
        element={
          <RequireAuth allowedRoles={['Admin']}>
            <AdminHome />
          </RequireAuth>
        }
      />

      {/* Redirect any unknown paths to appropriate home page or login */}
      <Route path="*" element={
        <RequireAuth allowedRoles={['Admin', 'Staff', 'Student']}>
          {() => {
            const userRole = localStorage.getItem('userRole');
            switch (userRole) {
              case 'Admin':
                return <Navigate to="/admin-home" replace />;
              case 'Staff':
                return <Navigate to="/staff-home" replace />;
              case 'Student':
                return <Navigate to="/student-home" replace />;
              default:
                return <Navigate to="/" replace />;
            }
          }}
        </RequireAuth>
      } />
    </Routes>
  </Router>
);

const rootElement = document.getElementById('root');
if (rootElement) {
  createRoot(rootElement).render(<Root />);
}
