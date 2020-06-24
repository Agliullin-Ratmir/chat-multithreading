import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Semaphore;

public class SocketClient implements Runnable {

    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream out = null;
    private DataInputStream in = null;
    private String address;
    private int port;
    private static volatile int index;
    private Semaphore semaphore;
    private static volatile boolean isClosed = true;
    private static volatile int amountUsers = 0;

    public SocketClient(String address, int port, int index,
                        Semaphore semaphore) {
        this.address = address;
        this.port = port;
        this.index = index;
        this.semaphore = semaphore;
    }

    private synchronized void reсeivingMessage(String currentUser, String message) {
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

    @Override
    public void run() {
        try {
            openConnections();
            System.out.println("Connection is opened");
            String currentUser = null;
            String firstMessage = null;
            String responseString = null;
            synchronized (this) {
                while (amountUsers < 3) {
                         semaphore.acquire();
                    System.out.println("Put your name for user №" + index);
                    input = new DataInputStream(System.in);
                    currentUser = input.readLine();
                    ++amountUsers;
                          semaphore.release();
                }
            }
            out.writeUTF(currentUser);
            out.writeUTF(getBroadcastMessage().toString());
            System.out.println("The first message for user №" + index);
            firstMessage = in.readUTF();

            reсeivingMessage(currentUser, firstMessage);
            responseString = in.readUTF();
            reсeivingMessage(currentUser, responseString);
        } catch (IOException | InterruptedException u) {
            System.out.println(u);
        } finally {
            try {
                closeConnections();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private synchronized void openConnections() throws InterruptedException {
       semaphore.acquire();
        if (isClosed) {
            try {
                socket = new Socket(address, port);
                out = new DataOutputStream(socket.getOutputStream());
                in = new DataInputStream(
                        new BufferedInputStream(socket.getInputStream()));
            } catch (IOException i) {
                System.out.println(i);
            } finally {
                isClosed = false;
                semaphore.release();
            }
        }
    }

    private synchronized void closeConnections() throws InterruptedException {
        semaphore.acquire();
        if (!isClosed) {
            try {
                input.close();
                out.close();
                socket.close();
            } catch (IOException i) {
                System.out.println(i);
            } finally {
                isClosed = true;
               semaphore.release();
            }
        }
    }
}
