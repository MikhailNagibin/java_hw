package com.mipt.nagibinmikhail;
import com.mipt.nagibinmikhail.hw9.FileProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class FileProcessorTest {

  @TempDir
  Path tempDir;

  @Test
  void testSplitAndMergeFile() throws IOException {
    FileProcessor processor = new FileProcessor();

    Path testFile = tempDir.resolve("test.dat");
    byte[] testData = new byte[1500]; // 1.5KB данных
    new Random().nextBytes(testData);
    Files.write(testFile, testData);

    Path outputDir = tempDir.resolve("parts");
    List<Path> parts = processor.splitFile(testFile.toString(), outputDir.toString(), 500);

    assertEquals(3, parts.size());

    assertEquals(500, Files.size(parts.get(0)));
    assertEquals(500, Files.size(parts.get(1)));
    assertEquals(500, Files.size(parts.get(2)));

    assertTrue(parts.get(0).toString().endsWith("test.dat.part1"));
    assertTrue(parts.get(1).toString().endsWith("test.dat.part2"));
    assertTrue(parts.get(2).toString().endsWith("test.dat.part3"));

    Path mergedFile = tempDir.resolve("merged.dat");
    processor.mergeFiles(parts, mergedFile.toString());

    assertArrayEquals(Files.readAllBytes(testFile), Files.readAllBytes(mergedFile));
  }

  @Test
  void testSplitSmallFile() throws IOException {
    FileProcessor processor = new FileProcessor();

    Path smallFile = tempDir.resolve("small.txt");
    Files.writeString(smallFile, "Hello World!");

    Path outputDir = tempDir.resolve("small_parts");
    List<Path> parts = processor.splitFile(smallFile.toString(), outputDir.toString(), 100);

    assertEquals(1, parts.size());
    assertEquals(Files.size(smallFile), Files.size(parts.get(0)));

    Path mergedFile = tempDir.resolve("merged_small.txt");
    processor.mergeFiles(parts, mergedFile.toString());

    assertEquals(Files.readString(smallFile), Files.readString(mergedFile));
  }

  @Test
  void testMergeWithMissingPart() throws IOException {
    FileProcessor processor = new FileProcessor();

    Path part1 = tempDir.resolve("part1.txt");
    Path part2 = tempDir.resolve("part2.txt");
    Files.writeString(part1, "Part 1 content");

    List<Path> parts = List.of(part1, part2); // part2 не существует

    Path outputFile = tempDir.resolve("output.txt");
    assertThrows(IOException.class, () -> processor.mergeFiles(parts, outputFile.toString()));
  }
}
