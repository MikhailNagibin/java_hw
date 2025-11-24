package com.mipt.nagibinmikhail;

import com.mipt.nagibinmikhail.hw9.TextFileAnalyzer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TextFileAnalyzerTest {

  @TempDir
  Path tempDir;

  @Test
  void testAnalyzeFile() throws IOException {
    TextFileAnalyzer analyzer = new TextFileAnalyzer();

    Path testFile = tempDir.resolve("test.txt");
    List<String> lines = Arrays.asList("Hello world!", "This is test.", "Java IO");
    Files.write(testFile, lines);

    TextFileAnalyzer.AnalysisResult result = analyzer.analyzeFile(testFile.toString());

    assertEquals(3, result.getLineCount());
    assertEquals(7, result.getWordCount());
    assertTrue(result.getCharCount() > 30);

    assertTrue(result.getCharFrequency().get('H') >= 1);
    assertTrue(result.getCharFrequency().get('e') >= 2);
  }

  @Test
  void testAnalyzeEmptyFile() throws IOException {
    TextFileAnalyzer analyzer = new TextFileAnalyzer();

    Path emptyFile = tempDir.resolve("empty.txt");
    Files.createFile(emptyFile);

    TextFileAnalyzer.AnalysisResult result = analyzer.analyzeFile(emptyFile.toString());

    assertEquals(0, result.getLineCount());
    assertEquals(0, result.getWordCount());
    assertEquals(0, result.getCharCount());
    assertTrue(result.getCharFrequency().isEmpty());
  }

  @Test
  void testSaveAnalysisResult() throws IOException {
    TextFileAnalyzer analyzer = new TextFileAnalyzer();

    java.util.Map<Character, Integer> frequency = new java.util.HashMap<>();
    frequency.put('A', 3);
    frequency.put('B', 2);

    TextFileAnalyzer.AnalysisResult result = new TextFileAnalyzer.AnalysisResult(2, 5, 20, frequency);

    Path outputFile = tempDir.resolve("analysis_result.txt");
    analyzer.saveAnalysisResult(result, outputFile.toString());

    assertTrue(Files.exists(outputFile));
    assertTrue(Files.size(outputFile) > 0);

    String content = Files.readString(outputFile);
    assertTrue(content.contains("Line count: 2"));
    assertTrue(content.contains("Word count: 5"));
    assertTrue(content.contains("Character count: 20"));
    assertTrue(content.contains("'A': 3"));
  }
}