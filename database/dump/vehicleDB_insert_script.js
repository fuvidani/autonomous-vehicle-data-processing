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
        "_id": "JALC4B16857004522",
        "manufacturerId": "Ford Europe",
        "model": "Ford Focus"
    }
);

db.vehicles.save(
    {
        "_id": "NM0LS7BN3BT044968",
        "manufacturerId": "Ford Europe",
        "model": "Ford Mondeo"
    }
);

db.vehicles.save(
    {
        "_id": "2B3LA53H98H202837",
        "manufacturerId": "Ford Europe",
        "model": "Ford Mustang"
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

db.vehicles.save(
    {
        "_id": "JF2SHADCXBH700562",
        "manufacturerId": "Acura",
        "model": "Acura CDX"
    }
);

db.vehicles.save(
    {
        "_id": "1G11J5SX1DF212944",
        "manufacturerId": "Acura",
        "model": "Acura ILX"
    }
);

print('initialization of vehicles completed');