import {combineReducers} from "redux";

import ManufacturerReducer from "./ManufacturerReducer";
import AuthorityReducer from "./AuthorityReducer";
import EmergencyServiceReducer from "./EmergencyServiceReducer";

export default combineReducers({
    AuthorityReducer, ManufacturerReducer, EmergencyServiceReducer
});
