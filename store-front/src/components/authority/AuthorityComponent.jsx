import React from "react";
import AccidentReportCard from "./AccidentReportCard";
import PlaceholderCard from "../PlaceholderCard";
import GeneralContainer from "../../containers/GeneralContainer";

const styles = {
    cardStyles: {
        margin: "20px 0"
    }
};

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
        return <div className="authorityContainer">
            <GeneralContainer/>
            <div className="container">
                <div className="row">
                    <div className="col-md-7 col-centered">
                        {
                            Object.getOwnPropertyNames(this.props.accidentReports).length === 0 ?
                                <PlaceholderCard text="No accident report to show."
                                                 cardStyles={styles.cardStyles}/> :
                                    Object.keys(this.props.accidentReports).sort().reverse().map(function (key) {
                                        return <AccidentReportCard key={key}
                                                                   cardStyles={styles.cardStyles}
                                                                   report={this.props.accidentReports[key]}/>;
                                    }, this)
                        }
                    </div>
                </div>
            </div>
        </div>;
    }
}
