package com.photon.moduleList;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.photon.data.CoreModel;
import com.photon.data.GitRepository;
import com.photon.data.SubModule;
import com.photon.provider.GitCloner;
import com.photon.utils.Constants;
import com.photon.utils.FileUtils;
import com.photon.utils.GitHelper;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;

/**
 * Module factory.
 */
public class ModuleConfigFactory implements ToolWindowFactory, GitHelper.GitStatusListener {

    private ToolWindow myToolWindow = null;
    private JPanel myRootPanel = null;
    private JPanel myFooterPanel = null;
    private JScrollPane mScrollPanel = null;
    private JTable mConfigTable = null;
    private JButton mConfigBtn = null;
    private JButton mCancelBtn = null;
    private JProgressBar mProgressBar = null;
    private JComboBox<String> mComboBox = null;

    private DefaultTableModel mConfigTableModel = null;
    private CoreModel mCoreModel = null;
    private java.util.List<SubModule> mSubModules = null;
    private Project mProject = null;

    private int mSelectedRow = 0;

    public ModuleConfigFactory() {
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        myToolWindow = toolWindow;
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(myRootPanel, "", false);
        toolWindow.getContentManager().addContent(content);

        mProject = project;
        fillTable();
    }

    private void fillTable() {

        String lProPath = mProject.getBasePath();
        String lSettingsPath = lProPath.concat(File.separator).concat(Constants.SETTINGS_FILE);
        String lGradlePropPath = lProPath.concat(File.separator).concat(Constants.GRADLE_DOT_PROPERTY_FILE);

        mCoreModel = FileUtils.parseSettingsFile(lGradlePropPath, lSettingsPath);
        mSubModules = new ArrayList<>(mCoreModel.getModuleSet());
        for (SubModule lModule : mSubModules) {
            mConfigTableModel.addRow(new String[]{lModule.moduleName,
                    lModule.getModuleType().getTypeStrValue(), lModule.getMvnInfo(), lModule.getSourcePath()});
        }
    }

    private void createUIComponents() {

        mConfigTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {

                if (column == 1) {
                    SubModule lModule = mSubModules.get(row);
                    if (null != lModule) {
                        String[] lModuleType = lModule.getSupportedModuleType();
                        return (null != lModuleType && lModuleType.length > 1);
                    }
                }
                return false;
            }
        };
        mScrollPanel = new javax.swing.JScrollPane();
        mConfigTable = new javax.swing.JTable();
        mConfigTable.setRowHeight(40);
        mConfigTable.setAutoCreateRowSorter(true);
        mScrollPanel.setViewportView(mConfigTable);

        myRootPanel = new javax.swing.JPanel();
        myFooterPanel = new javax.swing.JPanel();
        mCancelBtn = new javax.swing.JButton();
        mCancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                myToolWindow.hide(null);
            }
        });

        mConfigBtn = new javax.swing.JButton();
        mProgressBar = new JProgressBar();
        mProgressBar.setStringPainted(true);
        mProgressBar.setIndeterminate(true);

        mConfigBtn.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
//            myToolWindow.dispose();
                GitCloner lCloner = new GitCloner(mCoreModel, mProject, ModuleConfigFactory.this);
                lCloner.execute();
            }
        });

        //Column header
        mComboBox = new javax.swing.JComboBox<>();
        mComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(Constants.CONFIG_TABLE_COLUMN_NAMES));
        for (String aColumnHeader : Constants.CONFIG_TABLE_COLUMN_NAMES) {
            mConfigTableModel.addColumn(aColumnHeader);
        }
        mConfigTable.setPreferredScrollableViewportSize(new Dimension(550, 200));
        mConfigTable.setModel(mConfigTableModel);
        configureTypeColumn();
    }

    private void configureTypeColumn() {
        //Type selection Spinner
        final TableColumn lTypeColumn = mConfigTable.getColumn(Constants.COLUMN_MODULE_TYPE_TXT);

        if (null != lTypeColumn) {
            final JComboBox<String> lComboBox = new JComboBox(Constants.MODULE_TYPE);
            lComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent aEvent) {
                    String lStr = (String) lComboBox.getSelectedItem();
                    if (Constants.DEBUG) {
                        System.out.println("[Selected item][" + lStr + "]");
                    }
                }
            });
            lComboBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {

                    String lSelectedType = (String) e.getItem();
                    if (Constants.DEBUG) {
                        System.out.println("ItemStateChanged[Module,Type]["
                                + mSubModules.get(mSelectedRow).getModuleName()
                                + "," + lSelectedType + "]");
                    }
                    mSubModules.get(mSelectedRow).setModuleType(lSelectedType);
                }
            });

            lComboBox.setRenderer(new DefaultListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    Component lComponent = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                    //list.setBackground(Color.WHITE);
                    if (isSelected) {
                        setBackground(Color.WHITE);
                        setForeground(Color.BLACK);
                    } else {
                        setBackground(Color.BLACK);
                        setForeground(Color.WHITE);
                    }
                    return lComponent;
                }
            });
            lTypeColumn.setCellEditor(new DefaultCellEditor(lComboBox) {

                @Override
                public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                    mSelectedRow = row;
                    return super.getTableCellEditorComponent(table, value, isSelected, row, column);
                }
            });

        }
        lTypeColumn.setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {

                if (Constants.DEBUG) {
                    //System.out.println("[GetTableCellRendererComponent]--> [R,C][" + row + "," + column + "]");
                }

                SubModule lModule = mSubModules.get(row);
                String[] lModuleType = lModule.getSupportedModuleType();
                JLabel label = (JLabel) super.getTableCellRendererComponent(table,
                        value, isSelected, hasFocus, row, column);
                if (null != lModuleType && lModuleType.length > 1) {
                    label.setIcon(UIManager.getIcon("Table.descendingSortIcon"));
                    label.setIconTextGap(5);
                } else {
                    label.setIcon(null);
                }
                return label;
            }
        });

    }

    @Override
    public void onProgressUpdate(String aMsg) {
        mProgressBar.setString(aMsg);
    }

    @Override
    public void onStart() {
        mCancelBtn.setEnabled(false);
        mConfigBtn.setEnabled(false);
        mConfigTable.setEnabled(false);
        mProgressBar.setVisible(true);
    }

    @Override
    public void onComplete() {
        configureIndependentModules();
        mCancelBtn.setEnabled(true);
        mConfigBtn.setEnabled(true);
        mConfigTable.setEnabled(true);
        mProgressBar.setVisible(false);
    }

    private void configureIndependentModules() {
        ArrayList<String> lGradleFileList = new ArrayList<String>();
        FileUtils.search(new File(mProject.getBaseDir().getPath()), Constants.BUILD_DOT_GRADLE_FILE, lGradleFileList);

        for (SubModule lModule : mSubModules) {
            String lType = lModule.getModuleType().getTypeStrValue();
            System.out.println("File Editing [Module, Type]" + "[" + lModule.getModuleName() + "," + lModule.getModuleType() + "]");
            if (null != lType &&
                    lType.length() > 0 && lType.equalsIgnoreCase(Constants.Module.JAR.getTypeStrValue())) {
                FileUtils.switchToMVNDept(lGradleFileList, lModule.getModuleName(), lModule.getMvnInfo());
            } else {
                FileUtils.switchToSourceDept(lGradleFileList, lModule.getMvnInfo(), lModule.getModuleName());
            }
        }

    }
}
