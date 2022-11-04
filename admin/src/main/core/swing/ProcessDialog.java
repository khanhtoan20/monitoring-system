package swing;

import admin.Admin;
import controllers.ProduceController;
import org.json.JSONArray;
import swing.button.ButtonCustom;
import swing.table.TableCustom;
import utils.JSON;
import utils.console;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
/**
 * MOUSE WHEEL LISTENER
 * https://stackoverflow.com/questions/4178957/using-the-mouse-wheel-with-jspinner-in-java
 */

public class ProcessDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTable table;
    private JScrollPane jsp;
    private JTextField txt_process;
    private JSpinner spinner;
    private String pid;
    private Admin admin;
    public ProcessDialog(JSONArray process, Admin admin) {
        this.admin = admin;
        setContentPane(contentPane);
        setModal(true);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(buttonOK);

        DefaultTableModel defaultTableModel = new DefaultTableModel();
        defaultTableModel.setColumnIdentifiers(new Object[]{"pid", "cmd"});
        table.setModel(defaultTableModel);
        TableCustom.apply(jsp, TableCustom.TableType.MULTI_LINE);
        process.forEach(e -> {
            JSON temp = new JSON(e.toString());
            defaultTableModel.addRow(new Object[]{temp.get("pid"), temp.get("cmd")});
        });


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
            }
        });
        this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    return;
                }
                pid = (String) table.getValueAt(selectedRow, 0);
                txt_process.setText(String.format("[%s][%s]", pid, table.getValueAt(selectedRow, 1)));
            }
        });
        pack();
    }

    private void onOK() {
        try {
            if (pid == null) {
                JOptionPane.showMessageDialog(this, "Please select a process first!");
                return;
            }
            this.admin.onSend(ProduceController.endProcess(pid, spinner.getValue().toString()));
            JOptionPane.showMessageDialog(this, pid + " is scheduled to stop!");
            dispose();
        } catch (Exception e) {
            dispose();
        }
    }

    private void onCancel() {
        dispose();
    }
}
