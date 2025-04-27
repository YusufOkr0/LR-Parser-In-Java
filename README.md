
<div align="center">
<pre>
 _____                                             _____ 
( ___ )                                           ( ___ )
 |   |~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|   | 
 |   |                                             |   | 
 |   |   _    ___      ___  _   ___  ___ ___ ___   |   | 
 |   |  | |  | _ \    | _ \/_\ | _ \/ __| __| _ \  |   | 
 |   |  | |__|   /    |  _/ _ \|   /\__ \ _||   /  |   | 
 |   |  |____|_|_\    |_|/_/ \_\_|_\|___/___|_|_\  |   | 
 |   |                                             |   | 
 |___|~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|___| 
(_____)                                           (_____)

</pre>
  

  <p>A Java implementation of an LR parser that processes input strings based on predefined grammar rules and parsing tables.</p>

  <!-- Badges - Default Style, Blue for Java -->
  <p>
    <img src="https://img.shields.io/badge/Java-17-blue.svg" alt="Java Version">
    <img src="https://img.shields.io/badge/License-MIT-yellow" alt="License">
    <!-- Add other relevant badges here -->
  </p>

</div>

## Features

*   Parses input strings using the LR parsing algorithm.
*   Loads configuration from external files:
    *   Context-Free Grammar (`Grammar.txt`)
    *   Action Table (`ActionTable.txt`)
    *   GOTO Table (`GotoTable.txt`)
*   Generates a detailed step-by-step trace of the parsing process.
*   Constructs and outputs a Parse Tree for successfully parsed inputs.
*   Handles basic syntax error detection during parsing.
*   Configured to process multiple input files sequentially.
*   Uses standard Maven project structure.

## Project Structure

```
LR-Parser-in-java/
├── .gitignore                
├── LICENSE                  
├── pom.xml                   
├── .idea/                    
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/yusufokr0/
│   │   │       ├── dataloader/      
│   │   │       │   ├── ActionTableLoader.java
│   │   │       │   ├── GotoTableLoader.java
│   │   │       │   ├── GrammarLoader.java
│   │   │       │   ├── GrammarRule.java    
│   │   │       │   └── ParseTreeNode.java  
│   │   │       ├── LRParser.java      # Core LR Parsing logic implementation
│   │   │       └── Main.java          # Main application entry point
│   │   └── resources/
│   │       ├── config/              
│   │       │   ├── ActionTable.txt  # LR Action table data
│   │       │   ├── GotoTable.txt    # LR Goto table data
│   │       │   └── Grammar.txt      # Context-Free Grammar rules
│   │       └── inputs/              # Input files to be parsed by the application
│   │           ├── input-01.txt
│   │           ├── input-02.txt
│   │           ├── input-03.txt
│   │           ├── input-04.txt
│   │           ├── input-05.txt
│   │           ├── input-06.txt
│   │           ├── input-07.txt
│   │           ├── input-08.txt
│   │           └── input-09.txt
│   └── test/                     
└── target/                       
    ├── classes/                  
    ├── generated-sources/        
    └── output-traces/            # Generated output files with parsing traces and trees
        ├── output-01.txt
        ├── output-02.txt
        ├── output-03.txt
        ├── output-04.txt
        ├── output-05.txt
        ├── output-06.txt
        ├── output-07.txt
        ├── output-08.txt
        └── output-09.txt
```

## How It Works (Summary)

1.  **Setup:** The parser loads the **Grammar**, **Action Table**, and **GOTO Table** from the `config` directory.
2.  **Parsing (Per Input):**
    *   It processes the input string (from `inputs`) using a **stack**.
    *   At each step, it looks up an action (**Shift**, **Reduce**, **Accept**, **Error**) in the **Action Table** based on the current *state* (stack top) and the next *input token*.
    *   **Shift:** Pushes the token and a new state onto the stack.
    *   **Reduce:** Pops symbols matching a grammar rule's right side, then pushes the rule's left-side symbol and a new state (found via **GOTO Table**) onto the stack.
    *   Simultaneously, it builds a **Parse Tree**.
