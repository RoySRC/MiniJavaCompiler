import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class TestType {

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final InputStream originalIn = System.in;
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @Test
  public void test1() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test2() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test3() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test4() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test5() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test6() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test7() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test8() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test9() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test10() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test11() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test12() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test13() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test14() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test15() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test16() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test17() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test18() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test19() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test20() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test21() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test22() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test23() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test24() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test25() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test26() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test27() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test28() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test29() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test30() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test31() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test32() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test33() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test34() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test35() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test36() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test37() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test38() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test39() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test40() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test41() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test42() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test43() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test44() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test45() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test46() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test47() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test48() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test49() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test50() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test51() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test52() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test53() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test54() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test55() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test56() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test57() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test58() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test59() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test60() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test69() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test70() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test71() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test72() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test73() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test74() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test75() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test76() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test77() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void test78() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void test68() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void BinaryTree() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void BinaryTreeError() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void BubbleSort() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void BubbleSortError() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void Factorial() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void FactorialError() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void LinearSearch() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void LinearSearchError() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void LinkedList() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void LinkedListError() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void MoreThan4() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void MoreThan4Error() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void QuickSort() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void QuickSortError() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void TreeVisitor() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertTrue(typecheck.check());
  }

  @Test
  public void TreeVisitorError() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }

  @Test
  public void RedBlackTree() throws FileNotFoundException {
    String filename = "./src/test/resources/TypecheckingTestResource/" + Thread.currentThread().getStackTrace()[1].getMethodName() + ".java";
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    assertFalse(typecheck.check());
  }
}
