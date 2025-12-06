/*
  App.jsx - Main Application with Routing
  -----------------------------------------
  Routes:
  - / (landing) - Public landing page
  - /login - Login page
  - /register - Register page
  - /dashboard - Protected dashboard (after login)
*/

import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from '@/context/AuthContext';
import ProtectedRoute from '@/components/layout/ProtectedRoute';

// Public pages
import Landing from '@/pages/Landing';
import Login from '@/pages/auth/Login';
import Register from '@/pages/auth/Register';
import Search from '@/pages/Search';

// Placeholder for dashboard (we'll build this next)
const Dashboard = () => (
  <div className="min-h-screen bg-background p-8">
    <div className="max-w-2xl mx-auto">
      <h1 className="text-3xl font-bold text-primary mb-4">🚗 Tableau de bord</h1>
      <p className="text-muted-foreground mb-8">
        Bienvenue ! Vous êtes connecté.
      </p>
      <div className="space-y-2">
        <p>• Rechercher des trajets</p>
        <p>• Publier un trajet</p>
        <p>• Voir mes réservations</p>
      </div>
      <button
        onClick={() => {
          localStorage.removeItem('token');
          localStorage.removeItem('user');
          window.location.href = '/';
        }}
        className="mt-8 text-primary hover:underline"
      >
        Se déconnecter
      </button>
    </div>
  </div>
);

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* Public routes */}
          <Route path="/" element={<Landing />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/search" element={<Search />} />

          {/* Protected routes */}
          <Route element={<ProtectedRoute />}>
            <Route path="/dashboard" element={<Dashboard />} />
            {/* More protected routes will be added here */}
          </Route>

          {/* Fallback - redirect to home */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
