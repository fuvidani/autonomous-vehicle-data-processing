import React from "react";
import GeneralComponent from "../GeneralComponent";
import MapComponent from "./MapComponent";
import VehicleList from "./VehicleList";
import NotificationList from "./NotificationList";

const styles = {
    cardStyles: {
        margin: "10px 0",
        minHeight: '500px'
    },
    listStyles: {
        paddingBottom: 0
    }
};

let manufacturerId;

export default class ManufacturerComponent extends React.Component {
    constructor(props) {
        super(props);

        manufacturerId = this.props.match.params.id;
    }

    componentWillMount() {
        this.props.fetchManufacturerStreams(manufacturerId);
    }

    componentWillUnmount() {
        this.props.cancelManufacturerStreams();
    }

    render() {
        return <div>
            <GeneralComponent/>
            <div className="row p-0 m-0">
                <div className="col-md-3">
                    <VehicleList vehicles={this.props.vehicles} cardStyles={styles.cardStyles}
                                 listStyles={styles.listStyles}/>
                </div>
                <div className="col-md-6">
                    <MapComponent vehicleTrackingInformation={this.props.vehicleTrackingInformation}/>
                </div>
                <div className="col-md-3">
                    <NotificationList notifications={this.props.notifications} cardStyles={styles.cardStyles}
                                      listStyles={styles.listStyles}/>
                </div>
            </div>
        </div>;
    }
}
