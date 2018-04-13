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

const fetchServerEventsEpic = action$ =>
  action$.ofType('FETCH_SERVER_EVENTS')
    .mergeMap(() =>
      fromEventSource('http://localhost:8080/randomNumbers', 'random')
        .map(response => ({type: 'RANDOM_NUMBER_EVENT', payload: response.data}))
        .takeUntil(action$.ofType('CANCEL_SERVER_EVENTS'))
    );

const fetchNotificationEpic = action$ =>
  action$.ofType('FETCH_NOTIFICATIONS')
    .mergeMap(() =>
      fromEventSource('http://localhost:7000/notifications/vehicle1', 'message')
        .map(response => ({type: 'NOTIFICATION_EVENT', payload: JSON.parse(response.data).message}))
        .takeUntil(action$.ofType('CANCEL_NOTIFICATIONS'))
    );

export default combineEpics(
  fetchServerEventsEpic, fetchNotificationEpic
);