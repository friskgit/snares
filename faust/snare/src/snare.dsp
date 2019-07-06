// -*- compile-command: "cd .. && make sc && cd -"; -*-

declare version " 0.1 ";
declare author " Henrik Frisk " ;
declare author " henrikfr ";
declare license " BSD ";
declare copyright "(c) dinergy 2018 ";

import("stdfaust.lib");

//---------------`Snare drum synth` --------------------------
// A take at a snare drum synth
//
// A single hit snare drum synth controllable with midi. Each hit is distribute over `channels` speakers.
//
// Where:
// * midi note 67-89
// * stiffness 0-0.55 (mapped to note as in note 67 -> 0)§
// * midi velocity is mapped to pressure

// A useful parameter setting is:
//
// 30 Juni 2018	Henrik Frisk	mail@henrikfrisk.com
//---------------------------------------------------

// This is for automated playing
// pimp = ba.pulse(hslider("pan rate", 1000, 500, 10000, 1));
// env = en.ar(0.000001, 0.1, button("play"));

// Set the number of channels at compile time.
channels = 2;

// Main impulse for generating one hit.
imp = button("gate");

// Main envelope
env = en.ar(attack, rel, imp) * amp
with {
  attack = hslider("attack", 0.00000001, 0, 0.1, 0.000000001) : si.smooth(0.1);
  rel = hslider("rel", 0.1, 0.0000001, 0.5, 0.0000001) : si.smooth(0.2);
  amp = hslider("vol", 0.5, 0, 1, 0.0001);
};

////////
// Control the output channel
////////

pimp = imp : ba.impulsify;
focus = hslider("focus", 1, 0, 1, 0.0001);
position = hslider("position", 1, 0, channels, 1);
rate = ma.SR/1000.0;
rndctrl = (no.lfnoise(rate) * (channels + 1)) * focus : ma.fabs + position : int ;
outputctrl = rndctrl : ba.sAndH(pimp);

// Wrap channels around the array.
ch_wrapped = ma.modulo(outputctrl, channels);

// Noise generation and filter
n = no.multinoise(8) : par(i, 8, _ * env * 0.1);
filt = fi.resonbp(frq, q, gain)
with {
  frq = hslider("freq", 200, 50, 5000, 0.1);
  q = hslider("q", 1, 0.01, 10, 0.01);
  gain = hslider("gain", 0, 0, 2, 0.00001);
};

// Add this to disperse audio
process = n : par(i, 8, filt) :> _,_ :> ba.selectoutn(channels, ch_wrapped);