import React from "react";
import GeneralComponent from "./GeneralComponent";
import ReactTooltip from 'react-tooltip';
import {RaisedButton} from "material-ui";
import {Link} from "react-router-dom";

const styles = {
    button: {
        height: 220,
        width: 220
    }
};

export default class ManufacturersComponent extends React.Component {
    constructor(props) {
        super(props);
    }


    render() {
        return <div>
            <GeneralComponent/>
            <div className="vertical-center">
                <div className="container">
                    <div className="row">
                        <ReactTooltip/>
                        {
                            this.props.manufacturers.map(manufacturer =>
                                <div key={manufacturer.id} className="col-md-3 text-center col-home">
                                    <RaisedButton
                                        containerElement={<Link to={"/manufacturer/" + manufacturer.id} />}
                                        data-tip={manufacturer.id}
                                        primary={true}
                                        style={styles.button}
                                        label={manufacturer.name}
                                    />
                                </div>)
                        }

                    </div>

                </div>
            </div>
        </div>;
    }
}
