package com.example.data.local

object SampleData {
    val sampleTrip = TripEntity(
        id = 1L,
        name = "Goa Adventure 2026",
        destination = "Goa, India",
        startDate = "15 Oct 2026",
        endDate = "20 Oct 2026",
        travelers = 3,
        budget = 30000.0,
        description = "Sun, sand, spices, and historic Portuguese fort architecture across North and South Goa.",
        coverImageUrl = "img_hero_goa",
        status = "UPCOMING"
    )

    val sampleActivities = listOf(
        // Day 1
        ActivityEntity(
            id = 1L,
            tripId = 1L,
            name = "Airport Arrival & Rental Pickup",
            date = "15 Oct 2026",
            startTime = "09:00 AM",
            endTime = "10:30 AM",
            location = "Goa Dabolim Airport",
            category = "Transport",
            description = "Land at Dabolim, pick up booked self-drive Thar vehicle and grab refreshing tender coconut water.",
            orderIndex = 1,
            latitude = 15.3803,
            longitude = 73.8313
        ),
        ActivityEntity(
            id = 2L,
            tripId = 1L,
            name = "Hotel Check-in & Freshen Up",
            date = "15 Oct 2026",
            startTime = "11:00 AM",
            endTime = "12:30 PM",
            location = "Taj Fort Aguada Resort, Candolim",
            category = "Hotel",
            description = "Check in to seaside heritage cottage with private balcony facing the Arabian Sea.",
            orderIndex = 2,
            latitude = 15.4989,
            longitude = 73.7667
        ),
        ActivityEntity(
            id = 3L,
            tripId = 1L,
            name = "Traditional Goan Lunch",
            date = "15 Oct 2026",
            startTime = "01:00 PM",
            endTime = "02:30 PM",
            location = "Fisherman's Wharf, Panaji",
            category = "Food",
            description = "Authentic Goan prawn curry, butter garlic crab, poi bread, and freshly pressed kokum juice.",
            orderIndex = 3,
            latitude = 15.4989,
            longitude = 73.8278
        ),
        ActivityEntity(
            id = 4L,
            tripId = 1L,
            name = "Baga Beach Sunset & Watersports",
            date = "15 Oct 2026",
            startTime = "04:00 PM",
            endTime = "06:30 PM",
            location = "Baga Beach, North Goa",
            category = "Sightseeing",
            description = "Golden hour stroll along the coastline, parasailing, and relaxation by the beach shacks.",
            orderIndex = 4,
            latitude = 15.5553,
            longitude = 73.7517
        ),
        ActivityEntity(
            id = 5L,
            tripId = 1L,
            name = "Candlelit Dinner & Live Acoustic Music",
            date = "15 Oct 2026",
            startTime = "07:30 PM",
            endTime = "10:00 PM",
            location = "Britto's Shack, Baga",
            category = "Food",
            description = "Beachside dining under fairy lights with fresh seafood platter and live Portuguese acoustic melodies.",
            orderIndex = 5,
            latitude = 15.5560,
            longitude = 73.7520
        ),

        // Day 2
        ActivityEntity(
            id = 6L,
            tripId = 1L,
            name = "Fort Aguada Lighthouse Exploration",
            date = "16 Oct 2026",
            startTime = "09:30 AM",
            endTime = "11:30 AM",
            location = "Fort Aguada, Sinquerim",
            category = "Sightseeing",
            description = "17th-century Portuguese fortress overlooking Sinquerim beach with panoramic sea cliffs.",
            orderIndex = 6,
            latitude = 15.4925,
            longitude = 73.7736
        ),
        ActivityEntity(
            id = 7L,
            tripId = 1L,
            name = "Sinquerim Water Adventure & Jet Ski",
            date = "16 Oct 2026",
            startTime = "12:00 PM",
            endTime = "02:00 PM",
            location = "Sinquerim Beach",
            category = "Adventure",
            description = "High-speed jet ski ride and bumper boat rides guided by certified local lifeguards.",
            orderIndex = 7,
            latitude = 15.4995,
            longitude = 73.7690
        ),
        ActivityEntity(
            id = 8L,
            tripId = 1L,
            name = "Calangute Beach Shacks & Souvenirs",
            date = "16 Oct 2026",
            startTime = "03:30 PM",
            endTime = "06:00 PM",
            location = "Calangute Beach",
            category = "Shopping",
            description = "Browse vibrant Tibetan handicrafts, seashell trinkets, spices, and linen resort wear.",
            orderIndex = 8,
            latitude = 15.5439,
            longitude = 73.7553
        ),
        ActivityEntity(
            id = 9L,
            tripId = 1L,
            name = "Saturday Night Market Visit",
            date = "16 Oct 2026",
            startTime = "08:00 PM",
            endTime = "11:00 PM",
            location = "Arpora Night Market",
            category = "Shopping",
            description = "Electrifying market atmosphere with live fusion DJ sets, international food stalls, and bohemian boutique fashion.",
            orderIndex = 9,
            latitude = 15.5684,
            longitude = 73.7745
        ),

        // Day 3
        ActivityEntity(
            id = 10L,
            tripId = 1L,
            name = "Basilica of Bom Jesus Heritage Walk",
            date = "17 Oct 2026",
            startTime = "10:00 AM",
            endTime = "12:30 PM",
            location = "Old Goa",
            category = "Sightseeing",
            description = "UNESCO World Heritage Baroque church housing the sacred relics of St. Francis Xavier.",
            orderIndex = 10,
            latitude = 15.5009,
            longitude = 73.9116
        ),
        ActivityEntity(
            id = 11L,
            tripId = 1L,
            name = "Organic Spice Plantation Tour & Buffet",
            date = "17 Oct 2026",
            startTime = "01:30 PM",
            endTime = "04:30 PM",
            location = "Sahakari Spice Farm, Ponda",
            category = "Adventure",
            description = "Guided walking tour through cardamom, vanilla, and peri-peri groves followed by authentic clay pot buffet.",
            orderIndex = 11,
            latitude = 15.4050,
            longitude = 74.0150
        ),
        ActivityEntity(
            id = 12L,
            tripId = 1L,
            name = "Mandovi River Luxury Sunset Cruise",
            date = "17 Oct 2026",
            startTime = "06:00 PM",
            endTime = "08:30 PM",
            location = "Panaji Jetty",
            category = "Transport",
            description = "Catamaran cruise along Mandovi River with traditional Dekhnni & Fugdi folk dance performances.",
            orderIndex = 12,
            latitude = 15.4989,
            longitude = 73.8278
        )
    )

