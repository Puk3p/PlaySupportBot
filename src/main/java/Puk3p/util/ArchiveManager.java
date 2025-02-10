package Puk3p.util;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ArchiveManager {

    public static void createArchiveIfNecessary(String categoryFolderPath, String category) {
        File folder = new File(categoryFolderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("[Arhivare] Folderul specificat nu există sau nu este valid: " + categoryFolderPath);
            return;
        }

        File[] pdfFiles = folder.listFiles((dir, name) -> name.endsWith(".pdf"));

        if (pdfFiles != null && pdfFiles.length >= 100) {
            File[] sortedFiles = sortFilesByName(pdfFiles);

            String startDate = getFileDate(sortedFiles[0]);
            String endDate = getFileDate(sortedFiles[sortedFiles.length - 1]);

            String archiveName = "Arhiva_" + category.replace(" ", "_") + "_" + startDate + "_" + endDate + ".zip";
            String archivePath = categoryFolderPath + File.separator + archiveName;

            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(archivePath))) {
                for (File file : sortedFiles) {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        zos.putNextEntry(new ZipEntry(file.getName()));

                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fis.read(buffer)) >= 0) {
                            zos.write(buffer, 0, length);
                        }
                        zos.closeEntry();
                    }
                }

                System.out.println("[Arhivare] Arhiva creată cu succes: " + archivePath);

                DropboxUploader dropboxUploader = new DropboxUploader();
                String dropboxLink = dropboxUploader.uploadFile(archivePath, "/tickete");

                if (dropboxLink != null) {
                    System.out.println("[Arhivare] Arhiva încărcată pe Dropbox: " + dropboxLink);
                }

                for (File file : sortedFiles) {
                    if (!file.delete()) {
                        System.err.println("[Arhivare] Nu s-a putut șterge fișierul: " + file.getAbsolutePath());
                    }
                }
            } catch (IOException e) {
                System.err.println("[Arhivare] Eroare la crearea arhivei: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static File[] sortFilesByName(File[] files) {
        File[] sortedFiles = files.clone();
        java.util.Arrays.sort(sortedFiles, Comparator.comparing(File::getName));
        return sortedFiles;
    }

    private static String getFileDate(File file) {
        String fileName = file.getName();
        String[] parts = fileName.split("_");
        if (parts.length > 1) {
            return parts[1].split("\\.")[0];
        }
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

}
