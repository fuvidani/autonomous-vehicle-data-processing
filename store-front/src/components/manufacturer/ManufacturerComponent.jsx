import React from "react";
import GeneralComponent from "../GeneralComponent";
import MapComponent from "./MapComponent";

export default class ManufacturerComponent extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return <div>
            <GeneralComponent/>
            Manufacturer ({this.props.match.params.id}) component rendered!
            <MapComponent isMarkerShown />
        </div>;
    }
}
