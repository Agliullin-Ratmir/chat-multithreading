import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SocketServer {
    private Socket          socket   = null;
    private ServerSocket    server   = null;
    private DataInputStream in       =  null;
    private DataOutputStream out     = null;


    //refactoring+make a bus for private messages
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

            // sends output to the socket
            String line = in.readUTF();
            System.out.println("User " + line + " has been registered");
            String message = in.readUTF();
            System.out.println("This message has been received: " + message);
            out  = new DataOutputStream(socket.getOutputStream());
            // reads message from client until "Over" is sent
//            List<String> userNames = getListUsers(in);
//            userNames.stream().forEach(s -> System.out.println(s));
            out.writeUTF(message);
            out.writeUTF(getPrivateMessage().toString());
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

    private List<String> getListUsers(DataInputStream in) throws IOException {
        List<String> list = new ArrayList<>();
        for(int i=0; i < 3; i++) {
            String userName = in.readUTF();
            list.add(userName);
            System.out.println("User " + userName + "is written");
        }
        return list;
    }
}
