import * as ActionTypes from "./ActionTypes";

const fetchVehicleTrackingStream = () => ({type: ActionTypes.FETCH_VEHICLE_TRACKING_STREAM});
const cancelVehicleTrackingStream = () => ({type: ActionTypes.CANCEL_VEHICLE_TRACKING_STREAM});
const fetchVehicleInformation = () => ({type: ActionTypes.FETCH_VEHICLE_INFORMATION});
const cancelVehicleInformation = () => ({type: ActionTypes.CANCEL_VEHICLE_INFORMATION});

export {fetchVehicleTrackingStream, cancelVehicleTrackingStream, fetchVehicleInformation, cancelVehicleInformation}
