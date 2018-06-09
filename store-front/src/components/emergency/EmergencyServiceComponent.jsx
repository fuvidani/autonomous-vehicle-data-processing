import React from "react";
import GeneralComponent from "../GeneralComponent";
import PlaceholderCard from "../PlaceholderCard";
import CrashEventNotificationCard from "./CrashEventNotificationCard";

const styles = {
    cardStyles: {
        margin: "10px 0"
    }
};

export default class EmergencyServiceComponent extends React.Component {
    constructor(props) {
        super(props);
    }

    componentWillMount() {
        this.props.fetchCrashEventNotifications();
    }

    componentWillUnmount() {
        this.props.cancelCrashEventNotifications();
    }

    render() {
        return <div>
            <GeneralComponent/>
            <div className="container">
                <div className="row">
                    <div className="col-md-7 col-centered">
                        {this.props.crashEventNotifications.length === 0 ?
                            <PlaceholderCard text="No crash event notification to show."
                                             cardStyles={styles.cardStyles}/> : this.props.crashEventNotifications.reverse().map((notification, i) =>
                                <CrashEventNotificationCard key={i}
                                                            cardStyles={styles.cardStyles}
                                                            notification={notification}
                                                            arriveToCrashEvent={this.props.arriveToCrashEvent}
                                                            clearCrashEvent={this.props.clearCrashEvent}/>
                            )}
                    </div>
                </div>
            </div>
        </div>;
    }
}
