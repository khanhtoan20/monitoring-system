import utils.console;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Send {

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 13085);
        OutputStream outputStream = socket.getOutputStream();

//        BufferedImage image = ImageIO.read(new File("C:\\Users\\Jakub\\Pictures\\test.jpg"));
        BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
//        ImageIO.write(image, "png", new File("/screenshot.png"));
        console.log(image.toString());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpeg", outputStream);

//        byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
//        outputStream.write(size);
//        outputStream.write(byteArrayOutputStream.toByteArray());
//        outputStream.flush();
//        System.out.println("Flushed: " + System.currentTimeMillis());

        Thread.sleep(5000);
        ImageIO.write(image, "png", outputStream);
        System.out.println("Closing: " + System.currentTimeMillis());
        socket.close();
    }
}


