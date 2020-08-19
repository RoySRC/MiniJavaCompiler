

class p_Dog {

    public static void main (String [] id) {
        {
            System.out.println(new cat().fibonachi(10));
        }
    }

}

class cat {

    public int fibonachi(int a){
        int v;
        int t;
        v = 1;

        if(a < 1){

        } else  if (a < 2) {

        } else if (1 < a){
            t = a - 1;
            v = this.fibonachi(a - 1);
            v =  v + t;

        }else {}

        return v;
    }
}
