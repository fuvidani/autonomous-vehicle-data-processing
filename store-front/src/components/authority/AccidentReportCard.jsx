import {Card, CardMedia, CardText, CardTitle, List, ListItem} from "material-ui";
import React from "react";
import StaticMapComponent from "../StaticMapComponent";

const AccidentReportCard = (props) => (
    <Card style={props.cardStyles}>
        <CardMedia
            mediaStyle={{maxWidth: '100%'}} overlay={<CardTitle title="Accident Report" subtitle={props.report.id}/>}
        >
            <StaticMapComponent
                markers={[{location: props.report.location.lat + "," + props.report.location.lon}]}/>
        </CardMedia>
        <CardText>
            <List>
                <ListItem
                    primaryText={"AccidentId: " + props.report.accidentId}
                    disabled={true}
                />
                <ListItem
                    primaryText="VehicleMetaData:"
                    disabled={true}
                />
                <ListItem
                    primaryText={"IdentificationNumber: " + props.report.vehicleMetaData.identificationNumber}
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
                    primaryText={"EmergencyResponse: " + (props.report.emergencyResponseInMillis / 60000).toFixed(2) + " min"}
                    disabled={true}
                />
                <ListItem
                    primaryText={"DurationOfSiteClearing: " + (props.report.durationOfSiteClearingInMillis / 60000).toFixed(2) + " min"}
                    disabled={true}
                />
            </List>
        </CardText>
    </Card>
);

export default AccidentReportCard;
