/*
  Landing Page - Modern Travel Aesthetic
  --------------------------------------
  Refactored with split layout, glassmorphism, and bento grid.
*/

import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ShieldCheck, Heart, Wallet, Star, MapPin, Search } from 'lucide-react';
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

    const handleSearch = (e) => {
        e.preventDefault();
        if (!departure || !destination) {
            alert("Veuillez sélectionner un point de départ et une destination.");
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
        });
        navigate(`/search?${params.toString()}`);
    };

    return (
        <div className="min-h-screen flex flex-col font-sans bg-slate-50 relative overflow-x-hidden">
            <Navbar />

            {/* Background Pattern */}
            <div className="absolute inset-0 opacity-[0.03] pointer-events-none">
                <svg className="w-full h-full" xmlns="http://www.w3.org/2000/svg">
                    <defs>
                        <pattern id="grid-pattern" width="40" height="40" patternUnits="userSpaceOnUse">
                            <path d="M0 40L40 0H20L0 20M40 40V20L20 40" stroke="currentColor" strokeWidth="2" fill="none" />
                        </pattern>
                    </defs>
                    <rect width="100%" height="100%" fill="url(#grid-pattern)" />
                </svg>
            </div>

            {/* Hero Section - Split Layout */}
            <section className="relative pt-32 pb-20 lg:pt-40 lg:pb-32 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto w-full">
                <div className="grid lg:grid-cols-2 gap-12 lg:gap-20 items-center">

                    {/* Left: Content & Search */}
                    <div className="relative z-10 space-y-8">
                        <div className="space-y-4">
                            <div className="inline-flex items-center rounded-full border border-orange-200 bg-orange-50 px-3 py-1 text-sm text-orange-600">
                                <span className="flex h-2 w-2 rounded-full bg-orange-600 mr-2"></span>
                                Le covoiturage nouvelle génération
                            </div>
                            <h1 className="text-5xl lg:text-7xl font-bold tracking-tight text-slate-900 leading-[0.95]">
                                Voyagez <span className="text-transparent bg-clip-text bg-gradient-to-r from-orange-500 to-amber-500">ensemble</span>.
                            </h1>
                            <p className="text-lg text-slate-600 max-w-md leading-relaxed">
                                Connectez-vous avec des milliers de voyageurs.
                                Économique, convivial et sécurisé.
                            </p>
                        </div>

                        {/* Glassmorphic Search Form */}
                        <div className="relative group">
                            <div className="absolute -inset-1 bg-gradient-to-r from-orange-500 to-amber-500 rounded-2xl blur opacity-20 group-hover:opacity-30 transition duration-1000"></div>
                            <div className="relative bg-white/80 backdrop-blur-xl border border-white/50 shadow-2xl rounded-2xl p-6 lg:p-8">
                                <form onSubmit={handleSearch} className="space-y-5">
                                    <div className="space-y-4">
                                        <div className="grid grid-cols-1 gap-4">
                                            <LocationAutocomplete
                                                label="Départ *"
                                                placeholder="D'où partez-vous ?"
                                                value={departure}
                                                onChange={setDeparture}
                                                className="bg-white/50 border-orange-100 focus:border-orange-500"
                                            />
                                            <div className="hidden lg:block absolute left-4 top-[3.25rem] w-0.5 h-8 bg-gradient-to-b from-orange-200 to-transparent -z-10"></div>
                                            <LocationAutocomplete
                                                label="Destination *"
                                                placeholder="Où allez-vous ?"
                                                value={destination}
                                                onChange={setDestination}
                                                className="bg-white/50 border-orange-100 focus:border-orange-500"
                                            />
                                        </div>
                                        <div className="grid grid-cols-2 gap-4">
                                            <Input
                                                label="Date (Optionnel)"
                                                type="date"
                                                min={new Date().toISOString().split('T')[0]}
                                                value={date}
                                                onChange={(e) => setDate(e.target.value)}
                                                className="bg-white/50 border-orange-100"
                                            />
                                            <div className="space-y-2">
                                                <label className="text-sm font-medium text-slate-700">Passagers</label>
                                                <Select value={passengers} onValueChange={setPassengers}>
                                                    <SelectTrigger className="bg-white/50 border-orange-100">
                                                        <SelectValue />
                                                    </SelectTrigger>
                                                    <SelectContent>
                                                        {[1, 2, 3, 4].map(num => (
                                                            <SelectItem key={num} value={num.toString()}>{num} Passager{num > 1 ? 's' : ''}</SelectItem>
                                                        ))}
                                                    </SelectContent>
                                                </Select>
                                            </div>
                                        </div>
                                    </div>
                                    <Button
                                        type="submit"
                                        className="w-full h-14 text-lg font-bold bg-gradient-to-r from-orange-500 to-amber-500 hover:from-orange-600 hover:to-amber-600 shadow-lg shadow-orange-500/30 transition-all duration-300 transform hover:-translate-y-0.5"
                                    >
                                        <Search className="w-5 h-5 mr-2" />
                                        Rechercher
                                    </Button>
                                </form>
                            </div>
                        </div>
                    </div>

                    {/* Right: Lifestyle Image */}
                    <div className="relative hidden lg:block h-full min-h-[600px]">
                        <div className="absolute top-10 right-0 w-4/5 h-full bg-orange-100/50 rounded-[3rem] -rotate-3"></div>
                        <div className="absolute top-0 right-10 w-4/5 h-full rounded-[3rem] overflow-hidden shadow-2xl rotate-3 transition-transform duration-700 hover:rotate-2">
                            <img
                                src="https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80"
                                alt="Voyageurs heureux"
                                className="w-full h-full object-cover transform hover:scale-105 transition duration-700"
                            />
                            <div className="absolute bottom-0 left-0 right-0 p-8 bg-gradient-to-t from-black/60 to-transparent text-white">
                                <div className="flex items-center space-x-2 mb-2">
                                    <div className="flex -space-x-2">
                                        {[1, 2, 3].map(i => (
                                            <div key={i} className="w-8 h-8 rounded-full border-2 border-white bg-slate-200"></div>
                                        ))}
                                    </div>
                                    <span className="text-sm font-medium">+2.5k voyageurs aujourd'hui</span>
                                </div>
                            </div>
                        </div>

                        {/* Floating Card Element */}
                        <div className="absolute bottom-20 -left-12 bg-white p-4 rounded-2xl shadow-xl flex items-center gap-4 animate-bounce-slow">
                            <div className="p-3 bg-green-100 rounded-xl text-green-600">
                                <Wallet className="w-6 h-6" />
                            </div>
                            <div>
                                <p className="text-xs text-slate-500 font-medium">Économie réalisée</p>
                                <p className="text-lg font-bold text-slate-900">45.00 TND</p>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            {/* Features - Bento Grid */}
            <section className="py-24 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto w-full">
                <div className="text-center mb-16">
                    <h2 className="text-3xl lg:text-4xl font-bold text-slate-900 mb-4">Pourquoi Blassa ?</h2>
                    <p className="text-slate-600 max-w-2xl mx-auto">Une plateforme pensée pour votre sécurité et votre confort.</p>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                    {/* Large Card */}
                    <div className="md:col-span-2 group relative overflow-hidden bg-white rounded-3xl p-8 border border-slate-100 shadow-sm hover:shadow-xl transition duration-300">
                        <div className="absolute top-0 right-0 p-8 opacity-10 group-hover:opacity-20 transition">
                            <ShieldCheck className="w-32 h-32 text-orange-500" />
                        </div>
                        <div className="relative z-10">
                            <div className="w-12 h-12 bg-orange-100 rounded-2xl flex items-center justify-center text-orange-600 mb-6">
                                <ShieldCheck className="w-6 h-6" />
                            </div>
                            <h3 className="text-2xl font-bold text-slate-900 mb-3">Sécurité avant tout</h3>
                            <p className="text-slate-600 text-lg leading-relaxed max-w-md">
                                Profils vérifiés avec CNI et téléphone. Système d'avis communautaire rigoureux pour voyager en toute sérénité.
                            </p>
                        </div>
                    </div>

                    {/* Tall Card */}
                    <div className="md:row-span-2 bg-gradient-to-br from-slate-900 to-slate-800 rounded-3xl p-8 text-white relative overflow-hidden shadow-xl">
                        <div className="relative z-10 h-full flex flex-col justify-between">
                            <div>
                                <div className="w-12 h-12 bg-white/10 backdrop-blur rounded-2xl flex items-center justify-center text-white mb-6">
                                    <Star className="w-6 h-6" />
                                </div>
                                <h3 className="text-2xl font-bold mb-3">Top Conducteurs</h3>
                                <p className="text-slate-300 leading-relaxed">
                                    Nos super-conducteurs sont évalués 4.8/5 en moyenne. Qualité garantie.
                                </p>
                            </div>
                            <div className="mt-8 pt-8 border-t border-white/10">
                                <div className="flex items-center gap-4">
                                    <div className="text-4xl font-bold text-orange-400">4.9</div>
                                    <div className="text-sm text-slate-400">Note moyenne<br />des trajets</div>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Standard Card */}
                    <div className="group bg-white rounded-3xl p-8 border border-slate-100 shadow-sm hover:shadow-xl transition duration-300">
                        <div className="w-12 h-12 bg-rose-100 rounded-2xl flex items-center justify-center text-rose-600 mb-6 group-hover:scale-110 transition">
                            <Heart className="w-6 h-6" />
                        </div>
                        <h3 className="text-xl font-bold text-slate-900 mb-3">Ladies Only</h3>
                        <p className="text-slate-600">Option "Entre femmes" pour celles qui préfèrent.</p>
                    </div>

                    {/* Standard Card */}
                    <div className="group bg-white rounded-3xl p-8 border border-slate-100 shadow-sm hover:shadow-xl transition duration-300">
                        <div className="w-12 h-12 bg-emerald-100 rounded-2xl flex items-center justify-center text-emerald-600 mb-6 group-hover:scale-110 transition">
                            <Wallet className="w-6 h-6" />
                        </div>
                        <h3 className="text-xl font-bold text-slate-900 mb-3">Prix mini</h3>
                        <p className="text-slate-600">Frais réduits. Payez le prix juste.</p>
                    </div>
                </div>
            </section>

            <Footer />
        </div>
    );
};

export default Landing;
