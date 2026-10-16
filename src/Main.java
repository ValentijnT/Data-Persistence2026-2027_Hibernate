import dao.AdresDAO;
import dao.OVChipkaartDAO;
import daohibernate.AdresDAOHibernate;
import dao.ReizigerDAO;
import daohibernate.OVChipkaartDAOHibernate;
import daohibernate.ReizigerDAOHibernate;
import domain.Adres;
import domain.OVChipkaart;
import domain.Reiziger;
import util.HibernateUtil;

import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ReizigerDAO rdao = new ReizigerDAOHibernate();
        AdresDAO adao = new AdresDAOHibernate();
        OVChipkaartDAO odao = new OVChipkaartDAOHibernate();
        try {
            testReizigerDAO(rdao);
            testAdresDAO(rdao, adao);
            testOVChipkaartDAO(rdao, odao);
        } finally {
            HibernateUtil.shutdown();
        }
    }

    private static void testReizigerDAO(ReizigerDAO rdao) {
        System.out.println("\n---------- Test ReizigerDAO -------------");

        List<Reiziger> reizigers = rdao.findAll();
        System.out.println("[Test] ReizigerDAO.findAll() geeft de volgende reizigers:");
        for (Reiziger r : reizigers) {
            System.out.println(r);
        }
        System.out.println();

        LocalDate gbdatum = LocalDate.of(1981, 3, 14);
        Reiziger sietske = new Reiziger(77, "S", null, "Boers", gbdatum);
        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.save() ");
        rdao.save(sietske);
        reizigers = rdao.findAll();
        System.out.println(reizigers.size() + " reizigers\n");

        System.out.println("Reiziger voor update achternaam: \n" + rdao.findById(77));
        sietske.setAchternaam("Zusters");
        rdao.update(sietske);
        System.out.println("Reiziger na update achternaam: \n" + rdao.findById(77));

        System.out.println("\nReizigers die op 1981-03-14 geboren zijn:");
        for (Reiziger r : rdao.findByGbdatum(gbdatum)) {
            System.out.println(r);
        }

        rdao.delete(sietske);
        System.out.println("\nZoeken naar Sietske nadat ze is verwijderd:");
        System.out.println(rdao.findById(77));
    }

    private static void testAdresDAO(ReizigerDAO rdao, AdresDAO adao) {
        System.out.println("\n---------- Test AdresDAO -------------");

        // Alle bestaande adressen
        System.out.println("[Test] AdresDAO.findAll() geeft:");
        for (Adres a : adao.findAll()) {
            System.out.println(a);
        }

        // Reiziger en adres samen opslaan
        Reiziger valentijn = new Reiziger(78, "V", null, "Tol", LocalDate.of(2003, 9, 26));
        Adres adres = new Adres(78, "3704AZ", "13", "Harmonielaan", "Zeist", valentijn);
        valentijn.setAdres(adres);

        System.out.println("\n[Test] Reiziger + adres opslaan via ReizigerDAO.save():");
        rdao.save(valentijn);
        System.out.println(rdao.findById(78));

        // findByReiziger
        System.out.println("\n[Test] AdresDAO.findByReiziger():");
        System.out.println(adao.findByReiziger(valentijn));

        // Adres bijwerken via AdresDAO
        System.out.println("\n[Test] Adres bijwerken via AdresDAO.update():");
        adres.setStraat("Laan van Vollenhove");
        adres.setHuisnummer("10B");
        adao.update(adres);
        System.out.println(adao.findByReiziger(valentijn));

        // Reiziger en adres samen bijwerken (cascade)
        System.out.println("\n[Test] Reiziger + adres bijwerken via ReizigerDAO.update():");
        valentijn.setAchternaam("Tollenaar");
        adres.setWoonplaats("Utrecht");
        rdao.update(valentijn);
        System.out.println(rdao.findById(78));

        // Adres losmaken (orphanRemoval)
        System.out.println("\n[Test] Adres losmaken van de reiziger:");
        valentijn.setAdres(null);
        rdao.update(valentijn);
        System.out.println("Reiziger: " + rdao.findById(78));
        System.out.println("Adres: " + adao.findByReiziger(valentijn));

        // Nieuw adres koppelen en daarna alles verwijderen (cascade)
        Adres nieuwAdres = new Adres(78, "3511LX", "37", "Visschersplein", "Utrecht", valentijn);
        valentijn.setAdres(nieuwAdres);
        rdao.update(valentijn);
        System.out.println("\n[Test] Nieuw adres gekoppeld:");
        System.out.println(rdao.findById(78));

        System.out.println("\n[Test] Reiziger verwijderen, het adres moet mee:");
        rdao.delete(valentijn);
        System.out.println("Reiziger: " + rdao.findById(78));
        System.out.println("Adres: " + adao.findByReiziger(valentijn));

        System.out.println("\n[Test] AdresDAO.findAll() na afloop:");
        for (Adres a : adao.findAll()) {
            System.out.println(a);
        }
    }

    private static void testOVChipkaartDAO(ReizigerDAO rdao, OVChipkaartDAO odao) {
        System.out.println("\n---------- Test OVChipkaartDAO -------------");

        // Tijdelijke reiziger
        Reiziger reiziger1 = new Reiziger(80, "K", null, "Kaarthouder", LocalDate.of(2000, 1, 1));
        rdao.save(reiziger1);

        // save
        OVChipkaart kaart = new OVChipkaart(92001, LocalDate.of(2030, 12, 31), 2, 25.50, reiziger1);
        System.out.println("[Test] Eerst " + odao.findByReiziger(reiziger1).size() + " kaarten, na OVChipkaartDAO.save():");
        odao.save(kaart);
        for (OVChipkaart k : odao.findByReiziger(reiziger1)) {
            System.out.println(k);
        }

        // update
        System.out.println("\n[Test] Saldo en klasse wijzigen via OVChipkaartDAO.update():");
        kaart.setSaldo(75.00);
        kaart.setKlasse(1);
        odao.update(kaart);
        for (OVChipkaart k : odao.findByReiziger(reiziger1)) {
            System.out.println(k);
        }

        // delete
        System.out.println("\n[Test] Kaart verwijderen via OVChipkaartDAO.delete():");
        odao.delete(kaart);
        System.out.println("Kaarten na delete: " + odao.findByReiziger(reiziger1).size());

        // de reiziger weer weghalen
        rdao.delete(reiziger1);
        System.out.println("Reiziger opgeruimd: " + rdao.findById(80));

        System.out.println("\n---------- Test relatie Reiziger - OVChipkaart -------------");

        // 1. Reiziger met twee kaarten aanmaken
        Reiziger reiziger = new Reiziger(81, "R", null, "Relatie", LocalDate.of(1999, 5, 5));
        reiziger.addOVChipkaart(new OVChipkaart(93001, LocalDate.of(2030, 1, 1), 2, 10.00, reiziger));
        reiziger.addOVChipkaart(new OVChipkaart(93002, LocalDate.of(2031, 1, 1), 1, 20.00, reiziger));

        System.out.println("[Test] Reiziger met 2 kaarten opslaan via ReizigerDAO.save():");
        rdao.save(reiziger);
        System.out.println(rdao.findById(81));

        // 2. Een kaart updaten
        System.out.println("\n[Test] Saldo van een kaart wijzigen via ReizigerDAO.update():");
        Reiziger geladen = rdao.findById(81);
        geladen.getOVchipkaarten().get(0).setSaldo(99.00);
        rdao.update(geladen);
        System.out.println(rdao.findById(81));

        // 3. Een derde kaart toevoegen
        System.out.println("\n[Test] Derde kaart toevoegen via addOVChipkaart() en ReizigerDAO.update():");
        geladen = rdao.findById(81);
        geladen.addOVChipkaart(new OVChipkaart(93003, LocalDate.of(2032, 1, 1), 2, 5.00, geladen));
        rdao.update(geladen);
        System.out.println(rdao.findById(81));
        System.out.println("Kaarten in de database: " + odao.findByReiziger(reiziger).size());

        // 4. Een kaart uit de lijst verwijderen
        System.out.println("\n[Test] Kaart 93003 uit de lijst halen en ReizigerDAO.update():");
        geladen = rdao.findById(81);
        geladen.getOVchipkaarten().removeIf(k -> k.getKaartNummer() == 93003);
        rdao.update(geladen);
        System.out.println(rdao.findById(81));

        // 5. kaarten ophalen en de reiziger controleren
        System.out.println("\n[Test] OVChipkaartDAO.findByReiziger() geeft kaarten met reiziger-id:");
        for (OVChipkaart k : odao.findByReiziger(reiziger)) {
            System.out.println(k);
        }

        // 6. Reiziger verwijderen, alle kaarten moeten mee
        System.out.println("\n[Test] Reiziger verwijderen, de kaarten moeten mee:");
        rdao.delete(rdao.findById(81));
        System.out.println("Reiziger: " + rdao.findById(81));
        System.out.println("Kaarten in de database: " + odao.findByReiziger(reiziger).size());

    }
}