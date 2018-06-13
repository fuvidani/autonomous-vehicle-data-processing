import {Observable} from "rxjs/Rx";
import {serverConfig} from "../config/serverConfig";
import {ajax} from 'rxjs/observable/dom/ajax';
import ReconnectingEventSource from "reconnecting-eventsource";

const fetchInfiniteStream = (mapping) => {
    const url = serverConfig.gatewayUrl + mapping;

    return new Observable(observer => {
        const source = new ReconnectingEventSource(url);

        const onError = event => {
            if (event.eventPhase === EventSource.CLOSED) {
                console.log("event source closed, reconnecting..")
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
            console.log("closing source (" + url + ")");
            source.close();
        };
    });
};

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
            console.log("closing source (" + url + ")");
            source.close();
        };
    });
};

const postRequest = (mapping, payload) => {
    const url = serverConfig.gatewayUrl + mapping;

    return ajax.post(url, payload, {'Content-Type': 'application/json'});
};

export {fetchStream, postRequest, fetchInfiniteStream}
