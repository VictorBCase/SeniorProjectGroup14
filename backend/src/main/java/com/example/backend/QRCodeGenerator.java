package com.example.backend;

import java.nio.file.*;
import java.io.IOException;
import java.util.Map;
import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

/**
 * Utility class for generating QR codes using the ZXing library.
 * This class provides a static method to create QR codes and
 * write them as image files.
 */
public class QRCodeGenerator {

    /**
     * Generates a QR code image file from the given data string.
     * @param data             The data to encode in the QR code
     * @param path             The destination file path
     * @param charset          Character set encoding
     * @param hashMap          Optional encoding hints for ZXing (can be empty)
     * @param height           Height of the generated QR image in pixels
     * @param width            Width of the generated QR image in pixels
     * @throws WriterException if an error occurs during QR encoding
     * @throws IOException     if an error occurs while writing the file
     */
    public static void createQR(String data, String path, String charset, Map hashMap, int height, int width) throws WriterException, IOException {

        Path qrDir = Paths.get("QRCodes");
        if (!Files.exists(qrDir)) {
            Files.createDirectories(qrDir);
        }

        BitMatrix matrix = new MultiFormatWriter().encode(
                new String(data.getBytes(charset), charset),
                BarcodeFormat.QR_CODE, width, height);

        Path outputPath = qrDir.resolve(path);

        MatrixToImageWriter.writeToPath(matrix, path.substring(path.lastIndexOf('.') + 1), outputPath);
    }
}