    val sampleChecklist = listOf(
        // Documents
        ChecklistItemEntity(1L, 1L, "ID Proof (Aadhaar / Passport)", "Documents", true),
        ChecklistItemEntity(2L, 1L, "Flight Tickets & Boarding Passes", "Documents", true),
        ChecklistItemEntity(3L, 1L, "Hotel Booking Confirmation", "Documents", true),
        ChecklistItemEntity(4L, 1L, "Travel Insurance Policy Copy", "Documents", false),

        // Packing
        ChecklistItemEntity(5L, 1L, "Light Linen Clothes & Swimwear", "Packing", true),
        ChecklistItemEntity(6L, 1L, "Comfortable Walking Shoes & Flip-flops", "Packing", true),
        ChecklistItemEntity(7L, 1L, "Fast Mobile Chargers & Cables", "Packing", true),
        ChecklistItemEntity(8L, 1L, "20,000 mAh Power Bank", "Packing", true),
        ChecklistItemEntity(9L, 1L, "Sunscreen SPF 50 & Toiletries", "Packing", false),
        ChecklistItemEntity(10L, 1L, "Waterproof Travel Bag & Dry Pouch", "Packing", false),

        // Travel Preparation
        ChecklistItemEntity(11L, 1L, "Check Goa 7-Day Weather Forecast", "Travel Preparation", true),
        ChecklistItemEntity(12L, 1L, "Confirm Hotel Early Check-in", "Travel Preparation", true),
        ChecklistItemEntity(13L, 1L, "Confirm Thar Rental Delivery at Airport", "Travel Preparation", false),
        ChecklistItemEntity(14L, 1L, "Prepare Emergency Contacts Card", "Travel Preparation", false)
    )

