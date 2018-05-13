import {connect} from "react-redux";
import MainComponent from "../components/MainComponent";
import {
    cancelNotifications,
    cancelServerEvents,
    cancelStatistics,
    fetchNotification,
    fetchServerEvents, fetchStatistics
} from "../actions/actions";

const mapStateToProps = (state) => {
    return {
        randomNumber: state.ServerEventReducer.randomNumber,
        notification: state.ServerEventReducer.notification,
        statistics: state.ServerEventReducer.statistics
    };
};

const mapDispatchToProps = (dispatch) => {
    return {
        fetchServerEvents: () => dispatch(fetchServerEvents()),
        cancelServerEvents: () => dispatch(cancelServerEvents()),
        fetchNotification: () => dispatch(fetchNotification()),
        cancelNotifications: () => dispatch(cancelNotifications()),
        fetchStatistics: () => dispatch(fetchStatistics()),
        cancelStatistics: () => dispatch(cancelStatistics())
    };
};

const MainContainer = connect(mapStateToProps, mapDispatchToProps)(MainComponent);

export default MainContainer;
