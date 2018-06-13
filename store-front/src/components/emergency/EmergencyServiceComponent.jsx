import React from "react";
import PlaceholderCard from "../PlaceholderCard";
import CrashEventNotificationCard from "./CrashEventNotificationCard";
import GeneralContainer from "../../containers/GeneralContainer";

const styles = {
    cardStyles: {
        margin: "20px 0"
    }
};

export default class EmergencyServiceComponent extends React.Component {
    constructor(props) {
        super(props);
    }

    componentWillMount() {
        this.props.fetchCrashEventNotifications();
        this.props.fetchCrashEventNotificationsHistory();
    }

    componentWillUnmount() {
        this.props.cancelCrashEventNotifications();
    }

    render() {
        return <div>
            <GeneralContainer/>
            <div className="container">
                <div className="row">
                    <div className="col-md-7 col-centered">
                        {this.props.crashEventNotifications.length === 0 ?
                            <PlaceholderCard text="No crash event notification to show."
                                             cardStyles={styles.cardStyles}/> : this.props.crashEventNotifications.sort(function (a, b) {
                                return (a.timeStamp < b.timeStamp) ? 1 : ((b.timeStamp < a.timeStamp) ? -1 : 0);
                            }).map((notification, i) =>
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
