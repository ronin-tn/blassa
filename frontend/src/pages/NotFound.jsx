/*
  Not Found (404) Page
  --------------------
  Displayed when user navigates to a non-existent route.
*/

import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Home, Search, ArrowLeft } from 'lucide-react';

const NotFound = () => {
  return (
    <div className="min-h-screen flex items-center justify-center bg-background p-4">
      <div className="max-w-md w-full text-center space-y-8">
        {/* 404 Illustration */}
        <div className="space-y-4">
          <div className="text-8xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-primary to-primary/50">
            404
          </div>
          <div className="text-6xl">🚗💨</div>
        </div>

        {/* Message */}
        <div className="space-y-2">
          <h1 className="text-2xl font-bold text-foreground">Page introuvable</h1>
          <p className="text-muted-foreground">
            Oups ! La page que vous cherchez semble avoir pris un autre chemin. Peut-être qu'elle
            est partie en covoiturage ?
          </p>
        </div>

        {/* Action Buttons */}
        <div className="flex flex-col sm:flex-row gap-3 justify-center">
          <Button
            variant="ghost"
            onClick={() => window.history.back()}
            className="flex items-center gap-2"
          >
            <ArrowLeft className="w-4 h-4" />
            Retour
          </Button>
          <Link to="/">
            <Button variant="brand" className="flex items-center gap-2 w-full">
              <Home className="w-4 h-4" />
              Accueil
            </Button>
          </Link>
          <Link to="/">
            <Button variant="action" className="flex items-center gap-2 w-full">
              <Search className="w-4 h-4" />
              Rechercher un trajet
            </Button>
          </Link>
        </div>
      </div>
    </div>
  );
};

export default NotFound;
