import React from "react";
import {Card, CardText, CardTitle, List, ListItem, Popover} from "material-ui";

const NotificationDetailsPopover = (props) => (
    <Popover
        open={props.popoverState.open.indexOf(props.notification.id) !== -1}
        anchorEl={props.popoverState.anchorEl}
        anchorOrigin={{horizontal: 'left', vertical: 'bottom'}}
        targetOrigin={{horizontal: 'left', vertical: 'top'}}
        onRequestClose={props.handleRequestClose(props.notification.id)}
    >
        <Card>
            <CardTitle title={<h4 className="text-center">Details</h4>}/>
            <CardText>
                <List>
                    <ListItem
                        primaryText={"Accident: " + props.notification.eventInfo}
                        disabled={true}/>
                    <ListItem
                        primaryText={"Timestamp: " + new Date(props.notification.timeStamp).toString()}
                        disabled={true}/>
                    <ListItem
                        primaryText="Vehicle:"
                        disabled={true}
                    />
                    <ListItem
                        primaryText={"ID: " + props.notification.vehicleIdentificationNumber}
                        insetChildren={true}
                        disabled={true}
                    />
                    <ListItem
                        primaryText={"Model: " + props.notification.model}
                        insetChildren={true}
                        disabled={true}
                    />
                </List>
            </CardText>
        </Card>
    </Popover>
);

export default NotificationDetailsPopover;
