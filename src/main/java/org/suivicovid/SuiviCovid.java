package org.suivicovid;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.suivicovid.dao.JpaDao;
import org.suivicovid.gui.MainPanel;
import org.suivicovid.sync.Sync;

public class SuiviCovid {

    public static void main(String[] args) {
        // needed to talk to macs
        System.setProperty("java.net.preferIPv4Stack" , "true");

        try {
            JpaDao.getInstance().checkAndUpdateDb();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Impossible de démarrer : une autre version de SuiviCovid est peut-être active ?", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(-1);
        }

        Sync.start();
        JFrame f = new JFrame();

        MainPanel pp = new MainPanel(f);

        f.setPreferredSize(new Dimension(900, 600));
        f.getContentPane().add(pp);
        f.pack();
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setTitle("SuiviCovid 2.2");
    }
}
