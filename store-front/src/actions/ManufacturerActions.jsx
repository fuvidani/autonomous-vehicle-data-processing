import * as ActionTypes from "./ActionTypes";

const fetchManufacturerStreams = (manufacturerId) => ({type: ActionTypes.FETCH_MANUFACTURER_STREAMS, payload: manufacturerId});
const cancelManufacturerStreams = () => ({type: ActionTypes.CANCEL_MANUFACTURER_STREAMS});

export {fetchManufacturerStreams, cancelManufacturerStreams}
