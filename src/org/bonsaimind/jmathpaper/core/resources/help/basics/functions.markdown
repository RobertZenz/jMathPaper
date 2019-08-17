Functions
=========

The following functions are available by default:

    Function                   Result
    --------------------------------------------------------------------
    abs(value)                 The absolute value.
    acos(value)                The angle of the cosine (in degrees).
    acosh(value)               The angle of the hyperbolic cosine (in degrees).
    acot(value)                The angle of the cotangents (in degrees).
    asin(value)                The angle of the sine (in degrees).
    asinh(value)               The angle of the hyperbolic sine (in degrees).
    atan(value)                The angle of the tangents (in degrees).
    atanh(value)               The angle of the hyperbolic tangents (in degrees).
    ceiling(value)             Rounded up to the nearest integer.
    cos(value)                 The cosine.
    cosh(value)                The hyperbolic cosine.
    cot(value)                 The cotangents.
    coth(value)                The hyperbolic cotangents.
    csc(value)                 The cosecant (in degrees).
    csch(value)                The hyperbolic cosecant (in degrees).
    deg(value)                 Degrees from radians.
    fact(value)                The factorial value of an integer.
    floor(value)               Rounded down to the nearest integer.
    if(condition, true, false) Tests the conditions and returns the appropriate
                               value.
    log(value)                 The logarithm (base e).
    log10(value)               The logarithm (base10).
    max(values, ...)           The highest value from all.
    min(value, ...)            The lowest value from all.
    not(value)                 Negates the value.
    rad(value)                 Radians from degrees.
    random()                   A random number between 0 and 1.
    round(value, precision)    The rounded value.
    sec(value)                 The secant.
    sech(value)                The hyperbolic secant.
    sin(value)                 The sine.
    sinh(value)                The hyperbolic sine.
    sqrt(value)                The square root.
    tan(value)                 The tangents.
    tanh(value)                The hyperbolic tangents.

Functions can also be defined on the fly:

    add                      add(x, y, z)=x+y+z = true
    #1                             add(1, 2, 3) =    6
    value                             value()=5 =    6
    add              add(x, y, z)=x+y+z+value() = true
    #2                             add(1, 2, 3) =   11