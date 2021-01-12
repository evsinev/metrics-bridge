package com.payneteasy.metrics.bridge.agent.app;

import static com.payneteasy.startup.parameters.StartupParametersFactory.getStartupParameters;

public class MetricsAgentApplication {

    public static void main(String[] args) {
        if(System.getProperty("java.util.logging.SimpleFormatter.format") == null) {
            System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        }

        IAgentConfig startupConfig = getStartupParameters(IAgentConfig.class);

    }
}
