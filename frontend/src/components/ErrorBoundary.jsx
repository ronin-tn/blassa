/*
  Error Boundary Component
  ------------------------
  Catches JavaScript errors anywhere in the child component tree,
  logs those errors, and displays a fallback UI.
  
  Usage:
  <ErrorBoundary>
    <App />
  </ErrorBoundary>
*/

import { Component } from 'react';
import { Button } from '@/components/ui/button';
import { AlertTriangle, RefreshCw, Home } from 'lucide-react';

class ErrorBoundary extends Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    // Update state so the next render will show the fallback UI
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    // Log error to console (in production, send to error tracking service)
    console.error('ErrorBoundary caught an error:', error, errorInfo);
  }

  handleReload = () => {
    window.location.reload();
  };

  handleGoHome = () => {
    window.location.href = '/';
  };

  render() {
    if (this.state.hasError) {
      return (
        <div className="min-h-screen flex items-center justify-center bg-background p-4">
          <div className="max-w-md w-full text-center space-y-6">
            {/* Error Icon */}
            <div className="mx-auto w-20 h-20 bg-destructive/10 rounded-full flex items-center justify-center">
              <AlertTriangle className="w-10 h-10 text-destructive" />
            </div>

            {/* Error Message */}
            <div className="space-y-2">
              <h1 className="text-2xl font-bold text-foreground">Oups ! Une erreur est survenue</h1>
              <p className="text-muted-foreground">
                Nous sommes désolés, quelque chose s'est mal passé. Veuillez réessayer ou retourner
                à l'accueil.
              </p>
            </div>

            {/* Action Buttons */}
            <div className="flex flex-col sm:flex-row gap-3 justify-center">
              <Button
                onClick={this.handleReload}
                variant="outline"
                className="flex items-center gap-2"
              >
                <RefreshCw className="w-4 h-4" />
                Réessayer
              </Button>
              <Button
                onClick={this.handleGoHome}
                variant="brand"
                className="flex items-center gap-2"
              >
                <Home className="w-4 h-4" />
                Retour à l'accueil
              </Button>
            </div>

            {/* Debug info (only in development) */}
            {import.meta.env.DEV && this.state.error && (
              <details className="mt-6 text-left p-4 bg-muted rounded-lg">
                <summary className="cursor-pointer text-sm font-medium text-muted-foreground">
                  Détails techniques (dev only)
                </summary>
                <pre className="mt-2 text-xs text-destructive overflow-auto">
                  {this.state.error.toString()}
                </pre>
              </details>
            )}
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;
