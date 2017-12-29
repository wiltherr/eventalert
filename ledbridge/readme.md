LEDBridge can be executed with running `RunLEDBridge.java` in `de.haw.eventalert.ledbridge` package.

Please ensure that Kafka is started before. 

Additionally the Arduino controller have to be connected and should runs the `arduino-sketch` (can be found 
in `/arduino-controller-sketch`). Alternatively you can change the `ControllerConnector` to 
`LEDBridgeTestControllerConnector` in the `RunLEDBridge.java`
