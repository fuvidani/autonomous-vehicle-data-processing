import {postRequest} from "../util/RequestHandler";
import * as ActionTypes from "../actions/ActionTypes";

const postRestartSimulationEpic = action$ =>
    action$.ofType(ActionTypes.RESTART_SIMULATION)
        .mergeMap(() =>
            postRequest('/datasimulation/reset', {})
                .map(() => console.log("simulation restarted"))
        )
;

export {postRestartSimulationEpic}
