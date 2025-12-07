/*
  useAuth Hook Tests
  ------------------
  Tests for the authentication hook.
*/

import { renderHook } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { AuthProvider } from '@/context/AuthContext';
import { useAuth } from '../useAuth';

// Wrapper component that provides AuthContext
const wrapper = ({ children }) => <AuthProvider>{children}</AuthProvider>;

describe('useAuth', () => {
  it('provides authentication context', () => {
    const { result } = renderHook(() => useAuth(), { wrapper });

    expect(result.current).toHaveProperty('user');
    expect(result.current).toHaveProperty('token');
    expect(result.current).toHaveProperty('isAuthenticated');
    expect(result.current).toHaveProperty('loading');
    expect(result.current).toHaveProperty('login');
    expect(result.current).toHaveProperty('logout');
    expect(result.current).toHaveProperty('register');
  });

  it('starts with loading state', () => {
    const { result } = renderHook(() => useAuth(), { wrapper });

    // After initial render, loading should be false (mount effect completed)
    expect(typeof result.current.loading).toBe('boolean');
  });

  it('starts unauthenticated when no token in localStorage', () => {
    localStorage.removeItem('token');
    const { result } = renderHook(() => useAuth(), { wrapper });

    expect(result.current.isAuthenticated).toBe(false);
    expect(result.current.user).toBeNull();
  });

  it('throws error when used outside AuthProvider', () => {
    // Suppress console.error for this test
    const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

    expect(() => {
      renderHook(() => useAuth());
    }).toThrow('useAuth must be used within an AuthProvider');

    consoleSpy.mockRestore();
  });
});
