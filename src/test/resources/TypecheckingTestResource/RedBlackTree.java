/* Implementation of Array using Red-Black tree.
   Copyright (C) 2018 Alex Vong <alexvong1995@gmail.com>

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.


   Reference:

   Okasaki, C. (1999) Red-Black Trees in a Functional Setting.
   Journal of Functional Programming 9(4):471-477.

   https://course.ccs.neu.edu/cs5010f17/Inheritance/jfp99redblack.pdf  */


class Main
{
    public static void main(String[] z_)
    {
        int z2_;
        int n;
        int k;
        int j;
        Array arr;

        {
            n = 128;
            k = 0;
            j = 0;
            arr = new U().make_array(n);

            while (k < n)
                {
                    z2_ = arr.set(k, -k);
                    k = k + 1;
                }

            while (j < n)
                {
                    System.out.println(arr.ref(j));
                    j = j + 1;
                }

            System.out.println(-arr.len());
        }
    }
}


class U
{
    int z_;

    public Tree tree(boolean c, Tree l, int k, int v, Tree r)
    {
        Tree t;

        t = new Tree();
        z_ = t.set_size(l.size() + r.size() + 1);
        z_ = t.set_color(c);
        z_ = t.set_left(l);
        z_ = t.set_right(r);
        z_ = t.set_idx(k);
        z_ = t.set_val(v);

        return t;
    }

    public Tree balanced_tree_(Tree a,
                               int k1, int v1,
                               Tree b,
                               int k2, int v2,
                               Tree c,
                               int k3, int v3,
                               Tree d)
    {
        return this.tree(true,
                         this.tree(false, a, k1, v1, b),
                         k2, v2,
                         this.tree(false, c, k3, v3, d));
    }

    public Array make_array(int n)
    {
        Tree t;
        Array arr;

        if (!(0 < n))
            t = new Tree();
        else
            t = this.make_array(n - 1).tree().insert(n - 1, 0);

        arr = new Array();
        z_ = arr.set_tree(t);

        return arr;
    }
}


class Array
{
    Tree tree;

    public Tree tree() {return tree;}
    public int set_tree(Tree t) {tree = t; return 0;}

    public int len() {return tree.size();}
    public int ref(int k) {return tree.member(k);}

    public int set(int k, int v)
    {
        tree = tree.insert(k, v);
        return 0;
    }
}


class Tree
{
    int z_;

    int size;
    boolean color;

    Tree left;
    Tree right;

    int idx;
    int val;

    public boolean is_empty() {return !(size < 0) && !(0 < size);}

    public boolean is_black() {return !color;}
    public boolean is_red() {return color;}

    public boolean is_nonempty_black()
    {return !this.is_empty() && this.is_black();}
    public boolean is_nonempty_red()
    {return !this.is_empty() && this.is_red();}

    public int size() {return size;}

    public Tree left() {return left;}
    public Tree right() {return right;}

    public int idx() {return idx;}
    public int val() {return val;}

    public int set_size(int n) {size = n; return 0;}
    public int set_color(boolean c) {color = c; return 0;}

    public int set_left(Tree t) {left = t; return 0;}
    public int set_right(Tree t) {right = t; return 0;}

    public int set_idx(int k) {idx = k; return 0;}
    public int set_val(int v) {val = v; return 0;}

    public int member(int k)
    {
        int v;

        if (k < idx)
            v = left.member(k);
        else
            if (idx < k)
                v = right.member(k);
            else
                v = val;

        return v;
    }

    public Tree make_black()
    {
        Tree t;

        if (this.is_empty())
            t = this;
        else
            t = new U().tree(false, left, idx, val, right);

        return t;
    }

