import {combineEpics} from 'redux-observable';
import {Observable} from 'rxjs'
import {fromEvent} from 'rxjs/observable/fromEvent';

const fromEventSource = url => {
  return new Observable(observer => {
    const source = new EventSource(url);
    const message$ = fromEvent(source, 'random');
    const subscription = message$.subscribe(observer);

    return () => {
      subscription.unsubscribe();
      source.close();
    };
  });
};

const fetchServerEventsEpic = action$ =>
  action$.ofType('FETCH_SERVER_EVENTS')
    .mergeMap(() =>
      fromEventSource('http://localhost:8080/randomNumbers')
        .map(response => ({type: 'RANDOM_NUMBER_EVENT', payload: response.data}))
        .takeUntil(action$.ofType('CANCEL_SERVER_EVENTS'))
    );

export default combineEpics(
  fetchServerEventsEpic
);