import {GoogleMap, Marker, withGoogleMap, withScriptjs} from "react-google-maps"
import React from "react";
import {withProps, compose} from "recompose";
import {ApiKeys} from "../../config/ApiKeys";

const icon = require('../../../images/car.png');

const MapComponent = compose(
    withProps({
        googleMapURL: "https://maps.googleapis.com/maps/api/js?key=" + ApiKeys.GOOGLE_MAPS_API_KEY + "&v=3.exp&libraries=geometry,drawing,places",
        loadingElement: <div style={{height: `100%`}}/>,
        containerElement: <div style={{height: `400px`, margin: `10px 0`}}/>,
        mapElement: <div style={{height: `100%`}}/>,
    }),
    withScriptjs,
    withGoogleMap
)((props) =>
    <GoogleMap
        defaultZoom={11}
        defaultCenter={Object.getOwnPropertyNames(props.vehicleTrackingInformation).length === 0 ? {lat: 48.210033, lng: 16.363449} : {lat: props.vehicleTrackingInformation[Object.keys(props.vehicleTrackingInformation)[0]].location.lat, lng: props.vehicleTrackingInformation[Object.keys(props.vehicleTrackingInformation)[0]].location.lon}}
    >
        {Object.keys(props.vehicleTrackingInformation).map(function (key) {
            return <Marker key={key} position={{lat: props.vehicleTrackingInformation[key].location.lat, lng: props.vehicleTrackingInformation[key].location.lon}} icon={{url: icon}}/>;
        })}
    </GoogleMap>
);

export default MapComponent;