    public Tree insert_(int k, int v)
    {
        Tree t;

        if (this.is_empty())
            t = new U().tree(true, new Tree(), k, v, new Tree());
        else
            {
                if (k < idx)
                    t = new U().tree(color,
                                     left.insert_(k, v),
                                     idx, val,
                                     right).balance();
                else
                    if (idx < k)
                        t = new U().tree(color,
                                         left,
                                         idx, val,
                                         right.insert_(k, v)).balance();
                    else
                        t = new U().tree(color, left, k, v, right);
            }

        return t;
    }

    public Tree insert(int k, int v)
    {
        Tree t;
        t = this.insert_(k, v).make_black();
        return t;
    }

    public Tree balance()
    {
        Tree t;
        Tree a; Tree b; Tree c; Tree d;
        int k1; int v1; int k2; int v2; int k3; int v3;

        if (this.is_nonempty_black())
            if (left.is_nonempty_red())
                if (left.left().is_nonempty_red())
                    {
                        a = left.left().left();
                        k1 = left.left().idx();
                        v1 = left.left().val();
                        b = left.left().right();
                        k2 = left.idx();
                        v2 = left.val();
                        c = left.right();
                        k3 = idx;
                        v3 = val;
                        d = right;
                        t = new U().balanced_tree_(a,
                                                   k1, v1,
                                                   b,
                                                   k2, v2,
                                                   c,
                                                   k3, v3,
                                                   d);
                    }
                else
                    t = this.balance_();
            else
                t = this.balance_();
        else
            t = this.balance_();

        return t;
    }

    public Tree balance_()
    {
        Tree t;
        Tree a; Tree b; Tree c; Tree d;
        int k1; int v1; int k2; int v2; int k3; int v3;

        if (this.is_nonempty_black())
            if (left.is_nonempty_red())
                if (left.right().is_nonempty_red())
                    {
                        a = left.left();
                        k1 = left.idx();
                        v1 = left.val();
                        b = left.right().left();
                        k2 = left.right().idx();
                        v2 = left.right().val();
                        c = left.right().right();
                        k3 = idx;
                        v3 = val;
                        d = right;
                        t = new U().balanced_tree_(a,
                                                   k1, v1,
                                                   b,
                                                   k2, v2,
                                                   c,
                                                   k3, v3,
                                                   d);
                    }
                else
                    t = this.balance2_();
            else
                t = this.balance2_();
        else
            t = this.balance2_();

        return t;
    }

    public Tree balance2_()
    {
        Tree t;
        Tree a; Tree b; Tree c; Tree d;
        int k1; int v1; int k2; int v2; int k3; int v3;

        if (this.is_nonempty_black())
            if (right.is_nonempty_red())
                if (right.left().is_nonempty_red())
                    {
                        a = left;
                        k1 = idx;
                        v1 = val;
                        b = right.left().left();
                        k2 = right.left().idx();
                        v2 = right.left().val();
                        c = right.left().right();
                        k3 = right.idx();
                        v3 = right.val();
                        d = right.right();
                        t = new U().balanced_tree_(a,
                                                   k1, v1,
                                                   b,
                                                   k2, v2,
                                                   c,
                                                   k3, v3,
                                                   d);
                    }
                else
                    t = this.balance3_();
            else
                t = this.balance3_();
        else
            t = this.balance3_();

        return t;
    }

    public Tree balance3_()
    {
        Tree t;
        Tree a; Tree b; Tree c; Tree d;
        int k1; int v1; int k2; int v2; int k3; int v3;

        if (this.is_nonempty_black())
            if (right.is_nonempty_red())
                if (right.right().is_nonempty_red())
                    {
                        a = left;
                        k1 = idx;
                        v1 = val;
                        b = right.left();
                        k2 = right.idx();
                        v2 = right.val();
                        c = right.right().left();
                        k3 = right.right().idx();
                        v3 = right.right().val();
                        d = right.right().right();
                        t = new U().balanced_tree_(a,
                                                   k1, v1,
                                                   b,
                                                   k2, v2,
                                                   c,
                                                   k3, v3,
                                                   d);
                    }
                else
                    t = this;
            else
                t = this;
        else
            t = this;

        return t;
    }
}
