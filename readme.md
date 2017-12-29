Project structure
    <ul>/eventalert:  EventAlert Application
        <ul>
            <li>/core: EventAlertCore with all AlertEvent producer jobs and the consumer job
            <li>/sources: Custom Sources for EventAlert
            <ul>
                <li>/email-flink-source: Source for email events
                <li>/telegram-flink-source: Source for telegram events
                <li>/twitter-fliink-source: Source for twitter events
            </ul>
         </ul>
    </ul>
    <ul>/ledbridge: Bridge to the LED display</ul>
<p>
Project and all modules can be build with `mvn clean install`, maybe you add `-Dmaven.test.skip=true` to get the build-process work, 
because some sources needs a previous configuration for testing.    

====== <p>This project can be also found on GitHub under https://github.com/wiltherr/eventalert