Booleans
========

Support for booleans is built right in, internally true and false are treated
as zero and one.

    comparison                   comparison= 1==1 = true
    boolean      boolean= (true || false) && true = true

The following operations are supported:

    Operation    Description                          Aliases
    --------------------------------------------------------------------
    ==           equals                               equal, equals
    !=           not equals                           notequal, notequals
    >            greater                              greater
    >=           greater equals                       greaterequal,
                                                      greaterequals
    <            less                                 less
    <=           less equals                          lessequal, lessequals
    &&           and                                  and
    ||           or                                   or

These aliases should be treated as reserved words, but are only replace when
surrounded by spaces, example:

    aliases                  aliases= true and false = false
    aliases                   aliases= true or false =  true