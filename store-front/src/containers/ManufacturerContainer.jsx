import {connect} from "react-redux";
import ManufacturerComponent from "../components/manufacturer/ManufacturerComponent";
import {
    cancelManufacturerStreams, fetchManufacturerStreams, clickNotification, leaveNotification
} from "../actions/ManufacturerActions";

const mapStateToProps = (state) => {
    return {
        vehicleTrackingInformation: state.ManufacturerReducer.vehicleTrackingInformation,
        vehicles: state.ManufacturerReducer.vehicles,
        notifications: state.ManufacturerReducer.notifications,
        clickedNotification: state.ManufacturerReducer.clickedNotification
    };
};

const mapDispatchToProps = (dispatch) => {
    return {
        fetchManufacturerStreams: (manufacturerId) => dispatch(fetchManufacturerStreams(manufacturerId)),
        cancelManufacturerStreams: () => dispatch(cancelManufacturerStreams()),
        clickNotification: (notification) => dispatch(clickNotification(notification)),
        leaveNotification: () => dispatch(leaveNotification())
    };
};

const ManufacturerContainer = connect(mapStateToProps, mapDispatchToProps)(ManufacturerComponent);

export default ManufacturerContainer;
