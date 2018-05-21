import {serverConf} from "./localServerConfig";

export const serverConfig = {
    gatewayUrl: serverConf.gatewayUrl + ":" + serverConf.gatewayPort,
    dataSimulatorUrl: serverConf.dataSimulatorUrl + ":" + serverConf.dataSimulatorPort,
    logging: serverConf.logging,
};
