import React from "react";
import GeneralComponent from "../GeneralComponent";
import MapComponent from "./MapComponent";
import PlaceholderCard from "../PlaceholderCard";
import VehicleList from "./VehicleList";
import NotificationList from "./NotificationList";

const styles = {
    cardStyles: {
        margin: "10px 0"
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
            <div className="row row-padding">
                <div className="col-md-3">
                    <VehicleList vehicles={this.props.vehicles} cardStyles={styles.cardStyles} listStyles={styles.listStyles} />
                </div>
                <div className="col-md-6">
                    <MapComponent/>
                </div>
                <div className="col-md-3">
                    <NotificationList notifications={this.props.notifications} cardStyles={styles.cardStyles} listStyles={styles.listStyles} />
                </div>
            </div>
        </div>;
    }
}
