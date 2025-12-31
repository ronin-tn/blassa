"use client";

import { useEffect, useState } from "react";
import { Car, Plus } from "lucide-react";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue
} from "@/components/ui/select";
import { Button } from "@/components/ui/button";
import Link from "next/link";

interface Vehicle {
    id: string;
    make: string;
    model: string;
    color: string;
    licensePlate: string;
}

interface VehicleSelectorProps {
    value: string;
    onChange: (value: string) => void;
}

export function VehicleSelector({ value, onChange }: VehicleSelectorProps) {
    const [vehicles, setVehicles] = useState<Vehicle[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchVehicles = async () => {
            try {
                const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/vehicles`, {
                    credentials: 'include',
                });
                if (res.ok) {
                    const data = await res.json();
                    setVehicles(data);
                    // Auto-select if only one vehicle and none selected
                    if (data.length === 1 && !value) {
                        onChange(data[0].id);
                    }
                }
            } catch (e) {
                console.error(e);
            } finally {
                setLoading(false);
            }
        };

        fetchVehicles();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    if (loading) return <div className="h-10 w-full animate-pulse bg-slate-100 rounded-lg"></div>;

    if (vehicles.length === 0) {
        return (
            <div className="p-4 border border-dashed border-slate-300 rounded-xl bg-slate-50 text-center">
                <Car className="w-8 h-8 text-slate-400 mx-auto mb-2" />
                <p className="text-sm text-slate-600 mb-3">Vous n&apos;avez pas encore de véhicule.</p>
                <Link href="/dashboard/vehicles" target="_blank">
                    <Button type="button" variant="outline" size="sm" className="gap-2">
                        <Plus className="w-4 h-4" />
                        Ajouter un véhicule
                    </Button>
                </Link>
                <p className="text-xs text-slate-400 mt-2">Rechargez cette page après l&apos;ajout.</p>
            </div>
        );
    }

    return (
        <div className="space-y-2">
            <Select value={value} onValueChange={onChange}>
                <SelectTrigger className="w-full h-12">
                    <SelectValue placeholder="Sélectionnez un véhicule" />
                </SelectTrigger>
                <SelectContent>
                    {vehicles.map((v) => (
                        <SelectItem key={v.id} value={v.id}>
                            {v.make} {v.model} <span className="text-slate-500">({v.color})</span>
                        </SelectItem>
                    ))}
                </SelectContent>
            </Select>
            <div className="flex justify-end">
                <Link href="/dashboard/vehicles" target="_blank" className="text-xs text-[#006B8F] hover:underline flex items-center gap-1">
                    <Plus className="w-3 h-3" /> Gérer mes véhicules
                </Link>
            </div>
        </div>
    );
}
