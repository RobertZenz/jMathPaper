Custom conversions files
========================

Additional conversions can be specified in a file and passed to jMathPaper with
the `--conversions=CONVERSIONSFILE` parameter or in the user configuration file
located in the jMathPaper configuration directory (most likely
`~/.local/share/jmathpaper/`).

A well-formed conversions file has one conversion per line, looking like this:

    <targetunit> <value><sourceunit>

Or with values:

    foot 30cm
    byte 8bit
    torr 101325/760Pa
    some (x + 6)other
    other (x - 6)some

The target unit is the unit to which is converted.

The value can either be a fixed factor (read: a decimal number), a mathematical
expression which evaluates to a fixed factor or a mathematical expression in
parentheses, with `x` denoting the source value. If a mathematical expression
which does not evaluate to a fixed factor is specified, the reverse conversion
must also be added.

Comments can be specified with a leading `#` character.