import {applyMiddleware, createStore} from "redux";
import {serverConfig} from "../config/serverConfig";
import {logger} from "redux-logger";
import thunk from "redux-thunk";
import promise from "redux-promise-middleware";
import {createEpicMiddleware} from "redux-observable"
import reducers from "../reducers/reducers";
import {rootEpic} from "../epics/epics"

const epicMiddleware = createEpicMiddleware(rootEpic);

let middleware = null;
if (serverConfig.logging) {
    middleware = applyMiddleware(promise(), thunk, epicMiddleware, logger);
} else {
    middleware = applyMiddleware(promise(), thunk, epicMiddleware);
}

export default createStore(reducers, middleware);
