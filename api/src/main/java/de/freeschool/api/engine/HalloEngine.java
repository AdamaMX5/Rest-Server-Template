package de.freeschool.api.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Demurrage engine that processes the collection of interest and the distribution of the basic income
 */
@Component
public class HalloEngine {
    private static final Logger logger = LoggerFactory.getLogger(HalloEngine.class);


    @Scheduled(cron = "0 0/1 * * * *") // execute every 1 minutes
    public void execute() {
        logger.info("Hallo Welt sagt der TestCronJob");
    }


}
