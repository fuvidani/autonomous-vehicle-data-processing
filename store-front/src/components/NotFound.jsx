import React from 'react';
import GeneralContainer from "../containers/GeneralContainer";

const NotFound = () =>
    <div>
        <GeneralContainer/>
        <div className="text-center" style={{margin: '20% auto'}}>
            <h3>404 page not found</h3>
            <p>We are sorry but the page you are looking for does not exist.</p>
        </div>
    </div>;

export default NotFound;
