.data

vmt_Operator:
  Operator.compute

.text

  jal Main
  li $v0 10
  syscall

Main:
  sw $fp -8($sp)
  move $fp $sp
  subu $sp $sp 20
  sw $ra -4($fp)
  li $t9 8
  sw $t9 0($sp)
  sw :vmt_Operator 4($sp)
  jal ObjAlloc
  move $t0 $v0
  sw $t0 0($sp)
  bnez $t0 null1
  la $a0 _str0
  j _error
null1:
  lw $t0 0($sp)
  lw $t1 0($t0)
  lw $t1 0($t1)
  sw $t0 0($sp)
  jalr $t1
  move $t2 $v0
  move $a0 $t2
  jal _print
  lw $ra -4($fp)
  lw $fp -8($fp)
  addu $sp $sp 20
  jr $ra

Operator.compute:
  sw $fp -8($sp)
  move $fp $sp
  subu $sp $sp 8
  sw $ra -4($fp)
  li $t0 0
  lw $t1 0($fp)
  sw $t0 4($t1)
  li $v0 0
  lw $ra -4($fp)
  lw $fp -8($fp)
  addu $sp $sp 8
  jr $ra

ObjAlloc:
  sw $fp -8($sp)
  move $fp $sp
  subu $sp $sp 8
  sw $ra -4($fp)
  lw $t0 0($fp)
  move $a0 $t0
  jal _heapAlloc
  move $t1 $v0
  lw $t0 4($fp)
  sw $t0 0($t1)
  move $v0 $t1
  lw $ra -4($fp)
  lw $fp -8($fp)
  addu $sp $sp 8
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