    val sampleNotes = listOf(
        NoteEntity(
            id = 1L,
            tripId = 1L,
            title = "Emergency Contacts & Lifeguard Helplines",
            content = "• Goa Tourist Police Helpline: 1364\n• Medical Emergency: 108\n• Drishti Marine Beach Patrol: 0832-2415124\n• Resort Concierge Desk: +91 832 664 5858\n• Nearest Hospital: Manipal Hospital, Dona Paula",
            category = "Emergency contacts",
            isPinned = true
        ),
        NoteEntity(
            id = 2L,
            tripId = 1L,
            title = "Must-Try Goan Restaurants & Beach Cafes",
            content = "1. Fisherman's Wharf (Panaji) - Best Crab Xec Xec & Prawn Balchão\n2. Britto's (Baga) - Classic pork vindaloo and chocolate mud pie\n3. Gunpowder (Assagao) - South Indian coastal delicacy, appams, and spicy mango curry\n4. Thalassa (Siolim) - Greek sunset dining overlooking the river backwaters\n5. Baba Au Rhum (Anjuna) - Artisanal wood-fired pizzas & croissants",
            category = "Restaurant recommendations",
            isPinned = true
        ),
        NoteEntity(
            id = 3L,
            tripId = 1L,
            title = "Travel Tips for North Goa Exploration",
            content = "• Carry adequate cash; several beach shacks and flea market vendors face patchy network for UPI.\n• Wear high SPF reef-safe sunscreen and polarized sunglasses.\n• Strictly respect coastal flags; red flags mean no swimming.\n• Always wear a helmet when riding scooters across the coastal highway.",
            category = "Travel tips",
            isPinned = false
        ),
        NoteEntity(
            id = 4L,
            tripId = 1L,
            title = "Secret Photography Spots in Anjuna & Chapora",
            content = "• Sunset vantage point at Chapora Fort ramparts (Dil Chahta Hai spot)\n• Little Vagator red cliff formation during golden hour\n• St. Anthony's Church lane in Siolim for Portuguese heritage villas",
            category = "Places to visit",
            isPinned = false
        )
    )

    val sampleExpenses = listOf(
        ExpenseEntity(
            id = 1L,
            tripId = 1L,
            title = "Return Flight Tickets (3 Travelers)",
            amount = 8000.0,
            category = "Transport",
            date = "15 Oct 2026",
            description = "Bengaluru to Goa round-trip flight booking"
        ),
        ExpenseEntity(
            id = 2L,
            tripId = 1L,
            title = "Taj Fort Aguada Cottage (Advance)",
            amount = 7500.0,
            category = "Accommodation",
            date = "15 Oct 2026",
            description = "3-night heritage seaside cottage booking"
        ),
        ExpenseEntity(
            id = 3L,
            tripId = 1L,
            title = "Fisherman's Wharf Seafood Lunch",
            amount = 1800.0,
            category = "Food",
            date = "15 Oct 2026",
            description = "Signature king prawn curry, crabs, and drinks"
        ),
        ExpenseEntity(
            id = 4L,
            tripId = 1L,
            title = "Parasailing & Jet Ski Package",
            amount = 1200.0,
            category = "Activities",
            date = "16 Oct 2026",
            description = "Combined water sports passes at Sinquerim"
        )
    )

    val sampleUsers = listOf(
        UserEntity(
            uid = "alex_carter_01",
            email = "alex@travel.com",
            displayName = "Alex Carter"
        ),
        UserEntity(
            uid = "maya_lin_02",
            email = "maya@travel.com",
            displayName = "Maya Lin"
        ),
        UserEntity(
            uid = "sam_patel_03",
            email = "sam@travel.com",
            displayName = "Sam Patel"
        )
    )

