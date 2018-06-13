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
import {postRestartSimulationEpic} from "./GeneralEpics";

export const rootEpic = (action$, store) =>
    combineEpics(
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
        postEmergencyServiceClearedEpic,
        postRestartSimulationEpic
    )(action$, store)
        .catch((e, source) => {
            setTimeout(() => {
                throw e;
            }, 0);
            return source;
        });
