import * as ActionTypes from "../actions/ActionTypes";

export default function reducer(state = {
    manufacturers: [
        {
            id: "Ford Europe",
            name: "ford"
        },
        {
            id: "Acura",
            name: "Acura"
        }
    ],
    vehicleTrackingInformation: {},
    vehicleHistoryInformation: {},
    vehicles: {},
    notifications: [],
    clickedNotification: {
        id: "",
        timeStamp: 0,
        vehicleIdentificationNumber: "",
        model: "",
        location: {
            lat: 0.0,
            lon: 0.0
        },
        eventInfo: "",
        accidentId: ""
    }
}, action) {

    switch (action.type) {
        case ActionTypes.VEHICLE_TRACKING_INFORMATION_FETCHED: {

            // ignore vehicle data records older than 3 seconds
            // if ((new Date().getTime() - action.payload.timestamp) > 5000) {
            //     return state;
            // }

            let newVehicleTrackingInformation = Object.assign({}, state.vehicleTrackingInformation);
            newVehicleTrackingInformation[action.payload.vehicleIdentificationNumber] = action.payload;

            let newVehicles = Object.assign({}, state.vehicles);
            if (newVehicles[action.payload.vehicleIdentificationNumber]) newVehicles[action.payload.vehicleIdentificationNumber].moving = true;

            return {
                ...state,
                vehicles: newVehicles,
                vehicleTrackingInformation: newVehicleTrackingInformation
            };
        }

        case ActionTypes.VEHICLE_INFORMATION_FETCHED: {
            let vehicle = action.payload;
            vehicle.moving = false;

            let newVehicles = Object.assign({}, state.vehicles);
            newVehicles[vehicle.identificationNumber] = vehicle;

            return {
                ...state,
                vehicles: newVehicles
            };
        }

        case ActionTypes.MANUFACTURER_NOTIFICATION_FETCHED: {
            return {
                ...state,
                notifications: state.notifications.concat({...action.payload, show: false})
            };
        }

        case ActionTypes.CLEAR_MANUFACTURER_INFORMATION: {
            state.vehicleTrackingInformation = {};
            state.vehicles = {};
            state.notifications = [];

            return {
                ...state
            };
        }

        case ActionTypes.CLICK_NOTIFICATION: {
            return {
                ...state,
                clickedNotification: action.payload
            };
        }

        case ActionTypes.LEAVE_NOTIFICATION: {
            return {
                ...state,
                clickedNotification: {
                    id: "",
                    timeStamp: 0,
                    vehicleIdentificationNumber: "",
                    model: "",
                    location: {
                        lat: 0.0,
                        lon: 0.0
                    },
                    eventInfo: "",
                    accidentId: ""
                }
            };
        }
        default:
            return state;
    }
}
