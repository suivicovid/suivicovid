package org.suivicovid.sync;

import java.lang.reflect.InvocationTargetException;
import java.util.SortedSet;

import org.apache.commons.beanutils.BeanUtils;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.suivicovid.dao.JpaDao;
import org.suivicovid.data.Consult;
import org.suivicovid.data.Deleted;
import org.suivicovid.data.Patient;
import org.suivicovid.gui.MainPanel;
import org.suivicovid.sync.Info.Status;

// Quick implementation of a peer to peer sync with jgroups

public class Sync {

    JChannel channel;

    private static Sync instance;

    private volatile boolean active;

    public static void start() {
        instance = new Sync();
    }

    /**
     * @return the instance
     */
    public static Sync getInstance() {
        return instance;
    }

    private Sync() {
        active = JpaDao.getInstance().isNetworked();
        try {
            // channel = new JChannel("udp.xml");
            channel = new JChannel();
            channel.setDiscardOwnMessages(true);
            channel.setReceiver(new ReceiverAdapter() {
                public void receive(Message msg) {
                    System.out.println("received msg from " + msg.getSrc() + ": " + msg.getObject());
                    process(msg.getObject());
                }
            });
            channel.connect("SuiviCovid");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (active)
            send(new Info(Status.STARTING));
    }

    public void activate(boolean active) {
        this.active = active;
        if (active)
            send(new Info(Status.STARTING));
    }

    public void send(Object o) {
        if (!active)
            return;
        System.out.println("sending " + o);
        try {
            channel.send(new Message(null, o));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void process(Object o) {
        if (!active)
            return;
        try {
            System.out.println("received " + o);
            MainPanel.showActiveNet();
            if (o instanceof Info) {
                // quick for 2.2 : we send all objects
                // we could broadcast UUIDs with last update time, and request back the needed
                // ones
                // network optim question: should we send a big message with many objects?
                // not so many objects let's keep simple for 2.2

                // we need to send both Patient and Consult objects otherwise difficult to merge
                // we need to keep track of deleted uuid that should not be repropagated 

                if (((Info) o).getStatus() == Status.STARTING) {
                    JpaDao.getInstance().getAll(Patient.class).stream().forEach(x -> send(x));
                    JpaDao.getInstance().getAll(Consult.class).stream().forEach(x -> send(x));
                    JpaDao.getInstance().getAll(Deleted.class).stream().forEach(x -> send(x));
                }
            }
            if (o instanceof Patient) {
                Patient p = (Patient) o;
                System.out.println("+++ Patient " + p.getNom() + " " + p.getUuid());
                if (JpaDao.getInstance().getDeletedFromUID(p.getUuid()) != null) {
                    System.out.println("+++ deleted, ignored");
                    return;
                }

                Patient localP = JpaDao.getInstance().getPatientFromUID(p.getUuid());
                if (localP == null) {
                    // new patient, just save it.
                    System.out.println("+++new patient");
                    p.clearId();
                    // the object could have kept hibernate detached status through serialization,
                    // better to make a new copy

                    Patient newP = new Patient();

                    BeanUtils.copyProperties(newP, p);
                    newP.getConsults().clear();

                    p.getConsults().stream().forEach(c -> {
                        try {
                            Consult newC = new Consult();
                            BeanUtils.copyProperties(newC, c);
                            newC.setFromSync();
                            newP.getConsults().add(newC);
                            newC.setPatient(newP);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    });

                    System.out.println(BeanUtils.describe(p));
                    newP.setFromSync();
                    JpaDao.getInstance().persist(newP);
                    JpaDao.getInstance().commit();
                    return;
                }

                System.out.println("++local " + localP.getUpdated() + " remote " + p.getUpdated());
                if (!localP.getUpdated().before(p.getUpdated()))
                    return;

                try {
                    SortedSet<Consult> cs = localP.getConsults();
                    BeanUtils.copyProperties(localP, p);
                    localP.setConsults(cs);
                    localP.setUpdated(p.getUpdated());
                    System.out.println("+++copied, age " + localP.getAge() + " updated" + localP.getUpdated() + " "
                            + localP.getId());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }

                localP.setFromSync();
                JpaDao.getInstance().merge(localP);
                JpaDao.getInstance().commit();
            }
            if (o instanceof Consult) {
                Consult c = (Consult) o;
                System.out.println("+++ Consult " + c.getPatient().getNom() + " " + c.getCsDate() + " " + c.getUuid());
                if (JpaDao.getInstance().getDeletedFromUID(c.getUuid()) != null) {
                    System.out.println("+++ deleted, ignored");
                    return;
                }

                Consult localC = JpaDao.getInstance().getConsultFromUID(c.getUuid());
                if (localC == null) {
                    // new consult, just save it.
                    // we received the patient with the cs. Could just transmit its uuid ,and make
                    // the patient transient for serialisation
                    System.out.println("+++new consult");
                    Consult newC = new Consult();

                    BeanUtils.copyProperties(newC, c);
                    newC.setFromSync();

                    Patient localP = JpaDao.getInstance().getPatientFromUID(c.getPatient().getUuid());
                    localP.getConsults().add(newC);
                    newC.setPatient(localP);

                    JpaDao.getInstance().persist(newC);
                    JpaDao.getInstance().commit();
                    return;
                }
                System.out.println("++local " + localC.getUpdated() + " remote " + c.getUpdated());
                if (!localC.getUpdated().before(c.getUpdated()))
                    return;

                try {
                    Patient pp = localC.getPatient();
                    BeanUtils.copyProperties(localC, c);
                    localC.setPatient(pp);
                    localC.setUpdated(c.getUpdated());
                    System.out.println(
                            "+++copied " + localC.getUpdated() + " " + localC.getCreated() + " " + localC.getId());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }

                localC.setFromSync();
                JpaDao.getInstance().merge(localC);
                JpaDao.getInstance().commit();
            }
            if (o instanceof Deleted) {
                Deleted d = (Deleted) o;
                if (JpaDao.getInstance().getDeletedFromUID(d.getUuid()) != null) {
                    return;
                }
                Deleted newD = new Deleted();
                newD.setUuid(d.getUuid());
                newD.setFromSync();
                JpaDao.getInstance().persist(newD);

                Patient delP = JpaDao.getInstance().getPatientFromUID(d.getUuid());
                if (delP != null) {
                    delP.setFromSync();
                    JpaDao.getInstance().remove(delP);
                }
                Consult delC = JpaDao.getInstance().getConsultFromUID(d.getUuid());
                if (delC != null) {
                    delC.setFromSync();
                    Patient pp = delC.getPatient();
                    pp.getConsults().remove(delC);
                    JpaDao.getInstance().remove(delC);
                }

                JpaDao.getInstance().commit();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JpaDao.getInstance().close();
            MainPanel.refresh();
        }
    }

    public void close() {
        channel.close();
    }
}