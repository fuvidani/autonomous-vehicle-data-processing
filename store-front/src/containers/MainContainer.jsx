import {connect} from "react-redux";
import MainComponent from "../components/MainComponent";
import {cancelServerEvents, fetchServerEvents} from "../actions/actions";

const mapStateToProps = (state) => {
  return {
    randomNumber: state.ServerEventReducer.randomNumber
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchServerEvents: () => dispatch(fetchServerEvents()),
    cancelServerEvents: () => dispatch(cancelServerEvents())
  };
};

const MainContainer = connect(mapStateToProps, mapDispatchToProps)(MainComponent);

export default MainContainer;
