print('initializing database with statistics');

// Create capped collection for statistics
db.createCollection("statistics", { capped : true, size : 5242880, max : 5000 } );

// Insert test statistics
db.statistics.save(
    {
        "_id": "tDbdvAqCxpCQqqYXaRTC76Bm",
        "accidentId": "QgYZY8ntPurzGDhxxAcVYbYb",
        "serialNumber": "JH4DB8590SS001561",
        "vehicleMetaData": {
            "identificationNumber": "9KXfzswrhxzKEuX9uiAWcsaw",
            "model": "1995 Acura Integra"
        },
        "location": {
            "lat": "48.172450",
            "lon": "16.376432"
        },
        "passengers": 4,
        "emergencyResponseInMillis": 123456,
        "durationOfSiteClearingInMillis": 654321
    }
);

print('initialization of statistics completed');