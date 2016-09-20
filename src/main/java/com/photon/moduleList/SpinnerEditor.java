package com.photon.moduleList;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.EventObject;

/**
 *
 *
 */
class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {
    final JSpinner spinner = new JSpinner();

    public SpinnerEditor(String[] items) {

        spinner.setModel(new SpinnerListModel(Arrays.asList(items)));
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                                 int row, int column) {
        spinner.setValue(value);
        return spinner;
    }

    public boolean isCellEditable(EventObject evt) {
        if (evt instanceof MouseEvent) {
            return ((MouseEvent) evt).getClickCount() >= 1;
        }
        return true;
    }

    public Object getCellEditorValue() {
        return spinner.getValue();
    }
}

