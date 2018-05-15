import {combineReducers} from "redux";

import ManufacturerReducer from "./ManufacturerReducer";
import AuthorityReducer from "./AuthorityReducer";

export default combineReducers({
    AuthorityReducer, ManufacturerReducer
});
