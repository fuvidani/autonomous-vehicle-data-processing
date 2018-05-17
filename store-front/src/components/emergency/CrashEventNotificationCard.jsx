import {Card, CardActions, CardMedia, CardText, CardTitle, FlatButton, List, ListItem} from "material-ui";
import React from "react";
import StaticMapComponent from "../StaticMapComponent";

const CrashEventNotificationCard = (props) => (
    <Card style={props.cardStyles} initiallyExpanded={true}>
        <CardMedia
            mediaStyle={{maxWidth: '100%'}}
            overlay={<CardTitle title="Crash Event" subtitle={new Date(props.notification.timeStamp).toString()}/>}
        >
            <StaticMapComponent
                markers={[{location: props.notification.location.lat + "," + props.notification.location.lon}]}/>
        </CardMedia>
        <CardText>
            <List>
                <ListItem
                    primaryText={"Model: " + props.notification.model}
                    disabled={true}
                />
                <ListItem
                    primaryText={"Passengers: " + props.notification.passengers}
                    disabled={true}
                />
            </List>
        </CardText>
        <CardActions>
            <FlatButton primary={true} label="Arrive"
                        onClick={() => props.arriveToCrashEvent(props.notification.accidentId, new Date().getTime())}/>
            <FlatButton primary={true} label="Clear"
                        onClick={() => props.clearCrashEvent(props.notification.accidentId, new Date().getTime())}/>
        </CardActions>
    </Card>
);

export default CrashEventNotificationCard;
