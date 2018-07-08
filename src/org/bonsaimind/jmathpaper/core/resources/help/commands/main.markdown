Commands
========

There are also commands which can be used to control the application, they can
be entered instead of expressions:

    Command     Description                            Aliases
    --------------------------------------------------------------------
    add         Adds an unit, prefix or conversion on   add
                the fly to the current paper. The added
                item will not be saved in any way and
                will only be usable in the current
                paper.
                The first parameter must either be
                "unit", "prefix" or "conversion"
                followed by a valid definition.
    
    alias       Adds an alias on the fly to the current alias
                paper, the added alias will not be
                saved. A valid alias string is expected
                as parameter.
    
    clean       Cleans the current paper, removing      clean, clear
                all expressions from it.
    
    close       Closes the current paper.               close, :bdelete,
                                                        :bd
    
    copy        Copies the current paper to             copy, cp, y
                the clipboard by default. Accepts
                a type and a range.
    
    new         Creates a new paper.                    new, :new
    
    next        Switches to the next paper in           next, right,
                the list, if there is any.              :bnext, :bn
    
    note        Allows to edit the current note.        note
                Accepts the operation and parameters.
    
    open        Opens a paper from the given path.      open, :e
                The path to open the files is expected
                as parameter to this command. If
                multiple parameters are provided, all
                are opened.
    
    option      Set the given option with the given     option, opt, set,
                value. See the Options section for      setoption, setopt,
                further information.                    :so, :setopt
    
    previous    Switches to the previous paper in       previous, left,
                the list, if there is any.              :bprevious, :bp
    
    quit        Quits the application.                  quit, exit, :q,
                                                        :q!
    
    reload      Reloads the current paper, effectively  reload, reset
                undoing all changes.
    
    save        Saves the current paper, the location   save, store, :w
                can be given as parameter. If multiple
                parameters are provided, it is saved to
                all provided locations.
    
    save/quit   Saves the current paper and quits,      :x
                the location can be given as parameter.
                If multiple parameters are provided, it
                is saved to all provided locations.

Parameters are to be provided space separated and can be quoted, some examples:

    open /home/user/dir/somefile.jmathpaper
    open "./relative path with spaces/file.jmathpaper"
    open ./relative\ path\ with\ spaces/file.jmathpaper
    open a.jmathpaper b.jmathpaper c.jmathpaper