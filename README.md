# Table Editor
Jet Brains test task; making table editor with parser.

### Done
- [x] Parsing
- [x] Functions
- [x] Reference to cells and cell ranges (only absolute cells)
       


## Functionalities
Brief overview of the functionalities and usage.

### Functions.
> A1:C2. Use cell Labels for range with colon.
> Support functions SUM, AVG, POW(arg, arg), COUNT, MIN, MAX, CONCAT.

  
![functions](https://github.com/bilguudeiblgd/table-editor/assets/68243292/7129c18a-3cd2-48b7-84b5-8115a038eb22)


### Copying/Pasing

> CTRL+C -> CTRL + V
  
![copying_pasting](https://github.com/bilguudeiblgd/table-editor/assets/68243292/08b3d3b6-f8f0-41c2-bae3-6d9ba4a57a2f)

### Table editors

> Mouse two on any cell.
>

![table_editors](https://github.com/bilguudeiblgd/table-editor/assets/68243292/e4da4155-ebb1-4cb4-a163-f11a7677bf31)


# Parser.

My parser consist of Lexer and Parser. Lexer tokenizes the input and also has some sanitization for the expression. Parser is LL(1) parser implemented in recursive descent way.

### Operator precendence.

| Precedence | Operator      | Description                         | Associativity     |
|------------|---------------|-------------------------------------|-------------------|
| 1          | `:`           | Cell Scope resolution               | Left-to-right     |
| 2          | `()`          | Function call                       | Left-to-right     |
| 3          | `+` `-`       | Unary plus and minus                | Right-to-left     |
| 4          | `*` `/`       | Multiplication, division            | Left-to-right     |
| 5          | `+` `-`       | Addition, subtraction               | Left-to-right     |

As in the code, https://github.com/bilguudeiblgd/table-editor/blob/main/src/main/java/parser/Parser.java I use L1, L2, L3, L4, L5 functions which corresponds to this precendence.

