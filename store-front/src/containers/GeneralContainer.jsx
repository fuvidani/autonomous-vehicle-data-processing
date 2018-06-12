import {connect} from "react-redux";
import GeneralComponent from "../components/GeneralComponent";
import {restartSimulation} from "../actions/GeneralActions";

const mapStateToProps = () => {
    return {};
};

const mapDispatchToProps = (dispatch) => {
    return {
        restartSimulation: () => dispatch(restartSimulation())
    };
};

const GeneralContainer = connect(mapStateToProps, mapDispatchToProps)(GeneralComponent);

export default GeneralContainer;
