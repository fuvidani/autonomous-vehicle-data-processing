import React from "react";
import GeneralComponent from "./GeneralComponent";
import {RaisedButton} from "material-ui";
import {MapsLocalHospital, ActionBuild, ActionAccountBalance} from "material-ui/svg-icons/index.es";
import ReactTooltip from 'react-tooltip';
import {Link} from "react-router-dom";

const styles = {
    button: {
        height: 220,
        width: 220
    },
    largeIcon: {
        width: 80,
        height: 80
    },
};

export default class HomeComponent extends React.Component {
    render() {
        return <div>
            <GeneralComponent/>
            <div className="vertical-center">
                <div className="container">
                    <div className="row">
                        <ReactTooltip/>
                        <div className="col-md-4 text-center col-home">
                            <RaisedButton
                                containerElement={<Link to="/authority"/>}
                                data-tip="Authority"
                                labelPosition="after"
                                primary={true}
                                icon={<ActionAccountBalance style={styles.largeIcon}/>}
                                style={styles.button}
                            />
                        </div>
                        <div className="col-md-4 text-center col-home">
                            <RaisedButton
                                containerElement={<Link to="/emergency"/>}
                                data-tip="Emergency Service"
                                labelPosition="after"
                                primary={true}
                                icon={<MapsLocalHospital style={styles.largeIcon}/>}
                                style={styles.button}
                            />
                        </div>
                        <div className="col-md-4 text-center col-home">
                            <RaisedButton
                                containerElement={<Link to="/manufacturer"/>}
                                data-tip="Manufacturers"
                                labelPosition="after"
                                primary={true}
                                icon={<ActionBuild style={styles.largeIcon}/>}
                                style={styles.button}
                            />
                        </div>
                    </div>

                </div>
            </div>
        </div>;
    }
}
