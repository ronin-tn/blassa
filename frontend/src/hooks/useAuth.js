/*
  useAuth Hook - Consume Auth Context
  ------------------------------------
  A simple hook to access authentication state and functions.
  
  Usage:
  const { user, isAuthenticated, login, logout, register } = useAuth();
  
  if (isAuthenticated) {
    console.log('Logged in as:', user.email);
  }
*/

import { useContext } from 'react';
import { AuthContext } from '@/context/AuthContext';

export const useAuth = () => {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }

  return context;
};

export default useAuth;
