import * as ActionTypes from "./ActionTypes";

const fetchVehicleTrackingStream = () => ({type: ActionTypes.FETCH_VEHICLE_TRACKING_STREAM});
const cancelVehicleTrackingStream = () => ({type: ActionTypes.CANCEL_VEHICLE_TRACKING_STREAM});

export {fetchVehicleTrackingStream, cancelVehicleTrackingStream}
