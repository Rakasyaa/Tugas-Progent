# Tugas 2 - Aplikasi Download File

Program ini merupakan pengembangan dari contoh DownloadFile pada modul Praktikum Pemrograman Internet. Konsep dasar pengunduhan tetap menggunakan URL sebagai sumber file, stream untuk membaca data dari internet, dan FileOutputStream untuk menyimpan data ke komputer. Pengembangan utama pada tugas ini adalah penambahan GUI menggunakan Java Swing, progress bar, notifikasi, pengecekan ZIP, serta pilihan ekstraksi.

## Fitur Program

- GUI sebagai antarmuka aplikasi.
- Input URL file yang akan didownload.
- Pemilihan folder penyimpanan.
- Tombol Download untuk memulai proses.
- Progress Bar untuk menunjukkan persentase proses download.
- Pesan pop-up ketika download berhasil.
- Pengecekan apakah file yang selesai didownload merupakan ZIP.
- Pilihan untuk mengekstrak atau tidak mengekstrak file ZIP.
- Ekstraksi ZIP ke folder baru dengan nama file ZIP.
- Validasi URL dan penanganan kesalahan koneksi/file.

Konsep utama yang dipertahankan adalah:
`URL -> BufferedInputStream -> FileOutputStream`

## Menjalankan

Pastikan JDK 17 atau lebih baru tersedia.

```bash
javac -d out src/DownloadFileGUI.java
java -cp out DownloadFileGUI
```

Atau buka folder project pada IntelliJ IDEA / NetBeans / VS Code dengan Java Extension.

## Pengujian

URL file dapat menggunakan file publik yang dapat diakses langsung melalui HTTP/HTTPS. Untuk pengujian ZIP, gunakan URL langsung yang berakhiran `.zip`.

Berikut website yang dapat jadikan ujicoba:

- https://filesamples.com/
- https://filesamples.com/samples/video/mkv/sample_960x540.mkv
- https://filesamples.com/samples/video/mkv/sample_1280x720.mkv
- https://getsamplefiles.com/download/zip/sample-1.zip
