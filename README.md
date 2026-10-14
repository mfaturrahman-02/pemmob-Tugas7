# Light Sensor Dashboard

Aplikasi Android untuk memantau intensitas cahaya sekitar melalui sensor cahaya perangkat. Nilai ditampilkan dalam lux, disertai kategori kondisi, indikator rentang, dan nilai minimum/maksimum sejak dashboard dibuka atau terakhir direset.

## Fitur

- Membaca sensor cahaya (ambient light) secara langsung saat aplikasi aktif.
- Menampilkan intensitas dalam lux serta status **GELAP**, **REDUP**, atau **TERANG**.
- Menampilkan nilai minimum dan maksimum yang tercatat; tombol **Reset Min/Max** mengatur ulang pencatatan.
- Mengganti tema aplikasi secara otomatis: mode gelap saat cahaya di bawah 50 lux dan mode terang saat di atas 100 lux. Perubahan menunggu 2 detik agar tidak mudah berubah-ubah. Rentang 50–100 lux menjadi histeresis.
- Menampilkan informasi bila perangkat tidak menyediakan sensor cahaya.

## Sensor yang digunakan

Aplikasi menggunakan **sensor cahaya ambient** Android (`Sensor.TYPE_LIGHT`). Sensor mengukur iluminansi yang diterima dalam satuan **lux (lx)**. Ambang statusnya:

| Nilai | Status | Tema otomatis |
| --- | --- | --- |
| < 50 lx | Gelap | Mode gelap |
| 50–100 lx | Redup | Tidak memicu perubahan tema |
| > 100 lx | Terang | Mode terang |

Aplikasi hanya membaca sensor cahaya; tidak ada sensor lain yang dipakai.

## Struktur folder

```text
.
├── app/
│   └── src/
│       ├── main/
│       │   ├── java/com/example/tugas7/
│       │   │   ├── MainActivity.java          # Siklus hidup sensor, dashboard, reset, tema otomatis
│       │   │   ├── adapter/SensorAdapter.java # Menghubungkan data sensor ke daftar kartu UI
│       │   │   └── model/SensorData.java       # Nilai, status, dan format data sensor
│       │   ├── res/
│       │   │   ├── layout/                    # Layout halaman dan kartu sensor
│       │   │   ├── values/                    # String, warna, dan tema
│       │   │   ├── values-night/              # Tema dan warna mode gelap
│       │   │   ├── drawable/                  # Ikon dan drawable UI
│       │   │   ├── mipmap-anydpi-v26/          # Ikon launcher adaptif
│       │   │   └── xml/                       # Aturan backup dan ekstraksi data
│       │   └── AndroidManifest.xml            # Deklarasi aplikasi dan activity launcher
│       ├── test/                              # Unit test
│       └── androidTest/                       # Instrumented test
├── gradle/
│   ├── wrapper/                               # Konfigurasi Gradle Wrapper
│   └── libs.versions.toml                     # Versi plugin dan dependensi
├── build.gradle.kts                           # Konfigurasi Gradle tingkat proyek
├── settings.gradle.kts                        # Nama proyek dan modul yang disertakan
├── gradle.properties                          # Properti Gradle
├── gradlew / gradlew.bat                       # Gradle Wrapper untuk Unix/Windows
└── .gitignore                                 # Berkas yang tidak disertakan ke Git
```

Folder `build/`, `.gradle/`, dan konfigurasi lokal IDE/perangkat adalah berkas hasil build atau lokal, sehingga dikecualikan dari Git.

## Menjalankan aplikasi

1. Buka proyek ini di Android Studio.
2. Tunggu Gradle menyelesaikan sinkronisasi.
3. Jalankan konfigurasi `app` pada perangkat Android atau emulator.

Sensor cahaya mungkin tidak tersedia pada emulator atau perangkat tertentu. Dalam keadaan tersebut, aplikasi menampilkan keterangan bahwa sensor tidak tersedia.
