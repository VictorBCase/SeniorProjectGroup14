
public class QueueTests {
    public static void main(String[] args) {
        VirtualQueue vq = new VirtualQueue();

//Testing queue (no QR implemented yet)

        vq.joinQueue(new User("TestUser1"));
        vq.joinQueue(new User("TestUser2"));
        vq.joinQueue(new User("TestUser3"));

        vq.serveNext();
        vq.serveNext();

        vq.viewQueue();

        System.out.println("Queue size: " + vq.size());
    }
}
