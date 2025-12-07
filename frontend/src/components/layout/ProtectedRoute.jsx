/*
  Protected Route Component
  -------------------------
  Wraps routes that require authentication.
  Redirects to /login if user is not authenticated.
  
  Usage:
  <Route element={<ProtectedRoute />}>
    <Route path="/profile" element={<Profile />} />
  </Route>
*/

import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import { Spinner } from '@/components/common';

const ProtectedRoute = () => {
  const { isAuthenticated, loading } = useAuth();
  const location = useLocation();

  // Show loading spinner while checking auth
  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-background">
        <Spinner size="lg" className="text-primary" />
      </div>
    );
  }

  // Redirect to login if not authenticated
  if (!isAuthenticated) {
    // Save the attempted URL for redirecting after login
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // Render the protected content
  return <Outlet />;
};

export default ProtectedRoute;
