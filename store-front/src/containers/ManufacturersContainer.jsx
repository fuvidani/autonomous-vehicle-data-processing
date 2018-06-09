import {connect} from "react-redux";
import ManufacturersComponent from "../components/manufacturer/ManufacturersComponent";

const mapStateToProps = (state) => {
    return {
        manufacturers: state.ManufacturerReducer.manufacturers
    };
};

const mapDispatchToProps = () => {
    return {};
};

const ManufacturersContainer = connect(mapStateToProps, mapDispatchToProps)(ManufacturersComponent);

export default ManufacturersContainer;
