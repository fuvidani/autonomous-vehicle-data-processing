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
            passengers: 4,
            arrived: false,
            cleared: false
        }
    ]
}, action) {

    switch (action.type) {
        case ActionTypes.CLEAR_EMERGENCY_SERVICE_CRASH_EVENT_NOTIFICATIONS: {
            state.crashEventNotifications = [];

            return {
                ...state
            };
        }

        case ActionTypes.EMERGENCY_SERVICE_CRASH_EVENT_NOTIFICATION_FETCHED:
            return {
                ...state,
                crashEventNotifications: state.crashEventNotifications.concat({
                    ...action.payload,
                    arrived: false,
                    cleared: false
                })
            };

        case ActionTypes.ARRIVE_TO_CRASH_EVENT_POSTED: {
            for (let i = 0; i < state.crashEventNotifications.length; i++) {
                if (state.crashEventNotifications[i].accidentId === action.payload) {
                    state.crashEventNotifications[i].arrived = true;
                    break;
                }
            }

            return {
                ...state
            };
        }

        case ActionTypes.CLEAR_CRASH_EVENT_POSTED: {
            for (let i = 0; i < state.crashEventNotifications.length; i++) {
                if (state.crashEventNotifications[i].accidentId === action.payload) {
                    state.crashEventNotifications[i].cleared = true;
                    break;
                }
            }

            return {
                ...state
            };
        }

        default:
            return state;
    }
}
