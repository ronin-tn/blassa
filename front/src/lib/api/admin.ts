import { clientGet, clientPut } from "@/lib/api/client-api";

export interface AdminStats {
    totalUsers: number;
    totalRides: number;
    pendingReports: number;
}

export interface User {
    id: string;
    firstName: string;
    lastName: string;
    email: string;
    phoneNumber?: string;
    role: string;
    isVerified: boolean;
    emailVerified?: boolean;
    deletedAt: string | null;
}

export interface Report {
    id: number;
    reporter: { id: string; firstName: string; lastName: string; email: string; phone?: string };
    reportedUser?: { id: string; firstName: string; lastName: string; email: string; phone?: string };
    ride?: { id: string; originName: string; destinationName: string };
    reason: string;
    description: string;
    status: "PENDING" | "RESOLVED" | "DISMISSED";
    createdAt: string;
}

export const adminApi = {
    getStats: async (): Promise<AdminStats> => {
        return clientGet<AdminStats>("/admin/stats");
    },
    getUsers: async (): Promise<User[]> => {
        return clientGet<User[]>("/admin/users");
    },
    banUser: async (userId: string) => {
        return clientPut(`/admin/users/${userId}/ban`);
    },
    unbanUser: async (userId: string) => {
        return clientPut(`/admin/users/${userId}/unban`);
    },
    getReports: async (): Promise<Report[]> => {
        return clientGet<Report[]>("/admin/reports");
    },
    resolveReport: async (reportId: number, status: string) => {
        return clientPut(`/admin/reports/${reportId}/resolve?status=${status}`);
    },
};
