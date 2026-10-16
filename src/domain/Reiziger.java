package domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reiziger")
public class Reiziger {

    @Id
    @Column(name = "reiziger_id")
    private int id;

    @Column(name = "voorletters")
    private String voorletters;

    @Column(name = "tussenvoegsel")
    private String tussenvoegsel;

    @Column(name = "achternaam")
    private String achternaam;

    @Column(name = "geboortedatum")
    private LocalDate geboortedatum;

    @OneToOne(mappedBy = "reiziger", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Adres adres;

    @OneToMany(mappedBy = "reiziger", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<OVChipkaart> ovChipkaarten = new ArrayList<>();

    public Reiziger() {}

    public Reiziger(int reiziger_id, String voorletters, String tussenvoegsel, String achternaam, LocalDate geboortedatum) {
        this.id = reiziger_id;
        this.voorletters = voorletters;
        this.tussenvoegsel = tussenvoegsel;
        this.achternaam = achternaam;
        this.geboortedatum = geboortedatum;
    }

    public int getId() { return id; }
    public String getVoorletters() { return voorletters; }
    public String getTussenvoegsel() { return tussenvoegsel; }
    public String getAchternaam() { return achternaam; }
    public LocalDate getGeboortedatum() { return geboortedatum; }
    public Adres getAdres() { return adres; }
    public List<OVChipkaart> getOVchipkaarten() {return ovChipkaarten;}

    public void setId(int id) { this.id = id; }
    public void setVoorletters(String voorletters) { this.voorletters = voorletters; }
    public void setTussenvoegsel(String tussenvoegsel) { this.tussenvoegsel = tussenvoegsel; }
    public void setAchternaam(String achternaam) { this.achternaam = achternaam; }
    public void setGeboortedatum(LocalDate geboortedatum) { this.geboortedatum = geboortedatum; }
    public void setAdres(Adres adres) { this.adres = adres; }

    public void addOVChipkaart(OVChipkaart ovChipkaart) {
        ovChipkaarten.add(ovChipkaart);
        ovChipkaart.setReiziger(this);
    }


    public String getNaam(){
        String tv = (tussenvoegsel != null) ? tussenvoegsel + " " : "";
        return voorletters + ". " + tv + achternaam;
    }

    public String toString() {
        String adresString = (adres != null) ? ", Adres: " + adres : "";

        StringBuilder sb = new StringBuilder();
        if (ovChipkaarten != null && !ovChipkaarten.isEmpty()) {
            sb.append("\nOVChipkaarten:\n");
            for (OVChipkaart ov : ovChipkaarten) {
                sb.append(ov.toString()).append("\n");
            }
        } else {
            sb.append("\nOVChipkaarten: Geen\n");
        }
        String OVChipkaartString = sb.toString();

        return  "Reiziger {#" + getId() + ": " + getNaam() + " (" + geboortedatum + ")" + adresString + "}" + OVChipkaartString;
    }

}
