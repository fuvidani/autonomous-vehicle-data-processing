import {fetchStream} from "../util/RequestHandler";
import * as ActionTypes from "../actions/ActionTypes";

const fetchAccidentReportsEpic = action$ =>
    action$.ofType(ActionTypes.FETCH_ACCIDENT_REPORTS)
        .mergeMap(() =>
            fetchStream('/statistics/accidents')
                .map(response => ({type: ActionTypes.ACCIDENT_REPORT_FETCHED, payload: JSON.parse(response)}))
                .takeUntil(action$.ofType(ActionTypes.CANCEL_ACCIDENT_REPORTS))
        );

const clearAccidentReportsEpic = action$ =>
    action$.ofType(ActionTypes.CANCEL_ACCIDENT_REPORTS)
        .mapTo({type: ActionTypes.CLEAR_ACCIDENT_REPORTS});

export {fetchAccidentReportsEpic, clearAccidentReportsEpic}
