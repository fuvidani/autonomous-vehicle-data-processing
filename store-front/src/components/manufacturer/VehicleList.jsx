import React from "react";
import {Card, CardText, CardTitle, List, ListItem} from "material-ui";
import {AvPlayArrow, AvStop} from "material-ui/svg-icons/index.es";

const VehicleList = (props) => (
    <Card style={props.cardStyles}>
        <CardTitle title={<h4 className="text-center">Vehicles</h4>}/>
        <CardText>
            <List>
                {
                    Object.keys(props.vehicles).map(function (key) {
                        return <ListItem
                            key={key}
                            leftIcon={props.vehicles[key].moving ? <AvPlayArrow/> : <AvStop/>}
                            primaryText={props.vehicles[key].identificationNumber}
                            secondaryText={props.vehicles[key].model}
                            disabled={true}
                        />
                    })
                }
            </List>
        </CardText>
    </Card>
);

export default VehicleList;
