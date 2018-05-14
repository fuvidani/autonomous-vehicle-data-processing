import {fromEventSource} from "../util/RequestHandler";

const fetchAccidentReportsEpic = action$ =>
    action$.ofType('FETCH_ACCIDENT_REPORTS')
        .mergeMap(() =>
            fromEventSource('/statistics/accidents', 'message')
                .map(response => ({type: 'ACCIDENT_REPORT_FETCHED', payload: JSON.parse(response.data)}))
                .takeUntil(action$.ofType('CANCEL_ACCIDENT_REPORTS'))
        );

const clearAccidentReportsEpic = action$ =>
    action$.ofType('CANCEL_ACCIDENT_REPORTS')
        .mapTo({ type: 'CLEAR_ACCIDENT_REPORTS' });

export {fetchAccidentReportsEpic, clearAccidentReportsEpic}
