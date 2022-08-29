
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Clounter {

  private static final String FORMATT = "%1$-14s";
  static FileWriter fw;
  static Map<String, Statistics> stat = new HashMap<>();

  public static class Statistics {
    public String ext;
    public long charCount = 0;
    public long lineCount = 0;    
	public long fileCount = 0;
    public long effectiveLineCount = 0;
  }

  public static String padLeftZeros(String inputString, int length) {
    if (inputString.length() >= length) {
      return inputString;
    }
    StringBuilder sb = new StringBuilder();
    while (sb.length() < length - inputString.length()) {
      sb.append(' ');
    }
    sb.append(inputString);

    return sb.toString();
  }

  public static void main(String[] args) throws IOException {
//    fw = new FileWriter(OUTPUT);

//    Files.write(Paths.get(OUTPUT), "".getBytes(), StandardOpenOption.WRITE);

    String path = args.length > 0 ? args[0] : ".";

    showFiles(new File[]{new File(path)});

    System.out.println(String.format(FORMATT, "") +
      String.format(FORMATT, "CHAR") +
      String.format(FORMATT, "EFFECTIVE LOC") +
      String.format(FORMATT, "LOC")+
	  String.format(FORMATT, "FILES") 
      );


    stat.forEach((key, value) -> {
      System.out.println(String.format(FORMATT, key) +
        String.format(FORMATT, value.charCount) +
        String.format(FORMATT, value.effectiveLineCount) +
        String.format(FORMATT, value.lineCount)+
        String.format(FORMATT, value.fileCount)
		);
    });
  }


  public static void showFiles(File[] files) throws IOException {
    for (File file : files) {
      if (file.isDirectory()) {
        showFiles(file.listFiles()); // Calls same method again.
      } else {
        processFile(file);
      }
    }
  }

  public static String getExtensionByStringHandling(String filename) {
    return Optional.ofNullable(filename)
      .filter(f -> f.contains("."))
      .map(f -> f.substring(filename.lastIndexOf(".") + 1)).orElse(null);
  }

  public static void processFile(File inf) throws IOException {
//    if (!inf.getName().endsWith(".java")) {
//      return;
//    }
    if (inf.getAbsolutePath().contains("node_modules")) {
      return;
    }
    if (inf.getAbsolutePath().contains("target")) {
      return;
    }    if (inf.getAbsolutePath().contains(".git")) {
      return;
    }
System.out.println("Process"+inf.getAbsolutePath());


    String ext = getExtensionByStringHandling(inf.getName());

    if (inf.getAbsolutePath().contains("src/test")) {
      ext += "-test";
    }
    if (inf.getAbsolutePath().contains("src\\test")) {
      ext += "-test";
    }

    if (!stat.containsKey(ext)) {
      stat.put(ext, new Statistics());
    }
    Statistics item = stat.get(ext);
    item.ext = ext;
	item.fileCount++;
	
//    File outf = new File(OUTPUT);
    FileReader ins = null;
    FileWriter outs = null;
    try {
      ins = new FileReader(inf);
//      outs = new FileWriter(outf, true);
      int prevch = -1;
      int ch;
      while ((ch = ins.read()) != -1) {
//        outs.write(ch);

        if (ch == '\n') {
          item.lineCount++;
          
          if (prevch != ch) {
            item.effectiveLineCount++;
            
          }
        }
        if (Character.isAlphabetic(ch)) {
          item.charCount++;
          
        }

        prevch = ch;

      }
    } catch (IOException e) {
      System.out.println(e);
      System.exit(-1);
    } finally {
      try {
        ins.close();
//        outs.close();
      } catch (IOException e) {
      }
    }
  }

}
