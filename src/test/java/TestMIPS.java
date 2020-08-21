import MIPS.MIPSVisitor;
import org.junit.Test;

import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;

import static org.junit.Assert.assertArrayEquals;

public class TestMIPS {

  private final String mipsJarLocation = "src/main/java/lib/mars.jar";

  private String[] Expected(String filename) throws FileNotFoundException {
    File file = new File(filename);
    LinkedList<String> list = new LinkedList<>();
    Scanner sc = new Scanner(new FileInputStream(file));
    while (sc.hasNextLine()) {
      list.add(sc.nextLine());
    }
    sc.close();
    for (String s : list)
      System.out.println(s);

    return list.toArray(new String[list.size()]);
  }

  private String[] getVaporInterpreterOutput(String command) throws IOException {
    ProcessBuilder pb = new ProcessBuilder("java", "-jar", mipsJarLocation, "nc", command);
    Process p = pb.start();
    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
    String line;
    LinkedList<String> lines = new LinkedList<>();
    while ((line = reader.readLine()) != null) {
      lines.add(line);
    }
    lines.removeLast();
    reader.close();
    return lines.toArray(new String[lines.size()]);
  }

  private void writeToFile(MIPSVisitor vmv, String testFileName) throws IOException {
    FileWriter fileWriter = new FileWriter("/tmp/"+testFileName+".s");
    PrintWriter printWriter = new PrintWriter(fileWriter);
    for (String s : vmv.mipsProgram.instructions) {
      printWriter.println(s);
    }
    printWriter.println();
    printWriter.close();
  }

  private void deleteFile(String filename) {
    File file = new File(filename);

    if(file.delete())
    {
      System.out.println("File deleted successfully");
    }
    else
    {
      System.out.println("Failed to delete the file");
    }
  }

  private void TEST(String testFileName) throws Throwable {
    String filename = "./src/test/resources/MIPSTestResource/in/"+testFileName+".vaporm";
    String expected = "./src/test/resources/MIPSTestResource/out/"+testFileName+".out";
    MIPSVisitor vmv = new MIPSVisitor(VM2M.parseVapor(new FileInputStream(new File(filename)), System.err));
    writeToFile(vmv, testFileName);
    String[] actual = getVaporInterpreterOutput("/tmp/"+testFileName+".s");
    assertArrayEquals(Expected(expected), actual);
    deleteFile("/tmp/"+testFileName+".s");
  }


  @Test(timeout = 5000)
  public void BubbleSort() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test(timeout = 5000)
  public void LinkedList() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test(timeout = 5000)
  public void Factorial() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test(timeout = 5000)
  public void QuickSort() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test(timeout = 5000)
  public void LinearSearch() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test(timeout = 5000)
  public void BinaryTree() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test(timeout = 5000)
  public void MoreThan4() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }

  @Test(timeout = 5000)
  public void TreeVisitor() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }

  /**
   * ===============================================================================================
   */

  @Test(timeout = 5000)
  public void BubbleSortOpt() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test(timeout = 5000)
  public void LinkedListOpt() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test(timeout = 5000)
  public void FactorialOpt() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test(timeout = 5000)
  public void QuickSortOpt() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test(timeout = 5000)
  public void LinearSearchOpt() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test(timeout = 5000)
  public void BinaryTreeOpt() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test(timeout = 5000)
  public void MoreThan4Opt() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }

  @Test(timeout = 5000)
  public void TreeVisitorOpt() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }

  /**
   * ===============================================================================================
   */

  @Test(timeout = 5000)
  public void test89() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }

}
