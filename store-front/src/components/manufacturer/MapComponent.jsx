import {GoogleMap, Marker, withGoogleMap, withScriptjs} from "react-google-maps"
import React from "react";
import {withProps, compose} from "recompose";
import {ApiKeys} from "../../config/ApiKeys";

const MapComponent = compose(
    withProps({
        googleMapURL: "https://maps.googleapis.com/maps/api/js?key=" + ApiKeys.GOOGLE_MAPS_API_KEY + "&v=3.exp&libraries=geometry,drawing,places",
        loadingElement: <div style={{ height: `100%` }} />,
        containerElement: <div style={{ height: `400px`, margin: `10px 0`}} />,
        mapElement: <div style={{ height: `100%` }} />,
    }),
    withScriptjs,
    withGoogleMap
)((props) =>
    <GoogleMap
        defaultZoom={8}
        defaultCenter={{ lat: -34.397, lng: 150.644 }}
    >
        <Marker position={{ lat: -34.397, lng: 150.644 }} />
    </GoogleMap>
);

export default MapComponent;
