import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Server {
    private static User[] users = new User[2];
    private static Slot[][] board = {
            {Slot.EMPTY, Slot.EMPTY, Slot.EMPTY},
            {Slot.EMPTY, Slot.EMPTY, Slot.EMPTY},
            {Slot.EMPTY, Slot.EMPTY, Slot.EMPTY}};
    private static DatagramPacket receivingPacket;
    private static String message, username = "";

    public static void main(String[] args) throws Exception {
        DatagramSocket receivingSocket = new DatagramSocket(6950);
        byte[] receiveData = new byte[1024];
        //Thread Timeout = new Thread(new TimeoutRemover());
        //Timeout.start();

        for (int i = 0; i < 3; i++) {
            System.out.print('|');
            for (int j = 0; j < 3; j++) {
                System.out.print(board[i][j].getValue());
            }
            System.out.println('|');
        }

        System.out.println("Server ready");
        while (true) {
            receivingPacket = new DatagramPacket(receiveData, receiveData.length);
            receivingSocket.receive(receivingPacket);
            message = new String(receivingPacket.getData(), 0, receivingPacket.getLength());

            if (message.startsWith("JOIN ")) {
                int stop = message.indexOf(",");
                int freeSpot = 0;
                boolean userExists = false;
                username = message.substring(5, stop);

                for (User user:users) {
                    freeSpot = 0;

                    if (user == null) {
                        break;
                    } else if (username.equals(user.getUsername()) || receivingPacket.getAddress().equals(user.getIP())) {
                        userExists = true;
                    }
                    freeSpot++;
                }

                if (userExists) {
                    message = "J_ER 401: Username or IP address is already in use";
                    sendMessage(false);
                } else if (freeSpot == 2) {
                    message = "J_ER 666: Server full";
                }

                else {
                    users[freeSpot] = new User(username, receivingPacket.getAddress());
                    System.out.println("New user joined: \"" + username + "\"");
                    message = "J_OK";
                    sendMessage(false);
                    listUsers();
                }
            }

            else if (message.startsWith("PICK ")) {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {

                    }
                }
            }

            else if (message.startsWith("DATA ")) {
                updateTimeout();
                System.out.println("Received message from " + message.substring(5));
                sendMessage(true);
            }

            else if (message.equals("IMAV")) {
                updateTimeout();
            }

            else if (message.equals("QUIT")) {
                boolean isEmpty = true;

                for (int i = 0; i < 2; i++) {
                    if (users[i].getIP().equals(receivingPacket.getAddress())) {
                        message = "User \"" + users[i] + "\" has left the server!";
                        System.out.println(message);
                        sendMessage(true);
                        users[i] = null;
                    } else {
                        isEmpty = false;
                    }
                }

                if (isEmpty) {
                    System.out.println("No users online");
                } else {
                    listUsers();
                }
            }

            else {
                message = " J_ER 501: UNKNOWN COMMAND \"" + message + "\"";
                System.out.println(message);
                sendMessage(false);
            }
        }
    }

    private static void sendMessage(boolean toAll) throws Exception {
        DatagramSocket sendingSocket = new DatagramSocket();
        DatagramPacket sendingPacket;
        int clientPort = 6951;
        byte[] sendData;

        if (toAll) {
            for (User user : users) {
                if (user.getIP().equals(receivingPacket.getAddress())) {
                    username = user.getUsername();
                }
            }
            sendData = message.getBytes();
            for (User user : users) {
                if (!user.getUsername().equals(username)) {
                    sendingPacket = new DatagramPacket(sendData, sendData.length, user.getIP(), clientPort);
                    sendingSocket.send(sendingPacket);
                }
            }
        }

        else {
            sendData = message.getBytes();
            sendingPacket =
                    new DatagramPacket(sendData, sendData.length, receivingPacket.getAddress(), clientPort);
            sendingSocket.send(sendingPacket);
        }
    }

    private static void listUsers() throws Exception{
        message = "LIST";

        for (User user:users) {
            message = message.concat(" " + user);
        }

        System.out.println("Updated user list: [" + message.substring(5) + ']');

        sendMessage(false);
        sendMessage(true);
    }

    private static void updateTimeout() {
        for (User user:users) {
            if (user.getIP().equals(receivingPacket.getAddress())) {
                user.setCalendar();
                user.setTimedOut(false);
            }
        }
    }
}