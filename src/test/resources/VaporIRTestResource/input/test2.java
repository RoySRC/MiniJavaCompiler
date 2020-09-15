class A {
  public static void main(String[] a) {
    int[] a;
    int j;
    j = 89;
    a = new int[90];
    a[0] = 89;
    a[j] = 99;
    a[((((8 - 1) + 3) * 6) - ((3 + 7) * 2))] = 999;
    System.out.println(a[j]); //99
    System.out.println(a[(j-j)]); //89
    System.out.println(a[((((8 - 1) + 3) * 6) - ((3 + 7) * 2))]); //999
  }
}