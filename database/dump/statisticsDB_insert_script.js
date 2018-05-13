print('initializing database with statistics');

// Insert test statistics

db.statistics.save(
    {
        "_id": "Accident 1",
        "serialNumber": "JH4DB8590SS001561",
        "model": "1995 Acura Integra",
        "location": {
            "lat": "48.172450",
            "lon": "16.376432"
        },
        "passengers": 4,
        "emergencyResponseInMillis": 123456,
        "durationOfSiteClearing": 654321
    }
);

print('initialization of statistics completed');