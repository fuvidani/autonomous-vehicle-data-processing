import React from "react";

export default class MainComponent extends React.Component {
    render() {
        return <div>
            {/*<h1>Random number: {this.props.randomNumber}</h1>*/}
            {/*<h1>Notification: {this.props.notification}</h1>*/}
            <h1>Statistics</h1>
            <ul>
                {this.props.statistics.map((stat, index) => <li key={index}>{stat.id}</li>)}
            </ul>
            {/*<button onClick={this.props.fetchServerEvents}>Fetch Server Events</button>*/}
            {/*<button onClick={this.props.cancelServerEvents}>Cancel event fetching</button>*/}
            {/*<button onClick={this.props.fetchNotification}>Fetch Notifications</button>*/}
            {/*<button onClick={this.props.cancelNotifications}>Cancel notification fetching</button>*/}
            <button onClick={this.props.fetchStatistics}>Fetch Statistics</button>
            <button onClick={this.props.cancelStatistics}>Cancel statistics fetching</button>
        </div>;
    }
}
