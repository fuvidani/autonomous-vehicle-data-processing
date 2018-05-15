import {fromEvent} from "rxjs/observable/fromEvent";
import {Observable} from "rxjs/Rx";
import {serverConfig} from "../config/serverConfig";

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

export {fromEventSource}
