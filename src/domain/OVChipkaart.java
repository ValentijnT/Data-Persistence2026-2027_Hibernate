package domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ov_chipkaart")
public class OVChipkaart {

    @Id
    @Column(name = "kaart_nummer")
    private int kaart_nummer;

    @Column(name = "geldig_tot")
    private LocalDate geldig_tot;

    @Column(name = "klasse")
    private int klasse;

    @Column(name = "saldo")
    private double saldo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reiziger_id")
    private Reiziger reiziger;

    public OVChipkaart() {}

    public OVChipkaart(int kaart_nummer, LocalDate geldig_tot, int klasse, double saldo, Reiziger reiziger) {
        this.kaart_nummer = kaart_nummer;
        this.geldig_tot = geldig_tot;
        this.klasse = klasse;
        this.saldo = saldo;
        this.reiziger = reiziger;
    }


    public int getKaartNummer() { return kaart_nummer; }
    public LocalDate getGeldigTot() { return geldig_tot; }
    public int getKlasse() { return klasse; }
    public double getSaldo() { return saldo; }
    public Reiziger getReiziger() { return reiziger; }

    public void setKaartNummer(int kaartNummer) { this.kaart_nummer = kaartNummer; }
    public void setGeldigTot(LocalDate geldigTot) { this.geldig_tot = geldigTot; }
    public void setKlasse(int klasse) { this.klasse = klasse; }
    public void setSaldo(double saldo) { this.saldo = saldo; }
    public void setReiziger(Reiziger reiziger) { this.reiziger = reiziger; }

    @Override
    public String toString() {
        String reizigerInfo = (reiziger != null) ? " Reiziger id: " +  reiziger.getId() : "Reiziger: null";
        return "OV_Chipkaart {#" + kaart_nummer + ", geldig tot: " + geldig_tot + ", klasse: " +  klasse + ", saldo: " + saldo + "} reiziger_id: " + reizigerInfo;
    }
}
