import * as ActionTypes from "../actions/ActionTypes";

const clearEmergencyServiceNotifications = action$ =>
    action$.ofType(ActionTypes.CANCEL_EMERGENCY_SERVICE_CRASH_EVENT_NOTIFICATIONS)
        .mapTo({type: ActionTypes.CLEAR_EMERGENCY_SERVICE_CRASH_EVENT_NOTIFICATIONS});

export {clearEmergencyServiceNotifications}