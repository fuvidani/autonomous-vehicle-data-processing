import {fromEvent} from "rxjs/observable/fromEvent";
import {Observable} from "rxjs/Rx";
import {serverConfig} from "../config/serverConfig";
import {ajax} from 'rxjs/observable/dom/ajax';

const fromEventSource = (mapping, event) => {
    const url = serverConfig.url + mapping;

    return new Observable(observer => {
        const source = new EventSource(url);
        const message$ = fromEvent(source, event);
        const subscription = message$.subscribe(observer);

        return () => {
            subscription.unsubscribe();
            source.close();
        };
    });
};

const postRequest = (mapping, payload) => {
    const url = serverConfig.url + mapping;

    return ajax.post(url, payload, {'Content-Type': 'application/json'});
};

export {fromEventSource, postRequest}
