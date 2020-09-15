import VaporMIR.VaporMVisitor;
import org.junit.Test;

import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;

import static org.junit.Assert.assertArrayEquals;

public class TestVaporM {

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
    ProcessBuilder pb = new ProcessBuilder("java", "-jar", "src/main/java/lib/vapor.jar", "run", "-mips", command);
    Process p = pb.start();
    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
    String line;
    LinkedList<String> lines = new LinkedList<>();
    while ((line = reader.readLine()) != null) {
      lines.add(line);
    }
    reader.close();
    return lines.toArray(new String[lines.size()]);
  }

  private void writeToFile(VaporMVisitor vmv, String testFileName) throws IOException {
    FileWriter fileWriter = new FileWriter("/tmp/"+testFileName+".vaporm");
    PrintWriter printWriter = new PrintWriter(fileWriter);
    for (String s : vmv.vaporMProgram.instructions) {
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
    String filename = "./src/test/resources/VaporMIRTestResource/in/"+testFileName+".vapor";
    String expected = "./src/test/resources/VaporMIRTestResource/out/"+testFileName+".out";
    VaporMVisitor vmv = new VaporMVisitor(V2VM.parseVapor(new FileInputStream(new File(filename)), System.err));
    writeToFile(vmv, testFileName);
    String[] actual = getVaporInterpreterOutput("/tmp/"+testFileName+".vaporm");
    assertArrayEquals(Expected(expected), actual);
    deleteFile("/tmp/"+testFileName+".vapor");
  }


  @Test(timeout = 2000)
  public void BubbleSort() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test(timeout = 2000)
  public void BubbleSortOpt() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test(timeout = 2000)
  public void LinkedList() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test(timeout = 2000)
  public void LinkedListOpt() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test(timeout = 2000)
  public void Factorial() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test(timeout = 2000)
  public void FactorialOpt() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test(timeout = 2000)
  public void QuickSort() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test(timeout = 2000)
  public void QuickSortOpt() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test(timeout = 2000)
  public void LinearSearch() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test(timeout = 2000)
  public void LinearSearchOpt() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test(timeout = 2000)
  public void BinaryTree() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test(timeout = 2000)
  public void BinaryTreeOpt() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test(timeout = 2000)
  public void MoreThan4() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test(timeout = 2000)
  public void MoreThan4_2() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test(timeout = 2000)
  public void MoreThan4Opt() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }

  @Test(timeout = 2000)
  public void TreeVisitor() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }

  @Test(timeout = 2000)
  public void TreeVisitorOpt() throws Throwable {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }

}
