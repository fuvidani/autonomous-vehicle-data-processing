import * as ActionTypes from "../actions/ActionTypes";

export default function reducer(state = {
    crashEventNotifications: [
        {
            id: "notification1",
            timeStamp: 1526474808321,
            accidentId: "accident1",
            location: {
                lat: 48.172762,
                lon: 16.376349
            },
            model: "1972 Ford Mustang",
            passengers: 4
        }
    ]
}, action) {

    switch (action.type) {
        case ActionTypes.CLEAR_EMERGENCY_SERVICE_CRASH_EVENT_NOTIFICATIONS: {
            state.accidentReports = [];

            return {
                ...state
            };
        }

        case ActionTypes.EMERGENCY_SERVICE_CRASH_EVENT_NOTIFICATION_FETCHED:
            return {
                ...state,
                crashEventNotifications: state.crashEventNotifications.concat(action.payload)
            };

        default:
            return state;
    }
}
