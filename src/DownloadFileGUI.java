import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.zip.*;
import java.util.Enumeration;

public class DownloadFileGUI extends JFrame {
    private final JTextField urlField = new JTextField();
    private final JTextField folderField = new JTextField();
    private final JButton downloadButton = new JButton("Download");
    private final JButton browseButton = new JButton("Pilih Folder");
    private final JProgressBar progressBar = new JProgressBar(0, 100);
    private final JLabel statusLabel = new JLabel("Siap mengunduh.");
    private final JLabel fileLabel = new JLabel("File: -");

    public DownloadFileGUI() {
        setTitle("Download File - Pemrograman Internet");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(680, 360);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(20, 20, 20, 20));
        root.setBackground(new Color(245, 247, 250));

        JLabel title = new JLabel("File Downloader");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(new Color(30, 58, 138));

        JLabel subtitle = new JLabel("Download file dari URL dengan progress dan dukungan ZIP");
        subtitle.setForeground(new Color(90, 98, 110));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);
        root.add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 0, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        form.add(new JLabel("URL File"), c);
        c.gridx = 1; c.weightx = 1;
        form.add(urlField, c);

        c.gridx = 0; c.gridy = 1; c.weightx = 0;
        form.add(new JLabel("Folder"), c);
        c.gridx = 1; c.weightx = 1;
        folderField.setText(new File(System.getProperty("user.home"), "Downloads").getAbsolutePath());
        form.add(folderField, c);
        c.gridx = 2; c.weightx = 0;
        form.add(browseButton, c);

        c.gridx = 0; c.gridy = 2; c.gridwidth = 3;
        form.add(fileLabel, c);

        c.gridy = 3;
        progressBar.setStringPainted(true);
        progressBar.setValue(0);
        form.add(progressBar, c);

        c.gridy = 4;
        form.add(statusLabel, c);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        downloadButton.setPreferredSize(new Dimension(120, 35));
        actions.add(downloadButton);

        JPanel center = new JPanel(new BorderLayout(5, 5));
        center.setOpaque(false);
        center.add(form, BorderLayout.CENTER);
        center.add(actions, BorderLayout.SOUTH);
        root.add(center, BorderLayout.CENTER);

        setContentPane(root);

        browseButton.addActionListener(e -> chooseFolder());
        downloadButton.addActionListener(e -> startDownload());
        urlField.addActionListener(e -> startDownload());
    }

    private void chooseFolder() {
        JFileChooser chooser = new JFileChooser(folderField.getText());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            folderField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void startDownload() {
        String urlText = urlField.getText().trim();
        String folderText = folderField.getText().trim();

        if (urlText.isEmpty()) {
            showError("URL file belum diisi.");
            return;
        }

        try {
            URL url = new URI(urlText).toURL();
            File folder = new File(folderText);
            if (!folder.exists() && !folder.mkdirs()) {
                showError("Folder tujuan tidak dapat dibuat.");
                return;
            }

            String fileName = getFileName(url);
            File output = new File(folder, fileName);

            downloadButton.setEnabled(false);
            browseButton.setEnabled(false);
            urlField.setEnabled(false);
            progressBar.setIndeterminate(false);
            progressBar.setValue(0);
            fileLabel.setText("File: " + fileName);
            statusLabel.setText("Menghubungkan ke server...");

            new SwingWorker<File, Integer>() {
                @Override
                protected File doInBackground() throws Exception {
                    downloadFile(url, output);
                    return output;
                }

                @Override
                protected void process(java.util.List<Integer> chunks) {
                    if (!chunks.isEmpty()) {
                        int latest = chunks.get(chunks.size() - 1);
                        progressBar.setValue(latest);
                        statusLabel.setText("Progress: " + latest + "%");
                    }
                }

                @Override
                protected void done() {
                    downloadButton.setEnabled(true);
                    browseButton.setEnabled(true);
                    urlField.setEnabled(true);
                    try {
                        File downloaded = get();
                        progressBar.setValue(100);
                        statusLabel.setText("Download selesai.");
                        JOptionPane.showMessageDialog(
                                DownloadFileGUI.this,
                                "Download selesai!\n\nFile: " + downloaded.getName()
                                        + "\nLokasi: " + downloaded.getAbsolutePath(),
                                "Download Berhasil",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        if (isZipFile(downloaded)) {
                            askExtract(downloaded);
                        }
                    } catch (Exception ex) {
                        progressBar.setValue(0);
                        statusLabel.setText("Download gagal.");
                        showError("Download gagal:\n" + getRootMessage(ex));
                    }
                }
            }.execute();

        } catch (Exception ex) {
            showError("URL tidak valid:\n" + ex.getMessage());
        }
    }

    // Dasar implementasi mengikuti konsep DownloadFile:
    // URL -> BufferedInputStream -> FileOutputStream.
    private void downloadFile(URL url, File output) throws IOException {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(20000);
            connection.setRequestProperty("User-Agent", "Java DownloadFileGUI/1.0");
            connection.connect();

            int response = connection.getResponseCode();
            if (response < 200 || response >= 300) {
                throw new IOException("Server mengembalikan HTTP " + response
                        + " " + connection.getResponseMessage());
            }

            long totalBytes = connection.getContentLengthLong();

            try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                 FileOutputStream out = new FileOutputStream(output)) {

                byte[] buffer = new byte[8192];
                long downloaded = 0;
                int count;

                while ((count = in.read(buffer)) != -1) {
                    out.write(buffer, 0, count);
                    downloaded += count;

                    if (totalBytes > 0) {
                        int progress = (int) Math.min(100, (downloaded * 100) / totalBytes);
                        publishProgress(progress);
                    }
                }
            }
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private void publishProgress(int value) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(value);
            statusLabel.setText("Progress: " + value + "%");
        });
    }

    private String getFileName(URL url) {
        String path = url.getPath();
        String name = Paths.get(path).getFileName() == null ? "" : Paths.get(path).getFileName().toString();

        if (name.isBlank()) {
            name = "downloaded_file";
        }

        try {
            name = URLDecoder.decode(name, "UTF-8");
        } catch (Exception ignored) {}

        return name;
    }

    private boolean isZipFile(File file) {
        if (!file.isFile() || !file.getName().toLowerCase().endsWith(".zip")) {
            return false;
        }

        try (ZipFile zip = new ZipFile(file)) {
            zip.entries();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void askExtract(File zipFile) {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "File yang didownload terdeteksi sebagai ZIP.\n"
                        + "Apakah file ingin diekstrak?",
                "File ZIP Terdeteksi",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            try {
                File destination = new File(
                        zipFile.getParentFile(),
                        removeExtension(zipFile.getName())
                );
                extractZip(zipFile, destination);

                JOptionPane.showMessageDialog(
                        this,
                        "Ekstraksi selesai.\n\nLokasi: " + destination.getAbsolutePath(),
                        "Ekstraksi Berhasil",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception ex) {
                showError("Ekstraksi gagal:\n" + getRootMessage(ex));
            }
        }
    }

    private void extractZip(File zipFile, File destination) throws IOException {
        Path destinationPath = destination.toPath().toAbsolutePath().normalize();
        Files.createDirectories(destinationPath);

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            byte[] buffer = new byte[8192];

            while ((entry = zis.getNextEntry()) != null) {
                Path target = destinationPath.resolve(entry.getName()).normalize();

                // Mencegah Zip Slip.
                if (!target.startsWith(destinationPath)) {
                    throw new IOException("Entry ZIP tidak aman: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(target);
                } else {
                    Files.createDirectories(target.getParent());
                    try (OutputStream out = Files.newOutputStream(target)) {
                        int len;
                        while ((len = zis.read(buffer)) != -1) {
                            out.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }

    private String removeExtension(String name) {
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private String getRootMessage(Throwable e) {
        Throwable current = e;
        while (current.getCause() != null) current = current.getCause();
        return current.getMessage() != null ? current.getMessage() : current.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new DownloadFileGUI().setVisible(true);
        });
    }
}
