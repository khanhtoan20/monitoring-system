package GUI;

import admin.Admin;
import models.MessageModel;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.console;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import static utils.Environment.DEFAULT_SERVER_HOST;

public class DashboardGUI {
    private JPanel main;
    private JTable table1;
    private JButton button1;
    private JButton clients;
    private static DefaultTableModel defaultTableModel= new DefaultTableModel();
    private static final String[] tableHeaders = {"ID", "CPU", "RAM", "DISK"};

    private static Admin admin;
    public DashboardGUI() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {

        }
        defaultTableModel.setColumnIdentifiers(tableHeaders);
        table1.setModel(defaultTableModel);
//        TableColumnModel columns = table1.getColumnModel();
//        columns.getColumn(0).setMinWidth(200);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    admin.onSend(new MessageModel("ADMIN", DEFAULT_SERVER_HOST)
                            .put("command", "/login").json());
                    console.log("login");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(main,
                            ex.getMessage(),
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        clients.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    admin.onSend(new MessageModel("ADMIN", DEFAULT_SERVER_HOST, "/clients").json());
                    console.log("clients");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(main,
                            "WARNING.",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
            }
        }
        });
    }

    public static void add(JSONObject input) {
        JSONArray arr = new JSONArray(input.get("clients").toString());
        arr.forEach(e ->{
            JSONObject json = new JSONObject(e.toString());
            Vector<String> si = new Vector<>();
            si.add(json.get("UUID").toString());
            si.add(json.get("cpu").toString());
            si.add(json.get("ram").toString());
            si.add(json.get("disk").toString());
            defaultTableModel.addRow(si);
        });
    }


    public static void start() {
        (admin = new Admin()).start();
        JFrame frame = new JFrame();
        frame.setVisible(true);
        frame.setSize(800, 600);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Monitoring system");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new DashboardGUI().main);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                start();
            }
        });

    }
}
