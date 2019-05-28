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
// Each hit is output to a channel <= channels as controlled by the lfo
// in rndctrl. Due to the ma.fabs, there is a greater chance that signal
// is sent to lower outputs than higher
//
// Where:
// * midi note 67-89
// * stiffness 0-0.55 (mapped to note as in note 67 -> 0)§
// * midi velocity 75-127	    
// * midi velocity is mapped to pressure
// A useful parameter setting is:
//
//
// 30 Juni 2018	Henrik Frisk	mail@henrikfrisk.com
//---------------------------------------------------

channels = 2;
steps = 16;
integ(x) = x - ma.frac(x);
tmp = hslider("tempo", 300, 50, 10000, 1);
imp = ba.pulse(tmp);
attack = hslider("attack", 0.00000001, 0, 0.1, 0.000000001) : si.smooth(0.1);
rel = hslider("rel", 0.1, 0.0000001, 0.5, 0.0000001) : si.smooth(0.2);

// Nominator beat
env = en.ar(attack, rel, p) * amp
with {
  p = imp : ba.resetCtr(nom, 1);
  nom = hslider("nominator", 1, 1, steps, 1);
  amp = hslider("vol a", 0.5, 0, 1, 0.0001);
};

// Denominator beat
envb = en.ar(attack, rel, p) * amp
with {
//  p = imp : ba.resetCtr(steps / div, 1);
  p = imp : ba.resetCtr(denom, 1);
  denom = hslider("denominator", 1, 1, steps, 1);
  amp = hslider("vol b", 0.5, 0, 1, 0.0001);
};

// Control the output channel
// Define the focus
focus = hslider("focus", 1, 0, 1, 0.0001);
// Define the position
position = hslider("position", 1, 0, channels, 1);
rate = ma.SR/1000.0;
rndctrl = (no.lfnoise(rate) * (channels + 1)) * focus : ma.fabs + position : int ;
outputctrl = rndctrl : ba.sAndH(imp);

n = no.multinoise(8) : par(i, 8, _ * env * 0.1);
m = no.multinoise(8) : par(i, 8, _ * envb * 0.1);
filt = fi.resonbp(frq, q, gain)
with {
  frq = hslider("freq", 200, 50, 5000, 0.1);
  q = hslider("q", 1, 0.01, 10, 0.01);
  gain = hslider("gain", 0, 0, 2, 0.00001);
};

ch_wrapped = ma.modulo(outputctrl, channels);
process = n,m : par(i, 8, filt), par(i, 8, filt) :> _,_; // :> ba.selectoutn(channels, ch_wrapped);
//process = n : par(i, 8, filt) :> _,_;
