import org.junit.Test;

import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertArrayEquals;

public class TestVapor {

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
    ProcessBuilder pb = new ProcessBuilder("java", "-jar", "src/main/java/VaporIR/vapor.jar", "run", command);
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

  private void writeToFile(J2V java2vapor, String testFileName) throws IOException {
    FileWriter fileWriter = new FileWriter("/tmp/"+testFileName+".vapor");
    PrintWriter printWriter = new PrintWriter(fileWriter);
    for (String s : java2vapor.getFinalVaporCode()) {
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

  private void TEST(String testFileName) throws Exception {
    String filename = "./src/test/resources/VaporIRTestResource/input/"+testFileName+".java";
    String expected = "./src/test/resources/VaporIRTestResource/out/"+testFileName+".out";
    J2V java2vapor = new J2V(new FileInputStream(new File(filename)));
    java2vapor.generateTranslation();
    writeToFile(java2vapor, testFileName);
    String[] actual = getVaporInterpreterOutput("/tmp/"+testFileName+".vapor");
    assertArrayEquals(Expected(expected), actual);
    deleteFile("/tmp/"+testFileName+".vapor");
  }


  @Test
  public void BubbleSort() throws Exception {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test
  public void LinkedList() throws Exception {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test
  public void Factorial() throws Exception {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test
  public void Vars() throws Exception {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test
  public void OutOfBounds() throws Exception {
    String testFileName = Thread.currentThread().getStackTrace()[1].getMethodName();
    String filename = "./src/test/resources/VaporIRTestResource/input/"+testFileName+".java";
    String expected = "./src/test/resources/VaporIRTestResource/out/"+testFileName+".out";
    J2V java2vapor = new J2V(new FileInputStream(new File(filename)));
    java2vapor.generateTranslation();
    writeToFile(java2vapor, testFileName);
    String[] actual = getVaporInterpreterOutput("/tmp/"+testFileName+".vapor");
    assertEquals(Expected(expected)[0], actual[0]);
    deleteFile("/tmp/"+testFileName+".vapor");
  }


  @Test
  public void QuickSort() throws Exception {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test
  public void LinearSearch() throws Exception {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test
  public void BinaryTree() throws Exception {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test
  public void Add() throws Exception {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test
  public void MoreThan4() throws Exception {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test
  public void PrintLiteral() throws Exception {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test
  public void TreeVisitor() throws Exception {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }


  @Test
  public void Call() throws Exception {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }

  /**
   *
   */

  @Test
  public void test1() throws Exception {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }

  @Test
  public void test2() throws Exception {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }

  @Test
  public void test10() throws Exception {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }

  @Test
  public void test13() throws Exception {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }

  @Test
  public void test14() throws Exception {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }

  @Test
  public void test15() throws Exception {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }

  @Test
  public void test16() throws Exception {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }

  @Test
  public void test66() throws Exception {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }

  @Test
  public void test90() throws Exception {
    TEST(Thread.currentThread().getStackTrace()[1].getMethodName());
  }

}
