package org;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class CollectorMqttClient implements MqttCallback{
    private final String broker = "tcp://127.0.0.1:1883";
    private final String clientId = "JavaApp";
    private final String distSubTopic = "distance";
    private final String occSubTopic = "occupancy";
    private MqttClient mqttClient = null;
    private String carStatus = null;
    private boolean oldCarStatus = false;
    private int currentOccupancy;
    private int oldOccupancy = 0;
    private CoAPClient coapClient;

    //-----------------------------------------------------------------------*/

    public CollectorMqttClient() throws InterruptedException {
        coapClient = new CoAPClient();
        do {
            try {
                this.mqttClient = new MqttClient(this.broker,this.clientId);
                System.out.println("Connecting to broker: "+broker);

                this.mqttClient.setCallback( this );
                this.mqttClient.connect();

                this.mqttClient.subscribe(this.distSubTopic);
                System.out.println("Subscribed to topic: "+this.distSubTopic);

                this.mqttClient.subscribe(this.occSubTopic);
                System.out.println("Subscribed to topic: "+this.occSubTopic);
            }catch(MqttException me) {
                System.out.println("I could not connect, Retrying ...");
            }
        }while(!this.mqttClient.isConnected());
    }


    public void connectionLost(Throwable cause) {
        System.out.println("Connection is broken: " + cause);
        int timeWindow = 3000;
        while (!this.mqttClient.isConnected()) {
            try {
                System.out.println("Trying to reconnect in " + timeWindow/1000 + " seconds.");
                Thread.sleep(timeWindow);
                System.out.println("Reconnecting ...");
                timeWindow *= 2;
                this.mqttClient.connect();

                this.mqttClient.subscribe(this.distSubTopic);
                this.mqttClient.subscribe(this.occSubTopic);
                System.out.println("Connection is restored");
            }catch(MqttException me) {
                System.out.println("I could not connect");
            } catch (InterruptedException e) {
                System.out.println("I could not connect");
            }
        }
    }

    //-------------------------------------------------------------------------------------------------

    public void messageArrived(String topic, MqttMessage message) throws Exception {
        byte[] payload = message.getPayload();
        try {
            JSONObject sensorMessage = (JSONObject) JSONValue.parseWithException(new String(payload));
            //System.out.println("THIS IS THE TOPIC: " + topic);
            if(topic.equals(this.distSubTopic))
            {
                if (sensorMessage.containsKey("node") && sensorMessage.containsKey("isCarClose")) {
                    // Parsing
                    this.carStatus = sensorMessage.get("isCarClose").toString();
                    int node = Integer.parseInt(sensorMessage.get("node").toString());
                    // System.out.println("DISTANCE RECEIVE: " + this.carStatus + node);

                    // Put data in DB after conversion from string to boolean
                    if(carStatus.equals("true") && oldCarStatus == false){
                        // System.out.println("A car is close, barrier gate must be lifted");
                        oldCarStatus = true;
                        SmartParkingDB.insertCarStatus(node, oldCarStatus);
                        // Send data to the actuator
                        coapClient.barrierPutRequest(coapClient.getGateBarrierUri(), "Change barrier status");
                        return;
                    } else if(carStatus.equals("false") && oldCarStatus == true){
                        // System.out.println("The car is gone, barrier gate must be lowered");
                        oldCarStatus = false;
                        SmartParkingDB.insertCarStatus(node, oldCarStatus);
                        // Send data to the actuator
                        coapClient.barrierPutRequest(coapClient.getGateBarrierUri(), "Change barrier status");
                        return;
                    }
                    SmartParkingDB.insertCarStatus(node, oldCarStatus);
                } else {
                    System.out.println("Sensor is speaking the language of the gods");
                }
            // --------------------------------------------------------------------------------
            } else if (topic.equals(this.occSubTopic)) {
                if (sensorMessage.containsKey("node") && sensorMessage.containsKey("occupancy")) {
                    // Parsing
                    this.currentOccupancy = Integer.parseInt(sensorMessage.get("occupancy").toString());
                    int node = Integer.parseInt(sensorMessage.get("node").toString());
                    // System.out.println("OCCUPANCY RECEIVE: " + this.currentOccupancy + node);
                    // Put data in DB
                    SmartParkingDB.insertOccupancy(node, this.currentOccupancy);
                    // Send data to the actuator
                    if(currentOccupancy != oldOccupancy) {
                        coapClient.monitorPutRequest(coapClient.getMonitorUri(), this.currentOccupancy);
                        oldOccupancy = currentOccupancy;
                    }
                    // System.out.println("DONE WITH occupancy");

                } else {
                    System.out.println("Sensor is speaking the language of the gods");
                }

            // --------------------------------------------------------------------------------
            } else {
                System.out.println(String.format("Unknown topic: [%s] %s", topic, new String(payload)));
            }
        } catch (ParseException e) {
            System.out.println(String.format("Received badly formatted message: [%s] %s", topic, new String(payload)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //--------------------------------------------------------------------------------------------------------

    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("I love java -.-\n");
    }

    public String getCarStatus() {
        return this.carStatus;
    }

    public int getCurrentOccupancy() {
        return this.currentOccupancy;
    }
}