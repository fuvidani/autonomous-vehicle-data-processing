import {Card, CardMedia, CardText, CardTitle, List, ListItem} from "material-ui";
import React from "react";
import StaticMapComponent from "../StaticMapComponent";

const AccidentReportCard = (props) => (
    <Card style={props.cardStyles}>
        <CardMedia
            mediaStyle={{maxWidth: '100%'}} overlay={<CardTitle title="Accident Report" subtitle={props.report.timestampOfAccident ? new Date(props.report.timestampOfAccident).toString() : props.report.id}/>}
        >
            <StaticMapComponent
                markers={[{location: props.report.location.lat + "," + props.report.location.lon}]}/>
        </CardMedia>
        <CardText>
            <List>
                <ListItem
                    primaryText={"Accident: " + props.report.accidentId}
                    disabled={true}
                />
                <ListItem
                    primaryText="Vehicle:"
                    disabled={true}
                />
                <ListItem
                    primaryText={"ID: " + props.report.vehicleMetaData.identificationNumber}
                    insetChildren={true}
                    disabled={true}
                />
                <ListItem
                    primaryText={"Model: " + props.report.vehicleMetaData.model}
                    insetChildren={true}
                    disabled={true}
                />
                <ListItem
                    primaryText={"Passengers: " + props.report.passengers}
                    disabled={true}
                />
                <ListItem
                    primaryText={"Emergency response in " + (props.report.emergencyResponseInMillis / 60000).toFixed(2) + " min"}
                    disabled={true}
                />
                <ListItem
                    primaryText={"Site clearing in " + (props.report.durationOfSiteClearingInMillis / 60000).toFixed(2) + " min"}
                    disabled={true}
                />
            </List>
        </CardText>
    </Card>
);

export default AccidentReportCard;
