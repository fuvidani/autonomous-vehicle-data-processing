import React from "react";
import GeneralComponent from "../GeneralComponent";

export default class ManufacturerComponent extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return <div>
            <GeneralComponent/>
            Manufacturer ({this.props.match.params.id}) component rendered!
        </div>;
    }
}
