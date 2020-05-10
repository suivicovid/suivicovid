package org.suivicovid.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.suivicovid.dao.JpaDao;
import org.suivicovid.data.Patient;
import org.suivicovid.sync.Sync;

public class MainPanel extends JPanel {
    /**
     *
     */
    private static final long serialVersionUID = 5929454278434093182L;
    PatientTableModel mdPatient = new PatientTableModel();
    JTable table = new JTable(mdPatient);
    JScrollPane scrollpane = new JScrollPane(table);

    JLabel lbWarn = new JLabel("les scores sont purement indicatifs.");
    JLabel lbFill = new JLabel("");

    JButton btNew = new JButton("new");
    JButton btDelete = new JButton("delete");
    JButton btRefresh = new JButton("refresh");

    JCheckBox cbNetworked = new JCheckBox("multiposte");

    static private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void refresh() {
        if (instance != null)
            instance.refreshMe();
    }

    private static MainPanel instance;
    private JFrame parentFrame;

    public MainPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        add(scrollpane, BorderLayout.CENTER);

        JPanel buttons = new JPanel();

        GridBagLayout layout = new GridBagLayout();
        buttons.setLayout(layout);

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridy = 0;

        c.gridx = 0;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        buttons.add(cbNetworked, c);

        c.gridx = 2;
        c.gridwidth = 1;
        c.weightx = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        buttons.add(lbFill, c);

        c.gridx = 3;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        buttons.add(btNew, c);
        c.gridx = 5;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        buttons.add(btDelete, c);
        c.gridx = 7;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        buttons.add(btRefresh, c);
        c.gridx = 12;
        c.gridwidth = 3;
        c.weightx = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        buttons.add(lbWarn, c);

        btDelete.setEnabled(false);

        add(buttons, BorderLayout.SOUTH);

