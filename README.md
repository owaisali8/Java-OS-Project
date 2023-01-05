<img src="https://dev.java/assets/images/java-logo-vert-blk.png" height="200" width="200" /> <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/9/98/Apache_NetBeans_Logo.svg/1200px-Apache_NetBeans_Logo.svg.png" height="220" width="200" />

# Java OS Project
Virtual Environment build on Java for our OS course.

Project will be comprised of 3 phases.  
- Phase 1: Create a Virtual Environment.
- Phase 2: PCBs, Queues, Memory Allocation & Deallocation, Running binary files.
- Phase 3: CLI & GUI.


Institute of Business Administration Karachi

Operating Systems (CSE331) – Fall 2022

# Operating System - Course Project Specifications

## Phase I

## VM Architecture

### What to do?

- Build main architecture
- Implement instruction set

### Main Memory

- Memory addressing is 16 bits
- Memory is simulated by taking a byte (unsigned char) array of 64K
- All memory references are through Load and Store instructions between Memory and General purpose Registers
- Stack is of _**50 bytes**_ for **each** process

### Registers

- The architecture has sixteen, 16-bit general purpose registers
- There are sixteen, 16-bit special purpose registers:
- The value of first register is always Zero
- Three registers for code (base, limit, & counter)
- Three registers for stack (base, limit, & counter)
- Two registers for data (base & limit)
- One register for flags
- Six Registers reserved for future use
- Register code is of one byte
- The register codes are as follows:

| **Register** | **Register Code(hex)** |
| --- | --- |
| R0 | 00 |
| R1 | 01 |
| R2 | 02 |
| … | … |
| R10 | 0A |
| … | … |
| R14 | 0E |
| R15 | 0F |

- Use the flag register as follows:

| **Use** | Unused | Overflow | Sign | Zero | Carry |
| --- | --- | --- | --- | --- | --- |
| **Bit No.** | … | 4 | 3 | 2 | 1 | 0 |

- **Flag Register** will be set after Arithmetic, logical, Shift and Rotate Operations as follows:

- **Carry Bit:** Set for **Shift and Rotate** operations only. If the most significant bit is on before the operation.
- **Zero Bit:** If the result of **Arithmetic, logical, Shift and Rotate o** perations is Zero.
- **Sign Bit:** If the result of **Arithmetic, logical, Shift and Rotate o** perations is Negative.
- **Overflow Bit:** If the result of **Arithmetic and logical o** perations is either carry in or carry out of the most significant bit.

### Instruction Set

- All arithmetic is integer
- All number are signed
- Numbers are stored in memory in big-endian format
- There is a single addressing mode, 16 bit signed offset
- All Instructions are of 2 clock cycles
- Details of memory addressing are found in instruction format section
- All access violations (accessing out of bounds code, data, stack overflow & underflow) must be trapped and an error must be generated so that the Operating System component could act on it (kill the task after displaying error)

