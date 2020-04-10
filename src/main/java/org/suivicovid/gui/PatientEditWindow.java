package org.suivicovid.gui;

import java.awt.Dimension;

import javax.swing.JDialog;

import org.suivicovid.data.Patient;

public class PatientEditWindow extends JDialog {

    /**
     *
     */
    private static final long serialVersionUID = -5078487923005257308L;
    PatientEditPanel p;
    MainPanel parent;

    public PatientEditWindow(MainPanel parent, Patient pat) {
        super(parent.getParentFrame(), true);
        p = new PatientEditPanel(this, pat);
        getContentPane().add(p);
        setPreferredSize(new Dimension(1000, 800));
        setTitle("Patient");
        this.parent = parent;
    }

}
