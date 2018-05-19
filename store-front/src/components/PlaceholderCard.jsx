import {Card, CardText, List, ListItem} from "material-ui";
import React from "react";

const PlaceholderCard = (props) => (
    <Card style={props.cardStyles}>
        <CardText>
            <List>
                <ListItem
                    className="text-center"
                    primaryText={props.text}
                    disabled={true}
                />
            </List>
        </CardText>
    </Card>
);

export default PlaceholderCard;
