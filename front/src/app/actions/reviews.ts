"use server";

import { createReview } from "@/lib/api/reviews";
import { ReviewRequest } from "@/types/models";
import { revalidateTag } from "next/cache";

export async function submitReviewAction(data: ReviewRequest) {
    try {
        const review = await createReview(data);
        // Revalidate relevant tags if necessary, e.g. user stats or booking details
        // For now, simple revalidation
        revalidateTag("my-received-reviews", "max");
        revalidateTag("my-sent-reviews", "max");
        return { success: true, review };
    } catch (error) {
        return { success: false, error: error instanceof Error ? error.message : "Failed to submit review" };
    }
}
