import {combineReducers} from "redux";

import ServerEventReducer from "./ServerEventReducer";
import ManufacturerReducer from "./ManufacturerReducer";

export default combineReducers({
    ServerEventReducer, ManufacturerReducer
});