3.  **Output:** Writes a **trace** file (showing steps) and the final **Parse Tree** (if successful) to the `target/output-traces` directory.

## Configuration Files

The parser relies on configuration files located in `src/main/resources/config/`:

*   **`Grammar.txt`**: Defines the context-free grammar rules, one rule per line.
    *   Format: `NonTerminal -> Symbol1 Symbol2 ...`
    *   Example: `E -> E + T`
*   **`ActionTable.txt`**: Contains the LR Action Table entries.
    *   Format: `State Terminal1 Action1 Terminal2 Action2 ...`
    *   Actions: `SN` (Shift to state N), `RN` (Reduce using rule N), `accept`.
    *   Example: `0 id S5 ( S4`
*   **`GotoTable.txt`**: Contains the LR GOTO Table entries.
    *   Format: `State NonTerminal1 State1 NonTerminal2 State2 ...`
    *   Example: `0 E 1 T 2 F 3`

## Input and Output

*   **Input Files**: Place plain text files containing space-separated tokens fallowing by `$` signature in `src/main/resources/inputs/`. The parser expects filenames like `input-NN.txt`.
    *   Example (`input-01.txt`): `id + id * id $`
*   **Output Files**: Generated in `target/output-traces/` with names corresponding to input files (e.g., `output-01.txt`). Each file contains:
    1.  A header for the trace table columns.
    2.  The step-by-step parsing trace (Stack, Input, Action).
    3.  The flattened representation of the Parse Tree.

## How to Run

### 📥 1. Clone the Repository

First, clone the project from GitHub:

```bash
git clone https://github.com/YusufOkr0/LR-Parser-In-Java.git
cd LR-Parser-in-java
```

1.  **Prerequisites:**
    *   Java Development Kit (JDK) 17.
2. **Execute:** Open a terminal in the project root directory and run:
    *   Open the project in IntelliJ IDEA or your preferred Java IDE.

    * Navigate to: src/main/java/com/yusufokr0/Main.java

    * the green Run button next to the main method.

    * The parser will process all input files and generate output files automatically.
   
3.  **Check Output:** The trace and parse tree output files will be generated in the `target/output-traces/` directory.

## Example Output For Given Input (`id + id * id $`)

```
Stack                                    Input                                    Action                                  
---------------------------------------------------------------------------------------------------------
0                                        id + id * id $                           Shift 5                       
0id5                                     + id * id $                              Reduce 6 (GOTO [0, F])        
0F3                                      + id * id $                              Reduce 4 (GOTO [0, T])        
0T2                                      + id * id $                              Reduce 2 (GOTO [0, E])        
0E1                                      + id * id $                              Shift 6                       
0E1+6                                    id * id $                                Shift 5                       
0E1+6id5                                 * id $                                   Reduce 6 (GOTO [6, F])        
0E1+6F3                                  * id $                                   Reduce 4 (GOTO [6, T])        
0E1+6T9                                  * id $                                   Shift 7                       
0E1+6T9*7                                id $                                     Shift 5                       
0E1+6T9*7id5                             $                                        Reduce 6 (GOTO [7, F])        
0E1+6T9*7F10                             $                                        Reduce 3 (GOTO [6, T])        
0E1+6T9                                  $                                        Reduce 1 (GOTO [0, E])        
0E1                                      $                                        accept                        
---------------------------------------------------------------------------------------------------------
Parse tree:
E
E/E
E/E/T
E/E/T/F
E/E/T/F/id
E/+
E/T
E/T/T
E/T/T/F
E/T/T/F/id
E/T/*
E/T/F
E/T/F/id
```




## Example Log Output For Given Input (`id + id * id $`)
<details>
<summary><h3>Click to expand</h3></summary>

````
Action table loading...
Action table loaded:
------------------------------------------------------------
State	$	(	)	*	+	id
0	-	s4	-	-	-	s5
1	accept	-	-	-	s6	-
2	r2	-	r2	s7	r2	-
3	r4	-	r4	r4	r4	-
4	-	s4	-	-	-	s5
5	r6	-	r6	r6	r6	-
6	-	s4	-	-	-	s5
7	-	s4	-	-	-	s5
8	-	-	s11	-	s6	-
9	r1	-	r1	s7	r1	-
10	r3	-	r3	r3	r3	-
11	r5	-	r5	r5	r5	-
------------------------------------------------------------

