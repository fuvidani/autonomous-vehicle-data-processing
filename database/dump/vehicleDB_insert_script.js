print('initializing database with vehicles');

// Insert test vehicles

db.vehicles.save(
    {
        "_id": "1FMZU77KX4UA69977",
        "manufacturerId": "1234",
        "model": "Ford Explorer Sport Trac 2004"
    }
);

db.vehicles.save(
    {
        "_id": "JH4DA3350JS000592",
        "manufacturerId": "5678",
        "model": "1988 Acura Integra"
    }
);

print('initialization of vehicles completed');