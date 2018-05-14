import {connect} from "react-redux";
import {
    fetchAccidentReports,
    cancelAccidentReports
} from "../actions/actions";
import AuthorityComponent from "../components/AuthorityComponent";

const mapStateToProps = (state) => {
    return {
        accidentReports: state.AuthorityReducer.accidentReports
    };
};

const mapDispatchToProps = (dispatch) => {
    return {
        fetchAccidentReports: () => dispatch(fetchAccidentReports()),
        cancelAccidentReports: () => dispatch(cancelAccidentReports())
    };
};

const AuthorityContainer = connect(mapStateToProps, mapDispatchToProps)(AuthorityComponent);

export default AuthorityContainer;
