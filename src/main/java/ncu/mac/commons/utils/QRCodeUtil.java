package ncu.mac.commons.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.xml.bind.DatatypeConverter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class QRCodeUtil {
    private static final String PNG = "png";

    public static String generate(String message, int width, int height) throws IOException, WriterException {
//        final var baos = new ByteArrayOutputStream();
//        final var qrCodeWriter = new QRCodeWriter();
//        final var bitMatrix = qrCodeWriter.encode(message, BarcodeFormat.QR_CODE, width, height);
//
//        MatrixToImageWriter.writeToStream(bitMatrix, PNG, baos);
//        baos.flush();
//        byte[] imageInByte = baos.toByteArray();
//        baos.close();

        final var imageInByte = generateImage(message, width, height);

        String data = DatatypeConverter.printBase64Binary(imageInByte);
        String imageString = "data:image/png;base64," + data;

        return "<img src=\"" + imageString + "\">";
    }

    public static byte[] generateImage(String message, int width, int height) throws IOException, WriterException {
        final var baos = new ByteArrayOutputStream();
        final var qrCodeWriter = new QRCodeWriter();
        final var bitMatrix = qrCodeWriter.encode(message, BarcodeFormat.QR_CODE, width, height);

        MatrixToImageWriter.writeToStream(bitMatrix, PNG, baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();

        return imageInByte;
    }
}
