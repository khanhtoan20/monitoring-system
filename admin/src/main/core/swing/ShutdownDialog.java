package swing;

import admin.Admin;
import controllers.ProduceController;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

public class ShutdownDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JSpinner spinner;
    private JLabel lbl;
    private JPanel view;
    private JPanel action;
    private JPanel controll;
    private Admin admin;
    public ShutdownDialog(Admin admin) {
        this.admin = admin;
        setSize(400, 200);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setLocationRelativeTo(null);
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        this.spinner.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                Integer val = new Integer(((Integer) spinner.getValue()).intValue() - e.getWheelRotation());
                spinner.setValue(val > 0 ? val : 0);
                lbl.setText(String.format("Client will be shutdown in (%s) minutes", spinner.getValue()));
            }
        });
    }

    private void onOK() {
        try {
            this.admin.onSend(ProduceController.shutdown(spinner.getValue().toString()));
            JOptionPane.showMessageDialog(this, "Client is scheduled to shutdown!");
            dispose();
        } catch (Exception e) {
            dispose();
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
