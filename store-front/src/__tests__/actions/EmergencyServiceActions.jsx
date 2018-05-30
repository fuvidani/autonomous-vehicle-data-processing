import * as ActionTypes from "../../actions/ActionTypes";
import {
    arriveToCrashEvent,
    cancelCrashEventNotifications, clearCrashEvent,
    fetchCrashEventNotifications
} from "../../actions/EmergencyServiceActions";

describe('emergencyServiceActions', () => {
    it('should fire FETCH_EMERGENCY_SERVICE_CRASH_EVENT_NOTIFICATIONS action', () => {
        const fetchCrashEventNotificationsAction = {
            type: ActionTypes.FETCH_EMERGENCY_SERVICE_CRASH_EVENT_NOTIFICATIONS
        };
        expect(fetchCrashEventNotifications()).toEqual(fetchCrashEventNotificationsAction)
    });

    it('should fire CANCEL_EMERGENCY_SERVICE_CRASH_EVENT_NOTIFICATIONS action', () => {
        const cancelCrashEventNotificationsAction = {
            type: ActionTypes.CANCEL_EMERGENCY_SERVICE_CRASH_EVENT_NOTIFICATIONS
        };
        expect(cancelCrashEventNotifications()).toEqual(cancelCrashEventNotificationsAction)
    });

    it('should fire ARRIVE_TO_CRASH_EVENT action with valid payload', () => {
        const accidentId = 1;
        const timestamp = new Date().getTime();
        const arriveToCrashEventAction = {
            type: ActionTypes.ARRIVE_TO_CRASH_EVENT,
            payload: {
                accidentId: accidentId,
                timestamp: timestamp
            }
        };
        expect(arriveToCrashEvent(accidentId, timestamp)).toEqual(arriveToCrashEventAction)
    });

    it('should fire CLEAR_CRASH_EVENT action with valid payload', () => {
        const accidentId = 1;
        const timestamp = new Date().getTime();
        const clearCrashEventAction = {
            type: ActionTypes.CLEAR_CRASH_EVENT,
            payload: {
                accidentId: accidentId,
                timestamp: timestamp
            }
        };
        expect(clearCrashEvent(accidentId, timestamp)).toEqual(clearCrashEventAction)
    });
});