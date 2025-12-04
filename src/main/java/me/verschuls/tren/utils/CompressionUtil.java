package me.verschuls.tren.utils;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.Base64;

public class CompressionUtil {

    private static final LZ4Factory factory = LZ4Factory.fastestInstance();
    private static final LZ4Compressor compressor = factory.highCompressor();
    private static final LZ4FastDecompressor decompressor = factory.fastDecompressor();

    /**
     * Compresses a file to a base64-encoded string using LZ4 compression.
     * The compressed data includes a 4-byte header containing the original length.
     *
     * @param file The file to compress
     * @return Base64-encoded compressed data
     * @throws IOException If file reading fails
     */
    public static String compressFile(File file) throws IOException {
        byte[] originalBytes = Files.readAllBytes(file.toPath());
        int originalLength = originalBytes.length;
        int maxCompressedLength = compressor.maxCompressedLength(originalLength);
        byte[] compressed = new byte[maxCompressedLength];
        int compressedLength = compressor.compress(originalBytes, 0, originalLength, compressed, 0, maxCompressedLength);
        ByteBuffer buffer = ByteBuffer.allocate(4 + compressedLength);
        buffer.putInt(originalLength);
        buffer.put(compressed, 0, compressedLength);
        return Base64.getEncoder().encodeToString(buffer.array());
    }

    /**
     * Decompresses a base64-encoded string back to file bytes using LZ4 decompression.
     * The compressed data must include the 4-byte header with the original length.
     *
     * @param compressedData Base64-encoded compressed data
     * @return Decompressed file bytes
     * @throws IOException If decompression fails
     */
    public static byte[] decompressToFile(String compressedData) throws IOException {
        byte[] decoded = Base64.getDecoder().decode(compressedData);
        ByteBuffer buffer = ByteBuffer.wrap(decoded);
        int originalLength = buffer.getInt();
        byte[] compressed = new byte[decoded.length - 4];
        buffer.get(compressed);
        byte[] decompressed = new byte[originalLength];
        decompressor.decompress(compressed, 0, decompressed, 0, originalLength);
        return decompressed;
    }
}
