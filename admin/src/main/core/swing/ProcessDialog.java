package swing;

import org.json.JSONArray;
import swing.table.TableCustom;
import utils.JSON;
import utils.console;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;

public class ProcessDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTable table;
    private JScrollPane jsp;
    private JTextField txt_process;
    private JSpinner spinner;
    private String pid;
    public ProcessDialog(JSONArray process) {
        DefaultTableModel defaultTableModel = new DefaultTableModel();
        setLocationRelativeTo(null);
        defaultTableModel.setColumnIdentifiers(new Object[]{"pid", "cmd"});
        table.setModel(defaultTableModel);
        TableCustom.apply(jsp, TableCustom.TableType.MULTI_LINE);
        process.forEach(e -> {
            JSON temp = new JSON(e.toString());
            defaultTableModel.addRow(new Object[]{temp.get("pid"), temp.get("cmd")});
        });
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

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
    }

    private void onOK() {
        console.info(spinner.getValue()+"");
//        spinner.getValue();
//        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
