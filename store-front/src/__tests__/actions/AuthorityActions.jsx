import {cancelAccidentReports, fetchAccidentReports} from "../../actions/AuthorityActions";
import * as ActionTypes from "../../actions/ActionTypes";

describe('authorityActions', () => {
    it('should fire FETCH_ACCIDENT_REPORTS action', () => {
        const fetchAccidentReportsAction = {
            type: ActionTypes.FETCH_ACCIDENT_REPORTS
        };
        expect(fetchAccidentReports()).toEqual(fetchAccidentReportsAction)
    });

    it('should fire CANCEL_ACCIDENT_REPORTS action', () => {
        const cancelAccidentReportsAction = {
            type: ActionTypes.CANCEL_ACCIDENT_REPORTS
        };
        expect(cancelAccidentReports()).toEqual(cancelAccidentReportsAction)
    });
});
