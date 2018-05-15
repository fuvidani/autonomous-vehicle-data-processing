import React from "react";
import GeneralComponent from "../GeneralComponent";
import AccidentReportCard from "./AccidentReportCard";
import PlaceholderCard from "../PlaceholderCard";

const styles = {
    cardStyles: {
        margin: "10px 0"
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
        return <div>
            <GeneralComponent/>
            <div className="container">
                <div className="row">
                    <div className="col-md-8 col-centered">
                        {this.props.accidentReports.length === 0 ? <PlaceholderCard text="No accident report found." cardStyles={styles.cardStyles}/> : this.props.accidentReports.reverse().map((report, i) => <AccidentReportCard key={i}
                                                                                                                                                                                                                                    cardStyles={styles.cardStyles}
                                                                                                                                                                                                                                    report={report}/>
                        )}
                    </div>
                </div>
            </div>
        </div>;
    }
}