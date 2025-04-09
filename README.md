
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

