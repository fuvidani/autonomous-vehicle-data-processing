import {connect} from "react-redux";
import ManufacturerComponent from "../components/manufacturer/ManufacturerComponent";
import {cancelVehicleTrackingStream, fetchVehicleTrackingStream} from "../actions/ManufacturerActions";

const mapStateToProps = (state) => {
    return {
        vehicleTrackingInformation: state.ManufacturerReducer.vehicleTrackingInformation,
        vehicles: state.ManufacturerReducer.vehicles
    };
};

const mapDispatchToProps = (dispatch) => {
    return {
        fetchVehicleTrackingStream: () => dispatch(fetchVehicleTrackingStream()),
        cancelVehicleTrackingStream: () => dispatch(cancelVehicleTrackingStream())
    };
};

const ManufacturerContainer = connect(mapStateToProps, mapDispatchToProps)(ManufacturerComponent);

export default ManufacturerContainer;
