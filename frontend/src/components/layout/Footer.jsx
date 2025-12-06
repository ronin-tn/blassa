/*
  Footer Component
  -----------------
  Simple footer with branding and links.
*/

import { Link } from 'react-router-dom';

const Footer = () => {
    return (
        <footer className="bg-slate-900 text-slate-300">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
                <div className="grid md:grid-cols-4 gap-8">
                    {/* Brand */}
                    <div className="space-y-4">
                        <div className="flex items-center gap-2">
                            <span className="text-2xl">🚗</span>
                            <span className="text-xl font-bold text-white">Blassa</span>
                        </div>
                        <p className="text-sm">
                            La plateforme de covoiturage de confiance en Tunisie.
                        </p>
                    </div>

                    {/* Links */}
                    <div>
                        <h3 className="font-semibold text-white mb-4">Navigation</h3>
                        <ul className="space-y-2 text-sm">
                            <li>
                                <a href="#search" className="hover:text-white transition-colors">
                                    Rechercher un trajet
                                </a>
                            </li>
                            <li>
                                <Link to="/rides/create" className="hover:text-white transition-colors">
                                    Publier un trajet
                                </Link>
                            </li>
                        </ul>
                    </div>

                    {/* Support */}
                    <div>
                        <h3 className="font-semibold text-white mb-4">Support</h3>
                        <ul className="space-y-2 text-sm">
                            <li>
                                <a href="#" className="hover:text-white transition-colors">
                                    Aide
                                </a>
                            </li>
                            <li>
                                <a href="#" className="hover:text-white transition-colors">
                                    Contact
                                </a>
                            </li>
                        </ul>
                    </div>

                    {/* Legal */}
                    <div>
                        <h3 className="font-semibold text-white mb-4">Légal</h3>
                        <ul className="space-y-2 text-sm">
                            <li>
                                <a href="#" className="hover:text-white transition-colors">
                                    Conditions d'utilisation
                                </a>
                            </li>
                            <li>
                                <a href="#" className="hover:text-white transition-colors">
                                    Politique de confidentialité
                                </a>
                            </li>
                        </ul>
                    </div>
                </div>

                <div className="border-t border-slate-700 mt-8 pt-8 text-center text-sm">
                    <p>© 2025 Blassa. Tous droits réservés.</p>
                </div>
            </div>
        </footer>
    );
};

export default Footer;
