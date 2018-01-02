package de.haw.eventalert.core.global;

import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.StandaloneClusterClient;
import org.apache.flink.configuration.Configuration;

/**
 * Created by Tim on 01.01.2018.
 */
public class App {
    public static void main(String[] args) throws Exception {
        ClusterClient clusterClient = new StandaloneClusterClient(new Configuration());
        //clusterClient.run(new JobWithJars())
        //URL url = URI.create("target/")
        //clusterClient.run(new JobWithJars(null, new URL("target/core.jar")))
//        Program
    }
}
