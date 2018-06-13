import * as ActionTypes from "./ActionTypes";

const fetchAccidentReports = () => ({type: ActionTypes.FETCH_ACCIDENT_REPORTS});
const cancelAccidentReports = () => ({type: ActionTypes.CANCEL_ACCIDENT_REPORTS});

export {fetchAccidentReports, cancelAccidentReports}
