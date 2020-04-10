package org.suivicovid.gui;

import java.awt.Dimension;

import javax.swing.JDialog;

import org.suivicovid.data.Consult;

public class ConsultEditWindow extends JDialog {

    private static final long serialVersionUID = 8775243090378453121L;
    ConsultEditPanel p;
    PatientEditPanel parent;

    public ConsultEditWindow(PatientEditPanel parent, Consult c) {
        super((JDialog) parent.getTopFrame(), true);
        p = new ConsultEditPanel(this, c);
        getContentPane().add(p);
        setPreferredSize(new Dimension(1000, 800));
        setTitle("Consultation");
        this.parent = parent;
    }

}
