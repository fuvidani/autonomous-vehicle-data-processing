import * as ActionTypes from "../actions/ActionTypes";

export default function reducer(state = {
    manufacturers: [
        {
            id: "audi_id",
            name: "audi"
        },
        {
            id: "mercedes_id",
            name: "mercedes"
        },
        {
            id: "porsche_id",
            name: "porsche"
        },
        {
            id: "opel_id",
            name: "opel"
        }
    ],
    vehicleTrackingInformation: {},
    vehicles: {
        identificationNumber1: {
            identificationNumber: "identificationNumber1",
            manufacturerId: "manufacturerId",
            model: "1972 Ford Mustang",
            moving: true
        },
        identificationNumber2: {
            identificationNumber: "identificationNumber2",
            manufacturerId: "manufacturerId",
            model: "2012 Audi A8",
            moving: false
        },
    }
}, action) {

    switch (action.type) {
        case ActionTypes.VEHICLE_TRACKING_INFORMATION_FETCHED: {
            let newVehicleTrackingInformation = Object.assign({}, state.vehicleTrackingInformation);
            newVehicleTrackingInformation[action.payload.vehicleIdentificationNumber] = action.payload;

            state.vehicles[action.payload.vehicleIdentificationNumber].moving = true;

            return {
                ...state,
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

        case ActionTypes.CLEAR_MANUFACTURER_INFORMATION: {
            state.vehicleTrackingInformation = {};
            state.vehicles = [];

            return {
                ...state
            };
        }

        default:
            return state;
    }
}
