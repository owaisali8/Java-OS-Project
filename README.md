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


# Operating System Project - Phase 2 (Process and Memory Management)

## Prerequisite

**Phase 2 is built on phase 1.** For this phase, it is required that you complete the implementation of instruction set from phase 1.

## What to do?

- Creating process control block
- Maintaining ready and running queues
- Memory allocation (segmentation with paging) & deallocation
- Loading, running and terminating process
- Error Handling
- CPU Scheduling (multilevel feedback/multilevel queue scheduling)

### Process Control Block

Every process has a process control block (PCB), which has all the information required to run a process. PCB contains at least the following:

- Process ID
- Process priority
- Process size (code+data+segment)
- Process File name
- General Purpose Registers
- Special Purpose Registers
- Page Table
- \* Accounting Information (execution time, waiting time)

### Program Loading

A process is read from a file. When a process is loaded

- It is parsed for valid priority, valid data and code sizes. No need to check instruction syntax at this stage.
- Priority ranges from 0-31. Any priority less than 0 or greater than or equal to 32 is invalid
- The program is loaded into memory.
- PCB is created and added to the ready queue.

Priorities are grouped into two levels, 0-15 and 16-31. Two ready queues are maintained for each priority level.

Queue 1: for priorities 0-15 (higher priorities)

Queue 2: for priorities 16-31

A process will be assigned to any one of these queues according to the priority.

### Data loading

Data will be loaded in memory according to the size specified. For example if data size is defined as ' **0004'** , it means that data size is 4 bytes. Only four bytes will be loaded in the data segment.

### Code loading

Only that much code is read which is specified in code segment declaration. For example if code size is **'000e'** then code size is 14 bytes and only fourteen bytes will be read from the file and loaded in code segment.

You should be able to load more than one program in memory and maintain ready queues of the processes.

### Program Execution

The first process is selected and removed from the ready queue and its state is restored (its registers from PCB are restored to the CPU registers). Instructions are fetched from memory, decoded, executed and if required, values written back. Syntax errors are checked at process execution stage. Any invalid instruction or error condition terminates the process. During execution, a process will move between running and ready queues.

Write the dump of process memory and PCB in a file after each instruction execution.

### Program termination

When the process finishes, either due to abnormal conditions or reaching the 'end' statement, its PCB is removed from the queue and its memory dump (data, stack and code sections) and PCB are printedand also written to a file.

### Errors

An error terminates the process. Some of the errors are:

- Invalid opcode / register code
- Trying to access data outside allocated space
- Trying to jump, using call or branch statement, to area outside the allocated space
- Invalid priority (\<0 or \>31)
- Stack overflow/underflow
- Divide-by-zero
- Invalid code size (code size specified is less than or greater than the actual code)

## 1. Multiple Process Management

The salient features of Multiple Process Management are:

- Maintaining ready & running queues
- CPU Scheduling (multilevel feedback/multilevel queue scheduling)

### Ready & Running Queues

Ready and running queues are implemented as priority queues of PCBs. There will be only one process in running queue whereas ready queue may have more than one.

### CPU Scheduling

**Assumption:** Each instruction takes 2 clock cycles.

Multilevel Queue Scheduling

Process priorities are fixed and they do not change. Process once assigned to a queue will remain in the queue till it is terminated.

For queue 1, use Priority scheduling algorithm and for queue 2 Round Robin algorithm (time slice = 8 clock cycles \*this should be configurable). Process from queue 2 is selected only if queue 1 is empty.

If more than one process is in queue 2 (provided queue 1 is empty) then the first process in the queue is selected, its state is restored and it is run for one time slice. After its time slice finishes, the current state is saved in the PCB, which is then added to the ready queue and the state for the next process is restored

## **2. Memory management**

### What to do?

- Memory Allocation & De-allocation
- Trapping access violations

Segmentation with paging has to be used to address the memory. The Memory has to be divided into pages of size 128 Bytes. You would need the following data structure:

- _Page frames_ – Divide user memory in frames
- _Page tables_ – Page tables for all the processes in the memory
- _Free page frame list_ – List of all page frames not currently in use by any process

The memory requirement of the process is the sum of the size of stack, code and data. You will be translating the addresses in two steps:

1. Use the Base & Offset to calculate the logical Address into one linear address (segmentation concepts)
2. Depending upon the page size, find the frame number from page table and access the memory location (paging concepts)

![](https://loonytek.files.wordpress.com/2015/11/add.png)



## Phase 3 (CLI/GUI)

### What to do?

Make a command line interpreter/graphical user interface so that we can load the processes, run them and view the details

You are required to support the commands listed below. Any other useful command according to your implementation would be appreciated but not required.

| **Process Related Commands** |**Description**|
| --- | --- |
| Load Process **load** _file_ | Prompts the user for a process filename and loads the process into memory. Displays the process ID of the loaded process |
| Execute Process **run**** –p **_process\_id_ | Asks user to select / enter a process id and then** completely** execute only the selected process |
| Debug Process **debug -p** _process\_id_ | Asks user to select / enter a process id and then execute **only one instruction** of the selected process |
| Debug All **debug**** -a **| Execute** only one instruction** of all the loaded processes |
| Execute All **run -a** | **Completely** execute all the loaded processes |
| Kill Process **kill –p** _process\_id_ | Forcefully kill a process |
| \*Create Clone **clone** _process\_id_ | Create a clone of selected process |
| \*Block Process **block** _process\_id_ | Block a process (move it to blocked queue) |
| \*Unblock Process **unblock** _process\_id_ | Unblock a process (move it from blocked queue to ready queue) |
| **Display Related Commands** |
| List Process **list –a**  **list –b**** list –r ****list -e** | Lists the processes currently loaded. There should be additional option to display processes in a specified state (like blocked).-a all process-b blocked processes-r processes in ready state-e process in running state |
| Display PCB **display –p** _process\_id_ | Displays the PCB of the selected process |
| Display Page Table **display –m** _process\_id_ | Displays the page table of the selected process |
| Memory Dump **dump** _process\_id_ | Show the memory dump of the selected process. Dump will be shown on the screen and also written in a file named _process\_file\_name_.dump |
| Free Frames **frames -f** | Show the frame number and location of free frames |
| Memory Details **mem** | List the details about memory allocated to a given process |
| Allocated Frames **frames -a** | Show the process id and frame number of all allocated frames |
| Display Registers **registers** | Displays the current contents of the CPU registers (GPR, SPR, flag reg.) |

Notes for CLI:
- Commands are case-sensitive
- Give appropriate error message if any error in the command/option or the argument
- Return to the prompt immediately after executing the command and displaying the required output
Notes for CLI and GUI
- All output will also be written in a (log) file.
- Do not make any assumptions on file names (like specific extensions)
- A valid file name can contain letters/numbers/\_/.
- All output files must be saved in one directory

**Miscellaneous Commands** | **Description** |
| --- | --- |
| Shutdown | Shuts down the operating system, during shutdown process all processes should be killed and their process IDs displayed |
| \*Hibernate | Save the current state of the system and resume from the same state when the system is started again |
