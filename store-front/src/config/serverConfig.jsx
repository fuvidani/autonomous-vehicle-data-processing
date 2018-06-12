import {serverConf} from "./prodServerConfig";

export const serverConfig = {
    gatewayUrl: serverConf.gatewayUrl + ":" + serverConf.gatewayPort,
    dataSimulatorUrl: serverConf.dataSimulatorUrl + ":" + serverConf.dataSimulatorPort,
    logging: serverConf.logging,
};
