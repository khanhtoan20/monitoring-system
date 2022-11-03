package swing.ContextMenu;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
public class ContextMenu extends JPopupMenu
{
    private Clipboard clipboard;

    private UndoManager undoManager;

    private JMenuItem undo;
    private JMenuItem redo;
    private JMenuItem cut;
    private JMenuItem copy;
    private JMenuItem paste;
    private JMenuItem delete;
    private JMenuItem selectAll;

    private JTextComponent textComponent;

    public ContextMenu()
    {
        undoManager = new UndoManager();
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        addPopupMenuItems();
    }

    private void addPopupMenuItems()
    {
        delete = new JMenuItem("Clear");
        delete.setEnabled(false);
        delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        delete.addActionListener(event -> textComponent.setText(null));
        add(delete);
    }

    private void addTo(JTextComponent textComponent)
    {
        textComponent.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent pressedEvent)
            {
                if ((pressedEvent.getKeyCode() == KeyEvent.VK_Z)
                        && ((pressedEvent.getModifiersEx() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0))
                {
                    if (undoManager.canUndo())
                    {
                        undoManager.undo();
                    }
                }

                if ((pressedEvent.getKeyCode() == KeyEvent.VK_Y)
                        && ((pressedEvent.getModifiersEx() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0))
                {
                    if (undoManager.canRedo())
                    {
                        undoManager.redo();
                    }
                }
            }
        });

        textComponent.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent releasedEvent)
            {
                handleContextMenu(releasedEvent);
            }

            @Override
            public void mouseReleased(MouseEvent releasedEvent)
            {
                handleContextMenu(releasedEvent);
            }
        });

        textComponent.getDocument().addUndoableEditListener(event -> undoManager.addEdit(event.getEdit()));
    }

    private void handleContextMenu(MouseEvent releasedEvent)
    {
        if (releasedEvent.getButton() == MouseEvent.BUTTON3)
        {
            processClick(releasedEvent);
        }
    }

    private void processClick(MouseEvent event)
    {
        textComponent = (JTextComponent) event.getSource();
        textComponent.requestFocus();

        boolean enableUndo = undoManager.canUndo();
        boolean enableRedo = undoManager.canRedo();
        boolean enableCut = false;
        boolean enableCopy = false;
        boolean enablePaste = false;
        boolean enableDelete = false;
        boolean enableSelectAll = false;

        String selectedText = textComponent.getSelectedText();
        String text = textComponent.getText();

        if (text != null)
        {
            if (text.length() > 0)
            {
                enableSelectAll = true;
            }
        }

        if (selectedText != null)
        {
            if (selectedText.length() > 0)
            {
                enableCut = true;
                enableCopy = true;
                enableDelete = true;
            }
        }

        if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor) && textComponent.isEnabled())
        {
            enablePaste = true;
        }

//        undo.setEnabled(enableUndo);
//        redo.setEnabled(enableRedo);
//        cut.setEnabled(enableCut);
//        copy.setEnabled(enableCopy);
//        paste.setEnabled(enablePaste);
        delete.setEnabled(true);
//        selectAll.setEnabled(enableSelectAll);

        // Shows the popup menu
        show(textComponent, event.getX(), event.getY());
    }

    public static void addDefaultContextMenu(JTextComponent component)
    {
        ContextMenu defaultContextMenu = new ContextMenu();
        defaultContextMenu.addTo(component);
    }
}
