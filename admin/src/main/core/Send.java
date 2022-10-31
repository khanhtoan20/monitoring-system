import org.json.JSONArray;
import utils.console;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;

public class Send {

    public static void main(String[] args) throws Exception {
//        Socket socket = new Socket("localhost", 13085);
//        OutputStream outputStream = socket.getOutputStream();

//        BufferedImage image = ImageIO.read(new File("C:\\Users\\Jakub\\Pictures\\test.jpg"));
        BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
//        ImageIO.write(image, "png", new File("/screenshot.png"));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrayOutputStream);

//        console.log(byteArrayOutputStream.toByteArray()+"");

        JSONArray json = new JSONArray(byteArrayOutputStream.toByteArray());
        console.log(byteArrayOutputStream.toByteArray().length+"");


        JSONArray tmp = new JSONArray(json.toString());

        int length = tmp.length();
        console.log(length + "");

        byte[] arr = new byte[length];
        for (int i = 0; i < length; i++) {
//            System.out.println(Byte.valueOf(tmp.get(i).toString()));
            arr[0] = Byte.parseByte(tmp.get(i).toString());
        }

//        ByteBuffer.wrap(arr).array();
        ByteArrayInputStream temp = new ByteArrayInputStream(ByteBuffer.wrap(arr).array());
        System.out.println(ImageIO.createImageInputStream(temp));
        BufferedImage image2 = ImageIO.read(ImageIO.createImageInputStream(temp));
        System.out.println(image2);
        ImageIcon temp1 = new ImageIcon(image2);
        System.out.println(temp1.getIconHeight());
//        ImageIO.write(image2, "jpg", new File("C:\\Users\\USER\\Pictures\\hmm.jpg"));

//        byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
//        outputStream.write(size);
//        outputStream.write(byteArrayOutputStream.toByteArray());
//        outputStream.flush();
//        System.out.println("Flushed: " + System.currentTimeMillis());
//
////        Thread.sleep(5000);
////        ImageIO.write(image, "png", outputStream);
//        System.out.println("Closing: " + System.currentTimeMillis());
//        socket.close();
    }
}


