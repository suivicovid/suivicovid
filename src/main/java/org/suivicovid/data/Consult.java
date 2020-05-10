package org.suivicovid.data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import org.suivicovid.dao.JpaDao;
import org.suivicovid.sync.Sync;

@Entity
public class Consult implements Comparable<Consult>, Serializable {
    private static final long serialVersionUID = 2616058929474166351L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "patientId")
    private Patient patient;

    private LocalDate csDate = LocalDate.now();

    public enum CsType {
        Physique, Video, Telephone, Teleconsult;
    }

    @Enumerated(EnumType.STRING)
    private CsType csType = CsType.Telephone;
    private double temperature;
    private double pas, pad;
    private int freqRespi;
    private int satO2; // %
    private int freqCardio;
    private int saSentezVous = -1;
    private int toux; // 0-3 = +++
    private int geneRespi; // 0-3 = +++
    @Column(columnDefinition = "int default 0")
    private int asthenie; // 0-3 = +++
    private int essoufleEffort; // 0 non, 1 effort intense, 2 effort leger
    private boolean essoufleRepos;
    private boolean anosmie;
    private int comptez = -1;
    private int courbatures; // 0-10
    private int mauxTete; // 0-10
    private int thorax; // 0-10
    private boolean gorge; // => ORL
    private boolean diarrhee; // => digestif
    private boolean boireManger;

    private int gutFeeling; // 0-3

    @Column(nullable = false, columnDefinition = "int default 3")
    private int evolution = 3;
    public static String[] evolutionStr = { "stable", "amélioration", "aggravation", "-" };

    @Column(name = "s1")
    private String s1;
    @Column(name = "s2")
    private String s2;
    @Column(name = "s3")
    private String s3;
    @Column(name = "s4")
    private String s4;
    @Column(name = "s5")
    private String s5;

    @Column(name = "b1")
    private boolean b1;
    @Column(name = "b2")
    private boolean b2;
    @Column(name = "b3")
    private boolean b3;
    @Column(name = "b4")
    private boolean b4;
    @Column(name = "b5")
    private boolean b5;

    @Column(name = "i1")
    private int i1;
    @Column(name = "i2")
    private int i2;
    @Column(name = "i3")
    private int i3;
    @Column(name = "i4")
    private int i4;
    @Column(name = "i5")
    private int i5;

    private String uuid;
    private Timestamp created;
    private Timestamp updated;

    @Transient
    boolean fromSync;

    public Consult() {
    }

    public Consult(Patient p) {
        this.patient = p;
    }

    public long getId() {
        return id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDate getCsDate() {
        return csDate;
    }

    public void setCsDate(LocalDate csDate) {
        this.csDate = csDate;
    }

    public CsType getCsType() {
        return csType;
    }

    public void setCsType(CsType csType) {
        this.csType = csType;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getPas() {
        return pas;
    }

    public void setPas(double pas) {
        this.pas = pas;
    }

    public double getPad() {
        return pad;
    }

    public void setPad(double pad) {
        this.pad = pad;
    }

    public int getFreqRespi() {
        return freqRespi;
    }

    public void setFreqRespi(int freqRespi) {
        this.freqRespi = freqRespi;
    }

    public int getSatO2() {
        return satO2;
    }

    public void setSatO2(int satO2) {
        this.satO2 = satO2;
    }

    public int getFreqCardio() {
        return freqCardio;
    }

    public void setFreqCardio(int freqCardio) {
        this.freqCardio = freqCardio;
    }

    public int getSaSentezVous() {
        return saSentezVous;
    }

    public void setSaSentezVous(int saSentezVous) {
        this.saSentezVous = saSentezVous;
    }

    public int getToux() {
        return toux;
    }

    public void setToux(int toux) {
        this.toux = toux;
    }

    public int getGeneRespi() {
        return geneRespi;
    }

    public void setGeneRespi(int geneRespi) {
        this.geneRespi = geneRespi;
    }

    public int getEssoufleEffort() {
        return essoufleEffort;
    }

    public void setEssoufleEffort(int essoufleEffort) {
        this.essoufleEffort = essoufleEffort;
    }

    public boolean isEssoufleRepos() {
        return essoufleRepos;
    }

    public void setEssoufleRepos(boolean essoufleRepos) {
        this.essoufleRepos = essoufleRepos;
    }

    public boolean isAnosmie() {
        return anosmie;
    }

    public void setAnosmie(boolean anosmie) {
        this.anosmie = anosmie;
    }

    public int getComptez() {
        return comptez;
    }

    public void setComptez(int comptez) {
        this.comptez = comptez;
    }

    public int getCourbatures() {
        return courbatures;
    }

    public void setCourbatures(int courbatures) {
        this.courbatures = courbatures;
    }

    public int getMauxTete() {
        return mauxTete;
    }

    public void setMauxTete(int mauxTete) {
        this.mauxTete = mauxTete;
    }

    public int getThorax() {
        return thorax;
    }

    public void setThorax(int thorax) {
        this.thorax = thorax;
    }

    public boolean isGorge() {
        return gorge;
    }

    public void setGorge(boolean gorge) {
        this.gorge = gorge;
    }

    public boolean isDiarrhee() {
        return diarrhee;
    }

    public void setDiarrhee(boolean diarrhee) {
        this.diarrhee = diarrhee;
    }

    public boolean isBoireManger() {
        return boireManger;
    }

    public void setBoireManger(boolean boireManger) {
        this.boireManger = boireManger;
    }

    /**
     * @return the gutFeeling
     */
    public int getGutFeeling() {
        return gutFeeling;
    }

    /**
     * @param gutFeeling the gutFeeling to set
     */
    public void setGutFeeling(int gutFeeling) {
        this.gutFeeling = gutFeeling;
    }

    /**
     * @return the evolution
     */
    public int getEvolution() {
        return evolution;
    }

    /**
     * @param evolution the evolution to set
     */
    public void setEvolution(int evolution) {
        this.evolution = evolution;
    }

    // ===
    // a summary

    public Set<String> getSymptomes() {
        Set<String> s = new HashSet<String>();
        if (temperature >= 38)
            s.add("F");
        if (pas <= 90 && pas > 0)
            s.add("P90");
        if (satO2 <= 90 && satO2 > 0)
            s.add("S90");
        if (satO2 <= 95 && satO2 > 0)
            s.add("S95");
        if (freqRespi > 22)
            s.add("F22");
        if (toux > 0) {
            s.add("TX");
            s.add("TX" + plus(toux));
        }
        if (anosmie)
            s.add("AO");
        if (diarrhee)
            s.add("DR");
        if (essoufleRepos)
            s.add("ESR ");
        if (essoufleEffort > 0) {
            s.add("ESS");
            s.add("ESS" + plus(essoufleEffort));
        }
        if (asthenie > 0) {
            s.add("AST");
            s.add("AST" + plus(asthenie));
        }
        if (mauxTete > 0) {
            s.add("CEPH");
            s.add("CEPH" + plus(mauxTete));
        }
        if (courbatures > 0) {
            s.add("CBT");
            s.add("CBT" + plus(courbatures));
        }
        if (geneRespi > 0) {
            s.add("GR");
            s.add("GR" + plus(geneRespi));
        }
        if (gutFeeling > 0) {
            s.add("GF");
            s.add("GF" + plus(gutFeeling));
        }
        if (comptez < 15 && comptez > 0) {
            s.add("CP" + comptez);
            s.add("CP<15");
        }
        if (comptez < 8 && comptez > 0) {
            s.add("CP<8");
        }
        if (boireManger)
            s.add("BM");
        if (gorge)
            s.add("ORL");

        return s;
    }

    public String plus(int x) {
        switch (x) {
            case 1:
                return "+";
            case 2:
                return "++";
            case 3:
                return "+++";
            default:
                return "";
        }

    }

    public String getSummary() {
        // toux, essoufflement, gêne respi, gut feeling, douleur tho

        StringBuilder b = new StringBuilder();
        if (toux > 0)
            b.append("TX").append(plus(toux)).append(":");
        if (essoufleRepos)
            b.append("ESR:");
        if (essoufleEffort > 0)
            b.append("ES").append(plus(essoufleEffort)).append(":");
        if (geneRespi > 0)
            b.append("GR").append(plus(geneRespi)).append(":");
        if (gutFeeling > 0)
            b.append("GF").append(plus(gutFeeling)).append(":");
        if (thorax > 0)
            b.append("TH").append(plus(thorax)).append(":");
        if (b.length() > 1)
            b.deleteCharAt(b.length() - 1);

        return b.toString();
    }

    @Override
    public int compareTo(Consult c) {
        return getCsDate().compareTo(c.getCsDate());
    }

    ///////////////// v1.5

    public int getAsthenie() {
        return asthenie;
    }

    public void setAsthenie(int asthenie) {
        this.asthenie = asthenie;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @return the updated
     */
    public Timestamp getUpdated() {
        return updated;
    }

    /**
     * @return the created
     */
    public Timestamp getCreated() {
        return created;
    }

    public void setUuid() {
        if (uuid == null)
            uuid = UUID.randomUUID().toString();
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @PrePersist
    public void setCreated() {
        created = new Timestamp(System.currentTimeMillis());
        updated = created;
        setUuid();
        // do we tag the patient as updated? Not needed
        if (!isFromSync() && Sync.getInstance() != null)
            Sync.getInstance().send(this);
    }

    @PreUpdate
    public void setUpdated() {
        if (!isFromSync())
            updated = new Timestamp(System.currentTimeMillis());
        if (!isFromSync() && Sync.getInstance() != null)
            Sync.getInstance().send(this);
    }

    @PreRemove
    public void preRemove() {
        if (!isFromSync()) {
            Deleted d = new Deleted();
            d.setUuid(getUuid());
            JpaDao.getInstance().persist(d);
            if (Sync.getInstance() != null)
                Sync.getInstance().send(d);
        }
    }

    public void setUpdated(Timestamp ts) {
        updated = ts;
    }

    public void setCreated(Timestamp ts) {
        created = ts;
    }

    @PostUpdate
    @PostPersist
    protected void clearSync() {
        fromSync = false;
    }

    public boolean isFromSync() {
        return this.fromSync;
    }

    public void setFromSync() {
        this.fromSync = true;
    }

    public void clearId() {
        id = 0;
    }
}
