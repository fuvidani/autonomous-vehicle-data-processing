import * as ActionTypes from "../actions/ActionTypes";

export default function reducer(state = {
    accidentReports: {}
}, action) {

    switch (action.type) {
        case ActionTypes.CLEAR_ACCIDENT_REPORTS: {
            state.accidentReports = {};

            return {
                ...state
            };
        }

        case ActionTypes.ACCIDENT_REPORT_FETCHED:
            let newAccidentReports = Object.assign({}, state.accidentReports);
            newAccidentReports[action.payload.id] = action.payload;

            return {
                ...state,
                accidentReports: newAccidentReports
            };

        default:
            return state;
    }
}
