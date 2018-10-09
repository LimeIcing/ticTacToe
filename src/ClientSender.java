import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClientSender implements Runnable {
    private DatagramSocket sendingSocket;
    private InetAddress serverIP;
    private int serverPort;
    private String username;

    public ClientSender(DatagramSocket sendingSocket, InetAddress serverIP, int serverPort, String username) {
        this.sendingSocket = sendingSocket;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.username = username;
    }

    public void run() {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        DatagramPacket sendingPacket;
        byte[] sendData;
        String message;
        boolean shouldRun = true;

        System.out.println(Client.colourise("The server is ready to receive your message!", "green"));
        while (shouldRun) {
            try {
                message = input.readLine();

                if (message.length() > 250) {
                    System.out.println(Client.colourise("Message too long", "yellow"));
                } else {
                    if (message.equalsIgnoreCase("quit")) {
                        shouldRun = false;
                        message = message.toUpperCase();
                    } else {
                        message = "DATA " + username + ": " + message;
                    }

                    sendData = message.getBytes();
                    sendingPacket = new DatagramPacket(sendData, sendData.length, serverIP, serverPort);
                    sendingSocket.send(sendingPacket);
                }
            }  catch (IOException iOE) {
                System.out.println(Client.colourise("Failed to send the message", "red"));
            }
        }
        System.exit(0);
    }
}
