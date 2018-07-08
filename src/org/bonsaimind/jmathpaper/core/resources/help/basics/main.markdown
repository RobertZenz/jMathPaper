Basics
======

When you start jMathPaper you are greeted with a (currently empty) list of previous expressions and the input box to enter a new one. You can enter any expression you want to evaluate. Additionally, there is a larger input are to the right which allows you to take notes.

You can save and load papers from the menu, they are stored in a very simple cleartext format, so it is easy to create and edit them without jMathPaper. Examples can be found in the examples directory.

Let us start with something simple and enter a few simple expressions:

    ID                               Expression Result
    --------------------------------------------------
    #1                                       1+1 =   2
    #2                                       5*5 =  25
    #3                                    239-38 = 201

One of the big features of jMathPaper is that you can easily reference
a previous result by using its ID, like this:

    #1                                       1+1 =   2
    #2                                       5*5 =  25
    #3                                    239-38 = 201
    #4                                     #2*25 = 625

We can also define variables which we can reference:

    #1                                       1+1 =   2
    #2                                       5*5 =  25
    #3                                    239-38 = 201
    #4                                     #2*25 = 625
    length                             length=10 =  10
    width                                width=5 =   5
    area                       area=length*width =  50

Now if we made a mistake, for example the width really is 7, we can simply press
up and down to scroll through the history and execute the corrected expression
again. The previously executed expressions do not change, so we need to evaluate
them again:

    #1                                       1+1 =   2
    #2                                       5*5 =  25
    #3                                    239-38 = 201
    #4                                     #2*25 = 625
    length                             length=10 =  10
    width                                width=5 =   5
    area                       area=length*width =  50
    width                                width=7 =   7
    area                       area=length*width =  70

We can also use mathematical functions, like `sin`:

    #1                                   sin(70) = 0.9396926

See the documentation of [EvalEx](https://github.com/uklimaschewski/EvalEx/blob/master/README.md) to see what functions are supported.
