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
    public SocketClient(String address, int port) throws IOException {
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
        String line = "";
        in = new DataInputStream(
                new BufferedInputStream(socket.getInputStream()));
        // keep reading until "Over" is input
        while (!line.equals("Over"))
        {
            try
            {
                line = input.readLine();
                out.writeUTF(getBroadcastMessage().toString());
                System.out.println(in.readUTF());
            }
            catch(IOException i)
            {
                System.out.println(i);
            }
        }

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
        SocketClient client = new SocketClient("127.0.0.1", 5000);
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
