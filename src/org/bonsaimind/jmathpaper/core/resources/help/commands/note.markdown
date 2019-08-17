note
====

The note command allows you to manipulate the notes. It accepts additional
parameters, namely the action and the associated parameters.

    note action [index] line

The actions are:

    Action      Description                              Aliases
    -----------------------------------------------------------------------
    add         Adds the given value as separate line    add, append
                to the notes.
    
    clear       Clears the whole note.                   clear, clr, cls,
                                                         reset
    
    delete      Deletes the line with the given 1-based  delete, del,
                index.                                   remove, rem
    
    insert      Inserts the line at the given 1-based    insert, ins
                index.

Some examples.

    note add New line.
    note insert 1 Inserted before the first line.
    note delete 3
    note clear