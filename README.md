# MiniJAVA Cimpiler

## TypeChecking
In this homework assignment we build a type check system for the miniJAVA programming language. The input to this program is a valid miniJAVA program that parses but does not necessarily type check. The output of this program is either "Program type checked successfully" or "Type error". If there is a type error such as assigning an integer to a boolean the program will output "Type error", else the program will output "Program type checked successfully". This assignment also deals with checking for implicit type casting. For instance if we have two classes: class A and C, and A extends C, then the following program should type check:
```
class Main {
  public static void main(String[] args) {
    A a ;
    C c;
    int v;
    a = new A();
    c = new C();
    c = a;   // a has been implicitly type casted to C
    v = a.init((new A())); // a will be implicitly type casted to C
    System.out.println(v); //10
    v = a.init(new C());
    System.out.println(v); //10
  }
}

class A extends C {
    public int init(C c) {
        return 10;
    }
}

class C {
    public int init(int a) {
        return 12;
    }
}
```
In addition to checking for implicit typecasts, the program also checks for circular inheritance. If a circular inheritance is detected, the program prints "Type error".

## Vapor IR
For this assignment, we built a vapor translator for the miniJAVA programming language. The vapor programming language is one level closer to the MIPS assembly than miniJAVA. In the vapor programming language, the stack is managed by the vapor interpreter. Also, in this programming language there is an infinite number of registers. This program visits every node in the miniJAVA Abstract Syntax Tree (AST), and translates it to its vapor representation. 

## Vapor-M IR
The porpose of this assignment is to translate a vapor program into a vapor-M program. The vapor-M program is closer to the MIPS assembly than the respective vapor program. It is closer in the sense that the translator has to insert vapor-M code to manage the memory layout of the stack. The vapor-M program also has a limited number of registers. There are four registers for argument passing: `$a0, ..., $a3`. If a function takes more than 4 arguments, then the `in/out` stack is used to pass the additional arguments after the four argument registers have been used. In addition to the argument registers, there is also callee saved and caller saved and reserved registers and return value registers. The callee saved registers are: `$s0, .., $s5`. The caller sved registers are: `$t0, ..., $t8`. The return registers are `$v0`. The set of reserved registers are: `$s6, $s7, $v1`. The value of callee saved registers are preserved accross calls, while the value of caller saved registers is not guaranteed to be preserved across calls. Callee saved registers are preserved across calls using the following algorithm: 
  * Before the `call` statement, backup all the callee saved registers in the function local stack
  * After the `call` statement restore all backed up callee saved register.

For the purpose of correctness, I am also backing up the argument registers and restoring their values after the `call` statement. Since there is a limited number of registers, this programming assignment also uses the Linear Scan Register Allocation algorithm to make register allocations, i.e. deciding which variables are assigned to which register or the stack. One of the problems with this algorithm is that it does not take holes in live intervals into account. If a variable gets spilled on the stack it stays spilled till the end of the function.
In order to compute the live interval, this assignment uses control flow graph for a function. A control flow graph (CFG) captures all possible execution paths for a program, in our case it will capture all possible execution paths in the function. The CFG in this assignment used a basic block size of 1, i.e. each node in the CFG only contained one vapor statement. After the control flow graph is built, the use-def sets for each basic block is populated. This set contains the variables that are used and the variables that are defined in a basic block. Once the use-def sets for all basic blocks is computed, the live-in and live-out sets are computed using the following algorithm.

Once the live-in and live-out sets are computed. the live range for a variable are all the basic blocks where the variable is both live-in and live-out.

## MIPS Assembly
This is the last translation phase in the miniJAVA compiler. This translation phase takes in a vapor-M program and translates it into MIPS assembly using the vapor-M visitor to visit every node in the vapor-M abstract syntax tree and generate MIPS assembly code for that node. Here, the compiler must generate code that takes of stack frame allocations and deallocations during function calls. The compiler must also generate code for vapor and vapor-M builtins such as `HeapAllocZ`.
