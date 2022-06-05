// -*- compile-command: "cd .. && make jack src=skipping.dsp && cd -"; -*-&& cd -"; -*-
import("stdfaust.lib");

/* This version gets rid of the skips in the pulse. Thanks to Dario Sanfilippo 
Oddly enough, using ba.pulse reintroduces the problem, albeit not as much.

*/

/*
I had another look and using floats is still problematic in single-precision; pulses won't take place at the exact BPM for low BPM values.

The problem with the current ba.beat implementation is that it checks against 0: if we decrease the second operand of the % operation, there is no guarantee that the output will go through 0 when wrapping around. What we could do is to check if the first difference is negative to cover the more general case of wrapping around:
*/

freq = hslider("frq", 10000, 2, 20000, 1);
beat(t) = (diff(ba.period(t)) < 0) + impulse
with {
  diff(x) = x <: _ - _';
  import(".lib");pulse = 1 - 1';
};
process = beat(freq) , beat(freq);

/* This code has the same issue */

/*
import("stdfaust.lib");
imp = ba.pulse(hslider("test", 20000, 10, 40000, 1));
env = en.ar(0.00001, 0.05, imp);
process = imp, imp;
*/
