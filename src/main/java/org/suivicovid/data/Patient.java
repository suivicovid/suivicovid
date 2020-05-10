package org.suivicovid.data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import org.suivicovid.dao.JpaDao;
import org.suivicovid.sync.Sync;

@Entity
public class Patient implements Serializable {
    private static final long serialVersionUID = 4498027046553479379L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String nom;
    private String telephone;
    private String email;
    private LocalDate dateContact = LocalDate.now();
    private LocalDate dateSymptomes;
    private LocalDate dateSymptomesRespi;
    // private String noteSymptomes;
    private int age;
    // private String noteFragilites;
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean dateRappelManual = false;

    private LocalDate dateRappel;
    private boolean rappelFait;
    // private String evolution;

    private LocalDate dateRappelJ7;
    private LocalDate dateRappelJ13;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean rappelJ7Fait;
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean rappelJ13Fait;

    private boolean respiChronique;
    private boolean insuffCardiaque;
    private boolean atcdCV;
    private boolean diabete;
    private boolean immunodep;
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean immunosupp;
    private boolean cancerTtt;
    private boolean imc40;
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean imc30;
    private boolean dialyse;
    private boolean cirrhose;
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean gross3T;

    private boolean isolement;
    private boolean precarite;
    private boolean diffLinguistique;
    private boolean troubleNeuroPsy;
    private boolean manqueMoyenComm;

    private boolean procheFragile;
    private boolean pieceConfinement;

    private boolean grPolyp22;
    private boolean grSat90;
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean grSat95;
    private boolean grPas90;
    private boolean grDeshyd;
    private boolean grAltConsc;
    private boolean grAbeg;

    private LocalDate testCovid1Date;
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean testCovid1Result;
    private String testCovid1Type;

    private LocalDate testCovid2Date;
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean testCovid2Result;
    private String testCovid2Type;

    public static String[] covidTestType = { "PCR", "IgG/M" };

    @Column(length = 2048)
    private String notes;
    @Column(length = 2048)
    private String notesContexte;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int covidDiag; // 0 non exlcu, possible, probable, 3 certain
    public static String[] covidDiagStr = { "non exclu", "possible", "probable", "certain", "prouvé" };

    // Extra stuff
    // même si on restera sans doute en mode update
    // on peut changer le nom mais pas la colonne
    // ne pas oublier le not null pour les évolutions de int/double/boolean

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

    @Column(unique = true)
    private String uuid;
    private Timestamp created;
    private Timestamp updated;

