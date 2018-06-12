print('initializing database with statistics');

// Create capped collection for statistics
db.createCollection("statistics", { capped : true, size : 5242880, max : 5000 } );

// Insert test statistics
db.statistics.save(
    {
        "_id": "tDbdvAqCxpCQqqYXaRTC76Bm",
        "accidentId": "QgYZY8ntPurzGDhxxAcVYbYb",
        "vehicleMetaData": {
            "identificationNumber": "9KXfzswrhxzKEuX9uiAWcsaw",
            "model": "1995 Acura Integra"
        },
        "location": {
            "lat": 48.172450,
            "lon": 16.376432
        },
        "passengers": 4,
        "timestampOfAccident": 1528830631183,
        "emergencyResponseInMillis": 300000,
        "durationOfSiteClearingInMillis": 600000
    }
);

print('initialization of statistics completed');