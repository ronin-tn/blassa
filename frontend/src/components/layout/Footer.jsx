/*
  Footer Component
  -----------------
  Simple footer with branding and links.
*/

import { Link } from 'react-router-dom';
import { Facebook, Instagram, Twitter } from 'lucide-react';

const Footer = () => {
  return (
    <footer className="bg-slate-900 text-slate-300">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-16 pb-8">
        <div className="grid md:grid-cols-4 gap-12 mb-12">
          {/* Brand */}
          <div className="space-y-6">
            <Link to="/" className="inline-block">
              <img
                src="/blassa_logo.svg"
                alt="Blassa Logo"
                className="h-8 w-auto brightness-0 invert"
              />
            </Link>
            <div>
              <p className="text-sm font-medium text-slate-100 mb-1">
                Blassa — plateforme de covoiturage en Tunisie
              </p>
              <p className="text-sm text-slate-400">
                Connectez-vous, partagez et voyagez en toute confiance.
              </p>
            </div>
            <div className="flex items-center gap-4">
              <a
                href="#"
                className="w-10 h-10 rounded-full bg-slate-800 flex items-center justify-center text-slate-400 hover:bg-orange-600 hover:text-white transition-all duration-300"
              >
                <Facebook className="w-5 h-5" />
              </a>
              <a
                href="#"
                className="w-10 h-10 rounded-full bg-slate-800 flex items-center justify-center text-slate-400 hover:bg-pink-600 hover:text-white transition-all duration-300"
              >
                <Instagram className="w-5 h-5" />
              </a>
              <a
                href="#"
                className="w-10 h-10 rounded-full bg-slate-800 flex items-center justify-center text-slate-400 hover:bg-sky-500 hover:text-white transition-all duration-300"
              >
                <Twitter className="w-5 h-5" />
              </a>
            </div>
          </div>

          {/* Links */}
          <div>
            <h3 className="font-bold text-white mb-6 uppercase tracking-wider text-xs">
              Navigation
            </h3>
            <ul className="space-y-3 text-sm">
              <li>
                <a href="#search" className="hover:text-orange-400 transition-colors">
                  Rechercher un trajet
                </a>
              </li>
              <li>
                <Link to="/rides/create" className="hover:text-orange-400 transition-colors">
                  Publier un trajet
                </Link>
              </li>
            </ul>
          </div>

          {/* Support */}
          <div>
            <h3 className="font-bold text-white mb-6 uppercase tracking-wider text-xs">Support</h3>
            <ul className="space-y-3 text-sm">
              <li>
                <a href="#" className="hover:text-orange-400 transition-colors">
                  Centre d'aide
                </a>
              </li>
              <li>
                <a href="#" className="hover:text-orange-400 transition-colors">
                  Nous contacter
                </a>
              </li>
              <li>
                <a href="#" className="hover:text-orange-400 transition-colors">
                  Assurance
                </a>
              </li>
            </ul>
          </div>

          {/* Legal */}
          <div>
            <h3 className="font-bold text-white mb-6 uppercase tracking-wider text-xs">Légal</h3>
            <ul className="space-y-3 text-sm">
              <li>
                <a href="#" className="hover:text-orange-400 transition-colors">
                  Conditions d'utilisation
                </a>
              </li>
              <li>
                <a href="#" className="hover:text-orange-400 transition-colors">
                  Politique de confidentialité
                </a>
              </li>
              <li>
                <a href="#" className="hover:text-orange-400 transition-colors">
                  Cookies
                </a>
              </li>
            </ul>
          </div>
        </div>

        <div className="border-t border-slate-800 pt-8 flex flex-col md:flex-row justify-between items-center gap-4 text-xs text-slate-500">
          <p>© 2025 Blassa. Tous droits réservés.</p>
          <div className="flex gap-6">
            <span>Fait avec ❤️ en Tunisie</span>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
