"use client";

import { useEffect, useState } from "react";
import { adminApi, User } from "@/lib/api/admin";
import { Spinner } from "@/components/ui/Spinner";
import { ShieldAlert, CheckCircle, Ban, Search, X, ExternalLink, Mail, Shield, Phone } from "lucide-react";
import toast from "react-hot-toast";
import Link from "next/link";

export default function UsersPage() {
    const [users, setUsers] = useState<User[]>([]);
    const [filteredUsers, setFilteredUsers] = useState<User[]>([]);
    const [loading, setLoading] = useState(true);
    const [search, setSearch] = useState("");
    const [selectedUser, setSelectedUser] = useState<User | null>(null);

    useEffect(() => {
        loadUsers();
    }, []);

    useEffect(() => {
        // Simple client-side search
        if (!search) {
            setFilteredUsers(users);
        } else {
            const lower = search.toLowerCase();
            setFilteredUsers(users.filter(u =>
                u.email.toLowerCase().includes(lower) ||
                u.firstName.toLowerCase().includes(lower) ||
                u.lastName.toLowerCase().includes(lower) ||
                (u.phoneNumber && u.phoneNumber.includes(lower))
            ));
        }
    }, [search, users]);

    const loadUsers = async () => {
        try {
            const data = await adminApi.getUsers();
            setUsers(data);
            setFilteredUsers(data);
        } catch (e) {
            toast.error("Impossible de charger les utilisateurs");
        } finally {
            setLoading(false);
        }
    };

    const handleBan = async (id: string, isBanned: boolean) => {
        if (!confirm(isBanned ? "Réactiver cet utilisateur ?" : "Bannir cet utilisateur ?")) return;
        try {
            if (isBanned) {
                await adminApi.unbanUser(id);
                toast.success("Utilisateur réactivé");
            } else {
                await adminApi.banUser(id);
                toast.success("Utilisateur banni");
            }
            loadUsers();
            setSelectedUser(null);
        } catch (e) {
            toast.error("Erreur lors de l'action");
        }
    };

    if (loading) return (
        <div className="flex justify-center p-12">
            <Spinner size="lg" className="text-[#0e7490]" />
        </div>
    );

    return (
        <div className="space-y-6 animate-in fade-in duration-500">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                <div>
                    <h1 className="text-2xl font-bold text-gray-900">Gestion des Utilisateurs</h1>
                    <p className="text-gray-500">Gérez les comptes et les accès. Cliquez sur un utilisateur pour voir son profil.</p>
                </div>

                <div className="relative">
                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" />
                    <input
                        type="text"
                        placeholder="Rechercher..."
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                        className="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#0e7490] focus:border-transparent outline-none w-full sm:w-64"
                    />
                </div>
            </div>

            <div className="overflow-hidden rounded-xl bg-white shadow-sm ring-1 ring-gray-900/5">
                <div className="overflow-x-auto">
                    <table className="min-w-full divide-y divide-gray-200">
                        <thead className="bg-[#f8fafc]">
                            <tr>
                                <th className="px-6 py-3 text-left text-xs font-semibold uppercase tracking-wider text-gray-500">Utilisateur</th>
                                <th className="px-6 py-3 text-left text-xs font-semibold uppercase tracking-wider text-gray-500">Rôle</th>
                                <th className="px-6 py-3 text-left text-xs font-semibold uppercase tracking-wider text-gray-500">Statut</th>
                                <th className="px-6 py-3 text-right text-xs font-semibold uppercase tracking-wider text-gray-500">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-200 bg-white">
                            {filteredUsers.map((user) => {
                                const isBanned = !!user.deletedAt;
                                return (
                                    <tr
                                        key={user.id}
                                        className="hover:bg-gray-50/50 transition-colors cursor-pointer"
                                        onClick={() => setSelectedUser(user)}
                                    >
                                        <td className="whitespace-nowrap px-6 py-4">
                                            <div className="flex items-center">
                                                <div className="h-10 w-10 flex-shrink-0 rounded-full bg-[#0e7490]/10 flex items-center justify-center text-[#0e7490] font-bold">
                                                    {user.firstName[0]}{user.lastName[0]}
                                                </div>
                                                <div className="ml-4">
                                                    <div className="text-sm font-medium text-gray-900">{user.firstName} {user.lastName}</div>
                                                    <div className="text-sm text-gray-500">{user.email}</div>
                                                </div>
                                            </div>
                                        </td>
                                        <td className="whitespace-nowrap px-6 py-4">
                                            <span className={`inline-flex items-center rounded-md px-2 py-1 text-xs font-medium ring-1 ring-inset ${user.role === 'ADMIN'
                                                ? 'bg-amber-50 text-amber-700 ring-amber-600/20'
                                                : 'bg-slate-50 text-slate-700 ring-slate-600/20'
                                                }`}>
                                                {user.role}
                                            </span>
                                        </td>
                                        <td className="whitespace-nowrap px-6 py-4">
                                            <span className={`inline-flex items-center gap-1 rounded-full px-2 py-1 text-xs font-medium ring-1 ring-inset ${isBanned
                                                ? 'bg-red-50 text-red-700 ring-red-600/20'
                                                : 'bg-green-50 text-green-700 ring-green-600/20'
                                                }`}>
                                                {isBanned ? <ShieldAlert className="h-3 w-3" /> : <CheckCircle className="h-3 w-3" />}
                                                {isBanned ? 'Banni' : 'Actif'}
                                            </span>
                                        </td>
                                        <td className="whitespace-nowrap px-6 py-4 text-right text-sm font-medium">
                                            <button
                                                onClick={(e) => { e.stopPropagation(); handleBan(user.id, isBanned); }}
                                                className={`inline-flex items-center gap-1 px-3 py-1.5 rounded-md transition-colors ${isBanned
                                                    ? 'text-green-700 bg-green-50 hover:bg-green-100'
                                                    : 'text-red-700 bg-red-50 hover:bg-red-100'
                                                    }`}
                                            >
                                                {isBanned ? (
                                                    <><CheckCircle className="h-4 w-4" /> Réactiver</>
                                                ) : (
                                                    <><Ban className="h-4 w-4" /> Bannir</>
                                                )}
                                            </button>
                                        </td>
                                    </tr>
                                );
                            })}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* User Profile Modal */}
            {selectedUser && (
                <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4" onClick={() => setSelectedUser(null)}>
                    <div
                        className="bg-white rounded-2xl shadow-2xl w-full max-w-md animate-in zoom-in-95 duration-200"
                        onClick={(e) => e.stopPropagation()}
                    >
                        {/* Modal Header */}
                        <div className="relative bg-gradient-to-r from-[#0e7490] to-[#06b6d4] rounded-t-2xl p-6 text-white">
                            <button
                                onClick={() => setSelectedUser(null)}
                                className="absolute top-4 right-4 p-1 rounded-full hover:bg-white/20 transition-colors"
                            >
                                <X className="h-5 w-5" />
                            </button>
                            <div className="flex items-center gap-4">
                                <div className="h-16 w-16 rounded-full bg-white/20 flex items-center justify-center text-2xl font-bold">
                                    {selectedUser.firstName[0]}{selectedUser.lastName[0]}
                                </div>
                                <div>
                                    <h2 className="text-xl font-bold">{selectedUser.firstName} {selectedUser.lastName}</h2>
                                    <p className="text-white/80 text-sm">{selectedUser.role}</p>
                                </div>
                            </div>
                        </div>

                        {/* Modal Content */}
                        <div className="p-6 space-y-4">
                            {/* Email */}
                            <div className="flex items-center gap-3 p-3 bg-gray-50 rounded-lg">
                                <Mail className="h-5 w-5 text-gray-400" />
                                <div>
                                    <p className="text-xs text-gray-500">Email</p>
                                    <p className="text-sm font-medium text-gray-900">{selectedUser.email}</p>
                                </div>
                            </div>

                            {/* Phone Number */}
                            {selectedUser.phoneNumber && (
                                <div className="flex items-center gap-3 p-3 bg-gray-50 rounded-lg">
                                    <Phone className="h-5 w-5 text-gray-400" />
                                    <div>
                                        <p className="text-xs text-gray-500">Téléphone</p>
                                        <p className="text-sm font-medium text-gray-900">{selectedUser.phoneNumber}</p>
                                    </div>
                                </div>
                            )}

                            {/* Status */}
                            <div className="flex items-center gap-3 p-3 bg-gray-50 rounded-lg">
                                <Shield className="h-5 w-5 text-gray-400" />
                                <div>
                                    <p className="text-xs text-gray-500">Statut du compte</p>
                                    <span className={`inline-flex items-center gap-1 mt-1 rounded-full px-2 py-0.5 text-xs font-medium ring-1 ring-inset ${selectedUser.deletedAt
                                            ? 'bg-red-50 text-red-700 ring-red-600/20'
                                            : 'bg-green-50 text-green-700 ring-green-600/20'
                                        }`}>
                                        {selectedUser.deletedAt ? <ShieldAlert className="h-3 w-3" /> : <CheckCircle className="h-3 w-3" />}
                                        {selectedUser.deletedAt ? 'Banni' : 'Actif'}
                                    </span>
                                </div>
                            </div>

                            {/* Email Verification Status */}
                            <div className="flex items-center gap-3 p-3 bg-gray-50 rounded-lg">
                                <CheckCircle className="h-5 w-5 text-gray-400" />
                                <div>
                                    <p className="text-xs text-gray-500">Email vérifié</p>
                                    <span className={`inline-flex items-center gap-1 mt-1 rounded-full px-2 py-0.5 text-xs font-medium ring-1 ring-inset ${selectedUser.emailVerified
                                            ? 'bg-green-50 text-green-700 ring-green-600/20'
                                            : 'bg-yellow-50 text-yellow-700 ring-yellow-600/20'
                                        }`}>
                                        {selectedUser.emailVerified ? 'Vérifié' : 'Non vérifié'}
                                    </span>
                                </div>
                            </div>

                            {/* Actions */}
                            <div className="flex gap-3 pt-4 border-t border-gray-100">
                                <Link
                                    href={`/users/${selectedUser.id}`}
                                    className="flex-1 flex items-center justify-center gap-2 px-4 py-2 bg-[#0e7490] text-white rounded-lg hover:bg-[#0c657d] transition-colors font-medium text-sm"
                                    target="_blank"
                                >
                                    <ExternalLink className="h-4 w-4" />
                                    Voir le profil public
                                </Link>
                                <button
                                    onClick={() => handleBan(selectedUser.id, !!selectedUser.deletedAt)}
                                    className={`px-4 py-2 rounded-lg font-medium text-sm transition-colors ${selectedUser.deletedAt
                                            ? 'bg-green-50 text-green-700 hover:bg-green-100'
                                            : 'bg-red-50 text-red-700 hover:bg-red-100'
                                        }`}
                                >
                                    {selectedUser.deletedAt ? 'Réactiver' : 'Bannir'}
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
