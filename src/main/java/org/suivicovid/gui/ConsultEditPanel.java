package org.suivicovid.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.suivicovid.dao.JpaDao;
import org.suivicovid.data.Consult;
import org.suivicovid.data.Consult.CsType;
import org.suivicovid.data.Patient;

public class ConsultEditPanel extends JPanel {

    private static final long serialVersionUID = 1581716862999705263L;
    private ConsultEditWindow topFrame;
    private Consult c;

    private NumberFormat intFormat = new DecimalFormat("###0");
    private NumberFormat fltFormat = new DecimalFormat("###0.0");
    static private JFormattedTextField.AbstractFormatter dateFormatter = new PatientEditPanel.LocalDateFormatter();

    private JFormattedTextField tfDate = new JFormattedTextField(dateFormatter);
    private JLabel lbDate = new JLabel("date");

    private JComboBox<CsType> cboxType = new JComboBox<>(CsType.values());
    private JLabel lbType = new JLabel("type");

    private JFormattedTextField tfTemperature = new JFormattedTextField(fltFormat);
    private JLabel lbTemperature = new JLabel("température");

    private JFormattedTextField tfPas = new JFormattedTextField(intFormat);
    private JLabel lbPas = new JLabel("PA S mmHg");
    private JFormattedTextField tfPad = new JFormattedTextField(intFormat);
    private JLabel lbPad = new JLabel("PA D mmHg");

    private JFormattedTextField tfFreqRespi = new JFormattedTextField(intFormat);
    private JLabel lbFreqRespi = new JLabel("freq resp/mn");

    private JFormattedTextField tfSatO2 = new JFormattedTextField(intFormat);
    private JLabel lbSatO2 = new JLabel("satO2%");

    private JFormattedTextField tfFreqCardio = new JFormattedTextField(intFormat);
    private JLabel lbFreqCardio = new JLabel("freq cardio/mn");

    private JFormattedTextField tfSentezVous = new JFormattedTextField(intFormat);
    private JLabel lbSentezVous = new JLabel("cmt vs sentez-vs 0-10");

    private String[] plusValues = { "0", "+", "++", "+++" };

    private JComboBox<String> cboxToux = new JComboBox<>(plusValues);
    private JLabel lbToux = new JLabel("toux");

    private JComboBox<String> cboxAsthenie = new JComboBox<>(plusValues);
    private JLabel lbAsthenie = new JLabel("asthénie");

    private JComboBox<String> cboxGeneRespi = new JComboBox<>(plusValues);
    private JLabel lbGeneRespi = new JLabel("gêne respi");

    private String[] effortValues = { "non", "effort intense", "effort léger", "au repos" };

    private JComboBox<String> cboxEssEffort = new JComboBox<>(effortValues);
    private JLabel lbEssEffort = new JLabel("essouf effort");

    private JCheckBox cbAnosmie = new JCheckBox("anosmie/agueusie");
    private JCheckBox cbGorge = new JCheckBox("signes ORL");
    private JCheckBox cbDiarrhee = new JCheckBox("signes digestifs");
    private JCheckBox cbBoireManger = new JCheckBox("diff boire/manger");

    private JFormattedTextField tfComptez = new JFormattedTextField(intFormat);
    private JLabel lbComptez = new JLabel("comptez");

    private JComboBox<String> cboxCourbatures = new JComboBox<>(plusValues);
    private JLabel lbCourbatures = new JLabel("courbatures");

    private JComboBox<String> cboxMauxTete = new JComboBox<>(plusValues);
    private JLabel lbMauxTete = new JLabel("maux tête");

    private JComboBox<String> cboxThorax = new JComboBox<>(plusValues);
    private JLabel lbThorax = new JLabel("doul thorax");

    private JComboBox<String> cboxGutFeeling = new JComboBox<>(plusValues);
    private JLabel lbGutFeeling = new JLabel("gut feeling");

    private JComboBox<String> cboxEvol = new JComboBox<>(Consult.evolutionStr);
    private JLabel lbEvol = new JLabel("évolution");

    private JButton btOk = new JButton("OK");
    private JButton btCancel = new JButton("annuler");

