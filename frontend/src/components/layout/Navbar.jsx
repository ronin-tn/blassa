/*
  Navbar Component - Floating Pill Design
  ---------------------------------------
  Modern 'Floating Island' navigation.
*/

import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import { Button } from '@/components/ui/button';
import { Bell } from 'lucide-react';

const Navbar = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const { isAuthenticated, logout, user } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
    setIsMenuOpen(false);
    setIsProfileOpen(false);
  };

  // Get initials for avatar
  const getInitials = () => {
    if (!user || (!user.firstName && !user.lastName)) return 'U';
    return `${user.firstName?.[0] || ''}${user.lastName?.[0] || ''}`.toUpperCase();
  };

  return (
    <>
      <nav className="fixed top-6 left-1/2 transform -translate-x-1/2 w-[95%] max-w-5xl z-50">
        <div className="bg-white/90 backdrop-blur-xl shadow-2xl rounded-full px-6 py-3 border border-white/20 transition-all duration-300 hover:shadow-[0_20px_40px_rgba(0,0,0,0.1)]">
          <div className="flex justify-between items-center h-full">
            {/* LEFT SIDE: Logo */}
            <Link to="/" className="flex items-center gap-2 group mr-8">
              <span className="text-2xl">🚗</span>
              <span className="text-xl font-bold text-slate-800 tracking-tight group-hover:text-amber-600 transition-colors">
                Blassa
              </span>
            </Link>

            {/* RIGHT SIDE: Actions */}
            <div className="flex items-center gap-2 md:gap-4">
              {/* Desktop Actions */}
              <div className="hidden md:flex items-center gap-4">
                <Link to="/rides/create">
                  <Button
                    variant="action"
                    className="rounded-full px-6 font-bold shadow-lg shadow-orange-500/20 hover:shadow-orange-500/40 transition-all"
                  >
                    Publier un trajet
                  </Button>
                </Link>

                {isAuthenticated && (
                  <button className="p-2 rounded-full text-slate-600 hover:bg-slate-100 hover:text-amber-600 transition-colors relative">
                    <Bell className="w-5 h-5" />
                    <span className="absolute top-2 right-2 w-2 h-2 bg-red-500 rounded-full border border-white"></span>
                  </button>
                )}
              </div>

              {/* User Profile / Auth */}
              <div className="flex items-center gap-2 pl-2 md:pl-4 border-l border-slate-200/60">
                {isAuthenticated ? (
                  <div className="relative">
                    <button
                      onClick={() => setIsProfileOpen(!isProfileOpen)}
                      className="flex items-center gap-2 p-1 pl-2 pr-1 rounded-full hover:bg-slate-100/50 transition-all"
                    >
                      <div className="hidden sm:block text-sm font-semibold text-slate-700 ml-1">
                        {user?.firstName}
                      </div>
                      <div className="w-9 h-9 rounded-full bg-gradient-to-br from-amber-100 to-orange-100 flex items-center justify-center text-orange-700 font-bold text-sm ring-2 ring-white shadow-sm">
                        {getInitials()}
                      </div>
                    </button>

                    {/* Dropdown - Adjusted for floating pill */}
                    {isProfileOpen && (
                      <>
                        <div
                          className="fixed inset-0 z-40"
                          onClick={() => setIsProfileOpen(false)}
                        ></div>
                        <div className="absolute right-0 top-12 mt-2 w-64 bg-white rounded-3xl shadow-[0_20px_60px_-15px_rgba(0,0,0,0.3)] border border-slate-100 p-2 z-50 animate-in fade-in zoom-in-95 duration-200">
                          <div className="px-4 py-3 border-b border-slate-50 mb-1">
                            <p className="text-sm font-bold text-slate-900">
                              {user?.firstName} {user?.lastName}
                            </p>
                            <p className="text-xs text-slate-500 truncate">{user?.email}</p>
                          </div>
                          <Link
                            to="/my-bookings"
                            className="block px-4 py-2.5 rounded-xl text-sm font-medium text-slate-600 hover:bg-slate-50 hover:text-amber-600 transition-colors"
                            onClick={() => setIsProfileOpen(false)}
                          >
                            Mes réservations
                          </Link>
                          <Link
                            to="/profile"
                            className="block px-4 py-2.5 rounded-xl text-sm font-medium text-slate-600 hover:bg-slate-50 hover:text-amber-600 transition-colors"
                            onClick={() => setIsProfileOpen(false)}
                          >
                            Mon Profil
                          </Link>
                          <div className="h-px bg-slate-50 my-1"></div>
                          <button
                            onClick={handleLogout}
                            className="block w-full text-left px-4 py-2.5 rounded-xl text-sm font-medium text-red-600 hover:bg-red-50 transition-colors"
                          >
                            Déconnexion
                          </button>
                        </div>
                      </>
                    )}
                  </div>
                ) : (
                  <div className="flex items-center gap-2">
                    <Link to="/login">
                      <Button
                        variant="ghost"
                        className="rounded-full text-slate-600 hover:text-amber-600 hover:bg-amber-50"
                      >
                        Connexion
                      </Button>
                    </Link>
                    <Link to="/register" className="hidden sm:inline-flex">
                      <Button
                        variant="outline"
                        className="rounded-full border-slate-200 text-slate-700 hover:text-amber-600 hover:border-amber-200"
                      >
                        Inscription
                      </Button>
                    </Link>
                  </div>
                )}
              </div>

              {/* Mobile Hamburger - Styled for Pill */}
              <button
                className="md:hidden p-2 rounded-full text-slate-600 hover:bg-slate-100"
                onClick={() => setIsMenuOpen(!isMenuOpen)}
              >
                {isMenuOpen ? (
                  <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M6 18L18 6M6 6l12 12"
                    />
                  </svg>
                ) : (
                  <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M4 6h16M4 12h16M4 18h16"
                    />
                  </svg>
                )}
              </button>
            </div>
          </div>
        </div>

        {/* Mobile Menu - Floating Card */}
        {isMenuOpen && (
          <div className="absolute top-20 left-0 right-0 bg-white/95 backdrop-blur-xl rounded-3xl shadow-2xl border border-white/20 p-4 animate-in slide-in-from-top-4 duration-300">
            <div className="space-y-2">
              <Link
                to="/rides/create"
                className="flex items-center justify-between p-3 rounded-2xl bg-orange-50 text-orange-700 font-bold"
                onClick={() => setIsMenuOpen(false)}
              >
                Publier un trajet
                <span className="bg-white p-1 rounded-full">
                  <span className="block w-4 h-4 bg-orange-500 rounded-full"></span>
                </span>
              </Link>

              {isAuthenticated ? (
                <>
                  <Link
                    to="/my-bookings"
                    className="block p-3 rounded-2xl hover:bg-slate-50 text-slate-700 font-medium"
                    onClick={() => setIsMenuOpen(false)}
                  >
                    Mes réservations
                  </Link>
                  <button
                    onClick={handleLogout}
                    className="block w-full text-left p-3 rounded-2xl hover:bg-red-50 text-red-600 font-medium"
                  >
                    Déconnexion
                  </button>
                </>
              ) : (
                <div className="grid grid-cols-2 gap-3 pt-2">
                  <Link to="/login" onClick={() => setIsMenuOpen(false)}>
                    <Button variant="ghost" className="w-full rounded-xl">
                      Connexion
                    </Button>
                  </Link>
                  <Link to="/register" onClick={() => setIsMenuOpen(false)}>
                    <Button variant="brand" className="w-full rounded-xl">
                      Inscription
                    </Button>
                  </Link>
                </div>
              )}
            </div>
          </div>
        )}
      </nav>

      {/* Mobile Sticky Bottom Nav */}
      <div className="md:hidden fixed bottom-4 left-4 right-4 z-50">
        <div className="bg-slate-900/90 backdrop-blur-lg text-white rounded-full p-1.5 shadow-2xl border border-white/10 flex items-center justify-between px-6">
          <Link
            to="/"
            onClick={() =>
              setTimeout(
                () => document.getElementById('search')?.scrollIntoView({ behavior: 'smooth' }),
                100
              )
            }
            className="flex flex-col items-center py-2 px-4 rounded-full hover:bg-white/10 transition-colors"
          >
            <span className="text-sm font-bold">Rechercher</span>
          </Link>
          <div className="w-px h-6 bg-white/20"></div>
          <Link
            to="/rides/create"
            className="flex flex-col items-center py-2 px-4 rounded-full text-orange-400 hover:bg-white/10 transition-colors"
          >
            <span className="text-sm font-bold">Publier</span>
          </Link>
        </div>
      </div>
    </>
  );
};

export default Navbar;