        ListSelectionModel listMod = table.getSelectionModel();
        listMod.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    Patient p = mdPatient.data.get(row);
                    PatientEditWindow w = new PatientEditWindow(MainPanel.this, p);
                    w.pack();
                    w.setVisible(true);
                }

                btDelete.setEnabled(table.getSelectedRow() >= 0);

            }
        });

        btNew.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                Patient p = new Patient();
                PatientEditWindow w = new PatientEditWindow(MainPanel.this, p);
                w.pack();
                w.setVisible(true);
            }
        });

        btDelete.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                int row = table.getSelectedRow();
                if (row < 0)
                    return; // should not happen

                String message = "Supprimer le patient ?";
                String title = "Confirmation de suppression";
                // display the JOptionPane showConfirmDialog
                int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.OK_CANCEL_OPTION);
                if (reply != JOptionPane.OK_OPTION)
                    return;

                Patient p = mdPatient.data.get(row);
                JpaDao.getInstance().remove(p);
                JpaDao.getInstance().commit();
                MainPanel.refresh();

            }
        });

        cbNetworked.setSelected(JpaDao.getInstance().isNetworked());

        cbNetworked.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                boolean net = cbNetworked.isSelected();
                JpaDao.getInstance().setNetworked(net);
                Sync.getInstance().activate(net);
            }

        });

        btRefresh.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                MainPanel.refresh();
            }
        });

        instance = this;

        JTableHeader header = table.getTableHeader();
        header.setUpdateTableInRealTime(true);
        header.addMouseListener(mdPatient.new ColumnListener(table));
        header.setReorderingAllowed(true);

        List<Patient> l = JpaDao.getInstance().getAll(Patient.class);

        mdPatient.setData(l);

        table.getColumnModel().getColumn(4).setCellRenderer(new StatusColumnCellRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new StatusColumnCellRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new StatusColumnCellRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new StatusColumnCellRenderer());
        table.getColumnModel().getColumn(8).setCellRenderer(new StatusColumnCellRenderer());
        table.getColumnModel().getColumn(9).setCellRenderer(new StatusColumnCellRenderer());
        table.getColumnModel().getColumn(10).setCellRenderer(new StatusColumnCellRenderer());

        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(40);
        table.getColumnModel().getColumn(4).setPreferredWidth(70);
        table.getColumnModel().getColumn(5).setPreferredWidth(70);
        table.getColumnModel().getColumn(6).setPreferredWidth(70);
        table.getColumnModel().getColumn(7).setPreferredWidth(70);
        table.getColumnModel().getColumn(8).setPreferredWidth(100);
        table.getColumnModel().getColumn(9).setPreferredWidth(40);

        // table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    }

    public void refreshMe() {
        List<Patient> l = JpaDao.getInstance().getAll(Patient.class);

        mdPatient.setData(l);

        mdPatient.applySort();

        getParent().repaint();
        mdPatient.fireTableDataChanged();

        table.repaint();
        JpaDao.getInstance().close();
    }

    public class StatusColumnCellRenderer extends DefaultTableCellRenderer {
        /**
         *
         */
        private static final long serialVersionUID = 1917975198372143546L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int col) {

            // Cells are by default rendered as a JLabel.
            JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

            // Get the status for the current row.
            PatientTableModel tableModel = (PatientTableModel) table.getModel();
            Patient p = tableModel.data.get(row);
            if (col == 4) {
                if (p.hasComorbid())
                    l.setForeground(Color.RED);
                else
                    l.setForeground(Color.BLACK);
            } else if (col == 5) {
                if (p.hasFragilites())
                    l.setForeground(Color.RED);
                else
                    l.setForeground(Color.BLACK);
            } else if (col == 6) {
                if (p.hasGravite())
                    l.setForeground(Color.RED);
                else
                    l.setForeground(Color.BLACK);
            } else if (col == 7) {
                if (p.getCovidDiag() > 2)
                    l.setForeground(Color.RED);
                else if (p.getCovidDiag() > 0)
                    l.setForeground(Color.ORANGE);
                else
                    l.setForeground(Color.BLACK);
            } else if (col == 8) {
                if (p.aRappeler())
                    l.setForeground(Color.RED);
                else
                    l.setForeground(Color.BLACK);
            } else if (col == 9) {
                if (p.getScoreDiag() > 25)
                    l.setForeground(Color.RED);
                else if (p.getScoreDiag() > 12)
                    l.setForeground(Color.ORANGE);
                else
                    l.setForeground(Color.BLACK);
            } else if (col == 10) {
                if (p.getScoreGravite() > 25)
                    l.setForeground(Color.RED);
                else if (p.getScoreGravite() > 12)
                    l.setForeground(Color.ORANGE);
                else
                    l.setForeground(Color.BLACK);
            }

            // Return the JLabel which renders the cell.
            return l;

        }
    }

    static class PatientTableModel extends AbstractTableModel {
        /**
         *
         */
        private static final long serialVersionUID = -555041951138383830L;

        public PatientTableModel() {
        }

        public PatientTableModel(List<Patient> data) {
            this.data = data;
        }

        public void setData(List<Patient> data) {
            this.data = data;
        }

        List<Patient> data;

        public Object getValueAt(int row, int col) {
            if (row < 0 || row >= data.size())
                return null;
            Patient p = data.get(row);
            Object value = getPatientAttribute(p, col);
            if (value instanceof LocalDate) {
                value = ((LocalDate) value).format(dateFormat);
                if (col == 8) {
                    LocalDate symp = (LocalDate) p.getDateSymptomes();
                    LocalDate rapp = (LocalDate) p.getDateRappelSummary();
                    if (symp != null && rapp != null) {
                        value = value + " J" + symp.until(rapp, ChronoUnit.DAYS);
                    }
                }
            }
            return value;
        }

        public static Object getPatientAttribute(Patient p, int col) {
            switch (col) {
                case 0:
                    return p.getNom();
                case 1:
                    return p.getDateContact();
                case 2:
                    return p.getDateSymptomes();
                case 3:
                    return p.getAge();
                case 4:
                    return p.hasComorbid() ? "oui" : "non";
                case 5:
                    return p.hasFragilites() ? "oui" : "non";
                case 6:
                    return p.hasGravite() ? "oui" : "non";
                case 7:
                    return Patient.covidDiagStr[p.getCovidDiag()];
                case 8:
                    return p.getDateRappelSummary();
                case 9:
                    return p.getScoreDiag();
                case 10:
                    return p.getScoreGravite();
                default:
                    return null;
            }

        }

        String[] cNames = { "nom", "dateContact", "dateSympt", "age", "comorb.", "fragilités", "sgn grav", "covid",
                "dateRappel", "diag", "gravité" };

        @Override
        public String getColumnName(int i) {
            return cNames[i];
        }

        public int getColumnCount() {
            return cNames.length;
        }

        public int getRowCount() {
            return data.size();
        }

        protected int sortCol = 1;
        protected boolean isSortAsc = true;

        public void applySort() {
            Collections.sort(data, new MyComparator(sortCol, isSortAsc));
        }

        class ColumnListener extends MouseAdapter {
            protected JTable table;

            public ColumnListener(JTable t) {
                table = t;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                TableColumnModel colModel = table.getColumnModel();
                int columnModelIndex = colModel.getColumnIndexAtX(e.getX());
                int modelIndex = colModel.getColumn(columnModelIndex).getModelIndex();

                if (modelIndex < 0)
                    return;
                if (sortCol == modelIndex)
                    isSortAsc = !isSortAsc;
                else
                    sortCol = modelIndex;

                int columnsCount = getColumnCount();

                for (int i = 0; i < columnsCount; i++) {
                    TableColumn column = colModel.getColumn(i);
                    column.setHeaderValue(getColumnName(column.getModelIndex()));
                }
                table.getTableHeader().repaint();

                Collections.sort(data, new MyComparator(sortCol, isSortAsc));

                table.tableChanged(new TableModelEvent(PatientTableModel.this));
                table.repaint();
            }
        }

        class MyComparator implements Comparator<Object> {
            protected boolean isSortAsc;
            protected int sortColIndex;

            public MyComparator(int sortCol, boolean sortAsc) {
                isSortAsc = sortAsc;
                sortColIndex = sortCol;
            }

            public int compare(Object o1, Object o2) {
                if (!(o1 instanceof Patient) || !(o2 instanceof Patient))
                    return 0;
                Patient f1 = (Patient) o1;
                Patient f2 = (Patient) o2;

                Comparable<Object> s1 = (Comparable<Object>) PatientTableModel.getPatientAttribute(f1, sortColIndex);
                Comparable<Object> s2 = (Comparable<Object>) PatientTableModel.getPatientAttribute(f2, sortColIndex);

                int result = 0;
                if (s1 == null && s2 == null)
                    return 0;
                if (s1 == null)
                    return isSortAsc ? 1 : -1;
                if (s2 == null)
                    return isSortAsc ? -1 : 1;
                result = s1.compareTo(s2);
                if (!isSortAsc)
                    result = -result;
                return result;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof MyComparator) {
                    MyComparator compObj = (MyComparator) obj;
                    return compObj.isSortAsc == isSortAsc;
                }
                return false;
            }
        }

    }

    public JFrame getParentFrame() {
        return parentFrame;
    }

	public static void showActiveNet() {
        if (instance != null)
        instance.updateActiveNet(true);
	}

    private void updateActiveNet(boolean b) {
        cbNetworked.setForeground(b ? Color.GREEN : Color.BLACK);
    }

}