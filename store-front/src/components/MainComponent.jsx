import React from "react";

export default class MainComponent extends React.Component {
  render() {
    return <div>
      <h1>Random number: {this.props.randomNumber}</h1>
      <button onClick={this.props.fetchServerEvents}>Start fetching</button>
      <button onClick={this.props.cancelServerEvents}>Cancel fetching</button>
    </div>;
  }
}
