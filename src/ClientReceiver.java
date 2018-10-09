import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ClientReceiver implements Runnable {
    private DatagramSocket receivingSocket;

    public ClientReceiver(DatagramSocket receivingSocket) {
        this.receivingSocket = receivingSocket;
    }

    public void run() {
        DatagramPacket receivingPacket;
        byte[] receiveData = new byte[1024];
        String message;
        int userlistLength = 0;
        boolean shouldRun = true;

        while (shouldRun) {
            try {
                receivingPacket = new DatagramPacket(receiveData, receiveData.length);
                receivingSocket.receive(receivingPacket);
                message = new String(receivingPacket.getData(), 0, receivingPacket.getLength());

                if (message.startsWith("J_ER ")) {
                    System.out.println(Client.colourise("Error code " + message.substring(5), "red"));
                }

                else if (message.startsWith("DATA ")) {
                    int usernameStop = message.indexOf(":");
                    System.out.println(Client.colourise(message.substring(5, usernameStop), "blue") +
                            message.substring(usernameStop));
                }

                else if (message.startsWith("LIST ")) {
                    if (userlistLength > 0) {
                        if (userlistLength < message.length()) {
                            System.out.println(Client.colourise("A user joined the server!", "green"));
                        } else {
                            System.out.println(Client.colourise("A user left the server!", "yellow"));
                        }
                    }
                    userlistLength = message.length();
                    message = message.substring(5).replaceAll(" ", ", ");
                    System.out.println(Client.colourise("Online users: ", "green") +
                            Client.colourise(message, "blue"));
                }
            } catch (IOException iOE) {
                iOE.printStackTrace();
            }
        }
    }
}