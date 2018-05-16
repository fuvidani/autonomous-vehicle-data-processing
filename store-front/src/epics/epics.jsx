import {combineEpics} from 'redux-observable';
import {fetchAccidentReportsEpic, clearAccidentReportsEpic} from './AuthorityEpics';
import {cancelAndClearManufacturerInformationEpic, fetchManufacturerInformationEpic} from "./ManufacturerEpics";
import {clearEmergencyServiceNotifications} from "./EmergencyServiceEpics";

export default combineEpics(
    fetchAccidentReportsEpic, clearAccidentReportsEpic, cancelAndClearManufacturerInformationEpic, fetchManufacturerInformationEpic, clearEmergencyServiceNotifications
);
