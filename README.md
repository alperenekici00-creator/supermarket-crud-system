# 🛒 Süpermarket Ürün Bilgi Sistemi

Terminal tabanlı Java uygulaması. Ürün verilerini internetten çeker, dosyada saklar ve CRUD işlemleri ile kampanya yönetimi sunar.

---

## 📋 İçindekiler

- [Özellikler](#-özellikler)
- [Gereksinimler](#-gereksinimler)
- [Kurulum ve Çalıştırma](#-kurulum-ve-çalıştırma)
- [Kullanım](#-kullanım)
- [Dosya Yapısı](#-dosya-yapısı)
- [Veri Formatı](#-veri-formatı)
- [Teknik Detaylar](#-teknik-detaylar)

---

## ✨ Özellikler

| Modül | Açıklama |
|---|---|
| **Veri Çekme** | [dummyjson.com](https://dummyjson.com) API'sinden 35 ürün çeker, `urunler.txt` dosyasına kaydeder |
| **Listeleme** | Baş harf, fiyat aralığı veya kategori filtresiyle ya da tüm liste olarak görüntüler |
| **Ürün Ekleme** | Manuel ürün girişi, duplicate kontrolüyle birlikte |
| **Güncelleme** | Ad, kategori, fiyat veya stok alanlarını ayrı ayrı günceller |
| **Silme** | Onay alarak güvenli silme |
| **Kampanya Yönetimi** | Ürün kampanyaya ekleme / çıkarma / listeleme; aynı ürünü iki kez eklemeyi engeller |

---

## 🔧 Gereksinimler

- Java 11 veya üzeri
- İnternet bağlantısı *(yalnızca veri çekme işlemi için)*

---

## 🚀 Kurulum ve Çalıştırma

**1. Repoyu klonla**
```bash
git clone https://github.com/kullanici-adi/supermarket-urun-sistemi.git
cd supermarket-urun-sistemi
```

**2. Derle**
```bash
javac superMarket.java
```

**3. Çalıştır**
```bash
java superMarket
```

---

## 📖 Kullanım

Program başlatıldığında ana menü açılır:

```
  ╔════════════════════════════════════╗
  ║     URUN BILGI SISTEMI             ║
  ╠════════════════════════════════════╣
  ║  1. Verileri Cek                   ║
  ║  2. Listele                        ║
  ║  3. Yeni Urun Ekle                 ║
  ║  4. Guncelle                       ║
  ║  5. Sil                            ║
  ║  6. Kampanyali Urun Islemleri      ║
  ║  7. Cikis                          ║
  ╚════════════════════════════════════╝
```

### 1 — Verileri Çek
API'den ürünler çekilir ve `urunler.txt` dosyasına yazılır. Diğer işlemleri kullanmadan önce bu adımın tamamlanmış olması gerekir.

### 2 — Listele
Dört filtreleme seçeneği sunar:

- **a)** Baş harfe göre — örneğin `a` girilirse adı A ile başlayan tüm ürünler listelenir
- **b)** Fiyat aralığına göre — minimum ve maximum fiyat girilir
- **c)** Kategoriye göre — `beauty`, `fragrances`, `furniture`, `groceries`
- **d)** Tüm ürünleri listele

Sonuçlar hizalı tablo formatında gösterilir:

```
  +--------------------------------+--------------+--------+-------+
  | URUN ADI                       | KATEGORI     |  FIYAT |  STOK |
  +--------------------------------+--------------+--------+-------+
  | Essence Mascara Lash Princess  | beauty       |  $9.99 |     5 |
  | Eyeshadow Palette with Mirror  | beauty       | $19.99 |    44 |
  +--------------------------------+--------------+--------+-------+
  Toplam: 2 urun
```

### 3 — Yeni Ürün Ekle
Ad, kategori, fiyat ve stok adedi girilerek yeni ürün eklenir. Aynı adda ürün zaten varsa işlem reddedilir.

### 4 — Güncelle
Ürün adıyla arama yapılır, bulunan ürünün hangi alanının güncelleneceği seçilir (ad / kategori / fiyat / stok).

### 5 — Sil
Ürün adıyla arama yapılır, onay alındıktan sonra silinir.

### 6 — Kampanyalı Ürün İşlemleri

```
  +---------------------------------+
  |   KAMPANYALI URUNLER            |
  +---------------------------------+
  | 1. Kampanyaya Urun Ekle         |
  | 2. Kampanyadan Urun Cikar       |
  | 3. Kampanyali Urunleri Listele  |
  | 4. Geri Don                     |
  +---------------------------------+
```

Kampanyaya eklenen ürünler ayrı bir dosyada (`kampanyali_urunler.txt`) tutulur. Aynı ürün iki kez eklenemez.

---

## 📁 Dosya Yapısı

```
supermarket-urun-sistemi/
│
├── superMarket.java          # Ana uygulama
├── urunler.txt               # Ürün veritabanı (otomatik oluşur)
├── kampanyali_urunler.txt    # Kampanya listesi (otomatik oluşur)
└── README.md
```

> `urunler.txt` ve `kampanyali_urunler.txt` dosyaları uygulama tarafından otomatik oluşturulur, manuel oluşturmaya gerek yoktur.

---

## 🗃️ Veri Formatı

Her iki dosya da aynı yapıyı kullanır; her satır bir ürünü temsil eder:

```
UrunAdi;kategori;fiyat;stok
```

**Örnek:**
```
Essence Mascara Lash Princess;beauty;9.99;5
Eyeshadow Palette with Mirror;beauty;19.99;44
Powder Canister;beauty;14.99;59
```

---

## ⚙️ Teknik Detaylar

- **Dil:** Java (harici kütüphane kullanılmamıştır)
- **API:** [dummyjson.com/products](https://dummyjson.com/products) — `HttpURLConnection` ile GET isteği
- **JSON Ayrıştırma:** Harici kütüphane yerine `String.indexOf` tabanlı basit parser
- **Veri Saklama:** Düz metin dosyası, `;` ayraçlı satır formatı
- **Hata Yönetimi:** Dosya bulunamadı, geçersiz sayı girişi, bağlantı hatası durumları yakalanır

---

## 🌐 Kullanılan API

**dummyjson.com** — ücretsiz, kayıt gerektirmeyen sahte ürün verisi API'si.

İstek:
```
GET https://dummyjson.com/products?limit=35
```

Çekilen alanlar: `title`, `category`, `price`, `stock`
