import {combineEpics} from 'redux-observable';
import {fetchAccidentReportsEpic, clearAccidentReportsEpic} from './AuthorityEpics';


export default combineEpics(
    fetchAccidentReportsEpic, clearAccidentReportsEpic
);
