import React from "react";
import {AppBar, Drawer, MenuItem} from "material-ui";
import {Link} from "react-router-dom";
import {MapsLocalHospital, ActionBuild, ActionAccountBalance} from "material-ui/svg-icons/index.es";

const styles = {
    titleLink: {
        textDecoration: 'none',
        color: 'white'
    },
    menuItem: {
        padding: '5px 0'
    }
};

export default class GeneralComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            open: false
        };
    }

    handleToggle = () => this.setState({open: !this.state.open});

    render() {
        return <div>
            <AppBar
                title={<div><Link style={styles.titleLink} to="/">Autonomous Vehicle Data Processing</Link></div>}
                onLeftIconButtonClick={this.handleToggle}
            />

            <Drawer open={this.state.open} docked={false} onRequestChange={(open) => this.setState({open})}>
                <AppBar
                    title={<div><Link style={styles.titleLink} to="/">AVDP</Link></div>}
                    onLeftIconButtonClick={this.handleToggle}
                />
                <MenuItem style={styles.menuItem} onClick={this.handleToggle} containerElement={<Link to="/authority"/>}
                          leftIcon={<ActionAccountBalance/>}>Authority</MenuItem>
                <MenuItem style={styles.menuItem} onClick={this.handleToggle} containerElement={<Link to="/emergency"/>}
                          leftIcon={<MapsLocalHospital/>}>Emergency
                    service</MenuItem>
                <MenuItem style={styles.menuItem} onClick={this.handleToggle}
                          containerElement={<Link to="/manufacturer"/>}
                          leftIcon={<ActionBuild/>}>Manufacturers</MenuItem>
            </Drawer>
        </div>;
    }
}
