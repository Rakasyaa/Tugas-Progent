# Tugas 2 - Aplikasi Download File

Aplikasi Java Swing untuk mengunduh file dari URL dengan:
- Input URL
- Pemilihan folder tujuan
- Tombol Download
- Progress Bar
- Pesan setelah download selesai
- Pengecekan file ZIP
- Pilihan ekstraksi ZIP
- Ekstraksi ZIP dengan pencegahan Zip Slip

## Dasar Implementasi

Program dikembangkan dari konsep `DownloadFile` pada modul Praktikum Pemrograman Internet dosen:
https://ramads.github.io/mk/prognet/

Konsep utama yang dipertahankan adalah:
`URL -> BufferedInputStream -> FileOutputStream`

GUI dibuat menggunakan Java Swing dan proses download dijalankan menggunakan `SwingWorker` agar antarmuka tidak freeze selama proses download.

## Menjalankan

Pastikan JDK 17 atau lebih baru tersedia.

```bash
javac -d out src/DownloadFileGUI.java
java -cp out DownloadFileGUI
```

Atau buka folder project pada IntelliJ IDEA / NetBeans / VS Code dengan Java Extension.

## Pengujian

URL file dapat menggunakan file publik yang dapat diakses langsung melalui HTTP/HTTPS. Untuk pengujian ZIP, gunakan URL langsung yang berakhiran `.zip`.

Catatan: beberapa website menggunakan halaman HTML/redirect/proteksi sehingga URL halaman biasa belum tentu merupakan URL file langsung.
