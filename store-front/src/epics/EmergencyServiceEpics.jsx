import * as ActionTypes from "../actions/ActionTypes";
import {fetchStream, postRequest} from "../util/RequestHandler";

const fetchEmergencyServiceCrashEventNotificationsEpic = action$ =>
    action$.ofType(ActionTypes.FETCH_EMERGENCY_SERVICE_CRASH_EVENT_NOTIFICATIONS)
        .mergeMap(() =>
            fetchStream('/notifications/ems')
                .filter(response => JSON.parse(response).id !== "ping" && JSON.parse(response).id !== "")
                .map(response => ({
                    type: ActionTypes.EMERGENCY_SERVICE_CRASH_EVENT_NOTIFICATION_FETCHED,
                    payload: JSON.parse(response)
                }))
                .takeUntil(action$.ofType(ActionTypes.CANCEL_EMERGENCY_SERVICE_CRASH_EVENT_NOTIFICATIONS))
        );

const postEmergencyServiceArrivedEpic = action$ =>
    action$.ofType(ActionTypes.ARRIVE_TO_CRASH_EVENT)
        .mergeMap((action) =>
            postRequest('/datasimulation/updatestatus', {...action.payload, status: 'ARRIVED'})
                .map(() => ({type: ActionTypes.ARRIVE_TO_CRASH_EVENT_POSTED, payload: action.payload.accidentId}))
        );

const postEmergencyServiceClearedEpic = action$ =>
    action$.ofType(ActionTypes.CLEAR_CRASH_EVENT)
        .mergeMap((action) =>
            postRequest('/datasimulation/updatestatus', {...action.payload, status: 'AREA_CLEARED'})
                .map(() => ({type: ActionTypes.CLEAR_CRASH_EVENT_POSTED, payload: action.payload.accidentId}))
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
