import React from "react";
import {
    StaticGoogleMap,
    Marker
} from "react-static-google-map";
import {ApiKeys} from "../config/ApiKeys";

const StaticMapComponent = (props) => (
    <StaticGoogleMap size="640x400" apiKey={ApiKeys.GOOGLE_MAPS_API_KEY}>
        <Marker.Group>
            {props.markers.map((marker, i) => <Marker key={i} location={marker.location} />)}
        </Marker.Group>
    </StaticGoogleMap>
);

export default StaticMapComponent;
