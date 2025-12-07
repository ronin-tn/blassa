/*
  Dashboard Page
  --------------
  Protected dashboard page shown after login.
  Displays user's rides, bookings, and quick actions.
*/

import { Link } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import Navbar from '@/components/layout/Navbar';
import Footer from '@/components/layout/Footer';
import { Car, Calendar, Search, Plus, User, LogOut, MapPin } from 'lucide-react';

const Dashboard = () => {
  const { user, logout } = useAuth();

  const handleLogout = () => {
    logout();
    window.location.href = '/';
  };

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <Navbar />

      <main className="flex-1 pt-32 pb-16">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
          {/* Welcome Header */}
          <div className="mb-8">
            <h1 className="text-3xl font-bold text-foreground">
              Bonjour{user?.firstName ? `, ${user.firstName}` : ''} ! 👋
            </h1>
            <p className="text-muted-foreground mt-2">
              Bienvenue sur votre tableau de bord Blassa.
            </p>
          </div>

          {/* Quick Actions */}
          <div className="grid md:grid-cols-2 gap-4 mb-8">
            <Link to="/rides/create">
              <Card className="hover:shadow-lg transition-shadow cursor-pointer border-l-4 border-l-action">
                <CardContent className="p-6 flex items-center gap-4">
                  <div className="w-12 h-12 bg-action/10 rounded-full flex items-center justify-center">
                    <Plus className="w-6 h-6 text-action" />
                  </div>
                  <div>
                    <h3 className="font-bold text-foreground">Publier un trajet</h3>
                    <p className="text-sm text-muted-foreground">Partagez vos frais de route</p>
                  </div>
                </CardContent>
              </Card>
            </Link>

            <Link to="/">
              <Card className="hover:shadow-lg transition-shadow cursor-pointer border-l-4 border-l-primary">
                <CardContent className="p-6 flex items-center gap-4">
                  <div className="w-12 h-12 bg-primary/10 rounded-full flex items-center justify-center">
                    <Search className="w-6 h-6 text-primary" />
                  </div>
                  <div>
                    <h3 className="font-bold text-foreground">Chercher un trajet</h3>
                    <p className="text-sm text-muted-foreground">Trouvez votre prochain voyage</p>
                  </div>
                </CardContent>
              </Card>
            </Link>
          </div>

          {/* Dashboard Cards */}
          <div className="grid md:grid-cols-3 gap-4 mb-8">
            <Card>
              <CardHeader className="pb-2">
                <CardTitle className="text-lg flex items-center gap-2">
                  <Car className="w-5 h-5 text-primary" />
                  Mes trajets
                </CardTitle>
                <CardDescription>Trajets que vous proposez</CardDescription>
              </CardHeader>
              <CardContent>
                <p className="text-3xl font-bold text-primary">0</p>
                <Link to="/my-rides" className="text-sm text-primary hover:underline">
                  Voir tous →
                </Link>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className="pb-2">
                <CardTitle className="text-lg flex items-center gap-2">
                  <Calendar className="w-5 h-5 text-action" />
                  Mes réservations
                </CardTitle>
                <CardDescription>Trajets réservés</CardDescription>
              </CardHeader>
              <CardContent>
                <p className="text-3xl font-bold text-action">0</p>
                <Link to="/my-bookings" className="text-sm text-primary hover:underline">
                  Voir toutes →
                </Link>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className="pb-2">
                <CardTitle className="text-lg flex items-center gap-2">
                  <MapPin className="w-5 h-5 text-green-600" />
                  Trajets effectués
                </CardTitle>
                <CardDescription>Historique complet</CardDescription>
              </CardHeader>
              <CardContent>
                <p className="text-3xl font-bold text-green-600">0</p>
                <Link to="/history" className="text-sm text-primary hover:underline">
                  Voir l'historique →
                </Link>
              </CardContent>
            </Card>
          </div>

          {/* Account Section */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <User className="w-5 h-5" />
                Mon compte
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center justify-between py-2 border-b">
                <span className="text-muted-foreground">Email</span>
                <span className="font-medium">{user?.email || 'Non renseigné'}</span>
              </div>
              <div className="flex items-center justify-between py-2 border-b">
                <span className="text-muted-foreground">Membre depuis</span>
                <span className="font-medium">Décembre 2024</span>
              </div>
              <div className="flex gap-3 pt-4">
                <Link to="/profile">
                  <Button variant="outline">Modifier le profil</Button>
                </Link>
                <Button
                  variant="ghost"
                  className="text-destructive hover:text-destructive"
                  onClick={handleLogout}
                >
                  <LogOut className="w-4 h-4 mr-2" />
                  Déconnexion
                </Button>
              </div>
            </CardContent>
          </Card>
        </div>
      </main>

      <Footer />
    </div>
  );
};

export default Dashboard;