GOTO table loading...
GOTO table loaded:
----------------------------------------
State	E	F	T
0	1	3	2
1	-	-	-
2	-	-	-
3	-	-	-
4	8	3	2
5	-	-	-
6	-	3	9
7	-	10	-
8	-	-	-
9	-	-	-
10	-	-	-
11	-	-	-
----------------------------------------

Grammar rules loading...
Grammar rules loaded:
--------------------------------------------------
Rule 1: E -> E + T (RHS token count: 3)
Rule 2: E -> T (RHS token count: 1)
Rule 3: T -> T * F (RHS token count: 3)
Rule 4: T -> F (RHS token count: 1)
Rule 5: F -> ( E ) (RHS token count: 3)
Rule 6: F -> id (RHS token count: 1)
--------------------------------------------------


Parsing started for the input: id + id * id $

Current stack content 0
Current state (stack-top) is 0, next token is 'id'
Fetched action from the table: s5

Shift and goto state 5: push the token "id" and state 5 onto the stack
Parse tree: token "id" becomes a node (leaf)
Move to next token
--------------------------------------------------------------------------------
Current stack content 0id5
Current state (stack-top) is 5, next token is '+'
Fetched action from the table: r6

Reduce using grammar rule 6: F -> id
Stack content before reduce: 0id5
Pop 1 symbols and their states (2 items) from the stack based on RHS of rule 6: [id]
State revealed after pop: 0 (current stack content: 0)
Push LHS (F) of grammar rule 6 onto stack
Push new state from GOTO table onto stack: 3 (GOTO[0, F])
Parse tree: grammar rule's LHS (F) becomes a node (parent) and popped tokens/non-terminals become its children
Stack content after reduce: 0F3
--------------------------------------------------------------------------------
Current stack content 0F3
Current state (stack-top) is 3, next token is '+'
Fetched action from the table: r4

Reduce using grammar rule 4: T -> F
Stack content before reduce: 0F3
Pop 1 symbols and their states (2 items) from the stack based on RHS of rule 4: [F]
State revealed after pop: 0 (current stack content: 0)
Push LHS (T) of grammar rule 4 onto stack
Push new state from GOTO table onto stack: 2 (GOTO[0, T])
Parse tree: grammar rule's LHS (T) becomes a node (parent) and popped tokens/non-terminals become its children
Stack content after reduce: 0T2
--------------------------------------------------------------------------------
Current stack content 0T2
Current state (stack-top) is 2, next token is '+'
Fetched action from the table: r2

Reduce using grammar rule 2: E -> T
Stack content before reduce: 0T2
Pop 1 symbols and their states (2 items) from the stack based on RHS of rule 2: [T]
State revealed after pop: 0 (current stack content: 0)
Push LHS (E) of grammar rule 2 onto stack
Push new state from GOTO table onto stack: 1 (GOTO[0, E])
Parse tree: grammar rule's LHS (E) becomes a node (parent) and popped tokens/non-terminals become its children
Stack content after reduce: 0E1
--------------------------------------------------------------------------------
Current stack content 0E1
Current state (stack-top) is 1, next token is '+'
Fetched action from the table: s6

Shift and goto state 6: push the token "+" and state 6 onto the stack
Parse tree: token "+" becomes a node (leaf)
Move to next token
--------------------------------------------------------------------------------
Current stack content 0E1+6
Current state (stack-top) is 6, next token is 'id'
Fetched action from the table: s5

Shift and goto state 5: push the token "id" and state 5 onto the stack
Parse tree: token "id" becomes a node (leaf)
Move to next token
--------------------------------------------------------------------------------
Current stack content 0E1+6id5
Current state (stack-top) is 5, next token is '*'
Fetched action from the table: r6

