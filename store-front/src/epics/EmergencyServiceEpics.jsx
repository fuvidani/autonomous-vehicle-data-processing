import * as ActionTypes from "../actions/ActionTypes";
import {fromEventSource, postRequest} from "../util/RequestHandler";

const fetchEmergencyServiceCrashEventNotificationsEpic = action$ =>
    action$.ofType(ActionTypes.FETCH_EMERGENCY_SERVICE_CRASH_EVENT_NOTIFICATIONS)
        .mergeMap(() =>
            fromEventSource('/notifications/ems', 'message')
                .filter(response => JSON.parse(response.data).id === null || JSON.parse(response.data).id === "")
                .map(response => ({
                    type: ActionTypes.EMERGENCY_SERVICE_CRASH_EVENT_NOTIFICATION_FETCHED,
                    payload: JSON.parse(response.data)
                }))
                .takeUntil(action$.ofType(ActionTypes.CANCEL_EMERGENCY_SERVICE_CRASH_EVENT_NOTIFICATIONS))
        );

const postEmergencyServiceArrivedEpic = action$ =>
    action$.ofType(ActionTypes.ARRIVE_TO_CRASH_EVENT)
        .mergeMap((action) =>
            postRequest('/datasimulation/updatestatus', {...action.payload, status: 'ARRIVED'})
                .map(() => ({type: ActionTypes.ARRIVE_TO_CRASH_EVENT_POSTED}))
        );

const postEmergencyServiceClearedEpic = action$ =>
    action$.ofType(ActionTypes.CLEAR_CRASH_EVENT)
        .mergeMap((action) =>
            postRequest('/datasimulation/updatestatus', {...action.payload, status: 'AREA_CLEARED'})
                .map(() => ({type: ActionTypes.CLEAR_CRASH_EVENT_POSTED}))
        );

const clearEmergencyServiceNotifications = action$ =>
    action$.ofType(ActionTypes.CANCEL_EMERGENCY_SERVICE_CRASH_EVENT_NOTIFICATIONS)
        .mapTo({type: ActionTypes.CLEAR_EMERGENCY_SERVICE_CRASH_EVENT_NOTIFICATIONS});

export {
    clearEmergencyServiceNotifications,
    fetchEmergencyServiceCrashEventNotificationsEpic,
    postEmergencyServiceArrivedEpic,
    postEmergencyServiceClearedEpic
}
