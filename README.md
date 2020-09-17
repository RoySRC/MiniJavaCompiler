![](https://www.code-inspector.com/project/12409/score/svg?branch=master&kill_cache=1)
![](https://www.code-inspector.com/project/12409/status/svg?branch=master&kill_cache=1)

# MiniJAVA Compiler
This is a miniJAVA compiler for the MIPS platform. This compiler uses the vapor programming language as an
intermediate representation and does incremental lowering to vapor-M and finally to MIPS assembly. This compiler only
supports printing of integer values to screen, printing of strings are not supported. 

## MiniJAVA Grammar and TypeSystem
The Backus–Naur form of the minijava programming language can be found 
[here](http://www.cambridge.org/resources/052182060X/MCIIJ2e/grammar.htm). Documentation relating to the  MiniJava
type system can be found 
[here](https://cs.colostate.edu/~pouchet/classes/CS453SP20/hw25-minijava/cs453/doc/miniJava-typesystem.pdf).

## Project Layout
This project is separated into five modules, where each module does incremental lowering of the original miniJava
program. The reason this is done is because it is very difficult and more error prone to directly translate an object
-oriented programming language such as miniJava with high level constructs such as function overriding and
inheritance into assembly. As a result we use the strategy of incremental lowering though the use of the visitor
design pattern to translate a miniJava program into assembly. The flowchart for the project layout is slown below:

![](pictures/project_layout_flowchart.png)

The typechecking system is responsible for making sure that all types in the user input minJava program is valid, and
that the compiler during later steps of code generation does not do invalid implicit type conversion. This system is
also responsible for checking for circular inheritance. All of this is done through the use of a hierarchical
scoped symbol table and the visitor design pattern. If this system encounters a program that fails to typecheck, a
 `Type error` message is printed to stdout, else the control is passed to the Vapor IR code generation system.

The Vapor IR code generation system is responsible for translating a valid miniJava program that typechecks into its
equivalent Vapor intermediate representation. The Vapor IR code generation system is responsible for doing one way
implicit type casting and function overriding. To accomplish the generation of Vapor IR code, this system also uses a
symbol table that is different from the symbol table in the Typechecking system. The difference in the symbol table
is in the binding information of the symbols. The binding information of symbols in this symbol table contains three
fields: type name, offset, and scope as opposed to two fields in the Typechecking system. This module is also
responsible for creating the memory layout of the user input miniJava program. Advantages of incremental lowering to
Vapor IR is that it is closer to assembly, but still does not fully impose the constraints of writing code in
assembly. The constraints of writing code in assembly include limited number of registers, having to setup the
function call stack manually, and writing additional code to retrieve return values from functions. 

The Vapor-M IR code generation system is responsible for generating the Vapor-M translation of the code generated
from the Vapor IR code generation system. Vapor-M code is closer to MIPS assembly than Vapor. As a result, all of the
limitations of assembly is imposed on code written in Vapor-M. To deal with the issue of limited number of registers
we use the linear scan register allocation algorithm for optimal register allocation. The MIPS assembly has a set of
23 registers, these are `$a0, …, $a3`, `$s0, …, $s7`, `$t0, …, $t8`, and `$v0, $v1`. The `$a` registers are for
argument passing during function calls. If a function takes in more than four arguments, the `in/out` space in the
stack is used. For convenience in storing temporary result of calculations, we have a set of three reserved registers
: `$s6, $s7, $v1`. To get the return value of a function we use `$v0` register. All other registers are used in the
optimal register allocation algorithm. The function call stack is generated after analyzing the parameter list of
the function to be called. For instance if a function takes more than four arguments, then additional stack frames
are allocated for passing additional arguments. If the return from a function is void, then there is no additional code
generated to read or clean the return register: `$v0`. The signature to declare a function in Vapor-M is: ` func Main
[in 0, out 0, local 17]`. Here, `in` and `out` represents the number of in and out stack frames. If a function
wants to call another function with more than four arguments, then the additional argument values are written to the
out stack of the caller function and when control goes to the callee function, these additional arguments are read
from the `in` stack. The `local` represents the number of variables in the current function whose value resides in
the function local stack. These are generally values for variables that have been spilled by the register allocation
algorithm.

The MIPS assembly code generator is the final phase in the miniJava compiler. This is responsible for taking in the
code generated from the Vapor-M IR code generation system, and translating it into MIPS assembly to be run on the
MIPS emulator. Unlike the vapor intermediate representations, the MIPS assembly has no built-in functions such as
`PrintIntS()`, `Error()`, and `HeapAllocZ()`. Implementations for these built-ins are inserted in the epilogue
section of the final MIPS code. In MIPS assembly we also have to generate the function call stack manually. This
is done through the function prologue and epilogue before the call to the function.

## Helper Packages
In order to help us with type checking and translating the high level object-oriented miniJava program into Vapor IR
code, we have decided to write a series of helper packages for visualization of the control flow in the abstract
syntax tree (AST) nodes. We only need to write the visualizer for the initial type checking system and the conversion
to Vapor IR. Vapor IR and Vapor-M IR code does not have as complex of an AST structure as the equivalent miniJava
program. We call this miniJava AST visualizer, the Pretty Print Visitor. The stdout of this package is best
illustrated through the following code block and its visualized AST.
```java
class Factorial{
    public static void main(String[] a){
        System.out.println(new Fac().ComputeFac(10));
    }
}

class Fac {
    public int ComputeFac(int num){
        int num_aux ;
        if (num < 1)
            num_aux = 1 ;
        else
            num_aux = num * (this.ComputeFac(num-1)) ;
        return num_aux ;
    }
}
```
The AST visualization of the above code block is illustrated below:
```
Goal
├─ MainClass
│   ├─ NodeToken →  "class"
│   ├─ Identifier
│   │   └─ NodeToken →  "Factorial"
│   ├─ NodeToken →  "{"
│   ├─ NodeToken →  "public"
│   ├─ NodeToken →  "static"
│   ├─ NodeToken →  "void"
│   ├─ NodeToken →  "main"
│   ├─ NodeToken →  "("
│   ├─ NodeToken →  "String"
│   ├─ NodeToken →  "["
│   ├─ NodeToken →  "]"
│   ├─ Identifier
│   │   └─ NodeToken →  "a"
│   ├─ NodeToken →  ")"
│   ├─ NodeToken →  "{"
│   ├─ NodeListOptional
│   ├─ NodeListOptional
│   │   └─ Statement
│   │       └─ PrintStatement
│   │           ├─ NodeToken →  "System.out.println"
│   │           ├─ NodeToken →  "("
│   │           ├─ Expression
│   │           │   └─ MessageSend
│   │           │       ├─ PrimaryExpression
│   │           │       │   └─ AllocationExpression
│   │           │       │       ├─ NodeToken →  "new"
│   │           │       │       ├─ Identifier
│   │           │       │       │   └─ NodeToken →  "Fac"
│   │           │       │       ├─ NodeToken →  "("
│   │           │       │       └─ NodeToken →  ")"
│   │           │       ├─ NodeToken →  "."
│   │           │       ├─ Identifier
│   │           │       │   └─ NodeToken →  "ComputeFac"
│   │           │       ├─ NodeToken →  "("
│   │           │       ├─ NodeOptional
│   │           │       │   └─ ExpressionList
│   │           │       │       ├─ Expression
│   │           │       │       │   └─ PrimaryExpression
│   │           │       │       │       └─ IntegerLiteral
│   │           │       │       │           └─ NodeToken →  "10"
│   │           │       │       └─ NodeListOptional
│   │           │       └─ NodeToken →  ")"
│   │           ├─ NodeToken →  ")"
│   │           └─ NodeToken →  ";"
│   ├─ NodeToken →  "}"
│   └─ NodeToken →  "}"
├─ NodeListOptional
│   └─ TypeDeclaration
│       └─ ClassDeclaration
│           ├─ NodeToken →  "class"
│           ├─ Identifier
│           │   └─ NodeToken →  "Fac"
│           ├─ NodeToken →  "{"
│           ├─ NodeListOptional
│           ├─ NodeListOptional
│           │   └─ MethodDeclaration
│           │       ├─ NodeToken →  "public"
│           │       ├─ Type
│           │       │   └─ IntegerType
│           │       │       └─ NodeToken →  "int"
│           │       ├─ Identifier
│           │       │   └─ NodeToken →  "ComputeFac"
│           │       ├─ NodeToken →  "("
│           │       ├─ NodeOptional
│           │       │   └─ FormalParameterList
│           │       │       ├─ FormalParameter
│           │       │       │   ├─ Type
│           │       │       │   │   └─ IntegerType
│           │       │       │   │       └─ NodeToken →  "int"
│           │       │       │   └─ Identifier
│           │       │       │       └─ NodeToken →  "num"
│           │       │       └─ NodeListOptional
│           │       ├─ NodeToken →  ")"
│           │       ├─ NodeToken →  "{"
│           │       ├─ NodeListOptional
│           │       │   └─ VarDeclaration
│           │       │       ├─ Type
│           │       │       │   └─ IntegerType
│           │       │       │       └─ NodeToken →  "int"
│           │       │       ├─ Identifier
│           │       │       │   └─ NodeToken →  "num_aux"
│           │       │       └─ NodeToken →  ";"
│           │       ├─ NodeListOptional
│           │       │   └─ Statement
│           │       │       └─ IfStatement
│           │       │           ├─ NodeToken →  "if"
│           │       │           ├─ NodeToken →  "("
│           │       │           ├─ Expression
│           │       │           │   └─ CompareExpression
│           │       │           │       ├─ PrimaryExpression
│           │       │           │       │   └─ Identifier
│           │       │           │       │       └─ NodeToken →  "num"
│           │       │           │       ├─ NodeToken →  "<"
│           │       │           │       └─ PrimaryExpression
│           │       │           │           └─ IntegerLiteral
│           │       │           │               └─ NodeToken →  "1"
│           │       │           ├─ NodeToken →  ")"
│           │       │           ├─ Statement
│           │       │           │   └─ AssignmentStatement
│           │       │           │       ├─ Identifier
│           │       │           │       │   └─ NodeToken →  "num_aux"
│           │       │           │       ├─ NodeToken →  "="
│           │       │           │       ├─ Expression
│           │       │           │       │   └─ PrimaryExpression
│           │       │           │       │       └─ IntegerLiteral
│           │       │           │       │           └─ NodeToken →  "1"
│           │       │           │       └─ NodeToken →  ";"
│           │       │           ├─ NodeToken →  "else"
│           │       │           └─ Statement
│           │       │               └─ AssignmentStatement
│           │       │                   ├─ Identifier
│           │       │                   │   └─ NodeToken →  "num_aux"
│           │       │                   ├─ NodeToken →  "="
│           │       │                   ├─ Expression
│           │       │                   │   └─ TimesExpression
│           │       │                   │       ├─ PrimaryExpression
│           │       │                   │       │   └─ Identifier
│           │       │                   │       │       └─ NodeToken →  "num"
│           │       │                   │       ├─ NodeToken →  "*"
│           │       │                   │       └─ PrimaryExpression
│           │       │                   │           └─ BracketExpression
│           │       │                   │               ├─ NodeToken →  "("
│           │       │                   │               ├─ Expression
│           │       │                   │               │   └─ MessageSend
│           │       │                   │               │       ├─ PrimaryExpression
│           │       │                   │               │       │   └─ ThisExpression
│           │       │                   │               │       │       └─ NodeToken →  "this"
│           │       │                   │               │       ├─ NodeToken →  "."
│           │       │                   │               │       ├─ Identifier
│           │       │                   │               │       │   └─ NodeToken →  "ComputeFac"
│           │       │                   │               │       ├─ NodeToken →  "("
│           │       │                   │               │       ├─ NodeOptional
│           │       │                   │               │       │   └─ ExpressionList
│           │       │                   │               │       │       ├─ Expression
│           │       │                   │               │       │       │   └─ MinusExpression
│           │       │                   │               │       │       │       ├─ PrimaryExpression
│           │       │                   │               │       │       │       │   └─ Identifier
│           │       │                   │               │       │       │       │       └─ NodeToken →  "num"
│           │       │                   │               │       │       │       ├─ NodeToken →  "-"
│           │       │                   │               │       │       │       └─ PrimaryExpression
│           │       │                   │               │       │       │           └─ IntegerLiteral
│           │       │                   │               │       │       │               └─ NodeToken →  "1"
│           │       │                   │               │       │       └─ NodeListOptional
│           │       │                   │               │       └─ NodeToken →  ")"
│           │       │                   │               └─ NodeToken →  ")"
│           │       │                   └─ NodeToken →  ";"
│           │       ├─ NodeToken →  "return"
│           │       ├─ Expression
│           │       │   └─ PrimaryExpression
│           │       │       └─ Identifier
│           │       │           └─ NodeToken →  "num_aux"
│           │       ├─ NodeToken →  ";"
│           │       └─ NodeToken →  "}"
│           └─ NodeToken →  "}"
└─ NodeToken →  ""
```


## TypeChecking
In this section we describe the Typechecking system in depth, including the data structures and the layout of the
symbol table.

### Symbol Table Visitor
The symbol table is the core component of the type checking system. We are using a scoped hierarchical symbol table
data structure to efficiently capture information about functions in classes, variables in functions and inheritance
hierarchy. The hierarchical symbol table is a tree based data structure where the nodes are tables with two columns
. The first column contains the symbols, and the second column contains the binding information of the symbols. The
binding information stores information about the type of the symbol and whether the symbol is a parameter. If the
symbol is a function identifier, then the type in the binding information is the return type of the function.

To build the symbol table we perform three passes over the MiniJava program.  The first pass builds a preliminary
hierarchical symbol table tree data structure. In addition to building the tree based data structure, it also checks
for duplicate declaration of identifiers. If a duplicate declaration of an identifier is found, an error will be
thrown. We illustrate this first pass with the following code example:
```c++
class A {
  public static void main(String[] args) {
    B b;
    b = new B();
    System.out.println(b.a());
  }
}

class B extends C {
  int a;
  public int a() {
    int c;
    c = this.c();
    a = 12 * c;
    return a;
  }
}

class C {
  int c;

  public int c() {
    c = 12;
    return c;
  }
}
```
The symbol table for the above code block after the first pass is as follows:
![](pictures/Typecheck_symbol_table_eample.png)

As can be seen from the above generated symbol table that the global symbol table stores pointers to the symbol
tables of all classes. In this case there are three classes: `A`, `B`, and `C`. The binding information for these
three symbols in the global symbol table is `<class, null>`. This is because `A`, `B`, and `C` are classes. The
symbol tables for the three classes contains pointers to the symbol tables of member variables and functions. The
symbol tables for functions contains pointers to the symbol tables of the variables declared in the function scope
and the function parameter list. One thing that can be observed from the above generated symbol table is that there
is no link between the symbol `C` in the symbol table for B and the symbol table of class `C`. There should be a link
between these two because class `B` inherits from class `C`, and the link makes it possible to access all the member
variables and functions in class `C` from class `B`. This link is added in the second pass.

The second pass in the symbol table visitor is responsible for creating the inheritance links between nodes of the
hierarchical symbol table data structure. After the second pass, the above symbol table looks as follows:
![](pictures/Typecheck_symbol_table_example_inheritance_link.png)

This second pass uses a different symbol table visitor called the symbol table inheritance visitor. Notice the red
link. This link signifies that class `B` inherits from class `C`. During the second pass, an error is thrown if a
class inherits from a class that does not exist. 

After the second pass, there is one additional pass - done by a different symbol table visitor - that checks the
completed symbol table for circular inheritance. This symbol table visitor is called the symbol table inheritance
type check visitor. The algorithm for circular inheritance detection is as follows:
```
Input: Symbol table node of the child class
Output: True of circular inheritance detected, False otherwise
InheritanceChain = set()
ParentClass = getparent(Input)
While there is a ParentClass
	If ParentClass not in InheritanceChain
		Add ParentClass to InheritanceChain
	Else if ParentClass in InheritanceChain
		Return True
	ParentClass = getParent(ParentClass)
Return False
```
The intuition for the algorithm is that, given a child class node, continually try to find the parent until a base
class is encountered. If a class inherits from any other class that is already in the partial inheritance chain
, there is a circular inheritance. The reason for the inheritance chain is that if there are three classes: `A`, `B
`, and `C`, and `A` inherits from `B` and `B` inherits from `C`, then `A` will have access to all the member
variables and functions of both classes `B` and `C`.

Once the symbol table passes this final pass, it is then used by the Typecheck visitor. To perform additional type
checking.

### Typecheck Visitor










This assignment also deals with checking for implicit type casting. For instance if we have two classes: class A
and C, and A extends C, then the following program should type check:
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
In addition to checking for implicit typecasts, the program also checks for circular inheritance. If a circular
inheritance is detected, the program prints "Type error".

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
This is the last translation phase in the miniJAVA compiler. This translation phase takes in a vapor-M program and translates it into MIPS assembly using the vapor-M visitor to visit every node in the vapor-M abstract syntax tree and generate MIPS assembly code for that node. Here, the compiler must generate code that takes of stack frame allocations and deallocations during function calls. The compiler must also generate code for vapor and vapor-M builtins such as `HeapAllocZ` and `PrintIntS()`.

## Usage Example
This project requires that gradle be installed. Before you can run the project, you need to install it. This is done
by running the `make install` command. Once this command is issued, you will see a `install` directory. The
`MiniJavaCompiler` executable is located in this directory. To compile, for instance, the following program:
```java
class Main {
    public static void main(String[] a){
      System.out.println(12 + 21);
    }
}
```
which is saved as `add.java` navigate to the install directory and issue: `./MiniJavaCompiler --file add.java --o add
.s`. This will generate a file with the `.s` extension containing the MIPS assembly code of `add.java`. To get
 verbose details on the assembly code generation process use the additional `--verbose` flag. The generated assembly
  code for the above program is laid out below:
```mips
.data
 
 .text
 
   jal Main
   li $v0 10
   syscall
 
 Main:
   sw $fp -8($sp)
   move $fp $sp
   subu $sp $sp 76
   sw $ra -4($fp)
   li $t0 33 #2
   move $a0 $t0
   jal _print
   lw $ra -4($fp)
   lw $fp -8($fp)
   addu $sp $sp 76
   jr $ra
 
 AllocArray:
   sw $fp -8($sp)
   move $fp $sp
   subu $sp $sp 76
   sw $ra -4($fp)
   move $t1 $a0
   mul $t0 $t1 4
   addu $t0 $t0 4
   move $a0 $t0
   jal _heapAlloc
   move $t2 $v0
   sw $t1 0($t2)
   move $v0 $t2
   lw $ra -4($fp)
   lw $fp -8($fp)
   addu $sp $sp 76
   jr $ra
 
 _print:
   li $v0 1   # syscall: print integer
   syscall
   la $a0 _newline
   li $v0 4   # syscall: print string
   syscall
   jr $ra
 
 _error:
   li $v0 4   # syscall: print string
   syscall
   li $v0 10  # syscall: exit
   syscall
 
 _heapAlloc:
   li $v0 9   # syscall: sbrk
   syscall
   jr $ra
 
 .data
 .align 0
 _newline: .asciiz "\n"
 _str0: .asciiz "null pointer\n"
 _str1: .asciiz "array index out of bounds\n"  
```

## Running the generated MIPS code
This project also comes with a run script that is located in the root directory of the project. This allows you to run
your generated MIPS assembly code on the MARS MIPS emulator. To run the above generated `add.s` MIPS assembly code
use the following command from the project root:
```shell script
./run ./install/add.s
```
This will generate a value of `33` on the terminal.

## In-code citation
All external resources used during the project are cited within the code.

## License
MIT License

Copyright (c) 2020 RoySRC

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
