import React from "react";
import MapComponent from "./MapComponent";
import VehicleList from "./VehicleList";
import NotificationList from "./NotificationList";
import GeneralContainer from "../../containers/GeneralContainer";

const styles = {
    cardStyles: {
        margin: '10px 0',
        minHeight: '850px'
    },
    listStyles: {
        paddingBottom: 0,
        height: '740px',
        overflow: 'auto'
    },
    tableStyles: {
        height: '680px'
    }
};

let manufacturerId = null;

export default class ManufacturerComponent extends React.Component {
    constructor(props) {
        super(props);

        manufacturerId = this.props.match.params.id;

        this.state = {
            notShownNotificationIds: []
        }
    }

    componentWillMount() {
        this.props.fetchManufacturerStreams(manufacturerId);
    }

    componentWillUnmount() {
        this.props.cancelManufacturerStreams();
    }

    handleNotificationSelectionChange(selection) {
        let newNotShownIds = [];
        for (let i = 0; i < this.props.notifications.length; i++) {
            if (!selection.includes(i)) {
                newNotShownIds.push(i);
            }
        }

        this.setState({
            notShownNotificationIds: newNotShownIds
        });
    }

    handleDialogOpen = (notification) => {
        this.props.clickNotification(notification);
    };

    handleDialogClose = () => {
        this.props.leaveNotification();
    };

    render() {
        return <div>
            <GeneralContainer/>
            <div className="row p-0 m-0">
                <div className="col-md-3">
                    <VehicleList vehicles={this.props.vehicles} cardStyles={styles.cardStyles}
                                 listStyles={styles.listStyles}/>
                </div>
                <div className="col-md-6">
                    <MapComponent vehicleTrackingInformation={this.props.vehicleTrackingInformation}
                                  notShownNotificationIds={this.state.notShownNotificationIds}
                                  notifications={this.props.notifications}
                                  vehicleHistoryInformation={this.props.vehicleHistoryInformation}/>
                </div>
                <div className="col-md-3">
                    <NotificationList notifications={this.props.notifications}
                                      cardStyles={styles.cardStyles}
                                      listStyles={styles.listStyles}
                                      handleDialogOpen={this.handleDialogOpen}
                                      handleDialogClose={this.handleDialogClose}
                                      tableStyles={styles.tableStyles}
                                      clickedNotification={this.props.clickedNotification}
                                      notShownNotificationIds={this.state.notShownNotificationIds}
                                      handleNotificationSelectionChange={this.handleNotificationSelectionChange.bind(this)}/>
                </div>
            </div>
        </div>;
    }
}
