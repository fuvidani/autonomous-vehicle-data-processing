export default function reducer(state = {
    accidentReports: [
        {
            id: "tDbdvAqCxpCQqqYXaRTC76Bm",
            accidentId: "QgYZY8ntPurzGDhxxAcVYbYb",
            vehicleMetaData: {
                identificationNumber: "9KXfzswrhxzKEuX9uiAWcsaw",
                model: "1995 Acura Integra"
            },
            location: {
                lat: 48.172450,
                lon: 16.376432
            },
            passengers: 4,
            emergencyResponseInMillis: 123456,
            durationOfSiteClearingInMillis: 654321
        }
    ]
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
