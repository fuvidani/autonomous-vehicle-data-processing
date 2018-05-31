import React from "react";
import {Card, CardText, CardTitle, Divider, List, ListItem} from "material-ui";
import {AvPlayArrow, AvStop, ImageLens} from "material-ui/svg-icons/index.es";

const VehicleList = (props) => (
    <Card style={props.cardStyles}>
        <CardTitle title={<h4 className="text-center">Vehicles</h4>}/>
        <CardText>
            <List style={props.listStyles}>
                {
                    Object.getOwnPropertyNames(props.vehicles).length === 0 ?
                        <ListItem primaryText="No vehicle to show." disabled={true}
                                  className="text-center"/> :
                        <div>
                            {Object.keys(props.vehicles).sort().map(function (key) {
                                if (!props.vehicles[key].moving) return;
                                return <div key={key}>
                                    <Divider/>
                                    <ListItem
                                        key={key}
                                        leftIcon={<ImageLens
                                            style={{fill: '#66BB6A'}}/>}
                                        primaryText={props.vehicles[key].identificationNumber}
                                        secondaryText={props.vehicles[key].model}
                                        disabled={true}
                                    />
                                    <Divider/>
                                </div>
                            })}
                            {Object.keys(props.vehicles).sort().map(function (key) {
                                if (props.vehicles[key].moving) return;
                                return <div key={key}>
                                    <Divider/>
                                    <ListItem
                                        key={key}
                                        leftIcon={<ImageLens/>}
                                        primaryText={props.vehicles[key].identificationNumber}
                                        secondaryText={props.vehicles[key].model}
                                        disabled={true}
                                    />
                                    <Divider/>
                                </div>
                            })}
                        </div>
                }
            </List>
        </CardText>
    </Card>
);

export default VehicleList;
