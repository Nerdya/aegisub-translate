import java.io.*;

public class Main {
  public static String[] stylesToExcludeList = {
      "Signs",
  };

  static int findNthOccur(String str, char ch, int N) {
    int occur = 0;

    // Loop to find the Nth occurrence of the character
    for (int i = 0; i < str.length(); i++) {
      if (str.charAt(i) == ch) {
        occur += 1;
      }
      if (occur == N)
        return i;
    }
    return -1;
  }

  public static int countOccurrences(String str, char c) {
    int count = 0;
    for (int i = 0; i < str.length(); i++) {
      if (str.charAt(i) == c) {
        count++;
      }
    }
    return count;
  }

  public static void extractText() {
    try {
      // Open the input file for reading
      File inputFile = new File("src/aegisub.ass");
      BufferedReader reader = new BufferedReader(new FileReader(inputFile));

      // Open the output file for writing
      File outputFile = new File("src/en-text.txt");
      BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

      String readerLine;
      String writerLine;

      // Read the input file line by line until "Dialogue:" is found, then execute code until loop to the end of file
      while ((readerLine = reader.readLine()) != null) {
        // Check if the line contains the substring "Dialogue:"
        if (!readerLine.contains("Dialogue:")) {
          continue;
        }

        // Find style of current line
        boolean foundStyle = false;
        int index1 = findNthOccur(readerLine, ',', 3);
        if (index1 == -1) {
          return;
        }
        String subStr = readerLine.substring(index1 + 1);
        int index2 = subStr.indexOf(",");
        if (index2 == -1) {
          return;
        }
        String styleStr = subStr.substring(0, index2);

        // Check if style includes in stylesToExcludeList, write newLine if true
        for (String style : stylesToExcludeList) {
          if (styleStr.equals(style)) {
            foundStyle = true;
            break;
          }
        }

        // Check if the line contains the 9th ","
        int index3 = findNthOccur(readerLine, ',', 9);
        if (index3 != -1) {
          writerLine = readerLine.substring(index3 + 1);
          if (foundStyle) {
            writerLine = writerLine.replaceAll("\\{.*?}", "");
          }
          System.out.println(writerLine);
          writer.write(writerLine);
          writer.newLine();
        }
      }

      reader.close();
      writer.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void mergeText() {
    try {
      // Open the input file for reading
      File inputFile = new File("src/aegisub.ass");
      BufferedReader reader = new BufferedReader(new FileReader(inputFile));

      // Open the vn_text file for merging
      File vnTextFile = new File("src/vn-text.txt");
      BufferedReader vnReader = new BufferedReader(new FileReader(vnTextFile));

      // Open the output file for writing
      File outputFile = new File("src/result.txt");
      BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

      String readerLine;
      String prefixLine;
      String vnReaderLine;
      String writerLine = "";

      // Read the input file line by line, copy line from reader to writer until "Dialogue:" is found,
      // then execute code until loop to the end of file
      while ((readerLine = reader.readLine()) != null) {
        // Check if the line contains the substring "Dialogue:"
        if (!readerLine.contains("Dialogue:")) {
          writer.write(readerLine);
          writer.newLine();
          continue;
        }

        // Find style of current line
        boolean foundStyle = false;
        int index1 = findNthOccur(readerLine, ',', 3);
        if (index1 == -1) {
          return;
        }
        String subStr = readerLine.substring(index1 + 1);
        int index2 = subStr.indexOf(",");
        if (index2 == -1) {
          return;
        }
        String styleStr = subStr.substring(0, index2);

        // Check if style includes in stylesToExcludeList, write newLine if true
        for (String style : stylesToExcludeList) {
          if (styleStr.equals(style)) {
            foundStyle = true;
            break;
          }
        }

        if ((vnReaderLine = vnReader.readLine()) != null) {
          // Merging vn_text line to writer line
          int index3;
          if (foundStyle) {
            index3 = readerLine.lastIndexOf('}');
            if (index3 != -1) {
              if (countOccurrences(readerLine, '}') > 1) {
                prefixLine = readerLine.substring(0, index3 + 2) + " ";
              } else {
                prefixLine = readerLine.substring(0, index3 + 1);
              }
              writerLine = prefixLine + vnReaderLine;
            }
          } else {
            index3 = findNthOccur(readerLine, ',', 9);
            if (index3 != -1) {
              prefixLine = readerLine.substring(0, index3 + 1);
              writerLine = prefixLine + vnReaderLine;
            }
          }
          System.out.println(writerLine);
          writer.write(writerLine);
          writer.newLine();
        }
      }

      reader.close();
      writer.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
//    extractText();
    mergeText();
  }
}
