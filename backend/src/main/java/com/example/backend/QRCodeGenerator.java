package com.example.backend;

import java.nio.file.*;
import java.io.IOException;
import java.util.Map;
import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public class QRCodeGenerator {

    // Function to create the QR code
    public static void createQR(String data, String path, String charset, Map hashMap, int height, int width) throws WriterException, IOException {

        BitMatrix matrix = new MultiFormatWriter().encode(
                new String(data.getBytes(charset), charset),
                BarcodeFormat.QR_CODE, width, height);

        // Use java.nio.file for modern, non-deprecated file writing
        Path outputPath = java.nio.file.FileSystems.getDefault().getPath(path);

        // Write the matrix to an image file (PNG, JPG, etc.)
        MatrixToImageWriter.writeToPath(matrix, path.substring(path.lastIndexOf('.') + 1), outputPath);
    }
}
