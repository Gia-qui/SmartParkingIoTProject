package org;

import org.eclipse.paho.client.mqttv3.MqttClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;



public class App {
    private static CoAPClient coapClient;
    public static void liftBarrier() {
        // Lifts or lower the barrier gate for 3 seconds then it puts it back to its original status
        coapClient.barrierPutRequest(coapClient.getGateBarrierUri(), "Change barrier status");
        try {
            Thread.sleep(3000); // 3000 milliseconds = 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        coapClient.barrierPutRequest(coapClient.getGateBarrierUri(), "Change barrier status");
    }

    public static void spawnFakeCar(int carCardinality) {
        if(carCardinality == 4) return;
        carCardinality++;
        coapClient.monitorPutRequest(coapClient.getMonitorUri(), carCardinality);
    }

    public static void despawnCar(int carCardinality) {
        if(carCardinality == 0) return;
        carCardinality--;
        coapClient.monitorPutRequest(coapClient.getMonitorUri(), carCardinality);
    }

    public static void main(String args[]) throws SocketException, InterruptedException {
        // Istantiate te MQTT client
        CollectorMqttClient client = new CollectorMqttClient();
        coapClient = new CoAPClient();

        // Main menu
        System.out.println("\n--------SMART PARKING APP--------");
        System.out.println("!exit: close the application");
        System.out.println("!help: list all commands"); //command
        System.out.println("!checkCarPresence: see if a car is in front of the barrier gate");
        System.out.println("!checkOccupancy: check the current occupancy of the parking slot");
        System.out.println("!liftGate: lift the barrier gate just because you can");
        System.out.println("!spawnFakeCar: hack the monitor to temporarily add a fake car");
        System.out.println("!despawnCar: hack the monitor to temporarily remove a car");
        System.out.println("\n");

        // Reads the inputs from the terminal
        String command = "";
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            try {
                command = input.readLine();

                if (command.equals("!exit"))
                {
                    System.exit(1);
                } else if (command.equals("!help"))
                {
                    System.out.println("\n--------SMART PARKING APP--------");
                    System.out.println("!exit: close the application");
                    System.out.println("!help: list all commands"); //command
                    System.out.println("!checkCarPresence: see if a car is in front of the barrier gate");
                    System.out.println("!checkOccupancy: check the current occupancy of the parking slot");
                    System.out.println("!liftGate: lift the barrier gate just because you can");
                    System.out.println("!spawnFakeCar: hack the monitor to temporarily add a fake car");
                    System.out.println("!despawnCar: hack the monitor to temporarily remove a car");
                    System.out.println("\n");

                } else if (command.equals("!checkCarPresence"))
                {
                    System.out.println(client.getCarStatus().equals("true")?
                            "A car is in front of the barrier gate" :
                            "No car detected");

                } else if (command.equals("!checkOccupancy"))
                {
                    System.out.format("The total occupancy of the parking slot is: %d/4 cars", client.getCurrentOccupancy());

                } else if (command.equals("!liftGate"))
                {
                    liftBarrier();

                } else if (command.equals("!spawnFakeCar"))
                {
                    spawnFakeCar(client.getCurrentOccupancy());
                } else if (command.equals("!despawnCar"))
                {
                    despawnCar(client.getCurrentOccupancy());
                } else {
                    throw new IOException();
                }

                System.out.println("\n");

            } catch (IOException e) {
                System.out.println("Command not found, please retry!");
            }
        }
    }
}

