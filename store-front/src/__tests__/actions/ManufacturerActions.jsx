import * as ActionTypes from "../../actions/ActionTypes";
import {
    cancelManufacturerStreams, clickNotification,
    fetchManufacturerStreams,
    leaveNotification
} from "../../actions/ManufacturerActions";

describe('authorityActions', () => {
    it('should fire CANCEL_MANUFACTURER_STREAMS action', () => {
        const cancelManufacturerStreamsAction = {
            type: ActionTypes.CANCEL_MANUFACTURER_STREAMS
        };
        expect(cancelManufacturerStreams()).toEqual(cancelManufacturerStreamsAction)
    });

    it('should fire LEAVE_NOTIFICATION action', () => {
        const leaveNotificationAction = {
            type: ActionTypes.LEAVE_NOTIFICATION
        };
        expect(leaveNotification()).toEqual(leaveNotificationAction)
    });

    it('should fire FETCH_MANUFACTURER_STREAMS action with valid payload', () => {
        const manufacturerId = 1;
        const fetchManufacturerStreamsAction = {
            type: ActionTypes.FETCH_MANUFACTURER_STREAMS,
            payload: manufacturerId
        };
        expect(fetchManufacturerStreams(manufacturerId)).toEqual(fetchManufacturerStreamsAction)
    });

    it('should fire CLICK_NOTIFICATION action with valid payload', () => {
        const notification = {
            id: "1",
            timeStamp: new Date().getTime(),
            vehicleIdentificationNumber: "2",
            model: "3",
            location: {
                lat: 0.0,
                lon: 0.0
            },
            eventInfo: "4",
            accidentId: "5"
        };
        const clickNotificationAction = {
            type: ActionTypes.CLICK_NOTIFICATION,
            payload: notification
        };
        expect(clickNotification(notification)).toEqual(clickNotificationAction)
    });
});
