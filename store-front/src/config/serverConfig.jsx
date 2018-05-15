import {serverConf} from "./localServerConfig";

export const serverConfig = {
    url: serverConf.url + ":" + serverConf.port,
    logging: serverConf.logging,
};