Reduce using grammar rule 6: F -> id
Stack content before reduce: 0E1+6id5
Pop 1 symbols and their states (2 items) from the stack based on RHS of rule 6: [id]
State revealed after pop: 6 (current stack content: 0E1+6)
Push LHS (F) of grammar rule 6 onto stack
Push new state from GOTO table onto stack: 3 (GOTO[6, F])
Parse tree: grammar rule's LHS (F) becomes a node (parent) and popped tokens/non-terminals become its children
Stack content after reduce: 0E1+6F3
--------------------------------------------------------------------------------
Current stack content 0E1+6F3
Current state (stack-top) is 3, next token is '*'
Fetched action from the table: r4

Reduce using grammar rule 4: T -> F
Stack content before reduce: 0E1+6F3
Pop 1 symbols and their states (2 items) from the stack based on RHS of rule 4: [F]
State revealed after pop: 6 (current stack content: 0E1+6)
Push LHS (T) of grammar rule 4 onto stack
Push new state from GOTO table onto stack: 9 (GOTO[6, T])
Parse tree: grammar rule's LHS (T) becomes a node (parent) and popped tokens/non-terminals become its children
Stack content after reduce: 0E1+6T9
--------------------------------------------------------------------------------
Current stack content 0E1+6T9
Current state (stack-top) is 9, next token is '*'
Fetched action from the table: s7

Shift and goto state 7: push the token "*" and state 7 onto the stack
Parse tree: token "*" becomes a node (leaf)
Move to next token
--------------------------------------------------------------------------------
Current stack content 0E1+6T9*7
Current state (stack-top) is 7, next token is 'id'
Fetched action from the table: s5

Shift and goto state 5: push the token "id" and state 5 onto the stack
Parse tree: token "id" becomes a node (leaf)
Move to next token
--------------------------------------------------------------------------------
Current stack content 0E1+6T9*7id5
Current state (stack-top) is 5, next token is '$'
Fetched action from the table: r6

Reduce using grammar rule 6: F -> id
Stack content before reduce: 0E1+6T9*7id5
Pop 1 symbols and their states (2 items) from the stack based on RHS of rule 6: [id]
State revealed after pop: 7 (current stack content: 0E1+6T9*7)
Push LHS (F) of grammar rule 6 onto stack
Push new state from GOTO table onto stack: 10 (GOTO[7, F])
Parse tree: grammar rule's LHS (F) becomes a node (parent) and popped tokens/non-terminals become its children
Stack content after reduce: 0E1+6T9*7F10
--------------------------------------------------------------------------------
Current stack content 0E1+6T9*7F10
Current state (stack-top) is 10, next token is '$'
Fetched action from the table: r3

Reduce using grammar rule 3: T -> T * F
Stack content before reduce: 0E1+6T9*7F10
Pop 3 symbols and their states (6 items) from the stack based on RHS of rule 3: [T, *, F]
State revealed after pop: 6 (current stack content: 0E1+6)
Push LHS (T) of grammar rule 3 onto stack
Push new state from GOTO table onto stack: 9 (GOTO[6, T])
Parse tree: grammar rule's LHS (T) becomes a node (parent) and popped tokens/non-terminals become its children
Stack content after reduce: 0E1+6T9
--------------------------------------------------------------------------------
Current stack content 0E1+6T9
Current state (stack-top) is 9, next token is '$'
Fetched action from the table: r1

Reduce using grammar rule 1: E -> E + T
Stack content before reduce: 0E1+6T9
Pop 3 symbols and their states (6 items) from the stack based on RHS of rule 1: [E, +, T]
State revealed after pop: 0 (current stack content: 0)
Push LHS (E) of grammar rule 1 onto stack
Push new state from GOTO table onto stack: 1 (GOTO[0, E])
Parse tree: grammar rule's LHS (E) becomes a node (parent) and popped tokens/non-terminals become its children
Stack content after reduce: 0E1
--------------------------------------------------------------------------------
Current stack content 0E1
Current state (stack-top) is 1, next token is '$'
Fetched action from the table: accept

ACCEPTED
--------------------------------------------------------------------------------

Parse tree:
/E
/E/E
/E/E/T
/E/E/T/F
/E/E/T/F/id
/E/+
/E/T
/E/T/T
/E/T/T/F
/E/T/T/F/id
/E/T/*
/E/T/F
/E/T/F/id
````
</details>