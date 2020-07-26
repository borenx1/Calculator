# Calculator
A powerful calculator.

## How to open
APK: app-debug.apk

Google Play Store: https://play.google.com/store/apps/details?id=com.bx.calculator

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


# References
- [Shunting-yard algorithm](https://en.wikipedia.org/wiki/Shunting-yard_algorithm)
- [Reverse Polish Notation](https://en.wikipedia.org/wiki/Reverse_Polish_notation)
- [Abstract Syntax Tree](https://en.wikipedia.org/wiki/Abstract_syntax_tree)
- [Parsing math expressions](https://www.freecodecamp.org/news/parsing-math-expressions-with-javascript-7e8f5572276e/)
- [W3Eval](https://developer.ibm.com/articles/j-w3eval/)
