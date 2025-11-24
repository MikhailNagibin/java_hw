package com.mipt.nagibinmikhail.hw9;

import java.io.*;
import java.util.*;

public class TextFileAnalyzer {

  public static class AnalysisResult {
    private final long lineCount;
    private final long wordCount;
    private final long charCount;
    private final Map<Character, Integer> charFrequency;

    public AnalysisResult(long lineCount, long wordCount, long charCount, Map<Character, Integer> charFrequency) {
      this.lineCount = lineCount;
      this.wordCount = wordCount;
      this.charCount = charCount;
      this.charFrequency = charFrequency;
    }

    public long getLineCount() { return lineCount; }
    public long getWordCount() { return wordCount; }
    public long getCharCount() { return charCount; }
    public Map<Character, Integer> getCharFrequency() { return charFrequency; }

    @Override
    public String toString() {
      return String.format(
        "AnalysisResult{lineCount=%d, wordCount=%d, charCount=%d, charFrequency=%s}",
        lineCount, wordCount, charCount, charFrequency
      );
    }
  }

  public AnalysisResult analyzeFile(String filePath) throws IOException {
    long lineCount = 0;
    long wordCount = 0;
    long charCount = 0;
    Map<Character, Integer> charFrequency = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        lineCount++;
        charCount += line.length();

        String[] words = line.trim().split("\\s+");
        if (!line.trim().isEmpty()) {
          wordCount += words.length;
        }

        for (char c : line.toCharArray()) {
          charFrequency.put(c, charFrequency.getOrDefault(c, 0) + 1);
        }

        if (reader.ready()) {
          charCount++;
        }
      }
    }

    return new AnalysisResult(lineCount, wordCount, charCount, charFrequency);
  }

  public void saveAnalysisResult(AnalysisResult result, String outputPath) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
      writer.write("File Analysis Results\n\n");
      writer.write("Line count: " + result.getLineCount() + "\n");
      writer.write("Word count: " + result.getWordCount() + "\n");
      writer.write("Character count: " + result.getCharCount() + "\n");

      writer.write("\nCharacter Frequency:\n\n");
      for (Map.Entry<Character, Integer> entry : result.getCharFrequency().entrySet()) {
        char character = entry.getKey();
        String displayChar = (character == ' ') ? "[SPACE]" :
          (character == '\t') ? "[TAB]" :
            String.valueOf(character);
        writer.write(String.format("'%s': %d\n", displayChar, entry.getValue()));
      }
    }
  }
}