Custom prefixes files
=====================

Additional prefixes can be specified in a file and passd to jMathPaper with the
`--prefixes=PREFIXESFILE` parameter or in the user configuration file located in
the jMathPaper configuration directory (most likely
`~/.local/share/jmathpaper/`).

A well-formed prefixes file has one prefix per line, looking like this:

    <prefixname> <prefix> <base> <exponent>

Or with values:

    kilo k 10 3
    centi c 10 -2

The name is required and considered case-insensitive.

The prefix is required and considered case-sensitive.

The base and exponent are both required and form the factor for this prefix.

Comments can be specified with a leading `#` character.