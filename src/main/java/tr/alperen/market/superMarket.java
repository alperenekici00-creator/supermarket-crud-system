import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class superMarket {

    static String dosyaAdi = "urunler.txt";
    static String kampanyaDosyasi = "kampanyali_urunler.txt";
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println();
            System.out.println("  ╔════════════════════════════════════╗");
            System.out.println("  ║     URUN BILGI SISTEMI             ║");
            System.out.println("  ╠════════════════════════════════════╣");
            System.out.println("  ║  1. Verileri Cek                   ║");
            System.out.println("  ║  2. Listele                        ║");
            System.out.println("  ║  3. Yeni Urun Ekle                 ║");
            System.out.println("  ║  4. Guncelle                       ║");
            System.out.println("  ║  5. Sil                            ║");
            System.out.println("  ║  6. Kampanyali Urun Islemleri      ║");
            System.out.println("  ║  7. Cikis                          ║");
            System.out.println("  ╚════════════════════════════════════╝");
            System.out.print("  Seciminiz: ");
            String secim = scanner.nextLine();

            switch (secim) {
                case "1": veriCek();        break;
                case "2": listele();        break;
                case "3": urunEkle();       break;
                case "4": guncelle();       break;
                case "5": sil();            break;
                case "6": kampanyaMenusu(); break;
                case "7":
                    System.out.println("\n  Cikiliyor... Gule gule!");
                    return;
                default:
                    System.out.println("\n  [!] Gecersiz secim! Lutfen 1-7 arasi girin.");
            }
        }
    }

    public static void veriCek() {
        System.out.println("\n  >> Veriler cekiliyor...");
        try {
            URL url = new URL("https://dummyjson.com/products?limit=35");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder json = new StringBuilder();
            String satir;
            while ((satir = br.readLine()) != null) json.append(satir);
            br.close();

            String[] urunler = json.toString().split("\"title\":\"");
            FileWriter fw = new FileWriter(dosyaAdi, false);
            int sayac = 0;

            for (int i = 1; i < urunler.length; i++) {
                String p     = urunler[i];
                String ad    = p.split("\"")[0];
                String kat   = jsonDegerCek(p, "\"category\":\"", "\"");
                String fiyat = jsonDegerCek(p, "\"price\":", ",");
                if (fiyat.isEmpty()) fiyat = jsonDegerCek(p, "\"price\":", "}");
                String stok  = jsonDegerCek(p, "\"stock\":", ",");
                if (fiyat.isEmpty()) fiyat = "0";
                if (stok.isEmpty())  stok  = "0";
                if (kat.isEmpty())   kat   = "bilinmiyor";
                fw.write(ad + ";" + kat + ";" + fiyat + ";" + stok + "\n");
                sayac++;
            }
            fw.close();
            System.out.println("  [OK] " + sayac + " urun basariyla '" + dosyaAdi + "' dosyasina kaydedildi.");
        } catch (Exception e) {
            System.out.println("  [!] Baglanti hatasi: " + e.getMessage());
        }
    }

    static String jsonDegerCek(String kaynak, String anahtar, String bitis) {
        int idx = kaynak.indexOf(anahtar);
        if (idx == -1) return "";
        int baslangic = idx + anahtar.length();
        int son = kaynak.indexOf(bitis, baslangic);
        if (son == -1) return "";
        return kaynak.substring(baslangic, son).trim();
    }

    public static void listele() {
        System.out.println();
        System.out.println("  +---------------------------------+");
        System.out.println("  |   LISTELEME SECENEKLERI         |");
        System.out.println("  +---------------------------------+");
        System.out.println("  | a) Bas harfe gore filtrele      |");
        System.out.println("  | b) Fiyat araligina gore         |");
        System.out.println("  | c) Kategoriye gore filtrele     |");
        System.out.println("  | d) Tum urunleri listele         |");
        System.out.println("  +---------------------------------+");
        System.out.print("  Seciminiz: ");
        String secim = scanner.nextLine();

        List<String> liste = dosyaOku(dosyaAdi);
        if (liste == null) return;

        List<String> sonuc = new ArrayList<>();

        if (secim.equalsIgnoreCase("a")) {
            System.out.print("  Bas harf: ");
            String harf = scanner.nextLine().trim().toLowerCase();
            for (String u : liste)
                if (u.toLowerCase().startsWith(harf)) sonuc.add(u);

        } else if (secim.equalsIgnoreCase("b")) {
            double min = sayiSor("  Min fiyat (0 birak): ");
            double max = sayiSor("  Max fiyat: ");
            for (String u : liste) {
                String[] p = u.split(";");
                if (p.length < 3) continue;
                try {
                    double f = Double.parseDouble(p[2]);
                    if (f >= min && f <= max) sonuc.add(u);
                } catch (NumberFormatException ignored) {}
            }

        } else if (secim.equalsIgnoreCase("c")) {
            System.out.println("  Mevcut kategoriler: beauty, fragrances, furniture, groceries");
            System.out.print("  Kategori: ");
            String kat = scanner.nextLine().trim().toLowerCase();
            for (String u : liste) {
                String[] p = u.split(";");
                if (p.length > 1 && p[1].equalsIgnoreCase(kat)) sonuc.add(u);
            }

        } else if (secim.equalsIgnoreCase("d")) {
            sonuc = liste;

        } else {
            System.out.println("  [!] Gecersiz secim!");
            return;
        }

        if (sonuc.isEmpty()) {
            System.out.println("  [!] Sonuc bulunamadi.");
        } else {
            tabloYaz(sonuc);
        }
    }

    public static void urunEkle() {
        System.out.println("\n  --- Yeni Urun Ekle ---");
        System.out.print("  Urun Adi   : ");
        String ad = scanner.nextLine().trim();
        if (ad.isEmpty()) { System.out.println("  [!] Urun adi bos olamaz!"); return; }

        System.out.print("  Kategori   : ");
        String kat = scanner.nextLine().trim();

        double fiyat = sayiSor("  Fiyat ($)  : ");
        int stok     = (int) sayiSor("  Stok Adedi : ");

        List<String> liste = dosyaOku(dosyaAdi);
        if (liste != null) {
            for (String s : liste) {
                if (s.toLowerCase().startsWith(ad.toLowerCase() + ";")) {
                    System.out.println("  [!] Bu isimde bir urun zaten mevcut!");
                    return;
                }
            }
        }

        try (FileWriter fw = new FileWriter(dosyaAdi, true)) {
            fw.write(ad + ";" + kat + ";" + fiyat + ";" + stok + "\n");
            System.out.println("  [OK] Urun basariyla eklendi: " + ad);
        } catch (IOException e) {
            System.out.println("  [!] Kayit hatasi: " + e.getMessage());
        }
    }

    public static void guncelle() {
        System.out.println("\n  --- Urun Guncelle ---");
        System.out.print("  Guncellenecek urun adi: ");
        String aranan = scanner.nextLine().trim();

        List<String> liste = dosyaOku(dosyaAdi);
        if (liste == null) return;

        boolean bulundu = false;
        List<String> yeni = new ArrayList<>();

        for (String satir : liste) {
            if (satir.toLowerCase().startsWith(aranan.toLowerCase() + ";")) {
                bulundu = true;
                System.out.println("  Bulundu: " + satir);
                System.out.println("  Ne guncellensin?");
                System.out.println("  1) Urun Adi  2) Kategori  3) Fiyat  4) Stok");
                System.out.print("  Seciminiz: ");
                String alan = scanner.nextLine().trim();

                String[] p = satir.split(";");
                if (p.length < 4) p = Arrays.copyOf(p, 4);

                switch (alan) {
                    case "1": System.out.print("  Yeni ad: ");       p[0] = scanner.nextLine().trim(); break;
                    case "2": System.out.print("  Yeni kategori: "); p[1] = scanner.nextLine().trim(); break;
                    case "3": p[2] = String.valueOf(sayiSor("  Yeni fiyat: ")); break;
                    case "4": p[3] = String.valueOf((int) sayiSor("  Yeni stok: ")); break;
                    default: System.out.println("  [!] Gecersiz alan, degisiklik yapilmadi.");
                }
                satir = String.join(";", p);
                System.out.println("  [OK] Guncellendi: " + satir);
            }
            yeni.add(satir);
        }

        if (!bulundu) { System.out.println("  [!] Urun bulunamadi!"); return; }
        dosyaYaz(dosyaAdi, yeni);
    }

    public static void sil() {
        System.out.println("\n  --- Urun Sil ---");
        System.out.print("  Silinecek urun adi: ");
        String aranan = scanner.nextLine().trim();

        List<String> liste = dosyaOku(dosyaAdi);
        if (liste == null) return;

        boolean bulundu = false;
        List<String> yeni = new ArrayList<>();

        for (String satir : liste) {
            if (satir.toLowerCase().startsWith(aranan.toLowerCase() + ";")) {
                bulundu = true;
                System.out.println("  Bulundu: " + satir);
                System.out.print("  Silinsin mi? (e/h): ");
                String cevap = scanner.nextLine().trim();
                if (cevap.equalsIgnoreCase("e")) {
                    System.out.println("  [OK] Urun silindi.");
                    continue;
                }
            }
            yeni.add(satir);
        }

        if (!bulundu) { System.out.println("  [!] Urun bulunamadi!"); return; }
        dosyaYaz(dosyaAdi, yeni);
    }

    public static void kampanyaMenusu() {
        while (true) {
            System.out.println();
            System.out.println("  +---------------------------------+");
            System.out.println("  |   KAMPANYALI URUNLER            |");
            System.out.println("  +---------------------------------+");
            System.out.println("  | 1. Kampanyaya Urun Ekle         |");
            System.out.println("  | 2. Kampanyadan Urun Cikar       |");
            System.out.println("  | 3. Kampanyali Urunleri Listele  |");
            System.out.println("  | 4. Geri Don                     |");
            System.out.println("  +---------------------------------+");
            System.out.print("  Seciminiz: ");
            String secim = scanner.nextLine();

            switch (secim) {
                case "1": kampanyayaEkle();    break;
                case "2": kampanyadanCikar();  break;
                case "3": kampanyaliListele(); break;
                case "4": return;
                default: System.out.println("  [!] Gecersiz secim!");
            }
        }
    }

    public static void kampanyayaEkle() {
        System.out.print("\n  Kampanyaya eklenecek urun adi: ");
        String aranan = scanner.nextLine().trim();

        List<String> kampanya = dosyaOkuGuvenli(kampanyaDosyasi);
        for (String s : kampanya) {
            if (s.toLowerCase().startsWith(aranan.toLowerCase() + ";")) {
                System.out.println("  [!] Bu urun zaten kampanyada mevcut!");
                return;
            }
        }

        List<String> urunler = dosyaOku(dosyaAdi);
        if (urunler == null) return;

        for (String satir : urunler) {
            if (satir.toLowerCase().startsWith(aranan.toLowerCase() + ";")) {
                kampanya.add(satir);
                dosyaYaz(kampanyaDosyasi, kampanya);
                System.out.println("  [OK] Urun kampanyaya eklendi: " + satir.split(";")[0]);
                return;
            }
        }
        System.out.println("  [!] Urun bulunamadi.");
    }

    public static void kampanyadanCikar() {
        System.out.print("\n  Kampanyadan cikarilacak urun adi: ");
        String aranan = scanner.nextLine().trim();

        List<String> liste = dosyaOkuGuvenli(kampanyaDosyasi);
        if (liste.isEmpty()) { System.out.println("  [!] Kampanya listesi bos."); return; }

        boolean bulundu = false;
        List<String> yeni = new ArrayList<>();

        for (String satir : liste) {
            if (satir.toLowerCase().startsWith(aranan.toLowerCase() + ";")) {
                bulundu = true;
                System.out.println("  Bulundu: " + satir);
                System.out.print("  Kampanyadan cikarilsin mi? (e/h): ");
                if (scanner.nextLine().trim().equalsIgnoreCase("e")) {
                    System.out.println("  [OK] Urun kampanyadan cikarildi.");
                    continue;
                }
            }
            yeni.add(satir);
        }

        if (!bulundu) { System.out.println("  [!] Urun kampanya listesinde bulunamadi."); return; }
        dosyaYaz(kampanyaDosyasi, yeni);
    }

    public static void kampanyaliListele() {
        List<String> liste = dosyaOkuGuvenli(kampanyaDosyasi);
        if (liste.isEmpty()) { System.out.println("  [!] Kampanya listesi bos."); return; }
        System.out.println("\n  --- Kampanyali Urunler ---");
        tabloYaz(liste);
    }

    

    static List<String> dosyaOku(String dosya) {
        if (!new File(dosya).exists()) {
            System.out.println("  [!] '" + dosya + "' dosyasi bulunamadi. Once veri cekin (secenek 1).");
            return null;
        }
        List<String> liste = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(dosya))) {
            String satir;
            while ((satir = br.readLine()) != null)
                if (!satir.isBlank()) liste.add(satir);
        } catch (IOException e) {
            System.out.println("  [!] Okuma hatasi: " + e.getMessage());
            return null;
        }
        return liste;
    }

    static List<String> dosyaOkuGuvenli(String dosya) {
        List<String> liste = new ArrayList<>();
        if (!new File(dosya).exists()) return liste;
        try (BufferedReader br = new BufferedReader(new FileReader(dosya))) {
            String satir;
            while ((satir = br.readLine()) != null)
                if (!satir.isBlank()) liste.add(satir);
        } catch (IOException e) {
            System.out.println("  [!] Okuma hatasi: " + e.getMessage());
        }
        return liste;
    }

    static void dosyaYaz(String dosya, List<String> satirlar) {
        try (FileWriter fw = new FileWriter(dosya, false)) {
            for (String s : satirlar) fw.write(s + "\n");
        } catch (IOException e) {
            System.out.println("  [!] Yazma hatasi: " + e.getMessage());
        }
    }

    static double sayiSor(String mesaj) {
        while (true) {
            System.out.print(mesaj);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  [!] Lutfen gecerli bir sayi girin.");
            }
        }
    }

    static void tabloYaz(List<String> liste) {
        System.out.println();
        System.out.println("  +--------------------------------+--------------+--------+-------+");
        System.out.printf ("  | %-30s | %-12s | %6s | %5s |%n", "URUN ADI", "KATEGORI", "FIYAT", "STOK");
        System.out.println("  +--------------------------------+--------------+--------+-------+");
        for (String satir : liste) {
            String[] p   = satir.split(";");
            String ad    = p.length > 0 ? p[0] : "-";
            String kat   = p.length > 1 ? p[1] : "-";
            String fiyat = p.length > 2 ? "$" + p[2] : "-";
            String stok  = p.length > 3 ? p[3] : "-";
            if (ad.length()  > 30) ad  = ad.substring(0, 27)  + "...";
            if (kat.length() > 12) kat = kat.substring(0, 9) + "...";
            System.out.printf("  | %-30s | %-12s | %6s | %5s |%n", ad, kat, fiyat, stok);
        }
        System.out.println("  +--------------------------------+--------------+--------+-------+");
        System.out.println("  Toplam: " + liste.size() + " urun");
    }
}