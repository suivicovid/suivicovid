package org.suivicovid.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import org.suivicovid.dao.JpaDao;
import org.suivicovid.data.Consult;
import org.suivicovid.data.Patient;

public class PatientEditPanel extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 2207997935593786701L;
    Patient p;
    PatientEditWindow topFrame;

    private JTextField tfNom = new JTextField(50);
    private JLabel lbNom = new JLabel("nom");

    private JTextField tfTelephone = new JTextField(50);
    private JLabel lbTelephone = new JLabel("telephone");

    private JTextField tfEmail = new JTextField(50);
    private JLabel lbEmail = new JLabel("email");

    static private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            .withResolverStyle(ResolverStyle.SMART);
    static private DateTimeFormatter dateFormat2 = DateTimeFormatter.ofPattern("d/M/yyyy")
            .withResolverStyle(ResolverStyle.SMART);
    static private DateTimeFormatter dateFormat3 = DateTimeFormatter.ofPattern("d/M/yy")
            .withResolverStyle(ResolverStyle.SMART);

    static private NumberFormat intFormat = new DecimalFormat("###0");
    static private NumberFormat fltFormat = new DecimalFormat("###0.0");

    private boolean dateRappelEdited = false;
    private LocalDate autoDateRappel = null;

    public static class LocalDateFormatter extends JFormattedTextField.AbstractFormatter {

        private static final long serialVersionUID = 2777122041500418133L;

        @Override
        public Object stringToValue(String text) throws ParseException {
            if (text == null || text.equals(""))
                return null;
            try {
                return LocalDate.parse(text, dateFormat);
            } catch (DateTimeParseException e) {
            }
            try {
                return LocalDate.parse(text, dateFormat2);
            } catch (DateTimeParseException e) {
            }
            try {
                return LocalDate.parse(text, dateFormat3);
            } catch (DateTimeParseException e) {
                return null;
            }
        }

        @Override
        public String valueToString(Object value) throws ParseException {
            LocalDate d = (LocalDate) value;
            if (d == null)
                return null;
            return d.format(dateFormat);
        }
    }

    static private JFormattedTextField.AbstractFormatter dateFormatter = new LocalDateFormatter();

    private JFormattedTextField tfDateContact = new JFormattedTextField(dateFormatter);
    private JLabel lbDateContact = new JLabel("date contact");
    private JLabel lbJContact = new JLabel("J_");

    private JFormattedTextField tfDateSymp = new JFormattedTextField(dateFormatter);
    private JLabel lbDateSymp = new JLabel("date symptômes");

    private JFormattedTextField tfDateSympResp = new JFormattedTextField(dateFormatter);
    private JLabel lbDateSympResp = new JLabel("date symp respi");

    private JFormattedTextField tfAge = new JFormattedTextField(intFormat);
    private JLabel lbAge = new JLabel("âge");

    private JFormattedTextField tfDateRappel = new JFormattedTextField(dateFormatter);
    private JLabel lbDateRappel = new JLabel("date rappel");
    private JLabel lbJRappel = new JLabel("J_");
    private JLabel lbDateRappelJ7 = new JLabel("rappel J6-8");
    private JFormattedTextField tfDateRappelJ7 = new JFormattedTextField(dateFormatter);
    private JLabel lbJRappelJ7 = new JLabel("J_");
    private JLabel lbDateRappelJ13 = new JLabel("rappel J12-14");
    private JFormattedTextField tfDateRappelJ13 = new JFormattedTextField(dateFormatter);
    private JLabel lbJRappelJ13 = new JLabel("J_");

    private JCheckBox cbRappelFait = new JCheckBox("rappelé");
    private JCheckBox cbRappelJ7Fait = new JCheckBox("rappelé");
    private JCheckBox cbRappelJ13Fait = new JCheckBox("rappelé");

    private JTextArea taNotes = new JTextArea(5, 60);
    private JScrollPane spNotes = new JScrollPane(taNotes);
    private JLabel lbNotes = new JLabel("notes cliniques");

    private JTextArea taNotesContexte = new JTextArea(5, 60);
    private JScrollPane spNotesContexte = new JScrollPane(taNotesContexte);
    private JLabel lbNotesContexte = new JLabel("notes contexte");

    private JButton btOk = new JButton("OK");
    private JButton btCancel = new JButton("annuler");

    private JButton btAddCS = new JButton("+");
    private JButton btDelCS = new JButton("-");

    private JCheckBox cbRespiChronique = new JCheckBox("patho respir chronique");
    private JCheckBox cbInsuffCardiaque = new JCheckBox("insuff cardiaque");
    private JCheckBox cbAtcdCV = new JCheckBox("ATCD CV");
    private JCheckBox cbDiabete = new JCheckBox("diabete ID");
    private JCheckBox cbImmunodep = new JCheckBox("immunodéprimé");
    private JCheckBox cbCancerTtt = new JCheckBox("cancer en ttt");
    private JCheckBox cbImc40 = new JCheckBox("IMC>40");
    private JCheckBox cbImc30 = new JCheckBox("IMC>30");
    private JCheckBox cbDialyse = new JCheckBox("dialyse");
    private JCheckBox cbCirrhose = new JCheckBox("cirrhose");
    private JCheckBox cbGrs3T = new JCheckBox("gross 3T");

    private JCheckBox cbIsolement = new JCheckBox("isolement");
    private JCheckBox cbPrecarite = new JCheckBox("précarité");
    private JCheckBox cbDiffLinguistique = new JCheckBox("diff linguistiques");
    private JCheckBox cbTroubleNeuroPsy = new JCheckBox("trouble neuro/psy");
    private JCheckBox cbMoyenComm = new JCheckBox("pas de moyen de comm");

    private JCheckBox cbProcheFragile = new JCheckBox("proche fragile");
    private JCheckBox cbPieceConfinement = new JCheckBox("pièce de confinement");

    private JCheckBox cbGrPolyp22 = new JCheckBox("polypnée>22");
    private JCheckBox cbGrSat90 = new JCheckBox("sat<90");
    private JCheckBox cbGrSat95 = new JCheckBox("sat<95");
    private JCheckBox cbGrPas90 = new JCheckBox("PAS<90");
    private JCheckBox cbGrDeshyd = new JCheckBox("deshydratation");
    private JCheckBox cbGrAltConsc = new JCheckBox("alter conscience");
    private JCheckBox cbGAbeg = new JCheckBox("alteration brutale EG");

    private JLabel lbTestCovid1 = new JLabel("test 1");
    private JLabel lbTestCovid1Date = new JLabel("date");
    private JFormattedTextField tfDateTestCovid1 = new JFormattedTextField(dateFormatter);
    private JLabel lbTestCovid1Type = new JLabel("type");
    private JComboBox<String> cboxTypeTestCovid1 = new JComboBox<>(Patient.covidTestType);
    private JCheckBox cbTestCovid1Result = new JCheckBox("+");

    private JLabel lbTestCovid2 = new JLabel("test 2");
    private JLabel lbTestCovid2Date = new JLabel("date");
    private JFormattedTextField tfDateTestCovid2 = new JFormattedTextField(dateFormatter);
    private JLabel lbTestCovid2Type = new JLabel("type");
    private JComboBox<String> cboxTypeTestCovid2 = new JComboBox<>(Patient.covidTestType);
    private JCheckBox cbTestCovid2Result = new JCheckBox("+");

    private JComboBox<String> cboxCovidDiag = new JComboBox<>(Patient.covidDiagStr);
    private JLabel lbCovidDiag = new JLabel("covid");

    ConsultTableModel mdConsult = new ConsultTableModel();
    JTable tbConsult = new JTable(mdConsult);
    JScrollPane spConsult = new JScrollPane(tbConsult);
    private JLabel lbConsult = new JLabel("consult");

    public PatientEditPanel(PatientEditWindow topFrame, Patient p) {
        this.topFrame = topFrame;
        layoutWithGridBag();

        setup();

        this.p = p;

        patientToFields();

    }

    /**
     * @return the topFrame
     */
    public PatientEditWindow getTopFrame() {
        return topFrame;
    }

    private void updateDerived() {
        LocalDate dateContact = (LocalDate) tfDateContact.getValue();
        LocalDate dateSymptomes = (LocalDate) tfDateSymp.getValue();

        if (dateContact != null && dateSymptomes != null)
            lbJContact.setText("J" + (dateSymptomes.until(dateContact, ChronoUnit.DAYS)));
        else
            lbJContact.setText("J?");

        if (dateSymptomes != null) {
            if (tfDateRappelJ7.getValue() == null)
                tfDateRappelJ7.setValue(dateSymptomes.plusDays(7));
            if (tfDateRappelJ13.getValue() == null)
                tfDateRappelJ13.setValue(dateSymptomes.plusDays(13));
        }

        if (dateContact != null && tfDateRappelJ7.getValue() != null)
            lbJRappelJ7.setText("J" + (dateSymptomes.until((LocalDate) tfDateRappelJ7.getValue(), ChronoUnit.DAYS)));
        else
            lbJRappelJ7.setText("J?");

        if (dateContact != null && tfDateRappelJ13.getValue() != null)
            lbJRappelJ13.setText("J" + (dateSymptomes.until((LocalDate) tfDateRappelJ13.getValue(), ChronoUnit.DAYS)));
        else
            lbJRappelJ13.setText("J?");

        if (dateContact != null && tfDateRappel.getValue() != null)
            lbJRappel.setText("J" + (dateSymptomes.until((LocalDate) tfDateRappel.getValue(), ChronoUnit.DAYS)));
        else
            lbJRappel.setText("J?");

    }

    private void patientToFields() {
        tfNom.setText(p.getNom());
        tfTelephone.setText(p.getTelephone());
        tfEmail.setText(p.getEmail());
        tfAge.setValue(p.getAge());
        tfDateContact.setValue(p.getDateContact());
        tfDateSymp.setValue(p.getDateSymptomes());
        tfDateSympResp.setValue(p.getDateSymptomesRespi());
        tfDateRappel.setValue(p.getDateRappel());
        tfDateRappelJ7.setValue(p.getDateRappelJ7());
        tfDateRappelJ13.setValue(p.getDateRappelJ13());
        cbRappelFait.setSelected(p.isRappelFait());
        cbRappelJ7Fait.setSelected(p.isRappelJ7Fait());
        cbRappelJ13Fait.setSelected(p.isRappelJ13Fait());

        dateRappelEdited = p.isDateRappelManual();
        autoDateRappel = p.getDateRappel();

        taNotes.setText(p.getNotes());
        taNotesContexte.setText(p.getNotesContexte());

        cbRespiChronique.setSelected(p.isRespiChronique());
        cbInsuffCardiaque.setSelected(p.isInsuffCardiaque());
        cbAtcdCV.setSelected(p.isAtcdCV());
        cbDiabete.setSelected(p.isDiabete());
        cbImmunodep.setSelected(p.isImmunodep());
        cbCancerTtt.setSelected(p.isCancerTtt());
        cbImc40.setSelected(p.isImc40());
        cbImc30.setSelected(p.isImc30());
        cbDialyse.setSelected(p.isDialyse());
        cbCirrhose.setSelected(p.isCirrhose());
        cbGrs3T.setSelected(p.isGross3T());

        cbIsolement.setSelected(p.isIsolement());
        cbPrecarite.setSelected(p.isPrecarite());
        cbDiffLinguistique.setSelected(p.isDiffLinguistique());
        cbTroubleNeuroPsy.setSelected(p.isTroubleNeuroPsy());
        cbMoyenComm.setSelected(p.isManqueMoyenComm());

        cbProcheFragile.setSelected(p.isProcheFragile());
        cbPieceConfinement.setSelected(p.isPieceConfinement());

        cbGrPolyp22.setSelected(p.isGrPolyp22());
        cbGrSat90.setSelected(p.isGrSat90());
        cbGrSat95.setSelected(p.isGrSat95());
        cbGrPas90.setSelected(p.isGrPas90());
        cbGrDeshyd.setSelected(p.isGrDeshyd());
        cbGrAltConsc.setSelected(p.isGrAltConsc());
        cbGAbeg.setSelected(p.isGrAbeg());

        tfDateTestCovid1.setValue(p.getTestCovid1Date());
        cbTestCovid1Result.setSelected(p.isTestCovid1Result());
        cboxTypeTestCovid1.setSelectedItem(p.getTestCovid1Type());

        tfDateTestCovid2.setValue(p.getTestCovid2Date());
        cbTestCovid2Result.setSelected(p.isTestCovid2Result());
        cboxTypeTestCovid2.setSelectedItem(p.getTestCovid2Type());

        cboxCovidDiag.setSelectedIndex(p.getCovidDiag());

        mdConsult = new ConsultTableModel(p.getConsults());
        tbConsult.setModel(mdConsult);

        updateDerived();
    }

    private void fieldsToPatient() {
        p.setNom(tfNom.getText());
        p.setTelephone(tfTelephone.getText());
        p.setEmail(tfEmail.getText());
        p.setAge(((Number) tfAge.getValue()).intValue());

        p.setDateContact((LocalDate) tfDateContact.getValue());
        p.setDateSymptomes((LocalDate) tfDateSymp.getValue());
        p.setDateSymptomesRespi((LocalDate) tfDateSympResp.getValue());
        p.setDateRappel((LocalDate) tfDateRappel.getValue());
        p.setDateRappelJ7((LocalDate) tfDateRappelJ7.getValue());
        p.setDateRappelJ13((LocalDate) tfDateRappelJ13.getValue());
        p.setRappelFait(cbRappelFait.isSelected());
        p.setRappelJ7Fait(cbRappelJ7Fait.isSelected());
        p.setRappelJ13Fait(cbRappelJ13Fait.isSelected());

        p.setDateRappelManual(dateRappelEdited);

        p.setNotes(taNotes.getText());
        p.setNotesContexte(taNotesContexte.getText());

        p.setRespiChronique(cbRespiChronique.isSelected());
        p.setInsuffCardiaque(cbInsuffCardiaque.isSelected());
        p.setAtcdCV(cbAtcdCV.isSelected());
        p.setDiabete(cbDiabete.isSelected());
        p.setImmunodep(cbImmunodep.isSelected());
        p.setCancerTtt(cbCancerTtt.isSelected());
        p.setImc40(cbImc40.isSelected());
        p.setImc30(cbImc30.isSelected());
        p.setDialyse(cbDialyse.isSelected());
        p.setCirrhose(cbCirrhose.isSelected());
        p.setGross3T(cbGrs3T.isSelected());

        p.setIsolement(cbIsolement.isSelected());
        p.setPrecarite(cbPrecarite.isSelected());
        p.setDiffLinguistique(cbDiffLinguistique.isSelected());
        p.setTroubleNeuroPsy(cbTroubleNeuroPsy.isSelected());
        p.setManqueMoyenComm(cbMoyenComm.isSelected());

        p.setProcheFragile(cbProcheFragile.isSelected());
        p.setPieceConfinement(cbPieceConfinement.isSelected());

        p.setGrPolyp22(cbGrPolyp22.isSelected());
        p.setGrSat90(cbGrSat90.isSelected());
        p.setGrSat95(cbGrSat95.isSelected());
        p.setGrPas90(cbGrPas90.isSelected());
        p.setGrDeshyd(cbGrDeshyd.isSelected());
        p.setGrAltConsc(cbGrAltConsc.isSelected());
        p.setGrAbeg(cbGAbeg.isSelected());

        p.setTestCovid1Date((LocalDate) tfDateTestCovid1.getValue());
        p.setTestCovid1Result(cbTestCovid1Result.isSelected());
        p.setTestCovid1Type((String) cboxTypeTestCovid1.getSelectedItem());

        p.setTestCovid2Date((LocalDate) tfDateTestCovid2.getValue());
        p.setTestCovid2Result(cbTestCovid2Result.isSelected());
        p.setTestCovid2Type((String) cboxTypeTestCovid2.getSelectedItem());

        p.setCovidDiag(cboxCovidDiag.getSelectedIndex());

        p.setConsults(((ConsultTableModel) tbConsult.getModel()).data);
    }

    private void layoutWithGridBag() {
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3, 3, 3, 3);

        c.gridy = 0;

        c.gridx = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        add(lbNom, c);
        c.gridx = 2;
        c.gridwidth = 10;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(tfNom, c);

        ///////////
        c.gridy++;

        c.gridx = 0;
        c.gridwidth = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbAge, c);
        c.gridx = 2;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(tfAge, c);

        c.gridx = 4;
        c.gridwidth = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbTelephone, c);
        c.gridx = 6;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(tfTelephone, c);

        c.gridx = 8;
        c.gridwidth = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbEmail, c);
        c.gridx = 10;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(tfEmail, c);

        ///////////
        c.gridy++;

        c.gridx = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbDateContact, c);
        c.gridx = 2;
        c.gridwidth = 1;
        c.weightx = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(tfDateContact, c);
        c.gridx = 3;
        c.gridwidth = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        add(lbJContact, c);

        c.gridx = 4;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbDateSymp, c);
        c.gridx = 6;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(tfDateSymp, c);

        c.gridx = 8;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbDateSympResp, c);
        c.gridx = 10;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(tfDateSympResp, c);

        ///////////
        c.gridy++;

        c.gridx = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbDateRappelJ7, c);
        c.gridx = 2;
        c.gridwidth = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(tfDateRappelJ7, c);
        c.gridx = 3;
        c.gridwidth = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        add(lbJRappelJ7, c);

        c.gridx = 6;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbRappelJ7Fait, c);

        ///////////
        c.gridy++;

        c.gridx = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbDateRappelJ13, c);
        c.gridx = 2;
        c.gridwidth = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(tfDateRappelJ13, c);
        c.gridx = 3;
        c.gridwidth = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        add(lbJRappelJ13, c);

        c.gridx = 6;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbRappelJ13Fait, c);

        ///////////
        c.gridy++;

        c.gridx = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbDateRappel, c);
        c.gridx = 2;
        c.gridwidth = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(tfDateRappel, c);
        c.gridx = 3;
        c.gridwidth = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        add(lbJRappel, c);

        c.gridx = 6;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbRappelFait, c);

        ///////////
        c.gridy++;

        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbRespiChronique, c);
        c.gridx = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbInsuffCardiaque, c);

        c.gridx = 8;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbIsolement, c);
        c.gridx = 10;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbPrecarite, c);

        ///////////
        c.gridy++;

        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbAtcdCV, c);
        c.gridx = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbDiabete, c);

        c.gridx = 8;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbDiffLinguistique, c);
        c.gridx = 10;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbTroubleNeuroPsy, c);

        ///////////
        c.gridy++;

        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbImmunodep, c);
        c.gridx = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbCancerTtt, c);

        c.gridx = 8;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbMoyenComm, c);

        c.gridx = 10;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbProcheFragile, c);

        ///////////
        c.gridy++;

        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbImc30, c);
        c.gridx = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbDialyse, c);

        c.gridx = 4;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbImc40, c);

        ///////////
        c.gridy++;

        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbCirrhose, c);
        c.gridx = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbGrs3T, c);

        c.gridx = 8;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbPieceConfinement, c);

        ///////////
        c.gridy++;

        c.gridx = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbGrPolyp22, c);
        c.gridx = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        add(cbGrSat95, c);
        c.gridx = 3;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        add(cbGrSat90, c);

        c.gridx = 4;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbGrPas90, c);
        c.gridx = 6;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbGrDeshyd, c);
        c.gridx = 8;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbGrAltConsc, c);
        c.gridx = 10;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbGAbeg, c);

        ////////////////////////////////
        c.gridy++;

        c.gridx = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        add(lbTestCovid1, c);
        c.gridx = 1;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        add(lbTestCovid1Date, c);
        c.gridx = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;
        add(tfDateTestCovid1, c);
        c.gridx = 4;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbTestCovid1Type, c);
        c.gridx = 6;
        c.weightx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;
        add(cboxTypeTestCovid1, c);
        c.gridx = 8;
        c.weightx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;
        add(cbTestCovid1Result, c);

        /////////////////////////////////////////////
        c.gridy++;

        c.gridx = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        add(lbTestCovid2, c);
        c.gridx = 1;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        add(lbTestCovid2Date, c);
        c.gridx = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;
        add(tfDateTestCovid2, c);
        c.gridx = 4;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbTestCovid2Type, c);
        c.gridx = 6;
        c.weightx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;
        add(cboxTypeTestCovid2, c);
        c.gridx = 8;
        c.weightx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;
        add(cbTestCovid2Result, c);

        //////////////////////////////
        c.gridy++;

        c.gridx = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbCovidDiag, c);
        c.gridx = 2;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(cboxCovidDiag, c);

        ///////////
        c.gridy++;

        c.gridx = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridheight = 1;
        c.gridwidth = 2;
        add(lbNotes, c);
        c.gridx = 2;
        c.weightx = 0;
        c.weighty = 2;
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 10;
        c.gridheight = 3;
        add(spNotes, c);

        spNotes.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        spNotes.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        taNotes.setLineWrap(true);

        ///////////
        c.gridy += 3;

        c.gridx = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridheight = 1;
        c.gridwidth = 2;
        add(lbNotesContexte, c);
        c.gridx = 2;
        c.weightx = 0;
        c.weighty = 2;
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 10;
        c.gridheight = 3;
        add(spNotesContexte, c);

        spNotesContexte.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        spNotesContexte.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        taNotesContexte.setLineWrap(true);

        ///////////
        c.gridy += 3;

        c.gridx = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridheight = 1;
        c.gridwidth = 2;
        add(lbConsult, c);
        c.gridx = 2;
        c.weightx = 0;
        c.weighty = 4;
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 10;
        c.gridheight = 3;
        add(spConsult, c);

        spConsult.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        spConsult.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        ///////////
        c.gridy += 3;

        c.gridx = 8;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridheight = 1;
        c.gridwidth = 1;
        add(btAddCS, c);
        c.gridx = 9;
        c.weightx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridheight = 1;
        c.gridwidth = 1;
        add(btDelCS, c);

        ///////////
        c.gridy++;

        c.gridx = 3;
        c.weightx = 0;
        c.weighty = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridheight = 1;
        c.gridwidth = 4;
        add(btOk, c);
        c.gridx = 10;
        c.weightx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridheight = 1;
        c.gridwidth = 1;
        add(btCancel, c);

    }

    private void setup() {

        btOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                ok();
            }
        });

        btCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                cancel();
            }
        });

        btAddCS.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fieldsToPatient();
                p = JpaDao.getInstance().merge(p);
                Consult c = new Consult(p);
                ConsultEditWindow w = new ConsultEditWindow(PatientEditPanel.this, c);
                w.pack();
                w.setVisible(true);
            }

        });

        btDelCS.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                int row = tbConsult.getSelectedRow();
                if (row < 0)
                    return; // should not happen

                Consult c = (Consult) mdConsult.data.toArray()[row];

                String message = "Supprimer la CS de " + c.getCsDate() + " ?";
                String title = "Confirmation de suppression";
                // display the JOptionPane showConfirmDialog
                int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.OK_CANCEL_OPTION);
                if (reply != JOptionPane.OK_OPTION)
                    return;

                p = JpaDao.getInstance().merge(p);
                p.getConsults().remove(c);
                JpaDao.getInstance().remove(c);
                JpaDao.getInstance().commit();

                refreshMe();
            }
        });

        btDelCS.setEnabled(false);

        ListSelectionModel listMod = tbConsult.getSelectionModel();
        listMod.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tbConsult.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tbConsult.getSelectedRow();
                    Consult c = (Consult) mdConsult.data.toArray()[row];
                    fieldsToPatient();
                    ConsultEditWindow w = new ConsultEditWindow(PatientEditPanel.this, c);
                    w.pack();
                    w.setVisible(true);
                }

                btDelCS.setEnabled(tbConsult.getSelectedRow() >= 0);

            }
        });

        FocusListener dateFL = new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                try {
                    ((JFormattedTextField) e.getComponent()).commitEdit();
                    updateDerived();
                } catch (Exception ee) {
                }
            }
        };

        tfDateSymp.addFocusListener(dateFL);
        tfDateRappelJ7.addFocusListener(dateFL);
        tfDateRappelJ13.addFocusListener(dateFL);
        tfDateRappel.addFocusListener(dateFL);

        cbGrSat90.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cbGrSat90.isSelected())
                    cbGrSat95.setSelected(true);
            }
        });

        cbImc40.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cbImc40.isSelected())
                    cbImc30.setSelected(true);
            }
        });


        tfDateRappel.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                if (!dateRappelEdited) {
                    try {
                        tfDateRappel.commitEdit();
                        LocalDate d = (LocalDate) tfDateRappel.getValue();
                        if (!d.equals(autoDateRappel))
                            dateRappelEdited = true;
                    } catch (Exception ee) {
                    }
                }
            }
        });

        tbConsult.getColumnModel().getColumn(0).setPreferredWidth(80);
        tbConsult.getColumnModel().getColumn(1).setPreferredWidth(80);
        tbConsult.getColumnModel().getColumn(2).setPreferredWidth(50);
        tbConsult.getColumnModel().getColumn(3).setPreferredWidth(50);
        tbConsult.getColumnModel().getColumn(4).setPreferredWidth(50);
        tbConsult.getColumnModel().getColumn(5).setPreferredWidth(300);

        tbConsult.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    }

    protected void cancel() {
        if (topFrame != null) {
            topFrame.dispose();
        }
    }

    protected void ok() {
        try {
            fieldsToPatient();
            p = JpaDao.getInstance().merge(p);
            JpaDao.getInstance().commit();
            JpaDao.getInstance().close();
            MainPanel.refresh();
            if (topFrame != null)
                topFrame.dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur dans un champ. " + e.getLocalizedMessage() + "\n" + e.getStackTrace()[0], "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    public void refreshMe() {

        p = JpaDao.getInstance().merge(p);
        patientToFields();
        mdConsult.setData(p.getConsults());
        mdConsult.fireTableDataChanged();

        getParent().repaint();
        JpaDao.getInstance().close();
    }

    static class ConsultTableModel extends AbstractTableModel {

        private static final long serialVersionUID = -1176415256312081178L;

        public ConsultTableModel() {
        }

        public ConsultTableModel(SortedSet<Consult> data) {
            this.data = data;
        }

        public void setData(SortedSet<Consult> data) {
            this.data = data;
        }

        SortedSet<Consult> data;

        public Object getValueAt(int row, int col) {
            if (row < 0 || row >= data.size())
                return null;
            Collection<Consult> cc = data;
            Iterator<Consult> it = cc.iterator();
            for (int i = 0; i < row; i++)
                it.next();
            return getConsultAttribute(it.next(), col);
        }

        public static Object getConsultAttribute(Consult c, int col) {

            switch (col) {
                case 0:
                    String jj = "";
                    if (c.getPatient().getDateSymptomes() != null)
                        jj = "— J" + (c.getPatient().getDateSymptomes().until(c.getCsDate(), ChronoUnit.DAYS));
                    return c.getCsDate().format(dateFormat) + " " + jj;
                case 1:
                    return c.getCsType();
                case 2:
                    return fltFormat.format(c.getTemperature());
                case 3:
                    return c.getSatO2();
                case 4:
                    return Consult.evolutionStr[c.getEvolution()];
                case 5:
                    return c.getSummary();
                // toux, essoufflement, gêne respi, gut feeling, douleur tho

                default:
                    return null;
            }

        }

        String[] cNames = { "date", "type", "temp", "sat", "evol", "symptômes" };

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

    }
}
