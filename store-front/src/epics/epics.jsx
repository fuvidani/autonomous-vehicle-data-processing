import {combineEpics} from 'redux-observable';
import {Observable} from 'rxjs'
import {fromEvent} from 'rxjs/observable/fromEvent';

const fromEventSource = (url, event) => {
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

const fetchAccidentReportsEpic = action$ =>
    action$.ofType('FETCH_ACCIDENT_REPORTS')
        .mergeMap(() =>
            fromEventSource('http://localhost:8000/statistics/accidents', 'message')
                .map(response => ({type: 'ACCIDENT_REPORT_FETCHED', payload: JSON.parse(response.data)}))
                .takeUntil(action$.ofType('CANCEL_ACCIDENT_REPORTS'))
        );

export default combineEpics(
    fetchAccidentReportsEpic
);
