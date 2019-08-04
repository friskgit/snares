// -*- compile-command: "cd .. && make jack src=src/disperse.dsp && cd -"; -*-&& cd -"; -*-

declare version " 0.1 ";
declare author " Henrik Frisk " ;
declare author " henrikfr ";
declare license " BSD ";
declare copyright "(c) dinergy 2018 ";

import("stdfaust.lib");

//---------------`Disperse audio randomly over x channels` --------------------------
//
// Each hit is output to a channel <= channels as controlled by the lfo
// in rndctrl. Due to the ma.fabs, there is a greater chance that signal
// is sent to lower outputs than higher
//
//
// 18 Juli 2019	Henrik Frisk	mail@henrikfrisk.com
//---------------------------------------------------

// GUI
posgroup(x) = vgroup("[0]position", x);

// Set the number of channels at compile time.
channels = 14;
integ(x) = x - ma.frac(x);

//imp = ba.pulse(hslider("tempo", 5000, 500, 10000, 1));
// Control the output channel
focus = posgroup(hslider("[1]disperse", 1, 0, 1, 0.0001));
position = posgroup(hslider("[0]position", 1, 0, channels, 1));
rate = ma.SR/1000.0;
rndctrl = (no.lfnoise(rate) * (channels + 1)) * focus : ma.fabs + position : int ;
outputctrl(imp) = rndctrl : ba.sAndH(imp);

// Wrap channels around the array.
ch_wrapped(imp) = ma.modulo(outputctrl(imp), channels);

// Main gate
process(imp, sig) = sig : ba.selectoutn(channels, ch_wrapped(imp)) ;
