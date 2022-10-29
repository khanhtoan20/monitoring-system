package swing.button;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class caca extends JButton implements TableCellRenderer {

    public caca() {
        setOpaque(true);
    }
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        return new JButton();
    }
}
