Units
=====

There is support for converting units, out-of-the-box the most basic units being
possible to be converted. The conversion works by specifying a value and
the units to convert from and to. The following ways to write the conversion is
acceptable.

    #1                            3feet to meter = 0.9144
    #2                            3feet in meter = 0.9144
    #3                            3feet as meter = 0.9144
    #4                               3feet meter = 0.9144

Units can either be specified by name or by alias (for example `meter` and `m`).
Additionally, they can also be specified in plural (`meters`).

By default, SI and IEC prefixes are supported.

    #1                              3km to meter = 3000
    #2                             20yards to km = 0.018288
    #3                          2decimeter to in = 7.874015...

Not just plain values but whole expressions are supported.

    a                                        a=5 = 5
    b                                       b=10 = 10
    c                  c=a*sqrt(b) meter to inch = 622.4956...

Also combined units are being supported.

    #1                           5km/h to in/sec = 10.9361...
    #2               12 l/sec/m^2 in l/hour/sqin = 27.8709...


There is also an automatic conversion from a unit with a prefix to a unit
without one, if no target unit is specified.

    #1                                       1km = 1000
    #2                                     1km/h = 1000