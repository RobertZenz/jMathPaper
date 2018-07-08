copy
====

The copy command accepts additional parameters, namely a part and a range. If
no parameters are provided the whole paper will be copied, if parameters are
provided only the set parts of the given lines will be copied.

    copy [part] [range]

The types available are:

    Part        Description                              Aliases
    -----------------------------------------------------------------------
    expression  The expression of the selected lines.    expression, exp
    
    id          The ID of the selected lines.            id
    
    line        The whole lines.                         line
    
    paper       The whole paper.                         paper
    
    result      The result of the selected lines.        result, res

The range can either be nothing (for all), a single ID or index (starting at 1),
a comma separated list of ID's or indexes or a range specified with two dots.

    copy abc
    copy 4
    copy expression #3,abc,#7
    copy result 3..abc