import {connect} from "react-redux";
import ManufacturerComponent from "../components/manufacturer/ManufacturerComponent";
import {
    cancelManufacturerStreams, fetchManufacturerStreams
} from "../actions/ManufacturerActions";

const mapStateToProps = (state) => {
    return {
        vehicleTrackingInformation: state.ManufacturerReducer.vehicleTrackingInformation,
        vehicles: state.ManufacturerReducer.vehicles,
        notifications: state.ManufacturerReducer.notifications
    };
};

const mapDispatchToProps = (dispatch) => {
    return {
        fetchManufacturerStreams: (manufacturerId) => dispatch(fetchManufacturerStreams(manufacturerId)),
        cancelManufacturerStreams: () => dispatch(cancelManufacturerStreams())
    };
};

const ManufacturerContainer = connect(mapStateToProps, mapDispatchToProps)(ManufacturerComponent);

export default ManufacturerContainer;
