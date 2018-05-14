import React from "react";
import GeneralComponent from "./GeneralComponent";

export default class AuthorityComponent extends React.Component {
    constructor(props) {
        super(props);
    }

    componentWillMount() {
        this.props.fetchAccidentReports();
    }

    componentWillUnmount() {
        this.props.cancelAccidentReports();
    }


    render() {
        return <div>
            <GeneralComponent/>
            Authority component rendered!
            <ul>
                {this.props.accidentReports.map((report, i) => <li key={i}>{report.id}</li>)}
            </ul>
        </div>;
    }
}
