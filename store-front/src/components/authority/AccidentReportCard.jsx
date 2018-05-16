import {Card, CardText, CardTitle, List, ListItem} from "material-ui";
import React from "react";

const AccidentReportCard = (props) => (
    <Card style={props.cardStyles}>
        <CardTitle title="Accident Report" subtitle={props.report.id}/>
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
                    primaryText="Location:"
                    disabled={true}
                />
                <ListItem
                    primaryText={"Lat: " + props.report.location.lat}
                    insetChildren={true}
                    disabled={true}
                />
                <ListItem
                    primaryText={"Lon: " + props.report.location.lon}
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
