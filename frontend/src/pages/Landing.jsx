/*
  Landing Page - Modern Travel Aesthetic
  --------------------------------------
  Refactored with split layout, glassmorphism, and bento grid.
  Includes Conversion Optimizations: Ticker, Tabs, Popular Routes, Gradient CTA.
*/

import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import {
  ShieldCheck,
  Heart,
  Wallet,
  Star,
  MapPin,
  Search,
  Clock,
  ArrowRight,
  CheckCircle2,
} from 'lucide-react';
import Navbar from '@/components/layout/Navbar';
import Footer from '@/components/layout/Footer';
import { Button } from '@/components/ui/button';
import { Input, LocationAutocomplete } from '@/components/common';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';

const Landing = () => {
  const navigate = useNavigate();
  const [departure, setDeparture] = useState(null);
  const [destination, setDestination] = useState(null);
  const [date, setDate] = useState('');
  const [passengers, setPassengers] = useState('1');
  const [ladiesOnly, setLadiesOnly] = useState(false);
  const [activeTab, setActiveTab] = useState('search'); // 'search' | 'publish'
  const [tickerIndex, setTickerIndex] = useState(0);

  const tickers = [
    'Ahmed vient de publier Tunis → Sousse',
    'Sarra a réservé une place pour Nabeul',
    'Youssef cherche un trajet vers Bizerte',
    '34 trajets réservés cette dernière heure',
  ];

  useEffect(() => {
    const interval = setInterval(() => {
      setTickerIndex((prev) => (prev + 1) % tickers.length);
    }, 3000);
    return () => clearInterval(interval);
  }, [tickers.length]);

  const handleSearch = (e) => {
    e.preventDefault();
    if (!departure || !destination) {
      alert('Veuillez sélectionner un point de départ et une destination.');
      return;
    }
    const params = new URLSearchParams({
      origin: departure?.name || '',
      originLat: departure?.lat || '',
      originLon: departure?.lon || '',
      dest: destination?.name || '',
      destLat: destination?.lat || '',
      destLon: destination?.lon || '',
      date: date,
      passengers: passengers,
      genderFilter: ladiesOnly ? 'FEMALE_ONLY' : 'ANY',
    });
    navigate(`/search?${params.toString()}`);
  };

  const popularRoutes = [
    { from: 'Tunis', to: 'Sousse' },
    { from: 'Sousse', to: 'Monastir' },
    { from: 'Tunis', to: 'Bizerte' },
    { from: 'Sfax', to: 'Tunis' },
  ];

  return (
    <div className="min-h-screen flex flex-col font-sans bg-slate-50 relative overflow-x-hidden">
      <Navbar />

      {/* Live Ticker */}
      <div className="bg-slate-900/95 text-slate-300 text-xs sm:text-sm py-2 overflow-hidden border-b border-white/5 relative z-50">
        <div className="max-w-7xl mx-auto px-4 flex items-center justify-center gap-2 animate-in fade-in duration-500 key={tickerIndex}">
          <div className="w-2 h-2 rounded-full bg-green-500 animate-pulse"></div>
          <span className="font-medium tracking-wide">{tickers[tickerIndex]}</span>
        </div>
      </div>

      {/* Background Pattern & Ambient Blobs */}
      <div className="absolute inset-0 pointer-events-none overflow-hidden">
        <svg className="absolute w-full h-full opacity-[0.03]" xmlns="http://www.w3.org/2000/svg">
          <defs>
            <pattern id="grid-pattern" width="40" height="40" patternUnits="userSpaceOnUse">
              <path
                d="M0 40L40 0H20L0 20M40 40V20L20 40"
                stroke="currentColor"
                strokeWidth="2"
                fill="none"
              />
            </pattern>
          </defs>
          <rect width="100%" height="100%" fill="url(#grid-pattern)" />
        </svg>
        {/* Blobs */}
        <div className="absolute top-0 right-0 w-[500px] h-[500px] bg-amber-200 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob"></div>
        <div className="absolute top-0 -right-4 w-[500px] h-[500px] bg-orange-200 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob animation-delay-2000"></div>
        <div className="absolute -bottom-8 left-20 w-[500px] h-[500px] bg-pink-200 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob animation-delay-4000"></div>
      </div>

      {/* Hero Section - Split Layout */}
      <section className="relative pt-24 pb-20 lg:pt-32 lg:pb-32 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto w-full">
        <div className="grid lg:grid-cols-2 gap-12 lg:gap-20 items-center">
          {/* Left: Content & Search */}
          <div className="relative z-10 space-y-8">
            <div className="space-y-4">
              <div className="inline-flex items-center rounded-full border border-orange-200 bg-orange-50 px-3 py-1 text-sm text-orange-600">
                <span className="flex h-2 w-2 rounded-full bg-orange-600 mr-2"></span>
                Le covoiturage nouvelle génération
              </div>
              <h1 className="text-4xl lg:text-6xl font-bold tracking-tight text-slate-900 leading-[1.05]">
                Voyagez{' '}
                <span className="text-transparent bg-clip-text bg-gradient-to-r from-orange-500 to-amber-500">
                  ensemble
                </span>
                .
              </h1>
              <p className="text-xl text-slate-600 font-medium">
                Conducteur ou passager ? Rejoignez la communauté Blassa.
              </p>
            </div>

            {/* Glassmorphic Search/Publish Card */}
            <div className="relative group">
              <div className="absolute -inset-1 bg-gradient-to-r from-orange-500 to-amber-500 rounded-[1.5rem] blur opacity-20 group-hover:opacity-30 transition duration-1000"></div>
              <div className="relative bg-white/90 backdrop-blur-xl border border-slate-200 shadow-[0_8px_30px_rgb(0,0,0,0.04)] rounded-[1.25rem] overflow-hidden">
                {/* Tabs */}
                <div className="flex border-b border-slate-100">
                  <button
                    onClick={() => setActiveTab('search')}
                    className={`flex-1 py-4 text-center font-bold text-sm uppercase tracking-wide transition-colors ${activeTab === 'search' ? 'text-orange-600 bg-orange-50/50' : 'text-slate-500 hover:text-slate-700'}`}
                  >
                    Passager
                  </button>
                  <button
                    onClick={() => setActiveTab('publish')}
                    className={`flex-1 py-4 text-center font-bold text-sm uppercase tracking-wide transition-colors ${activeTab === 'publish' ? 'text-orange-600 bg-orange-50/50' : 'text-slate-500 hover:text-slate-700'}`}
                  >
                    Conducteur
                  </button>
                </div>

                <div className="p-6 lg:p-8">
                  {activeTab === 'search' ? (
                    <form onSubmit={handleSearch} className="space-y-5">
                      <div className="space-y-4">
                        <div className="grid grid-cols-1 gap-4">
                          <LocationAutocomplete
                            label="Départ"
                            placeholder="D'où partez-vous ?"
                            value={departure}
                            onChange={setDeparture}
                            className="bg-slate-50 border-slate-200 focus:border-orange-500 rounded-xl px-4 py-3"
                          />
                          <div className="hidden lg:block absolute left-4 top-[5.5rem] w-0.5 h-6 bg-gradient-to-b from-orange-200 to-transparent -z-10"></div>
                          <LocationAutocomplete
                            label="Destination"
                            placeholder="Où allez-vous ?"
                            value={destination}
                            onChange={setDestination}
                            className="bg-slate-50 border-slate-200 focus:border-orange-500 rounded-xl px-4 py-3"
                          />
                        </div>
                        <div className="grid grid-cols-2 gap-4">
                          <Input
                            label="Date"
                            type="date"
                            min={new Date().toISOString().split('T')[0]}
                            value={date}
                            onChange={(e) => setDate(e.target.value)}
                            className="bg-slate-50 border-slate-200 focus:border-orange-500 rounded-xl px-4 py-3"
                          />
                          <div className="space-y-2">
                            <label className="text-sm font-medium text-slate-700">Passagers</label>
                            <Select value={passengers} onValueChange={setPassengers}>
                              <SelectTrigger className="bg-slate-50 border-slate-200 focus:ring-orange-500 rounded-xl px-4 py-3 h-auto">
                                <SelectValue />
                              </SelectTrigger>
                              <SelectContent>
                                {[1, 2, 3, 4].map((num) => (
                                  <SelectItem key={num} value={num.toString()}>
                                    {num}
                                  </SelectItem>
                                ))}
                              </SelectContent>
                            </Select>
                          </div>
                        </div>

                        {/* Ladies Only Toggle */}
                        <div
                          className="flex items-center space-x-2 bg-pink-50 p-3 rounded-xl border border-pink-100 cursor-pointer"
                          onClick={() => setLadiesOnly(!ladiesOnly)}
                        >
                          <div
                            className={`w-5 h-5 rounded border flex items-center justify-center transition-colors ${ladiesOnly ? 'bg-pink-500 border-pink-500' : 'bg-white border-slate-300'}`}
                          >
                            {ladiesOnly && <CheckCircle2 className="w-3.5 h-3.5 text-white" />}
                          </div>
                          <span className="text-sm font-bold text-pink-700 select-none">
                            Ladies Only (Entre femmes)
                          </span>
                        </div>
                      </div>
                      <Button
                        type="submit"
                        className="w-full h-14 text-lg font-bold bg-gradient-to-r from-orange-500 to-amber-500 hover:from-orange-600 hover:to-amber-600 shadow-xl shadow-orange-500/20 transition-all duration-300 transform hover:-translate-y-0.5 rounded-xl"
                      >
                        <Search className="w-5 h-5 mr-3" />
                        Rechercher
                      </Button>
                    </form>
                  ) : (
                    <div className="text-center space-y-6 py-6 animate-in fade-in slide-in-from-bottom-2 duration-300">
                      <div className="w-16 h-16 bg-orange-100 rounded-full flex items-center justify-center mx-auto text-orange-600">
                        <Wallet className="w-8 h-8" />
                      </div>
                      <div>
                        <h3 className="text-xl font-bold text-slate-900 mb-2">
                          Gagnez de l'argent
                        </h3>
                        <p className="text-slate-600">
                          Partagez vos frais et rencontrez des gens sympas. C'est simple et rapide.
                        </p>
                      </div>
                      <Link to="/rides/create">
                        <Button className="w-full h-14 text-lg font-bold bg-slate-900 hover:bg-slate-800 shadow-xl transition-all duration-300 transform hover:-translate-y-0.5 rounded-xl">
                          Publier un trajet
                          <ArrowRight className="w-5 h-5 ml-2" />
                        </Button>
                      </Link>
                    </div>
                  )}
                </div>
              </div>
            </div>

            {/* Popular Routes */}
            <div className="pt-2">
              <p className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-3">
                Trajets populaires
              </p>
              <div className="flex flex-wrap gap-2">
                {popularRoutes.map((route, idx) => (
                  <button
                    key={idx}
                    className="bg-white border border-slate-200 rounded-full px-4 py-1.5 text-sm font-medium text-slate-600 hover:border-orange-300 hover:text-orange-600 transition-colors shadow-sm"
                  >
                    {route.from} <span className="text-slate-300 mx-1">→</span> {route.to}
                  </button>
                ))}
              </div>
            </div>
          </div>

          {/* Right: Lifestyle Image */}
          <div className="relative hidden lg:block h-full min-h-[600px]">
            {/* Background Connections */}
            <div className="absolute top-10 right-0 w-4/5 h-full bg-orange-50 rounded-[3rem] -rotate-3 z-0"></div>
            <div className="absolute top-0 right-10 w-4/5 h-full rounded-[3rem] overflow-hidden shadow-2xl rotate-3 transition-transform duration-700 hover:rotate-2 z-10">
              <img
                src="/images/hero-lifestyle.png"
                alt="Voyageurs heureux"
                className="w-full h-full object-cover transform hover:scale-105 transition duration-700"
              />
              <div className="absolute bottom-0 left-0 right-0 p-8 bg-gradient-to-t from-black/60 to-transparent text-white">
                <div className="flex items-center space-x-2 mb-2">
                  <div className="flex -space-x-2">
                    {[1, 2, 3].map((i) => (
                      <div
                        key={i}
                        className="w-8 h-8 rounded-full border-2 border-white bg-slate-200"
                      ></div>
                    ))}
                  </div>
                  <span className="text-sm font-medium">+2.5k voyageurs aujourd'hui</span>
                </div>
              </div>
            </div>

            {/* Floating Card: Economy */}
            <div className="absolute bottom-20 -left-12 bg-white/95 backdrop-blur-sm p-4 rounded-2xl shadow-xl flex items-center gap-4 animate-bounce-slow z-20 border border-slate-50">
              <div className="p-3 bg-green-100 rounded-xl text-green-600">
                <Wallet className="w-6 h-6" />
              </div>
              <div>
                <p className="text-xs text-slate-500 font-medium">Économie réalisée</p>
                <p className="text-lg font-bold text-slate-900">45.00 TND</p>
              </div>
            </div>

            {/* Floating Card: Trust - NEW */}
            <div className="absolute top-24 -left-8 bg-white/95 backdrop-blur-sm p-3 pr-6 rounded-full shadow-lg flex items-center gap-3 animate-bounce-slow animation-delay-2000 z-20 border border-slate-50">
              <div className="w-10 h-10 bg-yellow-100 rounded-full flex items-center justify-center text-yellow-600">
                <Star className="w-5 h-5 fill-current" />
              </div>
              <div>
                <p className="text-sm font-bold text-slate-900">4.9/5 Note</p>
                <p className="text-xs text-slate-500 font-medium">Conducteurs vérifiés</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Vision Section - Sketch Style */}
      <section className="py-20 bg-white border-b border-slate-100 overflow-hidden">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid lg:grid-cols-2 gap-16 items-center">
            <div className="order-2 lg:order-1 relative">
              {/* Decorative Elements */}
              <div className="absolute -top-10 -left-10 w-40 h-40 bg-orange-100 rounded-full mix-blend-multiply filter blur-2xl opacity-50"></div>
              <div className="absolute bottom-0 right-0 w-40 h-40 bg-blue-100 rounded-full mix-blend-multiply filter blur-2xl opacity-50"></div>

              <img
                src="https://images.unsplash.com/photo-1549317661-bd32c8ce0db2?auto=format&fit=crop&q=80&w=2070"
                alt="Covoiturage Tunisie"
                className="relative rounded-2xl shadow-xl border-4 border-white rotate-1 hover:rotate-0 transition duration-500"
              />
            </div>
            <div className="order-1 lg:order-2 space-y-6 text-center lg:text-left">
              <h2 className="text-3xl lg:text-4xl font-bold text-slate-900 leading-tight">
                DÉCOUVREZ L'AVENIR DU <br />
                <span className="text-orange-600">COVOITURAGE TUNISIEN</span>
              </h2>
              <div className="w-20 h-1.5 bg-orange-500 mx-auto lg:mx-0 rounded-full"></div>
              <p className="text-slate-600 text-lg leading-relaxed">
                Blassa révolutionne le transport en Tunisie en reliant conducteurs et passagers pour
                des trajets partagés.
              </p>
              <p className="text-slate-600 leading-relaxed">
                Notre plateforme favorise la communauté, la durabilité et l'accessibilité
                financière, facilitant ainsi les trajets pour tous. Rejoignez notre communauté dès
                aujourd'hui !
              </p>
              <Button
                variant="outline"
                className="mt-4 border-orange-500 text-orange-600 hover:bg-orange-50 px-8 py-6 rounded-full uppercase tracking-wider font-bold"
              >
                Pour en savoir plus
              </Button>
            </div>
          </div>
        </div>
      </section>

      {/* Services Grid - PREMIUM 4-COLUMN LAYOUT */}
      <section className="py-24 bg-slate-50 relative overflow-hidden">
        {/* Soft Background Gradient */}
        <div className="absolute inset-0 bg-gradient-to-b from-white via-slate-50 to-slate-100 opacity-80 pointer-events-none"></div>

        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10 w-full">
          <div className="text-center mb-20 space-y-4">
            <h2 className="text-3xl md:text-4xl font-bold text-slate-900 uppercase tracking-tight">
              L'excellence à chaque trajet
            </h2>
            <div className="w-24 h-1.5 bg-gradient-to-r from-orange-400 to-amber-500 mx-auto rounded-full shadow-sm"></div>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-8 lg:gap-12">
            {/* 1. FIND */}
            <div className="group text-center space-y-5">
              <div className="relative mx-auto w-24 h-24">
                <span className="absolute -top-3 -right-3 w-8 h-8 bg-slate-900 rounded-full text-white text-sm font-bold flex items-center justify-center border-4 border-slate-50 z-10 shadow-lg">
                  1
                </span>
                <div className="w-full h-full bg-white rounded-[2rem] flex items-center justify-center text-slate-700 shadow-xl shadow-slate-200/60 border border-slate-100 group-hover:scale-105 group-hover:bg-slate-900 group-hover:text-white transition-all duration-300">
                  <Search className="w-10 h-10 stroke-[2.5]" />
                </div>
              </div>
              <div>
                <h3 className="text-xl font-bold text-slate-900 mb-3">Trouver un trajet</h3>
                <p className="text-base text-slate-600 font-medium leading-relaxed px-2">
                  Trouvez instantanément des trajets disponibles, simples et rapides.
                </p>
              </div>
            </div>

            {/* 2. VERIFIED & SECURE */}
            <div className="group text-center space-y-5">
              <div className="relative mx-auto w-24 h-24">
                <span className="absolute -top-3 -right-3 w-8 h-8 bg-blue-600 rounded-full text-white text-sm font-bold flex items-center justify-center border-4 border-slate-50 z-10 shadow-lg">
                  2
                </span>
                <div className="w-full h-full bg-white rounded-[2rem] flex items-center justify-center text-blue-600 shadow-xl shadow-blue-100/60 border border-blue-50 group-hover:scale-105 group-hover:bg-blue-600 group-hover:text-white transition-all duration-300">
                  <ShieldCheck className="w-10 h-10 stroke-[2.5]" />
                </div>
              </div>
              <div>
                <h3 className="text-xl font-bold text-slate-900 mb-3">Sécurité & Vérification</h3>
                <p className="text-base text-slate-600 font-medium leading-relaxed px-2">
                  Identités validées, avis authentiques et assistance 24/7.
                </p>
              </div>
            </div>

            {/* 3. AFFORDABLE */}
            <div className="group text-center space-y-5">
              <div className="relative mx-auto w-24 h-24">
                <span className="absolute -top-3 -right-3 w-8 h-8 bg-emerald-500 rounded-full text-white text-sm font-bold flex items-center justify-center border-4 border-slate-50 z-10 shadow-lg">
                  3
                </span>
                <div className="w-full h-full bg-white rounded-[2rem] flex items-center justify-center text-emerald-600 shadow-xl shadow-emerald-100/60 border border-emerald-50 group-hover:scale-105 group-hover:bg-emerald-500 group-hover:text-white transition-all duration-300">
                  <Wallet className="w-10 h-10 stroke-[2.5]" />
                </div>
              </div>
              <div>
                <h3 className="text-xl font-bold text-slate-900 mb-3">Prix abordables</h3>
                <p className="text-base text-slate-600 font-medium leading-relaxed px-2">
                  Économisez sur chaque kilomètre avec des prix justes.
                </p>
              </div>
            </div>

            {/* 4. LADIES ONLY */}
            <div className="group text-center space-y-5">
              <div className="relative mx-auto w-24 h-24">
                <span className="absolute -top-3 -right-3 w-8 h-8 bg-rose-500 rounded-full text-white text-sm font-bold flex items-center justify-center border-4 border-slate-50 z-10 shadow-lg">
                  4
                </span>
                <div className="w-full h-full bg-white rounded-[2rem] flex items-center justify-center text-rose-500 shadow-xl shadow-rose-100/60 border border-rose-50 group-hover:scale-105 group-hover:bg-rose-500 group-hover:text-white transition-all duration-300">
                  <Heart className="w-10 h-10 stroke-[2.5] fill-current" />
                </div>
              </div>
              <div>
                <h3 className="text-xl font-bold text-slate-900 mb-3">Ladies Only</h3>
                <p className="text-base text-slate-600 font-medium leading-relaxed px-2">
                  L'option exclusive 100% féminine pour voyager sereine.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Brand Energy CTA */}
      <section className="pb-32 pt-10 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto w-full">
        <div className="relative rounded-[2.5rem] overflow-hidden bg-gradient-to-r from-orange-500 to-amber-600 text-center py-20 px-6 lg:px-20 shadow-2xl">
          {/* Background Pattern */}
          <div className="absolute inset-0 opacity-10 bg-[url('https://www.transparenttextures.com/patterns/cubes.png')]"></div>
          <div className="absolute top-0 right-0 w-[600px] h-[600px] bg-white opacity-10 rounded-full mix-blend-overlay filter blur-[80px] animate-blob"></div>

          <div className="relative z-10 space-y-8">
            <h2 className="text-4xl md:text-6xl font-bold text-white tracking-tight">
              Conducteur ou Passager ?
            </h2>
            <p className="text-orange-50 text-xl font-medium max-w-2xl mx-auto">
              Rejoignez la plus grande communauté de covoiturage en Tunisie.
            </p>
            <div className="flex flex-col sm:flex-row gap-5 justify-center pt-6">
              {/* Primary Button (Driver Focus) */}
              <button
                onClick={() => {
                  setActiveTab('publish');
                  window.scrollTo({ top: 0, behavior: 'smooth' });
                }}
              >
                <Button
                  size="lg"
                  className="h-16 px-10 rounded-full text-xl font-bold bg-white text-orange-600 hover:bg-orange-50 border-0 shadow-xl hover:scale-105 transition-transform w-full sm:w-auto"
                >
                  Publier un trajet
                </Button>
              </button>

              {/* Secondary Button (Passenger Focus) */}
              <button
                onClick={() => {
                  setActiveTab('search');
                  window.scrollTo({ top: 0, behavior: 'smooth' });
                }}
              >
                <Button
                  size="lg"
                  variant="outline"
                  className="h-16 px-10 rounded-full text-xl font-bold border-2 border-white text-white bg-transparent hover:bg-white/10 hover:text-white transition-all w-full sm:w-auto"
                >
                  Rechercher un trajet
                </Button>
              </button>
            </div>
          </div>
        </div>
      </section>

      <Footer />
    </div>
  );
};

export default Landing;
