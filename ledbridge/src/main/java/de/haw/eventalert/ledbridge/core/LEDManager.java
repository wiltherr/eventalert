package de.haw.eventalert.ledbridge.core;

import de.haw.eventalert.ledbridge.connector.LEDControllerConnector;
import de.haw.eventalert.ledbridge.entity.event.ColorLEDEvent;
import de.haw.eventalert.ledbridge.entity.event.LEDEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LEDManager {
    private static final int START_RETRIES = 10;
    private static final long RETRY_WAIT = 5000; //ms
    private static final Logger LOG = LoggerFactory.getLogger(LEDManager.class);
    private static LEDManager instance;
    private LEDEventConsumer ledEventConsumer;
    private Map<Long, LEDControllerConnector> controllerConnectors;

    private LEDManager() {
        controllerConnectors = new HashMap<>();
        ledEventConsumer = new LEDEventConsumer(this::processLEDEvent);
    }

    public static LEDManager getInstance() {
        if (instance == null)
            instance = new LEDManager();
        return instance;
    }

    private void processLEDEvent(LEDEvent ledEvent) {
        LOG.debug("received ledEvent from type={}", ledEvent.getType());
        //check if the target connector is present
        LEDControllerConnector targetConnector = controllerConnectors.get(ledEvent.getTargetLEDId());
        if (targetConnector == null) {
            LOG.error("connector with id={} is not available", ledEvent.getTargetLEDId());
            return;
        }
        //check type (subclass) of LEDEvent
        if (ledEvent instanceof ColorLEDEvent) {
            processColorLEDEvent((ColorLEDEvent) ledEvent, targetConnector);
        } else {
            LOG.error("ledEvent type={} is unknown", ledEvent.getType());
        }

    }

    private void processColorLEDEvent(ColorLEDEvent ledEvent, LEDControllerConnector targetConnector) {
        if (targetConnector instanceof LEDControllerConnector.ColorLEDEventSupport) {
            LOG.debug("delegate {} to connector {} (id={})", ledEvent.getType(), targetConnector.getClass(), ledEvent.getTargetLEDId());
            ((LEDControllerConnector.ColorLEDEventSupport) targetConnector).setColor(ledEvent.getColor());
        } else {
            LOG.error("ledConnector with id={} does not support ledEvent with type={}", ledEvent.getTargetLEDId(), ledEvent.getType());
        }
    }

    public void registerControllerConnector(LEDControllerConnector ledConnector, Long connectorId) {
        if (controllerConnectors.get(connectorId) != null) {
            throw new IllegalArgumentException("connector id " + connectorId + " is already in use!");
        }
        controllerConnectors.put(connectorId, ledConnector);
    }

    public void start() {
        LOG.info("starting connectors");
        startConnectors();
        LOG.info("starting consumer");
        ledEventConsumer.run();
        LOG.info("LEDManager started");
    }

    private void startConnectors() {
        controllerConnectors.entrySet().parallelStream().forEach(entry -> {
            Long id = entry.getKey();
            LEDControllerConnector ledControllerConnector = entry.getValue();
            LOG.debug("starting connector with id={}", id);
            boolean isConnected = false;
            for (int retryAttempt = 0; retryAttempt < START_RETRIES && !isConnected; retryAttempt++) {
                isConnected = ledControllerConnector.open();
                if (isConnected) {
                    break;
                } else {
                    //Wait for the next retry
                    LOG.warn("connector with id={} could not be started (retryAttempt {})! Waiting {}ms for next attempt.", id, retryAttempt, RETRY_WAIT);
                    try {
                        TimeUnit.MILLISECONDS.sleep(RETRY_WAIT);
                    } catch (InterruptedException e) {
                        LOG.error("interrupted while waiting connector with id={} to start", id, e);
                    }
                }
            }
            if (isConnected) {
                LOG.info("connector with id={} was started", id);
            } else {
                LOG.error("connector with id={} could not be started! All retry attempts were not successful!", id);
            }
        });
    }
}
