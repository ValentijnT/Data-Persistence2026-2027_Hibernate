import dao.AdresDAO;
import daohibernate.AdresDAOHibernate;
import dao.ReizigerDAO;
import daohibernate.ReizigerDAOHibernate;
import domain.Adres;
import domain.Reiziger;
import util.HibernateUtil;

import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ReizigerDAO rdao = new ReizigerDAOHibernate();
        AdresDAO adao = new AdresDAOHibernate();
        try {
            testReizigerDAO(rdao);
            testAdresDAO(rdao, adao);
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
}