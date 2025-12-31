package com.tp.blassa.navigation

sealed class BlassaRoute(val route: String) {

    data object Splash : BlassaRoute("splash")
    data object Onboarding : BlassaRoute("onboarding")
    data object Login : BlassaRoute("login")
    data object Register : BlassaRoute("register")
    data object ForgotPassword : BlassaRoute("forgot_password")
    data object CompleteProfile : BlassaRoute("complete_profile")

    data object EmailVerification : BlassaRoute("email_verification/{email}") {
        fun createRoute(email: String) = "email_verification/$email"
    }

    data object Dashboard : BlassaRoute("dashboard")
    data object PublishRide : BlassaRoute("publish_ride")
    data object AddVehicle : BlassaRoute("add_vehicle")
    data object ProfileMenu : BlassaRoute("profile_menu")
    data object MyVehicles : BlassaRoute("my_vehicles")
    data object Reviews : BlassaRoute("reviews")
    data object Notifications : BlassaRoute("notifications")
    data object RideHistory : BlassaRoute("ride_history")
    data object EditProfile : BlassaRoute("edit_profile")
    data object ChangePassword : BlassaRoute("change_password")

    data object RideDetails : BlassaRoute("ride_details/{rideId}") {
        fun createRoute(rideId: String) = "ride_details/$rideId"
    }
    data object UserProfile : BlassaRoute("user_profile/{userId}") {
        fun createRoute(userId: String) = "user_profile/$userId"
    }

    data object SearchRide : BlassaRoute("search_ride")

    data object SearchResults :
            BlassaRoute(
                    "search_results/{from}/{to}/{originLat}/{originLon}/{destLat}/{destLon}/{passengers}?date={date}&gender={gender}"
            ) {
        fun createRoute(
                from: String,
                to: String,
                originLat: Double,
                originLon: Double,
                destLat: Double,
                destLon: Double,
                passengers: Int,
                date: String? = null,
                gender: String? = null
        ): String {
            val base =
                    "search_results/$from/$to/$originLat/$originLon/$destLat/$destLon/$passengers"
            val params = buildList {
                date?.let { add("date=$it") }
                gender?.let { add("gender=$it") }
            }
            return if (params.isEmpty()) base else "$base?${params.joinToString("&")}"
        }
    }
}
