// -*- compile-command: "cd .. && make jack src=src/snare.dsp && cd -"; -*-

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
// It has its own trigger
//
// Where:
// * midi note 67-89
// * stiffness 0-0.55 (mapped to note as in note 67 -> 0)§
// * midi velocity is mapped to pressure
//
// A useful parameter setting is:
//
// 30 Juni 2018	Henrik Frisk	mail@henrikfrisk.com
//---------------------------------------------------

// Main impulse for generating one hit.
imp = button("gate");

// GUI
snaregroup(x) = vgroup("snare", x);

// Main envelope
env = en.ar(attack, rel, imp) * amp
with {
  attack = snaregroup(hslider("attack", 0.00000001, 0, 0.1, 0.000000001) : si.smooth(0.1));
  rel = snaregroup(hslider("rel", 0.1, 0.0000001, 0.5, 0.0000001) : si.smooth(0.2));
  amp = snaregroup(hslider("vol", 0.5, 0, 1, 0.0001));
};

// Noise generation and filter
snare(n) = no.multinoise(8) : par(i, 8, _ * env * 0.1);
filt = fi.resonbp(frq, q, gain)
with {
  frq = snaregroup(hslider("freq", 200, 50, 5000, 0.1));
  q = snaregroup(hslider("q", 1, 0.01, 10, 0.01));
  gain = snaregroup(hslider("gain", 0, 0, 2, 0.00001));
};

process = snare;
