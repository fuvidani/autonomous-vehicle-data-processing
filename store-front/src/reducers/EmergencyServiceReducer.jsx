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
                crashEventNotifications: state.crashEventNotifications.concat({
                    ...action.payload,
                    arrived: false,
                    cleared: false
                })
            };

        case ActionTypes.ARRIVE_TO_CRASH_EVENT_POSTED: {
            const updatedCrashEventNotifications = state.crashEventNotifications.map(notification => {
                if (notification.accidentId === action.payload) {
                    return {...notification, arrived: true}
                }

                return notification;
            });

            return {
                ...state,
                crashEventNotifications: updatedCrashEventNotifications
            };
        }

        case ActionTypes.CLEAR_CRASH_EVENT_POSTED: {
            const updatedCrashEventNotifications = state.crashEventNotifications.map(notification => {
                if (notification.accidentId === action.payload) {
                    return {...notification, cleared: true}
                }

                return notification;
            });

            return {
                ...state,
                crashEventNotifications: updatedCrashEventNotifications
            };
        }

        default:
            return state;
    }
}
