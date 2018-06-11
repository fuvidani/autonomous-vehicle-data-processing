import * as ActionTypes from "./ActionTypes";

const fetchCrashEventNotifications = () => ({type: ActionTypes.FETCH_EMERGENCY_SERVICE_CRASH_EVENT_NOTIFICATIONS});
const fetchCrashEventNotificationsHistory = () => ({type: ActionTypes.FETCH_EMERGENCY_SERVICE_CRASH_EVENT_NOTIFICATIONS_HISTORY});
const cancelCrashEventNotifications = () => ({type: ActionTypes.CANCEL_EMERGENCY_SERVICE_CRASH_EVENT_NOTIFICATIONS});
const arriveToCrashEvent = (accidentId, timestamp) => ({
    type: ActionTypes.ARRIVE_TO_CRASH_EVENT,
    payload: {accidentId: accidentId, timestamp: timestamp}
});
const clearCrashEvent = (accidentId, timestamp) => ({
    type: ActionTypes.CLEAR_CRASH_EVENT,
    payload: {accidentId: accidentId, timestamp: timestamp}
});

export {fetchCrashEventNotifications, fetchCrashEventNotificationsHistory, cancelCrashEventNotifications, arriveToCrashEvent, clearCrashEvent}
