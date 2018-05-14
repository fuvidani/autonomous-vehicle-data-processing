import React from "react";
import GeneralComponent from "./GeneralComponent";

export default class EmergencyServiceComponent extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return <div>
            <GeneralComponent/>
            Emergency service component rendered!
        </div>;
    }
}
