export default function reducer(state = {
    accidentReports: []
}, action) {

    switch (action.type) {
        case 'ACCIDENT_REPORT_FETCHED':
            return {
                ...state,
                accidentReports: state.accidentReports.concat(action.payload)
            };

        default:
            return state;
    }
}
