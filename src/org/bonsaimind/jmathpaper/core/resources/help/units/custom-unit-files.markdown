Custom units files
==================

Additional units can be specified in a file and passed to jMathPaper with the
`--units=UNITSFILE` parameter or in the user configuration file located in
the jMathPaper configuration directory (most likely
`~/.local/share/jmathpaper/`).

A well-formed units file has one unit per line, looking like this:

    <unitname> <exponent> [<alias,alias,alias,...>]

Or with values:

    foot 1 feet,ft
    meter 1 m

The name is required and considered case-insensitive.

The exponent is required and can be any positive value.

The aliases are an optional, comma separated list which are considered
case-sensitive.

Comments can be specified with a leading `#` character.