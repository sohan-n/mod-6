package com.example.csc325_firebase_webview_auth.model;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class StorageHelper {

    private static String bucketName;

    private static String getBucketName() {
        if (bucketName != null) {
            return bucketName;
        }
        try {
            Properties props = new Properties();
            InputStream in = StorageHelper.class.getResourceAsStream("/files/firebase-config.properties");
            props.load(in);
            bucketName = props.getProperty("storageBucket");
        } catch (Exception e) {
            bucketName = "";
        }
        return bucketName;
    }

    public static String uploadFile(File file) {
        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(
                    StorageHelper.class.getResourceAsStream("/files/key.json"));
            Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

            String objectName = "profiles/" + UUID.randomUUID() + ".jpg";
            BlobInfo blobInfo = BlobInfo.newBuilder(getBucketName(), objectName).build();
            storage.create(blobInfo, Files.readAllBytes(file.toPath()));

            return storage.signUrl(blobInfo, 7, TimeUnit.DAYS).toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }
}
