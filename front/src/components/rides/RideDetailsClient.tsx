"use client";

import { useState, useEffect, useRef } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { ArrowLeft, CheckCircle, XCircle } from "lucide-react";
import Navbar from "@/components/layout/Navbar";
import { Ride, PassengerInfo } from "@/types/models";
import { UserProfile } from "@/lib/api/user";
import { acceptBookingAction, rejectBookingAction } from "@/app/actions/bookings";
import { startRideAction, completeRideAction, deleteRideAction } from "@/app/actions/rides";
import { useToast } from "@/contexts/ToastContext";
import { useNotifications } from "@/contexts/NotificationContext";
import ConfirmationModal from "@/components/ui/confirmation-modal";

// Subcomponents
import RideHeader from "./RideHeader";
import RideDetailsGrid from "./RideDetailsGrid";
import PassengerList from "./driver/PassengerList";
import DriverActions from "./driver/DriverActions";
import DriverContactView from "./passenger/DriverContactView";
import BookingActions from "./passenger/BookingActions";
import ActiveDriverView from "./driver/ActiveDriverView";
import CompletedDriverView from "./driver/CompletedDriverView";
import ActivePassengerView from "./passenger/ActivePassengerView";
import CompletedPassengerView from "./passenger/CompletedPassengerView";

interface RideDetailsClientProps {
    ride: Ride;
    initialPassengers: PassengerInfo[];
    currentUser: UserProfile | null;
}

/**
 * Ride details page client component - orchestrates subcomponents
 * Handles state management and action handlers
 */
