package com.redessi;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

public class PingServer {
    private static final double LOSS_RATE = 0.3;
    private static final int AVERAGE_DELAY = 100; // milliseconds
    private static DatagramSocket socket;

    public static void main (String [] args) throws Exception {
        while (true) {
            if (args.length != 1) {
                System.out.println("Argumentos requiridos: porta");
                return;
            }
            int port = Integer.parseInt(args[0]);
            Random random = new Random();

            socket = new DatagramSocket(port);

            byte [] buffer = new byte[1024];

            DatagramPacket request = new DatagramPacket(buffer, buffer.length);

            socket.receive(request);

            printData(request);

            if (random.nextDouble() < LOSS_RATE) {
                System.out.println("Reposta nao enviada!");
                continue;
            }

            int slp = (int) (random.nextDouble() * 2 * AVERAGE_DELAY);
            Thread.sleep(slp);

            InetAddress clientHost = request.getAddress();
            int clientPort = request.getPort();
            byte [] buff = request.getData();
            DatagramPacket reply = new DatagramPacket(buff, buff.length, clientHost, clientPort);
            socket.send(reply);

            System.out.println("Resposta enviada!");
        }
    }

    private static void printData (DatagramPacket req) throws Exception {
        byte [] buff = req.getData();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(buff);

        InputStreamReader streamReader = new InputStreamReader(inputStream);

        BufferedReader reader = new BufferedReader(streamReader);

        String line = reader.readLine();

        System.out.println("Recebido de " + req.getAddress().getHostAddress() + " : " + new String(line));
    }
}

