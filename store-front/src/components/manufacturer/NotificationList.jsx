import React from "react";
import {
    Card,
    CardText,
    CardTitle,
    List,
    ListItem,
    Table, TableBody,
    TableHeader,
    TableHeaderColumn,
    TableRow, TableRowColumn
} from "material-ui";
import {ActionVisibility} from "material-ui/svg-icons/index.es";
import NotificationDetailsDialog from "./NotificationDetailsDialog";

const NotificationList = (props) => (
    <Card style={props.cardStyles}>
        <CardTitle title={<h4 className="text-center">Notifications</h4>}/>
        <CardText>
            {
                props.notifications.length === 0 ?
                    <List style={props.listStyles}><ListItem primaryText="No notifications to show." disabled={true}
                                                             className="text-center"/></List> :
                        <Table
                            height={props.tableStyles.height}
                            fixedHeader={true}
                            selectable={true}
                            multiSelectable={true}
                            onRowSelection={selection => props.handleNotificationSelectionChange(selection)}
                        >
                            <TableHeader
                                displaySelectAll={false}
                                adjustForCheckbox={true}
                                enableSelectAll={false}
                            >
                                <TableRow>
                                    <TableHeaderColumn colSpan={2}>Event type</TableHeaderColumn>
                                    <TableHeaderColumn colSpan={1}/>
                                </TableRow>
                            </TableHeader>
                            <TableBody
                                deselectOnClickaway={false}
                                displayRowCheckbox={true}
                                showRowHover={true}
                            >
                                {props.notifications.map((notification, i) => (
                                    <TableRow key={i} selected={!props.notShownNotificationIds.includes(i)}>
                                        <TableRowColumn colSpan={2}>{notification.eventInfo}</TableRowColumn>
                                        <TableRowColumn colSpan={1}>
                                            <span style={{cursor: "pointer"}} onClick={() => props.handleDialogOpen(notification)} >
                                                <ActionVisibility/>
                                            </span>
                                            <NotificationDetailsDialog clickedNotification={props.clickedNotification} handleDialogClose={props.handleDialogClose} />
                                        </TableRowColumn>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
            }
        </CardText>
    </Card>
);

export default NotificationList;