    public ConsultEditPanel(ConsultEditWindow topFrame, Consult c) {
        this.topFrame = topFrame;
        layoutWithGridBag();

        setup();

        this.c = c;

        consultToFields();

    }

    private void consultToFields() {
        tfDate.setValue(c.getCsDate());
        cboxType.setSelectedItem(c.getCsType());

        tfTemperature.setValue(c.getTemperature());
        tfPas.setValue(c.getPas());
        tfPad.setValue(c.getPad());

        tfFreqRespi.setValue(c.getFreqRespi());
        tfSatO2.setValue(c.getSatO2());
        tfFreqCardio.setValue(c.getFreqCardio());

        if (c.getSaSentezVous() < 0)
            tfSentezVous.setValue(null);
        else
            tfSentezVous.setValue(c.getSaSentezVous());

        cboxToux.setSelectedIndex(c.getToux());
        cboxAsthenie.setSelectedIndex(c.getAsthenie());
        cboxGeneRespi.setSelectedIndex(c.getGeneRespi());
        cboxEssEffort.setSelectedIndex(c.getEssoufleEffort());
        if (c.isEssoufleRepos())
            cboxEssEffort.setSelectedIndex(3);

        cbAnosmie.setSelected(c.isAnosmie());
        cbGorge.setSelected(c.isGorge());
        cbDiarrhee.setSelected(c.isDiarrhee());
        cbBoireManger.setSelected(c.isBoireManger());

        if (c.getComptez() < 0)
            tfComptez.setValue(null);
        else
            tfComptez.setValue(c.getComptez());

        cboxCourbatures.setSelectedIndex(c.getCourbatures());
        cboxMauxTete.setSelectedIndex(c.getMauxTete());
        cboxThorax.setSelectedIndex(c.getThorax());
        cboxGutFeeling.setSelectedIndex(c.getGutFeeling());

        cboxEvol.setSelectedIndex(c.getEvolution());

    }

    private void fieldsToConsult() {
        c.setCsDate((LocalDate) tfDate.getValue());
        c.setCsType((CsType) cboxType.getSelectedItem());

        c.setTemperature(((Number) tfTemperature.getValue()).floatValue());
        c.setPas(((Number) tfPas.getValue()).intValue());
        c.setPad(((Number) tfPad.getValue()).intValue());

        c.setFreqRespi(((Number) tfFreqRespi.getValue()).intValue());
        c.setSatO2(((Number) tfSatO2.getValue()).intValue());
        c.setFreqCardio(((Number) tfFreqCardio.getValue()).intValue());

        if (tfSentezVous.getValue() == null)
            c.setSaSentezVous(-1);
        else
            c.setSaSentezVous(((Number) tfSentezVous.getValue()).intValue());

        c.setToux(cboxToux.getSelectedIndex());
        c.setAsthenie(cboxAsthenie.getSelectedIndex());
        c.setGeneRespi(cboxGeneRespi.getSelectedIndex());

        if (cboxEssEffort.getSelectedIndex() == 3) {
            c.setEssoufleRepos(true);
            c.setEssoufleEffort(0);
        } else {
            c.setEssoufleRepos(false);
            c.setEssoufleEffort(cboxEssEffort.getSelectedIndex());
        }

        c.setAnosmie(cbAnosmie.isSelected());
        c.setGorge(cbGorge.isSelected());
        c.setDiarrhee(cbDiarrhee.isSelected());
        c.setBoireManger(cbBoireManger.isSelected());

        if (tfComptez.getValue() == null)
            c.setComptez(-1);
        else
            c.setComptez(((Number) tfComptez.getValue()).intValue());

        c.setCourbatures(cboxCourbatures.getSelectedIndex());
        c.setMauxTete(cboxMauxTete.getSelectedIndex());
        c.setThorax(cboxThorax.getSelectedIndex());
        c.setGutFeeling(cboxGutFeeling.getSelectedIndex());

        c.setEvolution(cboxEvol.getSelectedIndex());

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
        add(lbDate, c);
        c.gridx = 2;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(tfDate, c);

        c.gridx = 4;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        add(lbType, c);
        c.gridx = 6;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(cboxType, c);

        ///////////
        c.gridy++;

        c.gridx = 0;
        c.gridwidth = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbTemperature, c);
        c.gridx = 2;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(tfTemperature, c);

