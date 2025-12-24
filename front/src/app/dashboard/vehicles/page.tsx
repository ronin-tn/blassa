"use client";

import { useState, useEffect } from "react";
import { Plus, Trash2, Car, Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { toast } from "react-hot-toast";
import Navbar from "@/components/layout/Navbar";

interface Vehicle {
    id: string;
    make: string;
    model: string;
    color: string;
    licensePlate: string;
    productionYear?: number;
}

export default function VehiclesPage() {
    const [vehicles, setVehicles] = useState<Vehicle[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isAdding, setIsAdding] = useState(false);

    // Form state
    const [make, setMake] = useState("");
    const [model, setModel] = useState("");
    const [color, setColor] = useState("");
    const [licensePlate, setLicensePlate] = useState("");

    useEffect(() => {
        fetchVehicles();
    }, []);

    const fetchVehicles = async () => {
        try {
            const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/vehicles`, {
                credentials: 'include',
            });
            if (res.ok) {
                const data = await res.json();
                setVehicles(data);
            }
        } catch (error) {
            console.error("Failed to fetch vehicles", error);
        } finally {
            setIsLoading(false);
        }
    };

    const handleAddVehicle = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsAdding(true);

        try {
            const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/vehicles`, {
                method: "POST",
                credentials: 'include',
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ make, model, color, licensePlate }),
            });

            if (!res.ok) throw new Error("Erreur lors de l'ajout");

            const newVehicle = await res.json();
            setVehicles([...vehicles, newVehicle]);
            setMake("");
            setModel("");
            setColor("");
            setLicensePlate("");
            toast.success("Véhicule ajouté !");
        } catch (error) {
            toast.error("Impossible d'ajouter le véhicule");
        } finally {
            setIsAdding(false);
        }
    };

    const handleDelete = async (id: string) => {
        if (!confirm("Êtes-vous sûr de vouloir supprimer ce véhicule ?")) return;

        try {
            const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/vehicles/${id}`, {
                method: "DELETE",
                credentials: 'include',
            });

            if (!res.ok) throw new Error("Erreur lors de la suppression");

            setVehicles(vehicles.filter(v => v.id !== id));
            toast.success("Véhicule supprimé");
        } catch (error) {
            toast.error("Impossible de supprimer le véhicule");
        }
    };

    if (isLoading) {
        return <div className="p-8 text-center text-slate-500">Chargement...</div>;
    }

    return (
        <>
            <Navbar />
            <div className="max-w-4xl mx-auto py-8 px-4 pt-24">
                <h1 className="text-2xl font-bold text-slate-900 mb-2">Mes Véhicules</h1>
                <p className="text-slate-600 mb-8">Gérez vos véhicules pour les utiliser lors de vos trajets.</p>

                <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                    {/* List */}
                    <div className="space-y-4">
                        {vehicles.length === 0 ? (
                            <div className="p-8 bg-slate-50 border border-slate-200 rounded-xl text-center text-slate-500">
                                Aucun véhicule enregistré.
                            </div>
                        ) : (
                            vehicles.map((Vehicle) => (
                                <div key={Vehicle.id} className="bg-white p-4 rounded-xl border border-slate-200 flex items-center justify-between">
                                    <div className="flex items-center gap-4">
                                        <div className="w-12 h-12 bg-slate-100 rounded-full flex items-center justify-center text-slate-500">
                                            <Car className="w-6 h-6" />
                                        </div>
                                        <div>
                                            <h3 className="font-semibold text-slate-900">{Vehicle.make} {Vehicle.model}</h3>
                                            <p className="text-sm text-slate-500">{Vehicle.color} · {Vehicle.licensePlate}</p>
                                        </div>
                                    </div>
                                    <Button variant="ghost" size="icon" onClick={() => handleDelete(Vehicle.id)} className="text-red-500 hover:text-red-600 hover:bg-red-50">
                                        <Trash2 className="w-4 h-4" />
                                    </Button>
                                </div>
                            ))
                        )}
                    </div>

                    {/* Add Form */}
                    <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm h-fit">
                        <h2 className="font-semibold text-lg mb-4">Ajouter un véhicule</h2>
                        <form onSubmit={handleAddVehicle} className="space-y-4">
                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="text-xs font-medium text-slate-700 mb-1 block">Marque</label>
                                    <Input
                                        placeholder="ex: Volkswagen"
                                        value={make}
                                        onChange={(e) => setMake(e.target.value)}
                                        required
                                    />
                                </div>
                                <div>
                                    <label className="text-xs font-medium text-slate-700 mb-1 block">Modèle</label>
                                    <Input
                                        placeholder="ex: Golf 7"
                                        value={model}
                                        onChange={(e) => setModel(e.target.value)}
                                        required
                                    />
                                </div>
                            </div>
                            <div>
                                <label className="text-xs font-medium text-slate-700 mb-1 block">Couleur</label>
                                <Input
                                    placeholder="ex: Noir"
                                    value={color}
                                    onChange={(e) => setColor(e.target.value)}
                                    required
                                />
                            </div>
                            <div>
                                <label className="text-xs font-medium text-slate-700 mb-1 block">Immatriculation</label>
                                <Input
                                    placeholder="ex: 123 TN 4567"
                                    value={licensePlate}
                                    onChange={(e) => setLicensePlate(e.target.value)}
                                    required
                                />
                                <p className="text-xs text-slate-500 mt-1">
                                    Votre plaque sera masquée pour protéger votre vie privée jusqu'au départ du trajet.
                                </p>
                            </div>
                            <Button type="submit" disabled={isAdding} className="w-full bg-[#006B8F] hover:bg-[#005673]">
                                {isAdding ? "Ajout en cours..." : "Ajouter ce véhicule"}
                            </Button>
                        </form>
                    </div>
                </div>
            </div>
        </>
    );
}
