import java.io.*;
import java.util.Scanner;

/**
 * @author cuongnk
 * @since 11/4/2023
 */
public class Main {

  /**
   * Configuration
   */
  static String inputFilePath = "src/aegisub.ass";
  static String enTextFilePath = "src/en-text.txt";
  static String vnTextFilePath = "src/vn-text.txt";
  static String outputFilePath = "src/result.txt";
  static String[] excludedStyles = {
      "Signs",
  };
  static String[] excludedNames = {
      "Sign",
  };

  /**
   * Extracts Text from line which starts with "Dialogue:" like the line below:
   * Dialogue: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text
   *
   * @param line input line
   * @param mode for difference between extractText() and mergeText()
   * @return parsed Text of input line. If unable to resolve, returns input line.
   */
  static String parseLine(String line, String mode, BufferedReader vnReader) throws IOException {
    String resultLine = line;
    boolean foundStyle = false;
    boolean foundName = false;

    // Check if the line contains the 9th ','
    int indexOf9thComma = findIndexOfNthOccurOfChar(line, ',', 9);
    if (indexOf9thComma != -1) {
      String[] lineProps = line.substring(0, indexOf9thComma).split(",");
      String text = line.substring(indexOf9thComma + 1);

      // Check if style includes in excludedStyles
      for (String style : excludedStyles) {
        if (lineProps[3].equals(style)) {
          foundStyle = true;
          break;
        }
      }

      // Check if name includes in excludedNames
      for (String name : excludedNames) {
        if (lineProps[4].equals(name)) {
          foundName = true;
          break;
        }
      }

      if (mode.equals("extract")) {
        resultLine = text;
        if (foundStyle || foundName) {
          resultLine = resultLine.replaceAll("\\{.*?}", "");
        }
      }

      if (mode.equals("merge")) {
        String vnReaderLine;
        String prefixLine;

        if ((vnReaderLine = vnReader.readLine()) != null) {
          // Merging vn-text line to writer line
          if (foundStyle || foundName) {
            int indexToMergeText = line.lastIndexOf('}');
            System.out.print(indexToMergeText + " ");
            if (indexToMergeText != -1) {
              if (countOccurrences(line, '}') > 1) {
                prefixLine = line.substring(0, indexToMergeText + 1) + " ";
              } else {
                prefixLine = line.substring(0, indexToMergeText + 1);
              }
              System.out.println(prefixLine);
              resultLine = prefixLine + vnReaderLine;
            }
          } else {
            prefixLine = line.substring(0, indexOf9thComma + 1);
            resultLine = prefixLine + vnReaderLine;
          }
        }
      }
    }
    return resultLine;
  }

  /**
   * Like parseLine() but using regex for less time complexity.
   * (Coming soon)
   *
   * @param line input line
   * @param mode for difference between extractText() and mergeText()
   * @return parsed Text of input line. If unable to resolve, returns input line.
   */
  static String parseLineRegex(String line, String mode) {
    return line;
  }

  /**
   * Extracts Text from input file whose line starts with "Dialogue:" then write to output file.
   *
   * @param inputFilePath path of input file
   * @param enTextFilePath path of output file
   */
  static void extractText(String inputFilePath, String enTextFilePath) {
    try {
      // Open the input file for reading
      File inputFile = new File(inputFilePath);
      BufferedReader reader = new BufferedReader(new FileReader(inputFile));

      // Open the en-text file for writing
      File enTextFile = new File(enTextFilePath);
      BufferedWriter enWriter = new BufferedWriter(new FileWriter(enTextFile));

      String readerLine;
      String enWriterLine;

      // Read the input file line by line until "Dialogue:" is found then execute code
      while ((readerLine = reader.readLine()) != null) {
        // Check if the line contains the substring "Dialogue:"
        if (!readerLine.contains("Dialogue:")) {
          continue;
        }

        enWriterLine = parseLine(readerLine, "extract", null);
        enWriter.write(enWriterLine);
        enWriter.newLine();
      }

      reader.close();
      enWriter.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Extracts Text from input file whose line starts with "Dialogue:" then write to output file.
   *
   * @param inputFilePath path of input file
   * @param vnTextFilePath path of vn-text file
   * @param outputFilePath path of output file
   */
  static void mergeText(String inputFilePath, String vnTextFilePath, String outputFilePath) {
    try {
      // Open the input file for reading
      File inputFile = new File(inputFilePath);
      BufferedReader reader = new BufferedReader(new FileReader(inputFile));

      // Open the vn-text file for merging
      File vnTextFile = new File(vnTextFilePath);
      BufferedReader vnReader = new BufferedReader(new FileReader(vnTextFile));

      // Open the output file for writing
      File outputFile = new File(outputFilePath);
      BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

      String readerLine;
      String writerLine;

      // Read the input file, copy each line from reader to writer until "Dialogue:" is found,
      // then execute code until loop to the end of file
      while ((readerLine = reader.readLine()) != null) {
        // Check if the line contains the substring "Dialogue:"
        if (!readerLine.contains("Dialogue:")) {
          writer.write(readerLine);
          writer.newLine();
          continue;
        }

        writerLine = parseLine(readerLine, "merge", vnReader);
        writer.write(writerLine);
        writer.newLine();
      }

      reader.close();
      writer.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  static int findIndexOfNthOccurOfChar(String str, char ch, int N) {
    int occur = 0;

    // Loop to find index of the Nth occurrence of the character
    for (int i = 0; i < str.length(); i++) {
      if (str.charAt(i) == ch) {
        occur += 1;
      }
      if (occur == N)
        return i;
    }
    return -1;
  }

  static int countOccurrences(String str, char c) {
    int count = 0;
    for (int i = 0; i < str.length(); i++) {
      if (str.charAt(i) == c) {
        count++;
      }
    }
    return count;
  }

  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    System.out.println("1. Extract text");
    System.out.println("2. Merge text");
    System.out.print("Choose option: ");
    int choice = Integer.parseInt(sc.nextLine());
    switch (choice) {
      case 1:
        extractText(inputFilePath, enTextFilePath);
        break;
      case 2:
        mergeText(inputFilePath, vnTextFilePath, outputFilePath);
        break;
      default:
        System.out.println("Unknown choice.");
    }
  }
}
