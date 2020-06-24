import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class ClientExecutor {

    public ClientExecutor() {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        System.out.println("Threads are releasing");
        Semaphore semaphore = new Semaphore(1);
        executor.execute(new SocketClient("127.0.0.1", 5000, 1, semaphore));
        executor.execute(new SocketClient("127.0.0.1", 5000, 2, semaphore));
        executor.execute(new SocketClient("127.0.0.1", 5000, 3, semaphore));

        executor.shutdown();
        System.out.println("Threads are done");
    }

    public static void main(String[] args) {
        new ClientExecutor();
    }
}
