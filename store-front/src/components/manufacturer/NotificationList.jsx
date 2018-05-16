import React from "react";
import {Card, CardText, CardTitle, Divider, List, ListItem} from "material-ui";
import {SocialSentimentDissatisfied, SocialSentimentVeryDissatisfied} from "material-ui/svg-icons/index.es";

const NotificationList = (props) => (
    <Card style={props.cardStyles}>
        <CardTitle title={<h4 className="text-center">Notifications</h4>}/>
        <CardText>
            <List style={props.listStyles}>
                {
                    props.notifications.length === 0 ? <ListItem primaryText="No notifications to show." disabled={true}
                                                                 className="text-center"/> : props.notifications.map((notification, i) =>
                        <div key={notification.id}>
                            <Divider/>
                            <ListItem key={i}
                                      primaryText={notification.model + " - " + notification.vehicleIdentificationNumber}
                                      secondaryText={new Date(notification.timeStamp).toString()}
                                      disabled={true}
                                      leftIcon={notification.eventInfo === 'NEAR_CRASH' ?
                                          <SocialSentimentDissatisfied/> : <SocialSentimentVeryDissatisfied/>}/>
                            <Divider/>
                        </div>)
                }

            </List>
        </CardText>
    </Card>
);

export default NotificationList;
