/*
  App.jsx - Main Application with Routing
  -----------------------------------------
  Routes:
  - / (landing) - Public landing page
  - /login - Login page
  - /register - Register page
  - /search - Search results page
  - /dashboard - Protected dashboard (after login)
  - * - 404 Not Found
  
  Performance:
  - Lazy loading for route components
  - Suspense with loading fallback
*/

import { lazy, Suspense } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from '@/context/AuthContext';
import ProtectedRoute from '@/components/layout/ProtectedRoute';
import ErrorBoundary from '@/components/ErrorBoundary';
import { Spinner } from '@/components/common';

// Lazy load pages for better initial bundle size
const Landing = lazy(() => import('@/pages/Landing'));
const Login = lazy(() => import('@/pages/auth/Login'));
const Register = lazy(() => import('@/pages/auth/Register'));
const Search = lazy(() => import('@/pages/Search'));
const NotFound = lazy(() => import('@/pages/NotFound'));
const Dashboard = lazy(() => import('@/pages/Dashboard'));
const Profile = lazy(() => import('@/pages/profile/Profile'));
const RideDetails = lazy(() => import('@/pages/ride/RideDetails'));
const CreateRide = lazy(() => import('@/pages/ride/CreateRide'));
const EditRide = lazy(() => import('@/pages/ride/EditRide'));
const MyRides = lazy(() => import('@/pages/driver/MyRides'));
const MyBookings = lazy(() => import('@/pages/passenger/MyBookings'));

// Loading fallback component
const PageLoader = () => (
  <div className="min-h-screen flex items-center justify-center bg-background">
    <div className="text-center space-y-4">
      <Spinner size="lg" className="text-primary mx-auto" />
      <p className="text-muted-foreground">Chargement...</p>
    </div>
  </div>
);

function App() {
  return (
    <ErrorBoundary>
      <BrowserRouter>
        <AuthProvider>
          <Suspense fallback={<PageLoader />}>
            <Routes>
              {/* Public routes */}
              <Route path="/" element={<Landing />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/search" element={<Search />} />
              <Route path="/ride/:id" element={<RideDetails />} />

              {/* Protected routes */}
              <Route element={<ProtectedRoute />}>
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="/profile" element={<Profile />} />
                <Route path="/publish" element={<CreateRide />} />
                <Route path="/rides/create" element={<CreateRide />} /> {/* Alias for Search Empty State */}
                <Route path="/ride/:id/edit" element={<EditRide />} />
                <Route path="/my-rides" element={<MyRides />} />
                <Route path="/my-bookings" element={<MyBookings />} />
                {/* More protected routes will be added here */}
              </Route>

              {/* 404 - Catch all unmatched routes */}
              <Route path="*" element={<NotFound />} />
            </Routes>
          </Suspense>
        </AuthProvider>
      </BrowserRouter>
    </ErrorBoundary>
  );
}

export default App;
