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
    vehicleTrackingInformation: {

    }
}, action) {

    switch (action.type) {
        case ActionTypes.VEHICLE_TRACKING_INFORMATION_FETCHED: {
            let newVehicleTrackingInformation = Object.assign({}, state.vehicleTrackingInformation);
            newVehicleTrackingInformation[action.payload.vehicleIdentificationNumber] = action.payload;

            return {
                ...state,
                vehicleTrackingInformation: newVehicleTrackingInformation
            };
        }

        case ActionTypes.CLEAR_VEHICLE_TRACKING_INFORMATION: {
            state.vehicleTrackingInformation = {};

            return {
                ...state
            };
        }

        default:
            return state;
    }
}
