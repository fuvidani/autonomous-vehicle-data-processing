import * as ActionTypes from "../actions/ActionTypes";

export default function reducer(state = {
    crashEventNotifications: []
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
                crashEventNotifications: state.crashEventNotifications.concat(action.payload)
            };

        case ActionTypes.ARRIVE_TO_CRASH_EVENT_POSTED: {
            const updatedCrashEventNotifications = state.crashEventNotifications.reverse().filter(notification => { return notification.accidentId !== action.payload });

            return {
                ...state,
                crashEventNotifications: updatedCrashEventNotifications
            };
        }

        case ActionTypes.CLEAR_CRASH_EVENT_POSTED: {
            const updatedNotifications = state.crashEventNotifications.reverse().filter(notification => { return notification.accidentId !== action.payload});

            return {
                ...state,
                crashEventNotifications: updatedNotifications
            };
        }

        default:
            return state;
    }
}