        c.gridx = 4;
        c.gridwidth = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbPas, c);
        c.gridx = 6;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(tfPas, c);

        c.gridx = 8;
        c.gridwidth = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbPad, c);
        c.gridx = 10;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(tfPad, c);

        ///////////
        c.gridy++;

        c.gridx = 0;
        c.gridwidth = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbSatO2, c);
        c.gridx = 2;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(tfSatO2, c);

        c.gridx = 4;
        c.gridwidth = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbFreqRespi, c);
        c.gridx = 6;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(tfFreqRespi, c);

        c.gridx = 8;
        c.gridwidth = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbFreqCardio, c);
        c.gridx = 10;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(tfFreqCardio, c);

        ///////////
        c.gridy++;

        c.gridx = 0;
        c.gridwidth = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbSentezVous, c);
        c.gridx = 2;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(tfSentezVous, c);

        c.gridx = 4;
        c.gridwidth = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbToux, c);
        c.gridx = 6;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(cboxToux, c);

        c.gridx = 8;
        c.gridwidth = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbGeneRespi, c);
        c.gridx = 10;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(cboxGeneRespi, c);

        ///////////
        c.gridy++;

        c.gridx = 0;
        c.gridwidth = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbEssEffort, c);
        c.gridx = 2;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(cboxEssEffort, c);

        c.gridx = 4;
        c.gridwidth = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbAsthenie, c);
        c.gridx = 6;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;
        add(cboxAsthenie, c);

        c.gridx = 8;
        c.gridwidth = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbComptez, c);
        c.gridx = 10;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(tfComptez, c);

        ///////////
        c.gridy++;

        c.gridx = 0;
        c.gridwidth = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbAnosmie, c);
        c.gridx = 2;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(cbGorge, c);

        c.gridx = 4;
        c.gridwidth = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cbDiarrhee, c);
        c.gridx = 6;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(cbBoireManger, c);

        c.gridx = 8;
        c.gridwidth = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(cboxMauxTete, c);

        ///////////
        c.gridy++;

        c.gridx = 0;
        c.gridwidth = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbCourbatures, c);
        c.gridx = 2;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(cboxCourbatures, c);

        c.gridx = 4;
        c.gridwidth = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbMauxTete, c);
        c.gridx = 6;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(cboxMauxTete, c);

        c.gridx = 8;
        c.gridwidth = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbThorax, c);
        c.gridx = 10;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(cboxThorax, c);

        ///////////
        c.gridy++;

        c.gridx = 0;
        c.gridwidth = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbGutFeeling, c);
        c.gridx = 2;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(cboxGutFeeling, c);

        c.gridx = 4;
        c.gridwidth = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        add(lbEvol, c);
        c.gridx = 6;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(cboxEvol, c);

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

    }

    protected void cancel() {
        if (topFrame != null) {
            topFrame.dispose();
        }
    }

    protected void ok() {
        try {
            fieldsToConsult();

            c = JpaDao.getInstance().merge(c);
            Patient p = c.getPatient();
            p = JpaDao.getInstance().merge(p);

            Consult old = p.getConsults().stream().filter(cc -> cc.getCsDate().equals(c.getCsDate())).findFirst()
                    .orElse(null);

            if (old != null && old != c) {
                JOptionPane.showMessageDialog(null, "Déjà une consultation entrée pour le "
                        + c.getCsDate().format(DateTimeFormatter.ISO_LOCAL_DATE) + " fusionner ou changer la date !",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            c.setPatient(p);
            p.getConsults().add(c); // NOP if already there
            p.updateFromConsult(c);
            JpaDao.getInstance().commit();

            if (topFrame != null) {
                topFrame.dispose();
                topFrame.parent.refreshMe();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur dans un champ. " + e.getLocalizedMessage() + "\n" + e.getStackTrace()[0], "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