export default function RideDetailsClient({
    ride: initialRide,
    initialPassengers,
    currentUser
}: RideDetailsClientProps) {
    const router = useRouter();
    const { showError } = useToast();

    // State
    const [ride, setRide] = useState<Ride>(initialRide);
    const [passengers, setPassengers] = useState<PassengerInfo[]>(initialPassengers);
    const [actionLoading, setActionLoading] = useState<string | null>(null);
    const [rideActionLoading, setRideActionLoading] = useState(false);
    const [showCancelModal, setShowCancelModal] = useState(false);

    const { notifications } = useNotifications();

    const lastProcessedNotificationId = useRef<string | null>(null);

    // Watch for relevant notifications to refresh data AND optimistically update UI
    useEffect(() => {
        if (notifications.length > 0) {
            const latest = notifications[0];

            // Prevent processing the same notification multiple times
            if (latest.id === lastProcessedNotificationId.current) {
                return;
            }

            // If we get a booking update
            if (latest.type === "BOOKING_ACCEPTED") {
                // 1. Optimistically mark my pending booking as CONFIRMED
                if (currentUser) {
                    setPassengers(prev => {
                        return prev.map(p => {
                            if (p.passengerEmail.toLowerCase() === currentUser.email.toLowerCase()) {
                                return { ...p, status: "CONFIRMED" };
                            }
                            return p;
                        });
                    });
                }
                lastProcessedNotificationId.current = latest.id;
                // 2. Refresh server data
                router.refresh();
            } else if (latest.type === "BOOKING_REJECTED") {
                lastProcessedNotificationId.current = latest.id;
                // Optimistically remove
                if (currentUser) {
                    setPassengers(prev => prev.filter(p => p.passengerEmail.toLowerCase() !== currentUser.email.toLowerCase()));
                }
                // Refresh to clear pending state
                router.refresh();
            } else if (latest.type === "RIDE_CANCELLED") {
                lastProcessedNotificationId.current = latest.id;
                setRide(prev => ({ ...prev, status: "CANCELLED" }));
                router.refresh();
            }
        }
    }, [notifications, router, currentUser]);

    // Sync state with server props on refresh
    useEffect(() => {
        setRide(initialRide);
    }, [initialRide]);

    // Sync state with server props on refresh, but preserve my pending optimistic updates
    useEffect(() => {
        setPassengers(prev => {
            // Check for current user in previous state
            const myLocal = prev.find(p =>
                currentUser &&
                p.passengerEmail.toLowerCase() === currentUser.email.toLowerCase()
            );

            // If server now has me, use server data (it's confirmed or whatever server says)
            const serverHasMe = initialPassengers.some(p =>
                currentUser && p.passengerEmail.toLowerCase() === currentUser.email.toLowerCase()
            );

            if (myLocal && !serverHasMe) {
                // If I exist locally but not on server yet (likely race condition or optimistic update)
                // Keep me if I'm PENDING or CONFIRMED
                if (myLocal.status === "PENDING" || myLocal.status === "CONFIRMED") {
                    // Start with server data
                    const filteredServer = initialPassengers.filter(p => !p.bookingId.startsWith("temp-"));
                    // Make sure we don't add duplicates based on email (just in case)
                    const uniqueLocal = !filteredServer.some(p => p.passengerEmail.toLowerCase() === myLocal.passengerEmail.toLowerCase());
                    return uniqueLocal ? [...filteredServer, myLocal] : filteredServer;
                }
            }

            // Otherwise, purely trust server
            return initialPassengers;
        });
    }, [initialPassengers, currentUser]);

    // Fetch secure booking details (for license plate) if confirmed
    const [secureBooking, setSecureBooking] = useState<any>(null);
    useEffect(() => {
        const fetchSecureDetails = async () => {
            if (currentUser && passengers.some(p => p.passengerEmail === currentUser.email && p.status === 'CONFIRMED')) {
                const { getMyBookingForRideAction } = await import('@/app/actions/bookings');
                const result = await getMyBookingForRideAction(ride.id);
                if (result.success) {
                    setSecureBooking(result.booking);
                }
            }
        };
        fetchSecureDetails();
    }, [currentUser, passengers, ride.id]);

    // Derived state for Lifecycle Logic
    const isOwnRide = currentUser && ride.driverEmail.toLowerCase() === currentUser.email.toLowerCase();

    const confirmedPassengersCount = passengers.filter(p => p.status === "CONFIRMED").length;

    // Check if within 30 minutes of departure (Operational Window)
    const operationalThresholdMinutes = 30;
    const now = new Date();
    const departure = new Date(ride.departureTime);
    const diffInMinutes = (departure.getTime() - now.getTime()) / (1000 * 60);
    const isWithinOperationalWindow = diffInMinutes <= operationalThresholdMinutes;

    // Acquisition Phase: No bookings AND not yet close to departure
    // If we have >0 bookings OR we are close/past departure time, we are Operational
    const isAcquisitionPhase = confirmedPassengersCount === 0 && !isWithinOperationalWindow;

    const currentPassenger = currentUser ? passengers.find(p => p.passengerEmail.toLowerCase() === currentUser.email.toLowerCase()) : null;
    const isPassengerBooked = !!currentPassenger;



    const handleBookingSuccess = (seatsBooked: number) => {
        if (!currentUser) return;
        // Optimistic update
        const newPassenger: PassengerInfo = {
            bookingId: "temp-" + Date.now(), // Temporary ID until refresh
            passengerName: `${currentUser.firstName} ${currentUser.lastName}`,
            passengerEmail: currentUser.email,
            passengerPhone: currentUser.phoneNumber || "",
            facebookUrl: currentUser.facebookUrl || null,
            instagramUrl: currentUser.instagramUrl || null,
            seatsBooked: seatsBooked,
            status: "PENDING"
        };
        setPassengers(prev => [...prev, newPassenger]);
    };

    // Booking handlers
    const handleAcceptBooking = async (bookingId: string) => {
        setActionLoading(bookingId);
        try {
            const result = await acceptBookingAction(bookingId);
            if (!result.success) throw new Error(result.error);
            setPassengers((prev) =>
                prev.map((p) =>
                    p.bookingId === bookingId ? { ...p, status: "CONFIRMED" } : p
                )
            );
        } catch (err) {
            console.error("Failed to accept booking:", err);
            showError("Erreur lors de l'acceptation de la réservation");
        } finally {
            setActionLoading(null);
        }
    };

    const handleRejectBooking = async (bookingId: string) => {
        setActionLoading(bookingId);
        try {
            const result = await rejectBookingAction(bookingId);
            if (!result.success) throw new Error(result.error);
            setPassengers((prev) => prev.filter((p) => p.bookingId !== bookingId));
        } catch (err) {
            console.error("Failed to reject booking:", err);
            showError("Erreur lors du refus de la réservation");
        } finally {
            setActionLoading(null);
        }
    };

    // Ride action handlers
    const handleStartRide = async () => {
        setRideActionLoading(true);
        try {
            const result = await startRideAction(ride.id);
            if (!result.success || !result.ride) throw new Error(result.error);
            // API returns status only, merge it
            setRide(prev => ({ ...prev, status: result.ride!.status }));
        } catch (err) {
            console.error("Failed to start ride:", err);
            showError("Erreur lors du démarrage du trajet");
        } finally {
            setRideActionLoading(false);
        }
    };

    const handleCompleteRide = async () => {
        setRideActionLoading(true);
        try {
            const result = await completeRideAction(ride.id);
            if (!result.success || !result.ride) throw new Error(result.error);
            // The API returns a lightweight status response, merged here.
            setRide(prev => ({ ...prev, status: result.ride!.status }));
        } catch (err) {
            console.error("Failed to complete ride:", err);
            showError("Erreur lors de la clôture du trajet");
        } finally {
            setRideActionLoading(false);
        }
    };

    const handleCancelRide = () => {
        setShowCancelModal(true);
    };

    const executeCancelRide = async () => {
        setRideActionLoading(true);
        try {
            const result = await deleteRideAction(ride.id);
            if (!result.success) throw new Error(result.error);
            router.push("/dashboard/rides");
        } catch (err) {
            console.error("Failed to cancel ride:", err);
            showError("Erreur lors de l'annulation du trajet");
            setRideActionLoading(false); // Only stop loading on error, otherwise we navigate away
            setShowCancelModal(false);
        }
    };

    return (
        <div className="min-h-screen bg-[#F8FAFC] pb-24 lg:pb-8">
            <Navbar />
            <div className="h-16"></div>

            <main className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                {/* Back button */}
                <button
                    onClick={() => router.back()}
                    className="flex items-center gap-2 text-slate-600 hover:text-slate-900 mb-6 transition-colors"
                >
                    <ArrowLeft className="w-5 h-5" />
                    <span>Retour</span>
                </button>

                <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
                    {isOwnRide && ride.status === 'IN_PROGRESS' ? (
                        <ActiveDriverView
                            ride={ride}
                            passengers={passengers}
                            onComplete={handleCompleteRide}
                            onCancel={handleCancelRide}
                            isLoading={rideActionLoading}
                        />
                    ) : isOwnRide && ride.status === 'COMPLETED' ? (
                        <CompletedDriverView
                            ride={ride}
                            passengers={passengers}
                        />
                    ) : (
                        <>
                            {/* Route Header */}
                            <RideHeader ride={ride} />

                            {/* Details Grid */}
                            <RideDetailsGrid ride={ride} />

                            {/* Cancelled State Banner */}
                            {ride.status === 'CANCELLED' ? (
                                <div className="p-8 text-center border-t border-slate-100 bg-red-50">
                                    <div className="w-16 h-16 bg-red-100 text-red-600 rounded-full flex items-center justify-center mx-auto mb-4">
                                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-8 h-8">
                                            <path strokeLinecap="round" strokeLinejoin="round" d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636" />
                                        </svg>
                                    </div>
                                    <h3 className="text-xl font-bold text-red-900 mb-2">Trajet annulé</h3>
                                    <p className="text-red-700 max-w-md mx-auto">
                                        Ce trajet a été annulé par le conducteur et n'est plus disponible.
                                    </p>
                                </div>
                            ) : isOwnRide ? (
                                <>
                                    <PassengerList
                                        passengers={passengers}
                                        onAccept={handleAcceptBooking}
                                        onReject={handleRejectBooking}
                                        loadingBookingId={actionLoading}
                                    />
                                    <div className="px-6 pb-6 bg-emerald-50">
                                        {/* Demand Acquisition Mode Banner */}
                                        {isAcquisitionPhase && (
                                            <div className="mb-4 p-4 bg-amber-50 border border-amber-200 rounded-xl text-amber-800 text-sm">
                                                <p className="font-semibold mb-1">Phase de recherche de passagers</p>
                                                <p>
                                                    Le trajet n'a pas encore de passagers confirmés.
                                                    Vous pourrez le démarrer 30 minutes avant le départ ou dès la première réservation.
                                                </p>
                                            </div>
                                        )}

                                        <DriverActions
                                            ride={ride}
                                            isLoading={rideActionLoading}
                                            onStart={handleStartRide}
                                            onComplete={handleCompleteRide}
                                            onCancel={handleCancelRide}
                                            isAcquisitionPhase={isAcquisitionPhase}
                                            hasConfirmedBookings={confirmedPassengersCount > 0}
                                        />
                                        <div className="mt-4 pt-4 border-t border-emerald-200">
                                            <Link href="/dashboard/rides" className="text-sm text-emerald-700 hover:text-emerald-800 font-medium">
                                                ← Gérer mes trajets
                                            </Link>
                                        </div>
                                    </div>
                                </>
                            ) : ride.status === 'IN_PROGRESS' && currentPassenger?.status === 'CONFIRMED' ? (
                                <ActivePassengerView ride={ride} booking={secureBooking} />
                            ) : ride.status === 'COMPLETED' && currentPassenger?.status === 'CONFIRMED' ? (
                                <CompletedPassengerView
                                    ride={ride}
                                    currentPassenger={currentPassenger}
                                />
                            ) : (
                                /* Standard Passenger View */
                                <>
                                    <DriverContactView ride={ride} />

                                    {/* Secure Vehicle Info for Confirmed Passengers */}
                                    {secureBooking && secureBooking.carLicensePlate && (
                                        <div className="mb-6 p-4 border border-emerald-100 rounded-xl bg-emerald-50/50">
                                            <h3 className="font-semibold text-emerald-900 mb-2 flex items-center gap-2">
                                                <div className="p-1 bg-emerald-100 rounded-md">
                                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M19 17h2c.6 0 1-.4 1-1v-3c0-.9-.7-1.7-1.5-1.9C18.7 10.6 16 10 16 10s-1.3-1.4-2.2-2.3c-.5-.4-1.1-.7-1.8-.7H5c-.6 0-1.1.4-1.4.9l-1.4 2.9A3.7 3.7 0 0 0 2 12v4c0 .6.4 1 1 1h2" /><circle cx="7" cy="17" r="2" /><path d="M9 17h6" /><circle cx="17" cy="17" r="2" /></svg>
                                                </div>
                                                Véhicule confirmé
                                            </h3>
                                            <div className="flex items-center justify-between bg-white p-3 rounded-lg border border-emerald-100 shadow-sm">
                                                <span className="text-slate-700 font-medium">{secureBooking.carDescription}</span>
                                                <div className="flex flex-col items-end">
                                                    <span className="font-mono font-bold text-slate-900 px-2 py-1 bg-slate-100 rounded border border-slate-200 tracking-wider">
                                                        {secureBooking.carLicensePlate}
                                                    </span>
                                                    {secureBooking.carLicensePlate.includes("***") && (
                                                        <span className="text-[10px] text-slate-500 mt-1">Plaque complète 1h avant</span>
                                                    )}
                                                </div>
                                            </div>
                                        </div>
                                    )}

                                    {isPassengerBooked ? (
                                        <div className={`p-6 border-t border-slate-100 ${currentPassenger?.status === 'PENDING' ? 'bg-amber-50' :
                                            currentPassenger?.status === 'REJECTED' ? 'bg-red-50' :
                                                currentPassenger?.status === 'CANCELLED' ? 'bg-slate-50' :
                                                    'bg-emerald-50'
                                            }`}>
                                            <div className="text-center">
                                                <div className={`w-12 h-12 rounded-full flex items-center justify-center mx-auto mb-3 ${currentPassenger?.status === 'PENDING' ? 'bg-amber-100 text-amber-600' :
                                                    currentPassenger?.status === 'REJECTED' ? 'bg-red-100 text-red-600' :
                                                        currentPassenger?.status === 'CANCELLED' ? 'bg-slate-100 text-slate-500' :
                                                            'bg-emerald-100 text-emerald-600'
                                                    }`}>
                                                    {currentPassenger?.status === 'REJECTED' || currentPassenger?.status === 'CANCELLED' ? (
                                                        <XCircle className="w-6 h-6" />
                                                    ) : (
                                                        <CheckCircle className="w-6 h-6" />
                                                    )}
                                                </div>
                                                <h3 className={`text-lg font-semibold mb-1 ${currentPassenger?.status === 'PENDING' ? 'text-amber-900' :
                                                    currentPassenger?.status === 'REJECTED' ? 'text-red-900' :
                                                        currentPassenger?.status === 'CANCELLED' ? 'text-slate-900' :
                                                            'text-emerald-900'
                                                    }`}>
                                                    {currentPassenger?.status === 'PENDING' ? 'Demande envoyée' :
                                                        currentPassenger?.status === 'REJECTED' ? 'Demande refusée' :
                                                            currentPassenger?.status === 'CANCELLED' ? 'Réservation annulée' :
                                                                'Réservation confirmée'}
                                                </h3>
                                                <p className={`text-sm mb-4 ${currentPassenger?.status === 'PENDING' ? 'text-amber-700' :
                                                    currentPassenger?.status === 'REJECTED' ? 'text-red-700' :
                                                        currentPassenger?.status === 'CANCELLED' ? 'text-slate-600' :
                                                            'text-emerald-700'
                                                    }`}>
                                                    {currentPassenger?.status === 'PENDING'
                                                        ? "Votre demande a été envoyée au conducteur. Vous serez notifié dès qu'elle sera acceptée."
                                                        : currentPassenger?.status === 'REJECTED'
                                                            ? "Le conducteur a refusé votre demande de réservation pour ce trajet."
                                                            : currentPassenger?.status === 'CANCELLED'
                                                                ? "Vous avez annulé votre réservation pour ce trajet."
                                                                : "Vous avez réservé ce trajet. Retrouvez les détails dans \"Mes réservations\"."
                                                    }
                                                </p>
                                                <Link href="/dashboard/bookings" className={`text-sm font-medium underline ${currentPassenger?.status === 'PENDING' ? 'text-amber-800 hover:text-amber-900' :
                                                    currentPassenger?.status === 'REJECTED' ? 'text-red-800 hover:text-red-900' :
                                                        currentPassenger?.status === 'CANCELLED' ? 'text-slate-800 hover:text-slate-900' :
                                                            'text-emerald-800 hover:text-emerald-900'
                                                    }`}>
                                                    Voir ma réservation
                                                </Link>
                                            </div>
                                        </div>
                                    ) : (
                                        <BookingActions
                                            ride={ride}
                                            currentUser={currentUser}
                                            onBookingSuccess={handleBookingSuccess}
                                        />
                                    )}
                                </>
                            )}
                        </>
                    )}
                </div>


                <div className="mt-6 text-center">
                    <Link href="/dashboard/bookings" className="text-sm text-slate-500 hover:text-[#006B8F] transition-colors">
                        ← Retour à mes réservations
                    </Link>
                </div>
            </main>

            <ConfirmationModal
                isOpen={showCancelModal}
                onClose={() => !rideActionLoading && setShowCancelModal(false)}
                onConfirm={executeCancelRide}
                title="Annuler le trajet"
                message="Êtes-vous sûr de vouloir annuler ce trajet ? Cette action est irréversible et tous les passagers seront notifiés."
                confirmLabel="Oui, annuler le trajet"
                cancelLabel="Non, garder le trajet"
                isDestructive={true}
                isLoading={rideActionLoading}
            />
        </div>
    );
}
