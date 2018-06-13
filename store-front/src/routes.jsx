import React from 'react';
import {
    BrowserRouter as Router,
    Route
} from 'react-router-dom';
import Switch from "react-router-dom/es/Switch";
import AuthorityContainer from "./containers/AuthorityContainer";
import HomeComponent from "./components/HomeComponent";
import EmergencyServiceContainer from "./containers/EmergencyServiceContainer";
import ManufacturerContainer from "./containers/ManufacturerContainer";
import ManufacturersContainer from "./containers/ManufacturersContainer";
import NotFound from "./components/NotFound";

const Routes = () => (
    <Router>
        <div>
            <Switch>
                <Route exact path="/" component={HomeComponent}/>
                <Route exact path="/authority" component={AuthorityContainer}/>
                <Route exact path="/emergency" component={EmergencyServiceContainer}/>
                <Route exact path="/manufacturer" component={ManufacturersContainer}/>
                <Route exact path="/manufacturer/:id" component={ManufacturerContainer}/>
                <Route path="*" component={NotFound} />
            </Switch>
        </div>
    </Router>
);

export default Routes;
