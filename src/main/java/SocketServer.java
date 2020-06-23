import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketServer {
    private Socket          socket   = null;
    private ServerSocket    server   = null;
    private DataInputStream in       =  null;
    private DataOutputStream out     = null;

    // constructor with port
    public SocketServer(int port)
    {
        // starts server and waits for a connection
        try
        {
            server = new ServerSocket(port);
            System.out.println("Server started");

            System.out.println("Waiting for a client ...");

            socket = server.accept();
            System.out.println("Client accepted");

            // takes input from the client socket
            in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));

            String line = "";
            // sends output to the socket
            out    = new DataOutputStream(socket.getOutputStream());
            // reads message from client until "Over" is sent
            while (!line.equals("Over"))
            {
                try
                {
                    line = in.readUTF();
                    System.out.println(new JSONObject(line).get("message"));
                    out.writeUTF("I got the message");

                }
                catch(IOException i)
                {
                    System.out.println(i);
                }
            }
            System.out.println("Closing connection");

            // close connection
            socket.close();
            out.close();
            in.close();
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        SocketServer server = new SocketServer(5000);
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
