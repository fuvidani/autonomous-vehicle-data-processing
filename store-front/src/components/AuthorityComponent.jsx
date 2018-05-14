import React from "react";
import GeneralComponent from "./GeneralComponent";

export default class AuthorityComponent extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return <div>
            <GeneralComponent/>
            Authority component rendered!
        </div>;
    }
}
