import AuthorityReducer from "../../reducers/AuthorityReducer";
import * as ActionTypes from "../../actions/ActionTypes";

const reportObject = {
    id: "tDbdvAqCxpCQqqYXaRTC76Bm",
    timestampOfAccident: "123456789",
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
};

describe('authority reducer', () => {
    it('should return the initial state', () => {
        expect(AuthorityReducer(undefined, {})).toEqual( // eslint-disable-line no-undefined
            {
                accidentReports: {}
            }
        )
    });

    it('should handle ACCIDENT_REPORT_FETCHED', () => {
        expect(
            AuthorityReducer({accidentReports: []}, {
                type: ActionTypes.ACCIDENT_REPORT_FETCHED,
                payload: reportObject
            })
        ).toEqual(
            {
                accidentReports: {"tDbdvAqCxpCQqqYXaRTC76Bm": reportObject}
            }
        )
    });

    it('should handle CLEAR_ACCIDENT_REPORTS', () => {
        expect(
            AuthorityReducer({accidentReports: [reportObject]}, {
                type: ActionTypes.CLEAR_ACCIDENT_REPORTS
            })
        ).toEqual(
            {
                accidentReports: {}
            }
        )
    })
});
