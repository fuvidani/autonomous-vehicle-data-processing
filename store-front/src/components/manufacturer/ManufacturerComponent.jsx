import React from "react";
import GeneralComponent from "../GeneralComponent";
import MapComponent from "./MapComponent";
import PlaceholderCard from "../PlaceholderCard";
import VehicleList from "./VehicleList";

const styles = {
    cardStyles: {
        margin: "10px 0"
    }
};

let manufacturerId;

export default class ManufacturerComponent extends React.Component {
    constructor(props) {
        super(props);

        manufacturerId = this.props.match.params.id;
    }

    componentWillMount() {
        this.props.fetchVehicleTrackingStream();
    }

    componentWillUnmount() {
        this.props.cancelVehicleTrackingStream();
    }

    render() {
        return <div>
            <GeneralComponent/>
            <div className="row row-padding">
                <div className="col-md-3">
                    <VehicleList vehicles={this.props.vehicles} cardStyles={styles.cardStyles}/>
                </div>
                <div className="col-md-6">
                    <MapComponent/>
                </div>
                <div className="col-md-3">
                    <PlaceholderCard text="No crash event notification to show."
                                     cardStyles={styles.cardStyles}/>
                </div>
            </div>
        </div>;
    }
}
