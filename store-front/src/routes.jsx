import React from 'react';
import {
  BrowserRouter as Router,
  Route
} from 'react-router-dom';
import Redirect from "react-router-dom/es/Redirect";
import MainContainer from "./containers/MainContainer";
import Switch from "react-router-dom/es/Switch";

const Routes = () => (
  <Router>
    <div>
      <Switch>
        <Route exact path="/" component={MainContainer}/>
        <Redirect from="*" to="/"/>
      </Switch>
    </div>
  </Router>
);

export default Routes;
