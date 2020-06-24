import org.json.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketClient {

    private Socket socket            = null;
    private DataInputStream  input   = null;
    private DataOutputStream out     = null;
    private DataInputStream in       =  null;

    // constructor to put ip address and port
    public SocketClient(String address, int port, int index) throws IOException {
        // establish a connection
        try
        {
            socket = new Socket(address, port);
            System.out.println("Connected");

            // takes input from terminal
            input  = new DataInputStream(System.in);

            // sends output to the socket
            out    = new DataOutputStream(socket.getOutputStream());
        }
        catch(UnknownHostException u)
        {
            System.out.println(u);
        }
        catch(IOException i)
        {
            System.out.println(i);
        }

        // string to read message from input
        in = new DataInputStream(
                new BufferedInputStream(socket.getInputStream()));
        // keep reading until "Over" is input
        System.out.println("Put your name for user №" + index);
        String currentUser = input.readLine();
        out.writeUTF(currentUser);
        //send broadcast message
        out.writeUTF(getBroadcastMessage().toString());
        String firstMessage = in.readUTF();
        retrivingMessage(currentUser, firstMessage);
        String responseString = in.readUTF();
        retrivingMessage(currentUser, responseString);

        // close the connection
        try
        {
            input.close();
            out.close();
            socket.close();
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }

    public static void main(String[] args) throws IOException {
        SocketClient client = new SocketClient("127.0.0.1", 5000, 1);
    }

    private void retrivingMessage(String currentUser, String message) {
        JSONObject json = new JSONObject(message);
        if ("broadcast".equals(json.get("mode"))) {
            System.out.println("This message for everyone: "
            + json.get("message"));
        } else {
            if ("private".equals(json.get("mode")) &&
                    currentUser.equals(json.get("consumer"))) {
                System.out.println(json.get("message"));
            } else {
                System.out.println("There are no messages for the user " + currentUser);
            }
        }
    }

    private static JSONObject getBroadcastMessage() {
        JSONObject json = new JSONObject();
        json.put("mode", "broadcast");
        json.put("message", "this is a message for all");
        return json;
    }

    private static JSONObject getPrivateMessage() {
        JSONObject json = new JSONObject();
        json.put("mode", "private");
        json.put("consumer", "user1");
        json.put("message", "this is a message for user1");
        return json;
    }
}
