import React from "react";
import {Dialog, FlatButton, List, ListItem} from "material-ui";

const NotificationDetailsDialog = (props) => (
    <Dialog
        title="Notification details"
        modal={false}
        actions={[
            <FlatButton
                key={0}
                label="Cancel"
                primary={true}
                onClick={props.handleDialogClose}
            />
        ]}
        open={props.clickedNotification.id !== ""}
        onRequestClose={props.handleDialogClose}
    >
        <List>
            {props.clickedNotification.accidentId && <ListItem
                primaryText={"Accident: " + props.clickedNotification.accidentId}
                disabled={true}/>
            }
            <ListItem
                primaryText={"Timestamp: " + new Date(props.clickedNotification.timeStamp).toString()}
                disabled={true}/>
            <ListItem
                primaryText="Vehicle:"
                disabled={true}
            />
            <ListItem
                primaryText={"ID: " + props.clickedNotification.vehicleIdentificationNumber}
                insetChildren={true}
                disabled={true}
            />
            <ListItem
                primaryText={"Model: " + props.clickedNotification.model}
                insetChildren={true}
                disabled={true}
            />
        </List>
    </Dialog>
);

export default NotificationDetailsDialog;
