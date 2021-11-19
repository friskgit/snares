// -*- compile-command: "cd .. && make sc src=src/generic_snarefs.dsp && cd -"; -*-&& cd -"; -*-

declare version " 0.1 ";
declare author " Henrik Frisk " ;
declare author " henrikfr ";
declare license " BSD ";
declare copyright "(c) dinergy 2018 ";

import("stdfaust.lib");
import("math.lib") ; // for PI definition
import("music.lib") ; // for osci definition

//---------------`Snare drum synth` --------------------------
// A snare drum synth based on a frequency shifted osc.
//
// It takes a single input as the impulse for the synthesis and
// outputs a pair of signals where the first is the trigger and
// the second is the signal.
//
// Paramters:
// - osc1f: oscilator 1 frequency
// - osc2f: oscilator 2 frequency
// - tri1: tringle osc frequency
// - attack: envelope attack time
// - rel: envelope release time
// - noise attack: noise envelope attack time
// - noise rel: noise envelope release time
// - noise lvl: noise level
//
// 30 Juni 2018	Henrik Frisk	mail@henrikfrisk.com
//---------------------------------------------------
synthgrp(x) = vgroup("snare", x);

accent = 1;
amp = synthgrp(hslider("amp", 0.5, 0, 2, 0.001));
osc1f = synthgrp(hslider("osc 1 freq", 330, 50, 2000, 0.1));
osc2f = synthgrp(hslider("osc 2 freq", 180, 50, 2000, 0.1));
tri1f = synthgrp(hslider("triangle freq", 111, 50, 2000, 0.1));
fltsw = synthgrp(hslider("filter sweep", 1, 0, 2, 0.001));
osc1 = os.osc(osc1f) *(0.1);
osc2 = os.osc(osc2f) *(0.1);
tri1 = os.triangle(tri1f) *(0.1);

env(x) = en.ar(attack, rel, x)
with {
  attack = synthgrp(hslider("attack", 0.00000001, 0, 0.1, 0.000000001) : si.smooth(0.1));
  rel = synthgrp(hslider("rel", 0.1, 0.0000001, 0.5, 0.0000001) : si.smooth(0.2));
};

noiseenv(x) = en.ar(attack, rel, x)
with {
  attack = synthgrp(hslider("noise attack", 0.00000001, 0, 0.1, 0.000000001) : si.smooth(0.1));
  rel = synthgrp(hslider("noise rel", 0.1, 0.0000001, 0.5, 0.0000001) : si.smooth(0.2));
};

// Noise
noiselv = synthgrp(hslider("noise lvl", 0.1, 0, 1.5, 0.0001));
n(x) = no.multinoise(8) : par(i, 8, _ * env(x) * noiselv);

// Reduce to stereo
nse(x) = n(x) :> _ * noiseenv(x) : *(amp);

// filt = fi.resonbp(frq, q, gain)
// with {
//   frq = hslider("frq", 200, 50, 5000, 0.1);
//   q = hslider("q", 1, 0.01, 10, 0.01);
//   gain = hslider("gn", 0, 0, 2, 0.00001);
// };

// Frequence shift
mSR = fconstant(int fSamplingFreq , <math.h>);
f2smp(freq) = (mSR, freq : / ) ;
phasor(smp) =   fltsw : +~_ : _,smp : fmod : _,smp : / ;
				       
unit(v1) = (_ <: *(v1) , _'' : - ) : + ~ (_', v1 : *);
filters = _ <: _,_' :( unit(0.161758): unit(.733029) : unit (.94535) : unit(.990598) ), (unit(.479401) : unit(.876218) : unit (.976599) : unit(.9975) ) ;
cmpl_osc(freq) = f2smp(freq) : phasor : _, 6.2831853 : *<: sin,cos; 

cmpl_mul(in1,in2,in3,in4) = in1*(in3), in2*(in4) ;

volume(s) = s : *(amp) : *(accent);
trimod = tri1, tri1 : (filters, cmpl_osc) : cmpl_mul <: +,- ;
oscs(x) = osc1, osc2 : par(i, 2, _* env(x));
process(x) = trimod : par(i, 2, _ * env(x)), oscs(x) :> _+nse(x) ,_+nse(x) :> _ ; //x,volume(_) ;
