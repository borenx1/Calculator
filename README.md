# Calculator
A powerful calculator.

## Logic
### Expression Evaluating Algorithm:
[Shunting-yard algorithm](https://en.wikipedia.org/wiki/Shunting-yard_algorithm), which some modifications for pre-functions, post-functions, implied multiplication and negations.

[Reverse Polish Notation](https://en.wikipedia.org/wiki/Reverse_Polish_notation)

[Abstract Syntax Tree](https://en.wikipedia.org/wiki/Abstract_syntax_tree)


## Code
### Data types
- `CToken`

  A token in a mathematical expression. It could be a number, operator, variable, etc.
  
- `CExpression`

  An object representing a mathematical expression, an array of tokens.

- `CParams`

  An object containing parameters for a calculation, including angle units and values for variables.
  
- `CResult`

  An object respresenting the result of a calculation. It includes the original expression, calculation parameters and the calculated result. The result does not have to be correct after re-calculating the original expression if the calcuation algorithm has changed.
