import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createRide } from '@/api/rideApi';
import Navbar from '@/components/layout/Navbar';
import Footer from '@/components/layout/Footer';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Checkbox } from '@/components/ui/checkbox';
import { Calendar, MapPin, Users, Coins, Car, AlertCircle } from 'lucide-react';
import { TUNISIAN_CITIES } from '@/data/tunisianCities';
import { Alert, AlertDescription } from '@/components/ui/alert';

const CreateRide = () => {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const [formData, setFormData] = useState({
        originName: '',
        destinationName: '',
        date: '',
        time: '',
        totalSeats: 3,
        pricePerSeat: 15,
        allowsSmoking: false,
        genderPreference: 'ANY'
    });

    // Helper to find city coordinates
    const getCityCoords = (cityName) => {
        const city = TUNISIAN_CITIES.find(c => c.name === cityName);
        return city ? { lat: city.lat, lon: city.lon } : null;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);

        // Validation
        if (formData.originName === formData.destinationName) {
            setError("Le départ et l'arrivée ne peuvent pas être identiques.");
            setLoading(false);
            return;
        }

        const originCoords = getCityCoords(formData.originName);
        const destCoords = getCityCoords(formData.destinationName);

        if (!originCoords || !destCoords) {
            setError("Veuillez sélectionner des villes valides dans la liste.");
            setLoading(false);
            return;
        }

        try {
            // Combine date and time to ISO format
            const departureDateTime = new Date(`${formData.date}T${formData.time}`).toISOString();

            const rideData = {
                originName: formData.originName,
                originLat: originCoords.lat,
                originLon: originCoords.lon,
                destinationName: formData.destinationName,
                destinationLat: destCoords.lat,
                destinationLon: destCoords.lon,
                departureTime: departureDateTime,
                totalSeats: parseInt(formData.totalSeats),
                pricePerSeat: parseFloat(formData.pricePerSeat),
                allowsSmoking: formData.allowsSmoking,
                genderPreference: formData.genderPreference
            };

            const createdRide = await createRide(rideData); // rideApi.js createRide

            // Redirect to the new ride's details page
            navigate(`/ride/${createdRide.id}`);
        } catch (err) {
            console.error('Failed to create ride:', err);
            setError(err.response?.data?.message || "Erreur lors de la création du trajet.");
        } finally {
            setLoading(false);
        }
    };

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const handleSelectChange = (name, value) => {
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    // Helper for city selects
    const CitySelect = ({ value, onChange, label, placeholder, icon: Icon }) => (
        <div className="space-y-2">
            <Label className="text-slate-600 flex items-center gap-2">
                <Icon className="w-4 h-4" /> {label}
            </Label>
            <Select value={value} onValueChange={onChange}>
                <SelectTrigger className="border-slate-200 focus:ring-[#FF7A00] h-11">
                    <SelectValue placeholder={placeholder} />
                </SelectTrigger>
                <SelectContent>
                    {TUNISIAN_CITIES.map(city => (
                        <SelectItem key={city.name} value={city.name}>
                            {city.name}
                        </SelectItem>
                    ))}
                </SelectContent>
            </Select>
        </div>
    );

    return (
        <div className="min-h-screen bg-slate-50 flex flex-col">
            <Navbar />

            <main className="flex-grow pt-24 pb-12 px-4 sm:px-6 lg:px-8">
                <div className="max-w-2xl mx-auto">
                    <div className="mb-8 text-center">
                        <h1 className="text-3xl font-bold text-[#0A1B3C]">Publier un trajet</h1>
                        <p className="text-slate-600 mt-2">Partagez vos frais et rencontrez des passagers sympas</p>
                    </div>

                    <Card className="border-0 shadow-lg">
                        <CardHeader className="bg-[#0A1B3C] text-white rounded-t-lg p-6">
                            <CardTitle className="flex items-center gap-2">
                                <Car className="w-6 h-6 text-[#FF7A00]" />
                                Détails du voyage
                            </CardTitle>
                            <CardDescription className="text-slate-300">
                                Remplissez les informations ci-dessous pour proposer votre covoiturage.
                            </CardDescription>
                        </CardHeader>
                        <CardContent className="p-8">
                            {error && (
                                <Alert variant="destructive" className="mb-6">
                                    <AlertCircle className="h-4 w-4" />
                                    <AlertDescription>{error}</AlertDescription>
                                </Alert>
                            )}

                            <form onSubmit={handleSubmit} className="space-y-8">
                                {/* Itinerary Section */}
                                <div className="space-y-4">
                                    <h3 className="text-lg font-semibold text-[#0A1B3C] border-b pb-2">Itinéraire</h3>
                                    <div className="grid md:grid-cols-2 gap-4">
                                        <CitySelect
                                            label="Départ"
                                            placeholder="Ville de départ"
                                            value={formData.originName}
                                            onChange={(val) => handleSelectChange('originName', val)}
                                            icon={MapPin}
                                        />
                                        <CitySelect
                                            label="Arrivée"
                                            placeholder="Ville d'arrivée"
                                            value={formData.destinationName}
                                            onChange={(val) => handleSelectChange('destinationName', val)}
                                            icon={MapPin}
                                        />
                                    </div>
                                </div>

                                {/* Date & Time Section */}
                                <div className="space-y-4">
                                    <h3 className="text-lg font-semibold text-[#0A1B3C] border-b pb-2">Date et Heure</h3>
                                    <div className="grid md:grid-cols-2 gap-4">
                                        <div className="space-y-2">
                                            <Label className="text-slate-600 flex items-center gap-2">
                                                <Calendar className="w-4 h-4" /> Date
                                            </Label>
                                            <Input
                                                type="date"
                                                name="date"
                                                required
                                                min={new Date().toISOString().split('T')[0]} // Min today
                                                value={formData.date}
                                                onChange={handleChange}
                                                className="border-slate-200 focus:ring-[#FF7A00] h-11"
                                            />
                                        </div>
                                        <div className="space-y-2">
                                            <Label className="text-slate-600 flex items-center gap-2">
                                                <Calendar className="w-4 h-4" /> Heure
                                            </Label>
                                            <Input
                                                type="time"
                                                name="time"
                                                required
                                                value={formData.time}
                                                onChange={handleChange}
                                                className="border-slate-200 focus:ring-[#FF7A00] h-11"
                                            />
                                        </div>
                                    </div>
                                </div>

                                {/* Comfort Section */}
                                <div className="space-y-4">
                                    <h3 className="text-lg font-semibold text-[#0A1B3C] border-b pb-2">Confort et Prix</h3>
                                    <div className="grid md:grid-cols-2 gap-4">
                                        <div className="space-y-2">
                                            <Label className="text-slate-600 flex items-center gap-2">
                                                <Users className="w-4 h-4" /> Places disponibles
                                            </Label>
                                            <Select
                                                value={formData.totalSeats.toString()}
                                                onValueChange={(val) => handleSelectChange('totalSeats', val)}
                                            >
                                                <SelectTrigger className="border-slate-200 focus:ring-[#FF7A00] h-11">
                                                    <SelectValue placeholder="3" />
                                                </SelectTrigger>
                                                <SelectContent>
                                                    {[1, 2, 3, 4, 5, 6].map(num => (
                                                        <SelectItem key={num} value={num.toString()}>
                                                            {num} place{num > 1 ? 's' : ''}
                                                        </SelectItem>
                                                    ))}
                                                </SelectContent>
                                            </Select>
                                        </div>
                                        <div className="space-y-2">
                                            <Label className="text-slate-600 flex items-center gap-2">
                                                <Coins className="w-4 h-4" /> Prix par place (DT)
                                            </Label>
                                            <Input
                                                type="number"
                                                name="pricePerSeat"
                                                required
                                                min="1"
                                                step="0.1"
                                                value={formData.pricePerSeat}
                                                onChange={handleChange}
                                                className="border-slate-200 focus:ring-[#FF7A00] h-11"
                                            />
                                        </div>
                                    </div>
                                </div>

                                {/* Preferences Section */}
                                <div className="space-y-4">
                                    <h3 className="text-lg font-semibold text-[#0A1B3C] border-b pb-2">Préférences</h3>

                                    <div className="space-y-4">
                                        <div className="space-y-2">
                                            <Label className="text-slate-600">Qui peut réserver ?</Label>
                                            <Select
                                                value={formData.genderPreference}
                                                onValueChange={(val) => handleSelectChange('genderPreference', val)}
                                            >
                                                <SelectTrigger className="border-slate-200 focus:ring-[#FF7A00] h-11">
                                                    <SelectValue />
                                                </SelectTrigger>
                                                <SelectContent>
                                                    <SelectItem value="ANY">Tout le monde</SelectItem>
                                                    <SelectItem value="FEMALE_ONLY">Femmes uniquement</SelectItem>
                                                    <SelectItem value="MALE_ONLY">Hommes uniquement</SelectItem>
                                                </SelectContent>
                                            </Select>
                                        </div>

                                        <div className="flex items-center space-x-2 pt-2">
                                            <Checkbox
                                                id="allowsSmoking"
                                                name="allowsSmoking"
                                                checked={formData.allowsSmoking}
                                                onCheckedChange={(checked) => handleSelectChange('allowsSmoking', checked)}
                                            />
                                            <label
                                                htmlFor="allowsSmoking"
                                                className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
                                            >
                                                Fumeurs acceptés
                                            </label>
                                        </div>
                                    </div>
                                </div>

                                <Button
                                    type="submit"
                                    className="w-full bg-[#FF7A00] hover:bg-[#FF7A00]/90 text-white font-bold h-12 text-lg mt-4"
                                    disabled={loading}
                                >
                                    {loading ? (
                                        <div className="flex items-center gap-2">
                                            <div className="animate-spin h-5 w-5 border-t-2 border-white rounded-full"></div>
                                            Publication...
                                        </div>
                                    ) : (
                                        "Publier le trajet"
                                    )}
                                </Button>
                            </form>
                        </CardContent>
                    </Card>
                </div>
            </main>

            <Footer />
        </div>
    );
};

export default CreateRide;
