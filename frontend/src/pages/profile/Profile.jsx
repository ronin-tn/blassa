/*
  Mon Profil (User Profile) Page - Redesigned
  --------------------------------------------
  Modern tab-based profile with enhanced header, stats, and profile completion.
  
  Brand: Dark navy (#0A1B3C), Warm orange (#FF7A00)
*/

import { useState, useEffect, useMemo } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/common';
import { Badge } from '@/components/ui/badge';
import { Label } from '@/components/ui/label';
import Navbar from '@/components/layout/Navbar';
import Footer from '@/components/layout/Footer';
import {
    User,
    Mail,
    Phone,
    Calendar,
    MapPin,
    Star,
    Shield,
    Car,
    CheckCircle2,
    Camera,
    Bell,
    Globe,
    Lock,
    CreditCard,
    Edit3,
    Save,
    X,
    ChevronRight,
    Facebook,
    Instagram,
    Link2,
    Settings,
    FileText,
} from 'lucide-react';

const Profile = () => {
    const { user } = useAuth();
    const [activeTab, setActiveTab] = useState('info');
    const [isEditing, setIsEditing] = useState(false);
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        phoneNumber: '',
        dob: '',
        bio: '',
        facebookUrl: '',
        instagramUrl: '',
    });

    // Initialize form data from user when available
    useEffect(() => {
        if (user) {
            setFormData({
                firstName: user.firstName || '',
                lastName: user.lastName || '',
                email: user.email || '',
                phoneNumber: user.phoneNumber || '',
                dob: user.dob || '',
                bio: user.bio || '',
                facebookUrl: user.facebookUrl || '',
                instagramUrl: user.instagramUrl || '',
            });
        }
    }, [user]);

    // Calculate profile completion percentage
    const profileCompletion = useMemo(() => {
        const fields = [
            formData.firstName,
            formData.lastName,
            formData.email,
            formData.phoneNumber,
            formData.dob,
            formData.bio,
            formData.facebookUrl || formData.instagramUrl, // At least one social
        ];
        const filled = fields.filter(Boolean).length;
        return Math.round((filled / fields.length) * 100);
    }, [formData]);

    // Get initials for avatar
    const getInitials = () => {
        const first = formData.firstName?.[0] || user?.email?.[0] || '?';
        const last = formData.lastName?.[0] || '';
        return (first + last).toUpperCase();
    };

    // Get display name
    const getDisplayName = () => {
        if (formData.firstName && formData.lastName) {
            return `${formData.firstName} ${formData.lastName}`;
        }
        return user?.email || 'Utilisateur';
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSave = () => {
        // TODO: API call to save profile
        setIsEditing(false);
    };

    // Format date for display
    const formatDate = (dateString) => {
        if (!dateString) return 'Non renseigné';
        try {
            return new Date(dateString).toLocaleDateString('fr-FR', {
                day: 'numeric',
                month: 'long',
                year: 'numeric',
            });
        } catch {
            return dateString;
        }
    };

    // Tab configuration
    const tabs = [
        { id: 'info', label: 'Informations', icon: User },
        { id: 'social', label: 'Réseaux', icon: Link2 },
        { id: 'preferences', label: 'Préférences', icon: Settings },
        { id: 'security', label: 'Sécurité', icon: Shield },
    ];

    return (
        <div className="min-h-screen flex flex-col bg-slate-50">
            <Navbar />

            {/* Compact Enhanced Header */}
            <div className="relative pt-20 overflow-hidden">
                <div className="absolute inset-0 bg-[#0A1B3C]">
                    <svg className="absolute inset-0 w-full h-full opacity-10" xmlns="http://www.w3.org/2000/svg">
                        <defs>
                            <pattern id="profile-grid" width="60" height="60" patternUnits="userSpaceOnUse">
                                <circle cx="30" cy="30" r="1.5" fill="white" />
                            </pattern>
                        </defs>
                        <rect width="100%" height="100%" fill="url(#profile-grid)" />
                    </svg>
                </div>

                <div className="relative max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                    <div className="flex flex-col sm:flex-row items-center sm:items-start gap-6">
                        {/* Avatar */}
                        <div className="relative group flex-shrink-0">
                            <div className="w-24 h-24 rounded-full bg-gradient-to-br from-[#FF7A00] to-amber-400 p-1 shadow-xl">
                                <div className="w-full h-full rounded-full bg-white flex items-center justify-center">
                                    <span className="text-3xl font-bold text-[#0A1B3C]">{getInitials()}</span>
                                </div>
                            </div>
                            <button className="absolute bottom-0 right-0 w-8 h-8 bg-[#FF7A00] rounded-full flex items-center justify-center text-white shadow-lg hover:bg-orange-600 transition-colors">
                                <Camera className="w-4 h-4" />
                            </button>
                        </div>

                        {/* Info + Stats */}
                        <div className="flex-1 text-center sm:text-left">
                            <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
                                <div>
                                    <h1 className="text-2xl font-bold text-white">{getDisplayName()}</h1>
                                    <p className="text-slate-400 text-sm mt-1">{user?.email}</p>
                                </div>
                                <Button
                                    onClick={() => { setActiveTab('info'); setIsEditing(true); }}
                                    className="bg-white/10 hover:bg-white/20 text-white border-0"
                                >
                                    <Edit3 className="w-4 h-4 mr-2" />
                                    Modifier profil
                                </Button>
                            </div>

                            {/* Profile Completion Bar */}
                            <div className="mt-4">
                                <div className="flex items-center justify-between text-sm mb-1">
                                    <span className="text-slate-400">Profil complété</span>
                                    <span className="text-white font-medium">{profileCompletion}%</span>
                                </div>
                                <div className="h-2 bg-white/10 rounded-full overflow-hidden">
                                    <div
                                        className="h-full bg-gradient-to-r from-[#FF7A00] to-amber-400 transition-all duration-500"
                                        style={{ width: `${profileCompletion}%` }}
                                    />
                                </div>
                            </div>

                            {/* Stats Row */}
                            <div className="flex items-center justify-center sm:justify-start gap-6 mt-4">
                                <div className="text-center">
                                    <p className="text-2xl font-bold text-white">0</p>
                                    <p className="text-xs text-slate-400">Trajets conduits</p>
                                </div>
                                <div className="w-px h-8 bg-white/20" />
                                <div className="text-center">
                                    <p className="text-2xl font-bold text-white">0</p>
                                    <p className="text-xs text-slate-400">Comme passager</p>
                                </div>
                                <div className="w-px h-8 bg-white/20" />
                                <div className="text-center">
                                    <div className="flex items-center justify-center gap-1">
                                        <Star className="w-4 h-4 text-amber-400 fill-amber-400" />
                                        <span className="text-2xl font-bold text-white">--</span>
                                    </div>
                                    <p className="text-xs text-slate-400">Note</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Tab Navigation */}
            <div className="bg-white border-b border-slate-200 sticky top-16 z-10">
                <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
                    <nav className="flex gap-1 overflow-x-auto">
                        {tabs.map((tab) => (
                            <button
                                key={tab.id}
                                onClick={() => setActiveTab(tab.id)}
                                className={`flex items-center gap-2 px-4 py-3 text-sm font-medium border-b-2 transition-colors whitespace-nowrap ${activeTab === tab.id
                                        ? 'border-[#FF7A00] text-[#FF7A00]'
                                        : 'border-transparent text-slate-500 hover:text-slate-700'
                                    }`}
                            >
                                <tab.icon className="w-4 h-4" />
                                {tab.label}
                            </button>
                        ))}
                    </nav>
                </div>
            </div>

            {/* Main Content */}
            <main className="flex-1 py-8">
                <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">

                    {/* INFORMATIONS TAB */}
                    {activeTab === 'info' && (
                        <Card className="shadow-lg border-0">
                            <CardHeader className="border-b border-slate-100">
                                <div className="flex items-center justify-between">
                                    <div>
                                        <CardTitle className="text-lg text-[#0A1B3C]">Informations personnelles</CardTitle>
                                        <CardDescription>Gérez vos données de profil</CardDescription>
                                    </div>
                                    {!isEditing ? (
                                        <Button variant="outline" size="sm" onClick={() => setIsEditing(true)}>
                                            <Edit3 className="w-4 h-4 mr-2" />
                                            Modifier
                                        </Button>
                                    ) : (
                                        <div className="flex gap-2">
                                            <Button variant="ghost" size="sm" onClick={() => setIsEditing(false)}>
                                                Annuler
                                            </Button>
                                            <Button size="sm" onClick={handleSave} className="bg-[#FF7A00] hover:bg-orange-600">
                                                <Save className="w-4 h-4 mr-2" />
                                                Enregistrer
                                            </Button>
                                        </div>
                                    )}
                                </div>
                            </CardHeader>
                            <CardContent className="p-6">
                                {/* Identité Section */}
                                <div className="mb-6">
                                    <h3 className="text-sm font-medium text-slate-500 mb-4 flex items-center gap-2">
                                        <User className="w-4 h-4" /> Identité
                                    </h3>
                                    <div className="grid sm:grid-cols-2 gap-4">
                                        <div className="space-y-2">
                                            <Label className="text-slate-600">Prénom</Label>
                                            {isEditing ? (
                                                <Input name="firstName" value={formData.firstName} onChange={handleChange} placeholder="Votre prénom" />
                                            ) : (
                                                <p className="text-[#0A1B3C] font-medium py-2">{formData.firstName || 'Non renseigné'}</p>
                                            )}
                                        </div>
                                        <div className="space-y-2">
                                            <Label className="text-slate-600">Nom</Label>
                                            {isEditing ? (
                                                <Input name="lastName" value={formData.lastName} onChange={handleChange} placeholder="Votre nom" />
                                            ) : (
                                                <p className="text-[#0A1B3C] font-medium py-2">{formData.lastName || 'Non renseigné'}</p>
                                            )}
                                        </div>
                                        <div className="space-y-2">
                                            <Label className="text-slate-600">Date de naissance</Label>
                                            {isEditing ? (
                                                <Input name="dob" type="date" value={formData.dob} onChange={handleChange} />
                                            ) : (
                                                <p className="text-[#0A1B3C] font-medium py-2">{formatDate(formData.dob)}</p>
                                            )}
                                        </div>
                                    </div>
                                </div>

                                {/* Contact Section */}
                                <div className="mb-6 pt-6 border-t border-slate-100">
                                    <h3 className="text-sm font-medium text-slate-500 mb-4 flex items-center gap-2">
                                        <Phone className="w-4 h-4" /> Contact
                                    </h3>
                                    <div className="grid sm:grid-cols-2 gap-4">
                                        <div className="space-y-2">
                                            <Label className="text-slate-600">Email</Label>
                                            <p className="text-[#0A1B3C] font-medium py-2">{formData.email}</p>
                                        </div>
                                        <div className="space-y-2">
                                            <Label className="text-slate-600">Téléphone</Label>
                                            {isEditing ? (
                                                <Input name="phoneNumber" value={formData.phoneNumber} onChange={handleChange} placeholder="+216 XX XXX XXX" />
                                            ) : (
                                                <p className="text-[#0A1B3C] font-medium py-2">{formData.phoneNumber || 'Non renseigné'}</p>
                                            )}
                                        </div>
                                    </div>
                                </div>

                                {/* Bio Section */}
                                <div className="pt-6 border-t border-slate-100">
                                    <h3 className="text-sm font-medium text-slate-500 mb-4 flex items-center gap-2">
                                        <FileText className="w-4 h-4" /> Bio
                                    </h3>
                                    {isEditing ? (
                                        <textarea
                                            name="bio"
                                            value={formData.bio}
                                            onChange={handleChange}
                                            rows={3}
                                            placeholder="Parlez un peu de vous..."
                                            className="w-full px-4 py-3 border border-slate-200 rounded-xl focus:border-[#FF7A00] focus:ring-1 focus:ring-[#FF7A00] resize-none"
                                        />
                                    ) : (
                                        <p className="text-slate-600">{formData.bio || 'Aucune bio renseignée'}</p>
                                    )}
                                </div>
                            </CardContent>
                        </Card>
                    )}

                    {/* SOCIAL TAB */}
                    {activeTab === 'social' && (
                        <Card className="shadow-lg border-0">
                            <CardHeader className="border-b border-slate-100">
                                <div className="flex items-center justify-between">
                                    <div>
                                        <CardTitle className="text-lg text-[#0A1B3C]">Réseaux sociaux</CardTitle>
                                        <CardDescription>Aidez les passagers à vous retrouver</CardDescription>
                                    </div>
                                    {!isEditing ? (
                                        <Button variant="outline" size="sm" onClick={() => setIsEditing(true)}>
                                            <Edit3 className="w-4 h-4 mr-2" />
                                            Modifier
                                        </Button>
                                    ) : (
                                        <div className="flex gap-2">
                                            <Button variant="ghost" size="sm" onClick={() => setIsEditing(false)}>Annuler</Button>
                                            <Button size="sm" onClick={handleSave} className="bg-[#FF7A00] hover:bg-orange-600">
                                                <Save className="w-4 h-4 mr-2" />
                                                Enregistrer
                                            </Button>
                                        </div>
                                    )}
                                </div>
                            </CardHeader>
                            <CardContent className="p-6 space-y-6">
                                <div className="space-y-2">
                                    <Label className="text-slate-600 flex items-center gap-2">
                                        <Facebook className="w-4 h-4 text-blue-600" /> Facebook
                                    </Label>
                                    {isEditing ? (
                                        <div className="relative">
                                            <div className="absolute left-3 top-1/2 -translate-y-1/2">
                                                <Facebook className="w-4 h-4 text-blue-600" />
                                            </div>
                                            <Input
                                                name="facebookUrl"
                                                value={formData.facebookUrl}
                                                onChange={handleChange}
                                                placeholder="https://facebook.com/username"
                                                className="pl-10"
                                            />
                                        </div>
                                    ) : formData.facebookUrl ? (
                                        <a href={formData.facebookUrl} target="_blank" rel="noopener noreferrer" className="text-blue-600 hover:underline flex items-center gap-2">
                                            <Facebook className="w-4 h-4" />
                                            {formData.facebookUrl}
                                        </a>
                                    ) : (
                                        <p className="text-slate-400">Non renseigné</p>
                                    )}
                                </div>
                                <div className="space-y-2">
                                    <Label className="text-slate-600 flex items-center gap-2">
                                        <Instagram className="w-4 h-4 text-pink-600" /> Instagram
                                    </Label>
                                    {isEditing ? (
                                        <div className="relative">
                                            <div className="absolute left-3 top-1/2 -translate-y-1/2">
                                                <Instagram className="w-4 h-4 text-pink-600" />
                                            </div>
                                            <Input
                                                name="instagramUrl"
                                                value={formData.instagramUrl}
                                                onChange={handleChange}
                                                placeholder="https://instagram.com/username"
                                                className="pl-10"
                                            />
                                        </div>
                                    ) : formData.instagramUrl ? (
                                        <a href={formData.instagramUrl} target="_blank" rel="noopener noreferrer" className="text-pink-600 hover:underline flex items-center gap-2">
                                            <Instagram className="w-4 h-4" />
                                            {formData.instagramUrl}
                                        </a>
                                    ) : (
                                        <p className="text-slate-400">Non renseigné</p>
                                    )}
                                </div>
                            </CardContent>
                        </Card>
                    )}

                    {/* PREFERENCES TAB */}
                    {activeTab === 'preferences' && (
                        <Card className="shadow-lg border-0">
                            <CardHeader className="border-b border-slate-100">
                                <CardTitle className="text-lg text-[#0A1B3C]">Préférences</CardTitle>
                                <CardDescription>Personnalisez votre expérience</CardDescription>
                            </CardHeader>
                            <CardContent className="p-0">
                                <Link to="#" className="flex items-center justify-between p-4 hover:bg-slate-50 border-b border-slate-100 transition-colors">
                                    <div className="flex items-center gap-4">
                                        <div className="w-10 h-10 bg-blue-50 rounded-full flex items-center justify-center">
                                            <Bell className="w-5 h-5 text-blue-600" />
                                        </div>
                                        <div>
                                            <p className="font-medium text-[#0A1B3C]">Notifications</p>
                                            <p className="text-sm text-slate-500">Gérer les alertes email et push</p>
                                        </div>
                                    </div>
                                    <ChevronRight className="w-5 h-5 text-slate-400" />
                                </Link>
                                <Link to="#" className="flex items-center justify-between p-4 hover:bg-slate-50 border-b border-slate-100 transition-colors">
                                    <div className="flex items-center gap-4">
                                        <div className="w-10 h-10 bg-emerald-50 rounded-full flex items-center justify-center">
                                            <Globe className="w-5 h-5 text-emerald-600" />
                                        </div>
                                        <div>
                                            <p className="font-medium text-[#0A1B3C]">Langue</p>
                                            <p className="text-sm text-slate-500">Français</p>
                                        </div>
                                    </div>
                                    <ChevronRight className="w-5 h-5 text-slate-400" />
                                </Link>
                                <Link to="#" className="flex items-center justify-between p-4 hover:bg-slate-50 transition-colors">
                                    <div className="flex items-center gap-4">
                                        <div className="w-10 h-10 bg-orange-50 rounded-full flex items-center justify-center">
                                            <Car className="w-5 h-5 text-[#FF7A00]" />
                                        </div>
                                        <div>
                                            <p className="font-medium text-[#0A1B3C]">Préférences de trajet</p>
                                            <p className="text-sm text-slate-500">Musique, fumeur, discussions</p>
                                        </div>
                                    </div>
                                    <ChevronRight className="w-5 h-5 text-slate-400" />
                                </Link>
                            </CardContent>
                        </Card>
                    )}

                    {/* SECURITY TAB */}
                    {activeTab === 'security' && (
                        <Card className="shadow-lg border-0">
                            <CardHeader className="border-b border-slate-100">
                                <CardTitle className="text-lg text-[#0A1B3C]">Sécurité</CardTitle>
                                <CardDescription>Protégez votre compte</CardDescription>
                            </CardHeader>
                            <CardContent className="p-0">
                                <Link to="#" className="flex items-center justify-between p-4 hover:bg-slate-50 border-b border-slate-100 transition-colors">
                                    <div className="flex items-center gap-4">
                                        <div className="w-10 h-10 bg-purple-50 rounded-full flex items-center justify-center">
                                            <Lock className="w-5 h-5 text-purple-600" />
                                        </div>
                                        <div>
                                            <p className="font-medium text-[#0A1B3C]">Changer le mot de passe</p>
                                            <p className="text-sm text-slate-500">Dernière modification: jamais</p>
                                        </div>
                                    </div>
                                    <ChevronRight className="w-5 h-5 text-slate-400" />
                                </Link>
                                <Link to="#" className="flex items-center justify-between p-4 hover:bg-slate-50 border-b border-slate-100 transition-colors">
                                    <div className="flex items-center gap-4">
                                        <div className="w-10 h-10 bg-red-50 rounded-full flex items-center justify-center">
                                            <Shield className="w-5 h-5 text-red-600" />
                                        </div>
                                        <div>
                                            <p className="font-medium text-[#0A1B3C]">Sessions actives</p>
                                            <p className="text-sm text-slate-500">Gérer les appareils connectés</p>
                                        </div>
                                    </div>
                                    <ChevronRight className="w-5 h-5 text-slate-400" />
                                </Link>
                                <Link to="#" className="flex items-center justify-between p-4 hover:bg-slate-50 transition-colors">
                                    <div className="flex items-center gap-4">
                                        <div className="w-10 h-10 bg-amber-50 rounded-full flex items-center justify-center">
                                            <CreditCard className="w-5 h-5 text-amber-600" />
                                        </div>
                                        <div>
                                            <p className="font-medium text-[#0A1B3C]">Méthodes de paiement</p>
                                            <p className="text-sm text-slate-500">Aucune carte enregistrée</p>
                                        </div>
                                    </div>
                                    <ChevronRight className="w-5 h-5 text-slate-400" />
                                </Link>
                            </CardContent>
                        </Card>
                    )}

                </div>
            </main>

            <Footer />
        </div>
    );
};

export default Profile;
