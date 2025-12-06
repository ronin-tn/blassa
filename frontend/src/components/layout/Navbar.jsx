/*
  Navbar Component - Sticky Navigation
  -------------------------------------
  Responsive navbar with:
  - Desktop: Full navigation links
  - Mobile: Hamburger menu with slide-in drawer
*/

import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import { Button } from '@/components/ui/button';

const Navbar = () => {
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const { isAuthenticated, logout, user } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/');
        setIsMenuOpen(false);
    };

    return (
        <nav className="sticky top-0 bg-white/95 backdrop-blur-sm shadow-sm z-50">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="flex justify-between items-center h-16">
                    {/* Logo */}
                    <Link to="/" className="flex items-center gap-2">
                        <span className="text-2xl">🚗</span>
                        <span className="text-xl font-bold text-primary">Blassa</span>
                    </Link>

                    {/* Desktop Navigation */}
                    <div className="hidden md:flex items-center gap-4">
                        <a href="#search" className="text-muted-foreground hover:text-primary transition-colors">
                            Rechercher
                        </a>

                        {isAuthenticated ? (
                            <>
                                <Link to="/rides/create">
                                    <Button variant="outline" size="sm">
                                        Publier un trajet
                                    </Button>
                                </Link>
                                <Link to="/my-bookings">
                                    <Button variant="ghost" size="sm">
                                        Mes réservations
                                    </Button>
                                </Link>
                                <Button variant="ghost" size="sm" onClick={handleLogout}>
                                    Déconnexion
                                </Button>
                            </>
                        ) : (
                            <>
                                <Link to="/login">
                                    <Button variant="ghost" size="sm">
                                        Connexion
                                    </Button>
                                </Link>
                                <Link to="/register">
                                    <Button variant="brand" size="sm">
                                        Inscription
                                    </Button>
                                </Link>
                            </>
                        )}
                    </div>

                    {/* Mobile hamburger */}
                    <button
                        className="md:hidden p-2 rounded-md hover:bg-muted"
                        onClick={() => setIsMenuOpen(!isMenuOpen)}
                        aria-label="Menu"
                    >
                        {isMenuOpen ? (
                            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                            </svg>
                        ) : (
                            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
                            </svg>
                        )}
                    </button>
                </div>
            </div>

            {/* Mobile Menu Drawer */}
            {isMenuOpen && (
                <div className="md:hidden border-t bg-white">
                    <div className="px-4 py-4 space-y-3">
                        <a
                            href="#search"
                            className="block py-2 text-muted-foreground hover:text-primary"
                            onClick={() => setIsMenuOpen(false)}
                        >
                            Rechercher
                        </a>

                        {isAuthenticated ? (
                            <>
                                <Link
                                    to="/rides/create"
                                    className="block py-2 text-muted-foreground hover:text-primary"
                                    onClick={() => setIsMenuOpen(false)}
                                >
                                    Publier un trajet
                                </Link>
                                <Link
                                    to="/my-bookings"
                                    className="block py-2 text-muted-foreground hover:text-primary"
                                    onClick={() => setIsMenuOpen(false)}
                                >
                                    Mes réservations
                                </Link>
                                <button
                                    className="block w-full text-left py-2 text-destructive"
                                    onClick={handleLogout}
                                >
                                    Déconnexion
                                </button>
                            </>
                        ) : (
                            <div className="flex flex-col gap-2 pt-2">
                                <Link to="/login" onClick={() => setIsMenuOpen(false)}>
                                    <Button variant="outline" className="w-full">
                                        Connexion
                                    </Button>
                                </Link>
                                <Link to="/register" onClick={() => setIsMenuOpen(false)}>
                                    <Button variant="brand" className="w-full">
                                        Inscription
                                    </Button>
                                </Link>
                            </div>
                        )}
                    </div>
                </div>
            )}
        </nav>
    );
};

export default Navbar;
