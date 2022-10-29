import utils.console;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Receive {

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(13085);
        Socket socket = serverSocket.accept();
        InputStream inputStream = socket.getInputStream();

        System.out.println("Reading: " + System.currentTimeMillis());

//        byte[] sizeAr = new byte[1000000];
//        inputStream.read(sizeAr);
//        int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
//        System.out.println(size);
//        byte[] imageAr = new byte[size];
//        inputStream.read(imageAr);
        console.warn("block");
        BufferedImage image = ImageIO.read(inputStream);
        console.warn("unblock");
        System.out.println("Received " + image.getHeight() + "x" + image.getWidth() + ": " + System.currentTimeMillis());
        ImageIO.write(image, "png", new File("C:\\Users\\USER\\Pictures\\test4.jpg"));

        serverSocket.close();
    }

}