| **Opcode (hex)** | **Instruction** | **Description** | **Example** | **Details** |
| --- | --- | --- | --- | --- |
| **Register-register Instructions** |
| 16 | MOV | Copy Register Contents | MOV R1 R2 | R1  R2 |
| 17 | ADD | Add Register Contents | ADD R1 R2 | R1  R1 + R2 |
| 18 | SUB | Subtract | SUB R1 R2 | R1  R1 – R2 |
| 19 | MUL | Multiply | MUL R1 R2 | R1  R1 \* R2 |
| 1A | DIV | Division | DIV R1 R2 | R1  R1 / R2 |
| 1B | AND | Logical AND | AND R1 R2 | R1  R1 && R2 |
| 1C | OR | Logical OR | OR R1 R2 | R1  R1 || R2 |
| **Register-Immediate Instructions** |
| 30 | MOVI | Copy Immediate to register | MOVI R1 num | R1  num |
| 31 | ADDI | Add | ADDI R1 num | R1  R1 + num |
| 32 | SUBI | Subtract | SUBI R1 num | R1  R1 – num |
| 33 | MULI | Multiply | MULI R1 num | R1  R1 \* num |
| 34 | DIVI | Divide | DIVI R1 num | R1  R1 / num |
| 35 | ANDI | Logical AND | ANDI R1 num | R1  R1 && num |
| 36 | ORI | Logical OR | ORI R1 num | R1  R1|| num |
| 37 | BZ | Branch equal to zero | BZ num | Check flag register, and jump to offset \*\* |
| 38 | BNZ | Branch if not zero | BNZ num | Check flag register, and jump to offset \*\* |
| 39 | BC | Branch if carry | BC num | Check flag register, and jump to offset \*\* |
| 3A | BS | Branch if sign | BS num | Check flag register, and jump to offset \*\* |
| 3B | JMP | Jump | JMP num | Jump to offset \*\* |
| 3C | CALL | Procedure Call | CALL num | Push PC on stack, Jump to offset \*\* |
| \*3D | ACT | Action | ACT num | Do the service defined by num |
| **Memory Instructions** |
| \*51 | MOVL | Load Word | mov R1 offset | R1  Mem [location\*\*] |
| \*52 | MOVS | Store Word | mov R1 offset | Mem [location\*\*]  R1 |
| **Single Operand Instructions** |
| 71 | SHL | Shift Left Logical | SHL R1 | R1  R1 \<\< 1 |
| 72 | SHR | Shift Right Logical | SHR R1 | R1  R1 \>\> 1 |
| 73 | RTL | Rotate Left | RTL R1 | Shift left and set lower bit accordingly |
| 74 | RTR | Rotate Right | RTR R1 | Shift right and set lower bit accordingly |
| 75 | INC | Increment | INC R1 | R1  R1 + 1 |
| 76 | DEC | Decrement | DEC R1 | R1  R1 – 1 |
| 77 | PUSH | Push register on stack | PUSH R1 | Push contents of R1 on stack |
| 78 | POP | Pop the value in the register from the stack | POP R1 | Pop contents of top of stack on R1 |
| **No Operand Instructions** |
| F1 | RETURN | Return to original PC | RETURN | Pop PC from Stack |
| F2 | NOOP | No Operation | NOOP | No Operation |
| F3 | END | End of Process | END | Process Terminates |

| \* | Implementation of these instructions might have to be modified in the next parts |
| --- | --- |
| \*\* | Memory references can either be absolute address in the 64K memory, or could be an offset which should be used to compute the absolute address according to the technique described in instruction format section. In case you decide to use absolute addressing, then your OS will do the address translation. |


### Instruction Format

- Instruction op-code is of 1 byte
- Register code is 1 byte
- Immediate = 2 bytes

##### Register-Register Instructions

**Size** = 3 bytes

| Op-code | Register1 | Register2 |
| --- | --- | --- |
| 8 bits | 8 bits | 8 bits |

**Usage:**

- **Register-Register ALU Operations** : Register1  Register1 op Register2

##### Register-Immediate Instructions

**Size** = 4 bytes

| Op-code | Register | Immediate |
| --- | --- | --- |
| 8 bits | 8 bits | 16 bits |

**Usage:**

- **Register-Immediate ALU Operations:** Register  Register op Immediate
- **Call / Jump Instructions:** PC = code.Base + num (after necessary checks) Note that Register is not used here

##### Memory Instructions

**Size** = 4 bytes

| Op-code | Register | Immediate |
| --- | --- | --- |
| 8 bits | 8 bits | 16 bits |

**Usage:**

- **Storing Value in Memory:** memory [data.base + imm]  Register
- **Loading Value in Memory:** Register  memory [data.base + imm]

##### Single-Operand Instructions

**Size** = 2 bytes

| Op-code | Register |
| --- | --- |
| 8 bits | 8 bits |

**Usage:**

- **Push / Pop Instructions:** push or pop the register contents to or from stack
- **Other Instructions:** R1  R1 op

##### No-Operand Instructions

**Size** = 1 bytes

| Op-code |
| --- |
| 8 bits |

### Machine execution cycle

Each cycle contains the following steps:

- Fetch the instruction
- Decode the fetched instruction
- Execute the instruction.
- Write back result in the memory (if necessary)

##

## What you need to do in Phase 1:

1. Read the file given to you and write the contents in the memory (the array that you have declared) as is.
2. Set your program counter (code counter register) to the array index from where you have started to write.
3. Fetch one byte from the memory, decode the instruction, and fetch the remaining operands.
4. Increment the program counter by 1, 2 or 3(depending on the instruction you have fetched in step 3), so that it has the index of the next instruction.
5. Execute the instruction by updating registers, memory etc.
6. If the instruction from step 3 is 'End' then this indicates that the program was executed successfully otherwise go back to step 3.
7. Show the contents of all the registers (GPRs and SPRs) after executing one instruction.
