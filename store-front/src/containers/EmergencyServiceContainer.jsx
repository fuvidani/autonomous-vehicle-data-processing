import {connect} from "react-redux";
import EmergencyServiceComponent from "../components/emergency/EmergencyServiceComponent";
import {
    fetchCrashEventNotifications,
    cancelCrashEventNotifications,
    clearCrashEvent, arriveToCrashEvent
} from "../actions/EmergencyServiceActions";

const mapStateToProps = (state) => {
    return {
        crashEventNotifications: state.EmergencyServiceReducer.crashEventNotifications
    };
};

const mapDispatchToProps = (dispatch) => {
    return {
        fetchCrashEventNotifications: () => dispatch(fetchCrashEventNotifications()),
        cancelCrashEventNotifications: () => dispatch(cancelCrashEventNotifications()),
        arriveToCrashEvent: (accidentId) => dispatch(arriveToCrashEvent(accidentId)),
        clearCrashEvent: (accidentId) => dispatch(clearCrashEvent(accidentId))
    };
};

const EmergencyServiceContainer = connect(mapStateToProps, mapDispatchToProps)(EmergencyServiceComponent);

export default EmergencyServiceContainer;
