Chaining
========

Expressions and commands can be chained by using a semicolon (`;`). So
the following input:

    1+1;2+3;5+5;a=5;a+5

Turns into this:

    #1                                         1+1 =  2
    #2                                         2+3 =  5
    #3                                         5+5 = 10
    a                                          a=5 =  5
    #4                                         a+5 = 10

Of course one can also chain commands and expressions.