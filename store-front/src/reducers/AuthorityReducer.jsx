import * as ActionTypes from "../actions/ActionTypes";

export default function reducer(state = {
    accidentReports: []
}, action) {

    switch (action.type) {
        case ActionTypes.CLEAR_ACCIDENT_REPORTS: {
            state.accidentReports = [];

            return {
                ...state
            };
        }

        case ActionTypes.ACCIDENT_REPORT_FETCHED:
            return {
                ...state,
                accidentReports: state.accidentReports.concat(action.payload)
            };

        default:
            return state;
    }
}
