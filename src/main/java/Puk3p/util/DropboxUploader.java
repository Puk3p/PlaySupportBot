package Puk3p.util;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class DropboxUploader {

    private static final String DROPBOX_ACCESS_TOKEN = System.getenv("DROPBOX_TOKEN");

    private final DbxClientV2 client;

    public DropboxUploader() {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("TicketUploaderApp").build();
        client = new DbxClientV2(config, DROPBOX_ACCESS_TOKEN);
    }

    public String uploadFile(String localFilePath, String dropboxFolder) {
        File localFile = new File(localFilePath);
        if (!localFile.exists()) {
            System.err.println("[DropboxUploader] Fișierul nu există: " + localFilePath);
            return null;
        }

        try (InputStream in = new FileInputStream(localFile)) {
            String dropboxPath = dropboxFolder + "/" + localFile.getName();
            FileMetadata metadata = client.files()
                    .uploadBuilder(dropboxPath)
                    .withMode(WriteMode.OVERWRITE)
                    .uploadAndFinish(in);

            String sharedLink = client.sharing()
                    .createSharedLinkWithSettings(metadata.getPathLower())
                    .getUrl();
            System.out.println("[DropboxUploader] Fișier încărcat cu succes: " + sharedLink);

            return sharedLink;
        } catch (Exception e) {
            System.err.println("[DropboxUploader] Eroare la încărcarea fișierului: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

}
