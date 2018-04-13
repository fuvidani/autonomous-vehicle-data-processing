import {connect} from "react-redux";
import MainComponent from "../components/MainComponent";
import {cancelNotifications, cancelServerEvents, fetchNotification, fetchServerEvents} from "../actions/actions";

const mapStateToProps = (state) => {
  return {
    randomNumber: state.ServerEventReducer.randomNumber,
    notification: state.ServerEventReducer.notification
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchServerEvents: () => dispatch(fetchServerEvents()),
    cancelServerEvents: () => dispatch(cancelServerEvents()),
    fetchNotification: () => dispatch(fetchNotification()),
    cancelNotifications: () => dispatch(cancelNotifications())
  };
};

const MainContainer = connect(mapStateToProps, mapDispatchToProps)(MainComponent);

export default MainContainer;
