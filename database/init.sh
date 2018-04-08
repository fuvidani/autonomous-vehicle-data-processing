#!/bin/bash

auth="-u admin -p adminPassword"

# MONGODB USER CREATION
(
echo "setup mongodb auth"
create_user="if (!db.getUser('admin')) { db.createUser({ user: 'admin', pwd: 'adminPassword', roles: [ {role:'readWrite', db:'notificationDatabase'}, {role:'readWrite', db:'statisticsDatabase'}, {role:'readWrite', db:'trackerDatabase'}, {role:'readWrite', db:'vehicleDatabase'} ]}) }"
until mongo vehicleDatabase --eval "$create_user" || mongo vehicleDatabase ${auth} --eval "$create_user"; do sleep 5; done
killall mongod
sleep 1
killall -9 mongod
) &

# INIT DUMP EXECUTION
(
if test -n "vehicleDB_insert_script.js"; then
    echo "execute insert script"
	until mongo vehicleDatabase ${auth} insert_script.js; do sleep 5; done
fi
) &

echo "start mongodb without auth"
chown -R mongodb /data/db
gosu mongodb mongod --config /mongod.conf "$@"

echo "restarting with auth on"
sleep 5
exec gosu mongodb mongod --auth --config /mongod.conf "$@"