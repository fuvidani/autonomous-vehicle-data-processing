import {connect} from "react-redux";
import {
    cancelStatistics,
    fetchStatistics
} from "../actions/actions";
import AuthorityComponent from "../components/AuthorityComponent";

const mapStateToProps = (state) => {
    return {
        statistics: state.ServerEventReducer.statistics
    };
};

const mapDispatchToProps = (dispatch) => {
    return {
        fetchStatistics: () => dispatch(fetchStatistics()),
        cancelStatistics: () => dispatch(cancelStatistics())
    };
};

const AuthorityContainer = connect(mapStateToProps, mapDispatchToProps)(AuthorityComponent);

export default AuthorityContainer;
