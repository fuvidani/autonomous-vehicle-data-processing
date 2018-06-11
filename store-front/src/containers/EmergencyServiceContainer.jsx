import {connect} from "react-redux";
import EmergencyServiceComponent from "../components/emergency/EmergencyServiceComponent";
import {
    fetchCrashEventNotifications,
    cancelCrashEventNotifications,
    clearCrashEvent, arriveToCrashEvent, fetchCrashEventNotificationsHistory
} from "../actions/EmergencyServiceActions";

const mapStateToProps = (state) => {
    return {
        crashEventNotifications: state.EmergencyServiceReducer.crashEventNotifications
    };
};

const mapDispatchToProps = (dispatch) => {
    return {
        fetchCrashEventNotifications: () => dispatch(fetchCrashEventNotifications()),
        fetchCrashEventNotificationsHistory: () => dispatch(fetchCrashEventNotificationsHistory()),
        cancelCrashEventNotifications: () => dispatch(cancelCrashEventNotifications()),
        arriveToCrashEvent: (accidentId, timestamp) => dispatch(arriveToCrashEvent(accidentId, timestamp)),
        clearCrashEvent: (accidentId, timestamp) => dispatch(clearCrashEvent(accidentId, timestamp))
    };
};

const EmergencyServiceContainer = connect(mapStateToProps, mapDispatchToProps)(EmergencyServiceComponent);

export default EmergencyServiceContainer;
