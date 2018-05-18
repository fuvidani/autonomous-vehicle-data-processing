import * as ActionTypes from "./ActionTypes";

const fetchManufacturerStreams = (manufacturerId) => ({
    type: ActionTypes.FETCH_MANUFACTURER_STREAMS,
    payload: manufacturerId
});
const cancelManufacturerStreams = () => ({type: ActionTypes.CANCEL_MANUFACTURER_STREAMS});
const clickNotification = (notification) => ({type: ActionTypes.CLICK_NOTIFICATION, payload: notification});
const leaveNotification = () => ({type: ActionTypes.LEAVE_NOTIFICATION});

export {fetchManufacturerStreams, cancelManufacturerStreams, clickNotification, leaveNotification}
