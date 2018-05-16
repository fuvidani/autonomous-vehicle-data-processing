import * as ActionTypes from "./ActionTypes";

const fetchCrashEventNotifications = () => ({type: ActionTypes.FETCH_EMERGENCY_SERVICE_CRASH_EVENT_NOTIFICATIONS});
const cancelCrashEventNotifications = () => ({type: ActionTypes.CANCEL_EMERGENCY_SERVICE_CRASH_EVENT_NOTIFICATIONS});
const arriveToCrashEvent = (accidentId) => ({type: ActionTypes.ARRIVE_TO_CRASH_EVENT, payload: accidentId});
const clearCrashEvent = (accidentId) => ({type: ActionTypes.CLEAR_CRASH_EVENT, payload: accidentId});

export {fetchCrashEventNotifications, cancelCrashEventNotifications, arriveToCrashEvent, clearCrashEvent}
