import { apiPost, apiGet } from "./client";
import { ReviewRequest, ReviewResponse, PagedResponse } from "@/types/models";

/**
 * Create a new review
 */
export async function createReview(data: ReviewRequest): Promise<ReviewResponse> {
    return apiPost<ReviewResponse>("/reviews", data);
}

/**
 * Get reviews received by the current user
 */
export async function getMyReceivedReviews(page = 0, size = 10): Promise<PagedResponse<ReviewResponse>> {
    return apiGet<PagedResponse<ReviewResponse>>(`/reviews/me/received?page=${page}&size=${size}`);
}

/**
 * Get reviews sent by the current user
 */
export async function getMySentReviews(page = 0, size = 10): Promise<PagedResponse<ReviewResponse>> {
    return apiGet<PagedResponse<ReviewResponse>>(`/reviews/me/sent?page=${page}&size=${size}`);
}
