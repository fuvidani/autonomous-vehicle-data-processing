import {connect} from "react-redux";
import ManufacturersComponent from "../components/ManufacturersComponent";

const mapStateToProps = (state) => {
    return {
        manufacturers: state.ManufacturerReducer.manufacturers
    };
};

const mapDispatchToProps = (dispatch) => {
    return {};
};

const ManufacturersContainer = connect(mapStateToProps, mapDispatchToProps)(ManufacturersComponent);

export default ManufacturersContainer;
