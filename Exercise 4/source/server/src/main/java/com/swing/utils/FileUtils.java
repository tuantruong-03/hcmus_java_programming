package com.swing.utils;

import com.swing.types.Result;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {
    private FileUtils() {}
    public static Result<Void> store(byte[] data, String path) {
        File file = new File(path);
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                 parent.mkdirs();
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(data);
            }
            return Result.success(null);
        } catch (IOException e) {
            return Result.failure(e);
        }
    }

    public static Result<Boolean> delete(String path) {
        try {
            return Result.success(Files.deleteIfExists(Paths.get(path))); // returns true if deleted successfully, false otherwise
        } catch (IOException e) {
            return Result.failure(e);
        }
    }

    public static byte[] readBytes(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }
}
