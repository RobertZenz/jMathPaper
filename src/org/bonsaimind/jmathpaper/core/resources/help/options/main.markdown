Options
=======

There are various options available to change how jMathPaper behaves, they can
be set either through the GUI (if available) or with the `option` command.

    option OPTION VALUE

Will set `OPTION` to `VALUE`. The following options are available:

    Option        Description                             Aliases
    -----------------------------------------------------------------------
    numberformat  The format in which the results         numberformat,
                  should be displayed. The following      number-format,
                  symbols are supported:                  format, fmt
                   * 0 - Display either a number or
                         zero.
                   * # - Display either a number or
                         nothing.
                   * . - The decimal separator.
                   * , - The thousands separator.
                   * ? - Display as many decimal places
                         as there are.
                  Here are some examples of valid format
                  strings:
                   * 0.?    - Display the numbers with as
                              many decimal places as needed.
                   * 0.###  - Display the number with
                              three decimal places, if
                              they are not zero.
                   * 0.000  - Display the number with
                              three decimal places.
                   * ,##0.? - Display a thousands separator
                              with as many decimals as
                              needed.
    precision     The precision of the calculations.      precision, prec,
                  The value is expected to be a number,   decimals, dec
                  with 0 meaning "endless". Negatives
                  are not allowed.
    
    rounding      The rounding method that is used.       rounding,
                  Allowed values are:                     rounding-mode
                   * ceiling
                   * down
                   * floor
                   * half-down
                   * half-even
                   * half-up
                   * unnecessary
                   * up

Options only apply to the current paper.