package com.tp.blassa.data

data class City(val code: String, val name: String, val lat: Double, val lon: Double)

val TUNISIA_CITIES =
        listOf(
                City("TN1", "Tunis", 36.8065, 10.1815),
                City("TN2", "Ariana", 36.8663, 10.1647),
                City("TN3", "Ben Arous", 36.7533, 10.2283),
                City("TN4", "Manouba", 36.8101, 10.0863),
                City("TN5", "Nabeul", 36.4513, 10.7357),
                City("TN6", "Zaghouan", 36.4029, 10.1429),
                City("TN7", "Bizerte", 37.2746, 9.8739),
                City("TN8", "Beja", 36.7256, 9.1817),
                City("TN9", "Jendouba", 36.5011, 8.7803),
                City("TN10", "Le Kef", 36.1742, 8.7049),
                City("TN11", "Siliana", 36.0849, 9.3708),
                City("TN12", "Kairouan", 35.6781, 10.0963),
                City("TN13", "Kasserine", 35.1676, 8.8365),
                City("TN14", "Sidi Bou Zid", 35.0354, 9.4839),
                City("TN15", "Sousse", 35.8288, 10.6405),
                City("TN16", "Monastir", 35.7643, 10.8113),
                City("TN17", "Mahdia", 35.5047, 11.0622),
                City("TN18", "Sfax", 34.7406, 10.7603),
                City("TN19", "Gafsa", 34.4250, 8.7842),
                City("TN20", "Tozeur", 33.9197, 8.1339),
                City("TN21", "Kebili", 33.7044, 8.9690),
                City("TN22", "Gabes", 33.8815, 10.0982),
                City("TN23", "Medenine", 33.3549, 10.5055),
                City("TN24", "Tataouine", 32.9297, 10.4518)
        )
