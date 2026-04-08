package com.app.teamsplitter.utils;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

public class FileUtils {

    public static String saveImageToAppStorage(Context context, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            // Создаём папку для фото
            File photosDir = new File(context.getFilesDir(), "player_photos");
            if (!photosDir.exists()) {
                photosDir.mkdirs();
            }

            // Уникальное имя файла
            String fileName = "player_" + UUID.randomUUID().toString() + ".jpg";
            File outputFile = new File(photosDir, fileName);

            // Копируем файл
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            return outputFile.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}