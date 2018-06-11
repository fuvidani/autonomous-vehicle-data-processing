import {combineEpics} from 'redux-observable';
import {
    fetchAccidentReportsEpic,
    clearAccidentReportsEpic
} from './AuthorityEpics';
import {
    cancelAndClearManufacturerInformationEpic,
    fetchManufacturerInformationEpic,
    fetchManufacturerNotificationsEpic,
    fetchVehicleInformationEpic,
    fetchVehicleTrackingStreamEpic
} from "./ManufacturerEpics";
import {
    clearEmergencyServiceNotifications,
    fetchEmergencyServiceCrashEventNotificationsEpic,
    fetchEmergencyServiceCrashEventNotificationsHistoryEpic,
    postEmergencyServiceArrivedEpic,
    postEmergencyServiceClearedEpic
} from "./EmergencyServiceEpics";

export default combineEpics(
    fetchAccidentReportsEpic,
    clearAccidentReportsEpic,
    cancelAndClearManufacturerInformationEpic,
    fetchManufacturerInformationEpic,
    clearEmergencyServiceNotifications,
    fetchManufacturerNotificationsEpic,
    fetchVehicleInformationEpic,
    fetchVehicleTrackingStreamEpic,
    fetchEmergencyServiceCrashEventNotificationsEpic,
    fetchEmergencyServiceCrashEventNotificationsHistoryEpic,
    postEmergencyServiceArrivedEpic,
    postEmergencyServiceClearedEpic
);
