print('initializing database with vehicles');

// Insert test vehicles

db.manufacturers.save(
    {
        "_id": "Ford Europe",
        "label": "Ford"
    }
);

db.manufacturers.save(
    {
        "_id": "Acura",
        "label": "Acura"
    }
);

db.vehicles.save(
    {
        "_id": "1FMZU77KX4UA69977",
        "manufacturerId": "Ford Europe",
        "model": "Ford Explorer Sport Trac 2004"
    }
);

db.vehicles.save(
    {
        "_id": "1GTEK14Z36E235938",
        "manufacturerId": "Ford Europe",
        "model": "Ford Fiesta"
    }
);

db.vehicles.save(
    {
        "_id": "JH4DB8590SS001561",
        "manufacturerId": "Acura",
        "model": "1995 Acura Integra"
    }
);

db.vehicles.save(
    {
        "_id": "JH4DA3350JS000592",
        "manufacturerId": "Acura",
        "model": "1988 Acura Integra"
    }
);


db.vehicles.save(
    {
        "_id": "1J4RR5GT9AC101848",
        "manufacturerId": "Acura",
        "model": "Acura MDX"
    }
);

print('initialization of vehicles completed');