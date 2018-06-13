import {serverConf} from "./prodServerConfig";

export const serverConfig = {
    gatewayUrl: serverConf.gatewayUrl + ":" + serverConf.gatewayPort,
    logging: serverConf.logging
};
