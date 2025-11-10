package com.mipt.nagibinmikhail.hw9;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileProcessor {
  public List<Path> splitFile(String sourcePath, String outputDir, int partSize) throws IOException {
    List<Path> partPaths = new ArrayList<>();
    Path sourceFile = Paths.get(sourcePath);
    String fileName = sourceFile.getFileName().toString();

    Path outputDirectory = Paths.get(outputDir);
    Files.createDirectories(outputDirectory);

    try (FileChannel sourceChannel = FileChannel.open(sourceFile, StandardOpenOption.READ)) {
      long fileSize = sourceChannel.size();
      int partNumber = 1;
      long bytesProcessed = 0;

      ByteBuffer buffer = ByteBuffer.allocate(partSize);

      while (bytesProcessed < fileSize) {
        String partFileName = String.format("%s.part%d", fileName, partNumber);
        Path partFile = outputDirectory.resolve(partFileName);

        try (FileChannel partChannel = FileChannel.open(partFile,
          StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

          buffer.clear();
          int bytesRead = sourceChannel.read(buffer);
          if (bytesRead == -1) break;

          buffer.flip();
          partChannel.write(buffer);

          bytesProcessed += bytesRead;
          partPaths.add(partFile);
          partNumber++;
        }
      }
    }

    return partPaths;
  }

  public void mergeFiles(List<Path> partPaths, String outputPath) throws IOException {
    Path outputFile = Paths.get(outputPath);

    try (FileChannel outputChannel = FileChannel.open(outputFile,
      StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

      for (Path partPath : partPaths) {
        if (!Files.exists(partPath)) {
          throw new IOException("Part file not found: " + partPath);
        }

        try (FileChannel partChannel = FileChannel.open(partPath, StandardOpenOption.READ)) {
          ByteBuffer buffer = ByteBuffer.allocate(8192);

          while (partChannel.read(buffer) > 0) {
            buffer.flip();
            outputChannel.write(buffer);
            buffer.compact();
          }
        }
      }
    }
  }
}