    val sampleCollaborators = listOf(
        // For Goa Adventure (Trip 1 - Creator: Alex)
        TripCollaboratorEntity(
            id = 1L,
            tripId = 1L,
            userId = "alex_carter_01",
            email = "alex@travel.com",
            displayName = "Alex Carter",
            role = "OWNER",
            status = "ACCEPTED"
        ),
        TripCollaboratorEntity(
            id = 2L,
            tripId = 1L,
            userId = "maya_lin_02",
            email = "maya@travel.com",
            displayName = "Maya Lin",
            role = "EDITOR",
            status = "ACCEPTED"
        ),
        TripCollaboratorEntity(
            id = 3L,
            tripId = 1L,
            userId = "sam_patel_03",
            email = "sam@travel.com",
            displayName = "Sam Patel",
            role = "VIEWER",
            status = "ACCEPTED"
        ),
        // For Himachal Mountain Trek (Trip 2 - Creator: Maya)
        TripCollaboratorEntity(
            id = 4L,
            tripId = 2L,
            userId = "maya_lin_02",
            email = "maya@travel.com",
            displayName = "Maya Lin",
            role = "OWNER",
            status = "ACCEPTED"
        ),
        TripCollaboratorEntity(
            id = 5L,
            tripId = 2L,
            userId = "alex_carter_01",
            email = "alex@travel.com",
            displayName = "Alex Carter",
            role = "EDITOR",
            status = "ACCEPTED"
        ),
        TripCollaboratorEntity(
            id = 6L,
            tripId = 2L,
            userId = "sam_patel_03",
            email = "sam@travel.com",
            displayName = "Sam Patel",
            role = "VIEWER",
            status = "ACCEPTED"
        )
    )

    val sampleSharedTrip = TripEntity(
        id = 2L,
        name = "Himachal Alpine Expedition",
        destination = "Manali & Spiti, India",
        startDate = "12 Nov 2026",
        endDate = "18 Nov 2026",
        travelers = 4,
        budget = 42000.0,
        description = "High altitude alpine trekking, pine forest camping, and Buddhist monastery cultural tour.",
        coverImageUrl = "img_hero_goa",
        status = "UPCOMING",
        creatorId = "maya_lin_02",
        creatorEmail = "maya@travel.com"
    )

    val sampleSharedTripActivities = listOf(
        ActivityEntity(
            id = 10L,
            tripId = 2L,
            name = "Arrival at Old Manali Camp",
            date = "12 Nov 2026",
            startTime = "10:00 AM",
            endTime = "12:00 PM",
            location = "Old Manali Riverside",
            category = "Hotel",
            description = "Settle into alpine base camp beside the pristine Beas river stream.",
            orderIndex = 1,
            latitude = 32.2565,
            longitude = 77.1887
        ),
        ActivityEntity(
            id = 11L,
            tripId = 2L,
            name = "Jogini Waterfall Hike",
            date = "12 Nov 2026",
            startTime = "02:00 PM",
            endTime = "05:00 PM",
            location = "Vashisht Village",
            category = "Adventure",
            description = "Trek through apple orchards to the cascading mountain spring waters.",
            orderIndex = 2,
            latitude = 32.2680,
            longitude = 77.1950
        )
    )

    val sampleSharedTripChecklist = listOf(
        ChecklistItemEntity(
            id = 20L,
            tripId = 2L,
            title = "Thermal innerwear & waterproof windcheater",
            category = "Packing",
            isCompleted = true
        ),
        ChecklistItemEntity(
            id = 21L,
            tripId = 2L,
            title = "High-ankle trekking boots with crampons",
            category = "Packing",
            isCompleted = false
        ),
        ChecklistItemEntity(
            id = 22L,
            tripId = 2L,
            title = "Forest trail entry permit documents",
            category = "Documents",
            isCompleted = true
        )
    )
}
