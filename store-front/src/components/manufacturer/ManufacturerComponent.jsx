import React from "react";
import GeneralComponent from "../GeneralComponent";
import MapComponent from "./MapComponent";
import VehicleList from "./VehicleList";
import NotificationList from "./NotificationList";

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

let manufacturerId;

export default class ManufacturerComponent extends React.Component {
    constructor(props) {
        super(props);

        manufacturerId = this.props.match.params.id;

        this.state = {
            notShownNotificationIds: []
        }
    }

    componentWillMount() {
        // this.props.fetchManufacturerStreams(manufacturerId);
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

    render() {
        return <div>
            <GeneralComponent/>
            <div className="row p-0 m-0">
                <div className="col-md-3">
                    <VehicleList vehicles={this.props.vehicles} cardStyles={styles.cardStyles}
                                 listStyles={styles.listStyles}/>
                </div>
                <div className="col-md-6">
                    <MapComponent vehicleTrackingInformation={this.props.vehicleTrackingInformation}
                                  notShownNotificationIds={this.state.notShownNotificationIds}
                                  notifications={this.props.notifications}/>
                </div>
                <div className="col-md-3">
                    <NotificationList notifications={this.props.notifications}
                                      cardStyles={styles.cardStyles}
                                      listStyles={styles.listStyles}
                                      popoverState={this.state}
                                      handleClick={this.handleClick}
                                      handleRequestClose={this.handleRequestClose}
                                      tableStyles={styles.tableStyles}
                                      notShownNotificationIds={this.state.notShownNotificationIds}
                                      handleNotificationSelectionChange={this.handleNotificationSelectionChange.bind(this)}/>
                </div>
            </div>
        </div>;
    }
}
