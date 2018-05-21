import {Observable} from "rxjs/Rx";
import {serverConfig} from "../config/serverConfig";
import {ajax} from 'rxjs/observable/dom/ajax';

const fetchStream = (mapping) => {
    const url = serverConfig.gatewayUrl + mapping;

    return new Observable(observer => {
        const source = new EventSource(url);

        const onError = event => {
            if (event.eventPhase === EventSource.CLOSED) {
                observer.complete();
            } else {
                observer.error(event);
            }
        };

        const onMessage = event => {
            observer.next(event.data);
        };

        source.addEventListener('error', onError, false);
        source.addEventListener('message', onMessage, false);

        return () => {
            source.removeEventListener('error', onError, false);
            source.removeEventListener('message', onMessage, false);
            source.close();
        };
    });
};

const postRequest = (mapping, payload) => {
    const url = serverConfig.dataSimulatorUrl + mapping;

    return ajax.post(url, payload, {'Content-Type': 'application/json'});
};

export {fetchStream, postRequest}
