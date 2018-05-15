export default function reducer(state = {
    accidentReports: []
}, action) {

    switch (action.type) {
        case 'CLEAR_ACCIDENT_REPORTS': {
            state.accidentReports = [];

            return {
                ...state
            };
        }

        case 'ACCIDENT_REPORT_FETCHED':
            return {
                ...state,
                accidentReports: state.accidentReports.concat(action.payload)
            };

        default:
            return state;
    }
}
