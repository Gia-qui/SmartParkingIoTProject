package org;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.eclipse.californium.core.CoapClient;

public class CoAPClient {
    private String gateBarrierUri;
    private String monitorUri;

    public CoAPClient() {
        readActuatorURIs("actuatorURIs.txt");
    }

    private void readActuatorURIs(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            gateBarrierUri = reader.readLine();
            monitorUri = reader.readLine();
        } catch (IOException e) {
            System.out.println("Failed to read actuator URIs from file");
            e.printStackTrace();
        }
    }

    public String getGateBarrierUri() {
        return ("coap://["+gateBarrierUri+"]/auto_gate");
    }

    public String getMonitorUri() {
        return ("coap://["+monitorUri+"]/monitor");
    }

    public void barrierPutRequest(String uri, String payload) {
        CoapClient coapClient = new CoapClient(uri);
        coapClient.put(payload, 0); // Send PUT request
    }

    public void monitorPutRequest(String uri, int payload) {
        CoapClient coapClient = new CoapClient(uri);
        coapClient.put(String.valueOf(payload), 0); // Send PUT request
    }
}
