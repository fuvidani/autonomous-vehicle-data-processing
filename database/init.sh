#!/bin/bash

# MONGODB USER AND DB CREATION
(
echo "setup mongodb auth"
vehicleDb_user="if (!db.getUser('vehicleService')) { db.createUser({ user: 'vehicleService', pwd: 'vehicleDatabasePassword', roles: [ {role:'readWrite', db:'vehicleDatabase'} ]}) }"
trackerDb_user="if (!db.getUser('trackerService')) { db.createUser({ user: 'trackerService', pwd: 'trackerDatabasePassword', roles: [ {role:'readWrite', db:'trackerDatabase'} ]}) }"
statisticsDb_user="if (!db.getUser('statisticsService')) { db.createUser({ user: 'statisticsService', pwd: 'statisticsDatabasePassword', roles: [ {role:'readWrite', db:'statisticsDatabase'} ]}) }"
notificationDb_user="if (!db.getUser('notificationService')) { db.createUser({ user: 'notificationService', pwd: 'notificationDatabasePassword', roles: [ {role:'readWrite', db:'notificationDatabase'} ]}) }"
until mongo trackerDatabase --eval "$trackerDb_user" && mongo vehicleDatabase --eval "$vehicleDb_user" && mongo statisticsDatabase --eval "$statisticsDb_user" && mongo notificationDatabase --eval "$notificationDb_user"; do sleep 5; done
killall mongod
sleep 1
killall -9 mongod
) &

# INIT DUMP EXECUTION
(
if test -n "vehicleDB_insert_script.js"; then
    echo "execute insert script"
	until mongo vehicleDatabase -u 'vehicleService' -p 'vehicleDatabasePassword' vehicleDB_insert_script.js; do sleep 5; done
fi
) &

(
if test -n "statisticsDB_insert_script.js"; then
    echo "execute insert script"
	until mongo statisticsDatabase -u 'statisticsService' -p 'statisticsDatabasePassword' statisticsDB_insert_script.js; do sleep 5; done
fi
) &

echo "start mongodb without auth"
chown -R mongodb /data/db
gosu mongodb mongod --config /mongod.conf "$@"

echo "restarting with auth on"
sleep 5
exec gosu mongodb mongod --auth --config /mongod.conf "$@"