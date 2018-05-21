import * as ActionTypes from "../actions/ActionTypes";
import {Observable} from "rxjs/Rx";
import {fetchStream} from "../util/RequestHandler";

const fetchManufacturerInformationEpic = action$ =>
    action$.ofType(ActionTypes.FETCH_MANUFACTURER_STREAMS)
        .flatMap(action =>
            Observable.concat(
                Observable.of({type: ActionTypes.FETCH_VEHICLE_TRACKING_STREAM, payload: action.payload}),
                Observable.of({type: ActionTypes.FETCH_VEHICLE_INFORMATION, payload: action.payload}),
                Observable.of({type: ActionTypes.FETCH_MANUFACTURER_NOTIFICATIONS, payload: action.payload})
            )
        );

const fetchVehicleTrackingStreamEpic = action$ =>
    action$.ofType(ActionTypes.FETCH_VEHICLE_TRACKING_STREAM)
        .mergeMap((action) =>
            fetchStream('/tracking/manufacturer/' + action.payload)
                .filter(response => JSON.parse(response).id !== "ping" && JSON.parse(response).id !== "")
                .map(response => ({
                    type: ActionTypes.VEHICLE_TRACKING_INFORMATION_FETCHED,
                    payload: JSON.parse(response)
                }))
                .takeUntil(action$.ofType(ActionTypes.CANCEL_VEHICLE_TRACKING_STREAM))
        );

const fetchVehicleInformationEpic = action$ =>
    action$.ofType(ActionTypes.FETCH_VEHICLE_INFORMATION)
        .mergeMap((action) =>
            fetchStream('/vehicle/' + action.payload + '/vehicles')
                .map(response => ({type: ActionTypes.VEHICLE_INFORMATION_FETCHED, payload: JSON.parse(response)}))
                .takeUntil(action$.ofType(ActionTypes.CANCEL_VEHICLE_INFORMATION))
        );

const fetchManufacturerNotificationsEpic = action$ =>
    action$.ofType(ActionTypes.FETCH_MANUFACTURER_NOTIFICATIONS)
        .mergeMap((action) =>
            fetchStream('/notifications/manufacturer/' + action.payload)
                .filter(response => JSON.parse(response).id !== "ping" && JSON.parse(response).id !== "")
                .map(response => ({
                    type: ActionTypes.MANUFACTURER_NOTIFICATION_FETCHED,
                    payload: JSON.parse(response)
                }))
                .takeUntil(action$.ofType(ActionTypes.CANCEL_MANUFACTURER_NOTIFICATIONS))
        );

const cancelAndClearManufacturerInformationEpic = action$ =>
    action$.ofType(ActionTypes.CANCEL_MANUFACTURER_STREAMS)
        .flatMap(() =>
            Observable.concat(
                Observable.of({type: ActionTypes.CANCEL_VEHICLE_INFORMATION}),
                Observable.of({type: ActionTypes.CANCEL_VEHICLE_TRACKING_STREAM}),
                Observable.of({type: ActionTypes.CANCEL_MANUFACTURER_NOTIFICATIONS}),
                Observable.of({type: ActionTypes.CLEAR_MANUFACTURER_INFORMATION})
            )
        );

export {
    cancelAndClearManufacturerInformationEpic,
    fetchManufacturerInformationEpic,
    fetchVehicleTrackingStreamEpic,
    fetchVehicleInformationEpic,
    fetchManufacturerNotificationsEpic
}