    @Transient
    boolean fromSync;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "patient", fetch = FetchType.EAGER)
    @OrderBy("csDate")
    private SortedSet<Consult> consults = new TreeSet<Consult>();

    public long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public LocalDate getDateContact() {
        return dateContact;
    }

    public void setDateContact(LocalDate dateContact) {
        this.dateContact = dateContact;
    }

    public LocalDate getDateSymptomes() {
        return dateSymptomes;
    }

    public void setDateSymptomes(LocalDate dateSymptomes) {
        this.dateSymptomes = dateSymptomes;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public LocalDate getDateRappel() {
        return dateRappel;
    }

    public void setDateRappel(LocalDate dateRappel) {
        this.dateRappel = dateRappel;
    }

    public boolean isRespiChronique() {
        return respiChronique;
    }

    public void setRespiChronique(boolean respiChronique) {
        this.respiChronique = respiChronique;
    }

    public boolean isInsuffCardiaque() {
        return insuffCardiaque;
    }

    public void setInsuffCardiaque(boolean insuffCardiaque) {
        this.insuffCardiaque = insuffCardiaque;
    }

    public boolean isAtcdCV() {
        return atcdCV;
    }

    public void setAtcdCV(boolean atcdCV) {
        this.atcdCV = atcdCV;
    }

    public boolean isDiabete() {
        return diabete;
    }

    public void setDiabete(boolean diabete) {
        this.diabete = diabete;
    }

    public boolean isImmunodep() {
        return immunodep;
    }

    public void setImmunodep(boolean immunodep) {
        this.immunodep = immunodep;
    }

    public boolean isCancerTtt() {
        return cancerTtt;
    }

    public void setCancerTtt(boolean cancerTtt) {
        this.cancerTtt = cancerTtt;
    }

    public boolean isImc40() {
        return imc40;
    }

    public void setImc40(boolean imc40) {
        this.imc40 = imc40;
    }

    public boolean isDialyse() {
        return dialyse;
    }

    public void setDialyse(boolean dialyse) {
        this.dialyse = dialyse;
    }

    public boolean isCirrhose() {
        return cirrhose;
    }

    public void setCirrhose(boolean cirrhose) {
        this.cirrhose = cirrhose;
    }

    public boolean isIsolement() {
        return isolement;
    }

    public void setIsolement(boolean isolement) {
        this.isolement = isolement;
    }

    public boolean isPrecarite() {
        return precarite;
    }

    public void setPrecarite(boolean precarite) {
        this.precarite = precarite;
    }

    public boolean isDiffLinguistique() {
        return diffLinguistique;
    }

    public void setDiffLinguistique(boolean diffLinguistique) {
        this.diffLinguistique = diffLinguistique;
    }

    public boolean isTroubleNeuroPsy() {
        return troubleNeuroPsy;
    }

    public void setTroubleNeuroPsy(boolean troubleNeuroPsy) {
        this.troubleNeuroPsy = troubleNeuroPsy;
    }

    public boolean isManqueMoyenComm() {
        return manqueMoyenComm;
    }

    public void setManqueMoyenComm(boolean manqueMoyenComm) {
        this.manqueMoyenComm = manqueMoyenComm;
    }

    public boolean isProcheFragile() {
        return procheFragile;
    }

    public void setProcheFragile(boolean procheFragile) {
        this.procheFragile = procheFragile;
    }

    public boolean isPieceConfinement() {
        return pieceConfinement;
    }

    public void setPieceConfinement(boolean pieceConfinement) {
        this.pieceConfinement = pieceConfinement;
    }

    public boolean isGrPolyp22() {
        return grPolyp22;
    }

    public void setGrPolyp22(boolean grPolyp22) {
        this.grPolyp22 = grPolyp22;
    }

    public boolean isGrSat90() {
        return grSat90;
    }

    public void setGrSat90(boolean grSat90) {
        this.grSat90 = grSat90;
    }

    public boolean isGrPas90() {
        return grPas90;
    }

    public void setGrPas90(boolean grPas90) {
        this.grPas90 = grPas90;
    }

    public boolean isGrDeshyd() {
        return grDeshyd;
    }

    public void setGrDeshyd(boolean grDeshyd) {
        this.grDeshyd = grDeshyd;
    }

    public boolean isGrAltConsc() {
        return grAltConsc;
    }

    public void setGrAltConsc(boolean grAltConsc) {
        this.grAltConsc = grAltConsc;
    }

    public boolean isGrAbeg() {
        return grAbeg;
    }

    public void setGrAbeg(boolean grAbeg) {
        this.grAbeg = grAbeg;
    }

    public SortedSet<Consult> getConsults() {
        return consults;
    }

    public void setConsults(SortedSet<Consult> consults) {
        this.consults = consults;
    }

    /**
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * @param notes the notes to set
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * @return the notesContexte
     */
    public String getNotesContexte() {
        return notesContexte;
    }

    /**
     * @param notesContexte the notesContexte to set
     */
    public void setNotesContexte(String notesContexte) {
        this.notesContexte = notesContexte;
    }

    /**
     * @return the covidDiag
     */
    public int getCovidDiag() {
        return covidDiag;
    }

    /**
     * @param covidDiag the covidDiag to set
     */
    public void setCovidDiag(int covidDiag) {
        this.covidDiag = covidDiag;
    }

    /**
     * @return the immunosupp
     */
    public boolean isImmunosupp() {
        return immunosupp;
    }

    /**
     * @param immunosupp the immunosupp to set
     */
    public void setImmunosupp(boolean immunosupp) {
        this.immunosupp = immunosupp;
    }

    /**
     * @return the dateSymptomesRespi
     */
    public LocalDate getDateSymptomesRespi() {
        return dateSymptomesRespi;
    }

    /**
     * @param dateSymptomesRespi the dateSymptomesRespi to set
     */
    public void setDateSymptomesRespi(LocalDate dateSymptomesRespi) {
        this.dateSymptomesRespi = dateSymptomesRespi;
    }

    // =================

    public String getFragilites() {
        StringBuilder b = new StringBuilder();
        if (isolement)
            b.append("I:");
        if (precarite)
            b.append("P:");
        if (diffLinguistique)
            b.append("L:");
        if (troubleNeuroPsy)
            b.append("N:");
        if (manqueMoyenComm)
            b.append("C:");
        if (age > 70)
            b.append("A70:");
        if (b.length() > 1)
            b.deleteCharAt(b.length() - 1);
        return b.toString();
    }

    public Set<String> getTopSymptomes() {
        Set<String> s = new HashSet<String>();

        if (grDeshyd)
            s.add("DES");
        if (grAltConsc)
            s.add("ALT");
        if (grAbeg)
            s.add("AEG");
        if (grPas90)
            s.add("P90");
        if (grSat90)
            s.add("S90");
        if (grPolyp22)
            s.add("F22");

        return s;
    }

    public String getSymptomes() {
        return Stream
                .concat(getConsults().stream().flatMap(c -> c.getSymptomes().stream()).collect(Collectors.toSet())
                        .stream(), getTopSymptomes().stream())
                .distinct().filter(s -> !s.startsWith("CP<")).collect(Collectors.joining(":"));
    }

    // used for v1.5 data update
    public void updateConsistency() {
        // System.out.println("update consistency " + getId() + " " + getNom());
        getConsults().stream().forEach(c -> updateFromConsult(c));
        // mise en cohérence des seuils
        if (isGrSat90())
            setGrSat95(true);

        if (isImc40())
            setImc30(true);

        // mise en cohérence des rappels
        checkRappels();
    }

    // prochain rappel pas encore fait
    public LocalDate getProchainRappel() {
        // le premier pas encore fait.
        LocalDate d = null;
        if (!isRappelJ7Fait() && getDateRappelJ7() != null && (d == null || d.isAfter(getDateRappelJ7())))
            d = getDateRappelJ7();
        if (!isRappelJ13Fait() && getDateRappelJ13() != null && (d == null || d.isAfter(getDateRappelJ13())))
            d = getDateRappelJ13();
        if (!isRappelFait() && getDateRappel() != null && (d == null || d.isAfter(getDateRappel())))
            d = getDateRappel();
        return d;
    }

    public void dumpRappels() {
        System.out.println("\n\n***patient " + getId());
        System.out.println("J7  " + getDateRappelJ7() + " " + isRappelJ7Fait());
        System.out.println("J13 " + getDateRappelJ13() + " " + isRappelJ13Fait());
        System.out.println("man " + getDateRappel() + " " + isRappelFait());
        System.out.println("prochain " + getProchainRappel() + " dernier " + getDernierRappelFait() + " summ "
                + getDateRappelSummary());
        System.out.println("   " + aRappeler());
    }

    public boolean aRappeler() {
        // return (!rappelFait && dateRappel != null &&
        // !dateRappel.isAfter(LocalDate.now()));
        LocalDate d = getProchainRappel();
        return (d != null && !d.isAfter(LocalDate.now()));
    }

    public LocalDate getDateRappelSummary() {
        LocalDate d = getProchainRappel();
        if (d != null)
            return d;
        return getDernierRappelFait();
    }

    public LocalDate getDernierRappelFait() {
        LocalDate d = null;
        if (isRappelJ7Fait() && getDateRappelJ7() != null && (d == null || d.isBefore(getDateRappelJ7())))
            d = getDateRappelJ7();
        if (isRappelJ13Fait() && getDateRappelJ13() != null && (d == null || d.isBefore(getDateRappelJ13())))
            d = getDateRappelJ13();
        if (isRappelFait() && getDateRappel() != null && (d == null || d.isBefore(getDateRappel())))
            d = getDateRappel();
        return d;
    }

    // cohérence des rappels
    public void checkRappels() {
        LocalDate j7 = getDateRappelJ7();
        boolean j7done = isRappelJ7Fait();

        LocalDate j13 = getDateRappelJ13();
        boolean j13done = isRappelJ13Fait();

        LocalDate r = getDateRappel();
        boolean done = isRappelFait();

        if (j7 == null && getDateSymptomes() != null) {
            setDateRappelJ7(getDateSymptomes().plusDays(7));
            j7 = getDateRappelJ7();
        }

        if (j13 == null && getDateSymptomes() != null) {
            setDateRappelJ13(getDateSymptomes().plusDays(13));
            j13 = getDateRappelJ13();
        }

        // Si le j13 est fait, alors le j7 peut être considéré comme fait.
        if (j13done && !j7done) {
            j7done = true;
            setRappelJ7Fait(true);
        }

        // si on a un rappel manuel défini et marqué comme fait, les rappels par défaut
        // antérieurs sont faits
        if (r != null && done && !j7done && j7 != null && !r.isBefore(j7))
            setRappelJ7Fait(true);

        if (r != null && done && !j13done && j13 != null && !r.isBefore(j13))
            setRappelJ13Fait(true);

        // dumpRappels();
    }

    public void updateFromConsult(Consult c) {
        if (c.getSatO2() < 90 && c.getSatO2() > 0)
            setGrSat90(true);
        if (c.getSatO2() < 95 && c.getSatO2() > 0)
            setGrSat95(true);
        if (c.getPas() < 90 && c.getPas() > 0)
            setGrPas90(true);
    }

    public int getScoreGravite() {

        // On récupère tous les symptômes mesurés
        Set<String> ss = getConsults().stream().flatMap(c -> c.getSymptomes().stream()).collect(Collectors.toSet());

        int score = 0;
        // +10 si âge >70
        if (age > 70)
            score += 10;
        // + 10 pour toute comorbidité (atcd médical facteur de gravité)
        if (hasComorbid()) {
            int n = getComorbidCount();
            if (n > 2)
                score += 20;
            else if (n == 2)
                score += 15;
            else
                score += 10;
        }
        // + 5 pour toute fragilité sociale
        if (hasFragilites())
            score += 5;
        // + 30 pour tout signe de gravité (désaturation, polypnée, hypotension etc)
        if (hasGravite())
            score += 30;
        // + 4 pour dernière temp renseignée >39
        double lastTemp = getConsults().stream().filter(c -> c.getTemperature() > 0)
                .mapToDouble(c -> c.getTemperature()).reduce((first, second) -> second).orElse(0);
        if (lastTemp > 39)
            score += 4;
        // +5 si FC >100
        int lastFC = getConsults().stream().filter(c -> c.getFreqCardio() > 0).mapToInt(c -> c.getFreqCardio())
                .reduce((first, second) -> second).orElse(0);
        if (lastFC > 100)
            score += 5;
        // + 5 si <5 à "comment vous sentez vous" (dernier renseigné)
        int lastSentez = getConsults().stream().filter(c -> c.getSaSentezVous() >= 0).mapToInt(c -> c.getSaSentezVous())
                .reduce((first, second) -> second).orElse(0);
        if (lastSentez > 0 && lastSentez < 5)
            score += 5;
        // +5 si toux +++
        if (ss.contains("TX+++"))
            score += 5;
        // +5 si asthénie +++
        if (ss.contains("AST+++"))
            score += 4;

        // + 4 si gêne respi ++
        // +10 si gêne respi +++
        if (ss.contains("GR+++")) {
            score += 10;
        } else if (ss.contains("GR++")) {
            score += 4;
        }
        // +5 si essoufflement effort intense
        // +15 si essoufflement au moindre effort
        // Essoufflement 5 15 30

        if (ss.contains("ESS++")) {
            score += 15;
        } else if (ss.contains("ESS+")) {
            score += 5;
        }

        // +30 si essoufflement au repos
        if (ss.contains("ESR"))
            score += 30;

        // +3 si difficulté boire / manger
        if (ss.contains("BM"))
            score += 3;

        // Gut feeling 0 5 10 15

        if (ss.contains("GF+++")) {
            score += 15;
        } else if (ss.contains("GF++")) {
            score += 10;
        } else if (ss.contains("GF+")) {
            score += 5;
        } else {
            // nothing
        }

        if (ss.contains("CBT+++")) {
            score += 5;
        }

        return score;
    }

    public int getScoreDiag() {

        int score = 0;
        // On récupère tous les symptômes mesurés
        Set<String> ss = getConsults().stream().flatMap(c -> c.getSymptomes().stream()).collect(Collectors.toSet());

        // temp>38
        if (ss.contains("F"))
            score += 4;
        // sat<95
        if (ss.contains("S95") || grSat95)
            score += 4;
        // toux
        if (ss.contains("TX"))
            score += 4;
        // respi > 22
        if (ss.contains("F22"))
            score += 4;
        // anosmie
        if (ss.contains("AO"))
            score += 10;
        // diarrhée
        if (ss.contains("DR"))
            score += 2;
        // essouflé aur repos ou effort
        if (ss.contains("ESR") || ss.contains("ESS"))
            score += 4;

        // combi diarrée et symptome haut

        String[] listSH = { "ESR", "TX++", "TX+++", "TH", "ESS++", "GR", "CP<8", "AO" };

        boolean sh = Arrays.stream(listSH).anyMatch(s -> ss.contains(s));

        if (ss.contains("DR") && sh)
            score += 5;

        // courbatures
        if (ss.contains("CBT")) {
            score += 1;
        }

        // gêne respi
        if (ss.contains("GR")) {
            score += 2;
        }

        // asthénie ++ ou +++
        if (ss.contains("AST++") || ss.contains("AST+++")) {
            score += 2;
        }

        // ORL
        if (ss.contains("ORL")) {
            score += 1;
        }

        // maux de tête
        if (ss.contains("CEPH")) {
            score += 1;
        }

        if (ss.contains("CP<8")) {
            score += 4;
        } else if (ss.contains("CP<15")) {
            score += 2;
        }

        return score;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isRappelFait() {
        return rappelFait;
    }

    public void setRappelFait(boolean rappelFait) {
        this.rappelFait = rappelFait;
    }

    public boolean hasGravite() {
        return grPolyp22 || grSat90 || grSat95 || grPas90 || grDeshyd || grAltConsc || grAbeg;
    }

    public boolean hasComorbid() {
        return respiChronique || insuffCardiaque || atcdCV || diabete || immunodep || cancerTtt || imc40 || dialyse
                || cirrhose || imc30 || gross3T;
    }

    public int getComorbidCount() {
        int i = 0;
        if (respiChronique)
            i++;
        if (insuffCardiaque)
            i++;
        if (atcdCV)
            i++;
        if (diabete)
            i++;
        if (immunodep)
            i++;
        if (cancerTtt)
            i++;
        if (imc40 || imc30)
            i++;
        if (dialyse)
            i++;
        if (cirrhose)
            i++;
        if (gross3T)
            i++;

        return i;
    }

    public boolean hasFragilites() {
        return isolement || precarite || diffLinguistique || troubleNeuroPsy || manqueMoyenComm;
    }

    public LocalDate getTestCovid1Date() {
        return testCovid1Date;
    }

    public void setTestCovid1Date(LocalDate testCovid1Date) {
        this.testCovid1Date = testCovid1Date;
    }

    public boolean isTestCovid1Result() {
        return testCovid1Result;
    }

    public void setTestCovid1Result(boolean testCovid1Result) {
        this.testCovid1Result = testCovid1Result;
    }

    public String getTestCovid1Type() {
        return testCovid1Type;
    }

    public void setTestCovid1Type(String testCovid1Type) {
        this.testCovid1Type = testCovid1Type;
    }

    public LocalDate getTestCovid2Date() {
        return testCovid2Date;
    }

    public void setTestCovid2Date(LocalDate testCovid2Date) {
        this.testCovid2Date = testCovid2Date;
    }

    public boolean isTestCovid2Result() {
        return testCovid2Result;
    }

    public void setTestCovid2Result(boolean testCovid2Result) {
        this.testCovid2Result = testCovid2Result;
    }

    public String getTestCovid2Type() {
        return testCovid2Type;
    }

    public void setTestCovid2Type(String testCovid2Type) {
        this.testCovid2Type = testCovid2Type;
    }

    public static String[] getCovidDiagStr() {
        return covidDiagStr;
    }

    public static void setCovidDiagStr(String[] covidDiagStr) {
        Patient.covidDiagStr = covidDiagStr;
    }

    /**
     * @return the dateRappelManual
     */
    public boolean isDateRappelManual() {
        return dateRappelManual;
    }

    /**
     * @param dateRappelManual the dateRappelManual to set
     */
    public void setDateRappelManual(boolean dateRappelManual) {
        this.dateRappelManual = dateRappelManual;
    }

    ////////////// v1.5

    public boolean isImc30() {
        return imc30;
    }

    public void setImc30(boolean imc30) {
        this.imc30 = imc30;
    }

    public boolean isGross3T() {
        return gross3T;
    }

    public void setGross3T(boolean gr3t) {
        gross3T = gr3t;
    }

    public boolean isGrSat95() {
        return grSat95;
    }

    public void setGrSat95(boolean grSat95) {
        this.grSat95 = grSat95;
    }

    /**
     * @return the rappelJ13Fait
     */
    public boolean isRappelJ13Fait() {
        return rappelJ13Fait;
    }

    /**
     * @return the rappelJ7Fait
     */
    public boolean isRappelJ7Fait() {
        return rappelJ7Fait;
    }

    /**
     * @param rappelJ13Fait the rappelJ13Fait to set
     */
    public void setRappelJ13Fait(boolean rappelJ13Fait) {
        this.rappelJ13Fait = rappelJ13Fait;
    }

    /**
     * @param rappelJ7Fait the rappelJ7Fait to set
     */
    public void setRappelJ7Fait(boolean rappelJ7Fait) {
        this.rappelJ7Fait = rappelJ7Fait;
    }

    public LocalDate getDateRappelJ7() {
        return dateRappelJ7;
    }

    public LocalDate getDateRappelJ13() {
        return dateRappelJ13;
    }

    /**
     * @param dateRappelJ7 the dateRappelJ7 to set
     */
    public void setDateRappelJ7(LocalDate dateRappelJ7) {
        this.dateRappelJ7 = dateRappelJ7;
    }

    /**
     * @param dateRappelJ13 the dateRappelJ13 to set
     */
    public void setDateRappelJ13(LocalDate dateRappelJ13) {
        this.dateRappelJ13 = dateRappelJ13;
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
        System.out.println("patient prepersist");
        if (!isFromSync() && Sync.getInstance() != null)
            Sync.getInstance().send(this);
    }

    @PreUpdate
    public void setUpdated() {
        if (!isFromSync())
            updated = new Timestamp(System.currentTimeMillis());
        System.out.println("patient preupdate");

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

    @PostUpdate
    @PostPersist
    protected void clearSync() {
        fromSync = false;
    }

    public void setUpdated(Timestamp ts) {
        updated = ts;
    }

    public void setCreated(Timestamp ts) {
        created = ts;
    }

    public boolean isFromSync() {
        return this.fromSync;
    }

    public void setFromSync() {
        this.fromSync = true;
        getConsults().stream().forEach(c -> c.setFromSync());
    }

    public void clearId() {
        id = 0;
    }

}