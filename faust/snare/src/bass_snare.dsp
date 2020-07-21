// -*- compile-command: "cd .. && make jack src=src/bass_snare.dsp && cd -"; -*-

declare version " 0.1 ";
declare author " Henrik Frisk " ;
declare author " henrikfr ";
declare license " BSD ";
declare copyright "(c) dinergy 2018 ";

import("stdfaust.lib");
import("math.lib") ; // for PI definition

//---------------`Snare drum synth` --------------------------
// A bassy snare drum with controllable noise level and
// independent envelope. It takes the impulse as input.
//
//
// 04 Augusti 2019	Henrik Frisk	mail@henrikfrisk.com
//---------------------------------------------------

// GUI
syngroup(x) = vgroup("[0]synthesis", x);
envgroup(x) = vgroup("[1]envelopes", x);
nsegroup(x) = vgroup("[2]noise", x);

trif = syngroup(hslider("[2]triangle freq", 10, 10, 5000, 0.1));
osc1f = syngroup(hslider("[0]osc 1 freq", 50, 10, 5000, 0.1));
osc2f = syngroup(hslider("[1]osc 2 freq", 130, 10, 5000, 0.1));
osc1 = os.osc(osc1f) *(0.1);
osc2 = os.osc(osc2f) *(0.1);
tri1 = os.triangle(trif) *(0.1);

env(imp) = en.ar(attack, rel, imp)
with {
  attack = envgroup(hslider("[0]attack", 0.00000001, 0, 0.1, 0.000000001) : si.smooth(0.1));
  rel = envgroup(hslider("[1]rel", 0.1, 0.0000001, 0.5, 0.0000001) : si.smooth(0.2));
};

// Noise envelope
n_env(imp) = en.asr(attack, sustain, rel, imp)
with {
  attack = envgroup(hslider("[2]noise attack", 0.00000001, 0, 0.1, 0.0001) : si.smooth(0.1));
  sustain = envgroup(hslider("[3]noise sustain", 0.016, 0, 1, 0.0001) : si.smooth(0.1));
  rel = envgroup(hslider("[4] noise rel", 0.1, 0.049, 0.5, 0.0001) : si.smooth(0.2));
};

// Noise
nse_vol = nsegroup(hslider("noise vol", 0.073, 0, 1.0, 0.0000001) : si.smooth(0.1));
n(imp) = no.multinoise(8) : par(i, 8, _ * n_env(imp) * nse_vol);
filt = fi.resonbp(frq, q, gain)
with {
  frq = nsegroup(hslider("frq", 200, 50, 5000, 0.1));
  q = nsegroup(hslider("q", 1, 0.01, 10, 0.01));
  gain = nsegroup(hslider("gn", 0, 0, 2, 0.00001));
};

nse(imp) = n(imp) :> _,_;

// Frequence shift
mSR = fconstant(int fSamplingFreq , <math.h>);
f2smp(freq) = (mSR, freq:/) ;
phasor(smp) = syngroup(hslider("modulation freq", 0, 0, 10, 0.0001)) : +~_ : _,smp : fmod : _,smp : / ;

unit(v1) = (_ <: *(v1) , _'' : - ) : + ~ (_', v1 : *);
filters = _ <: _,_' :( unit(0.161758): unit(.733029) : unit (.94535) : unit(.990598) ), (unit(.479401) : unit(.876218) : unit (.976599) : unit(.9975) ) ;
cmpl_osc(freq) = f2smp(freq) : phasor : _, 6.2831853 : *<: sin,cos; 

cmpl_mul(in1,in2,in3,in4) = in1*(in3), in2*(in4) ;

// Bandbass to give punch to the sound
bpass(x) = x : fi.resonlp(fc, q, gain)
with {
  fc = syngroup(hslider("flt frq", 100, 10, 10000, 0.1));
  q = syngroup(hslider("flt Q", 0.1, 0.00001, 20, 0.01));
  gain = syngroup(hslider("flt gain", 1, 0.0001, 2, 0.001));
  };

// random amp
// samp = no.lfnoise(100) : ma.fabs;
trimod = tri1, tri1 : (filters, cmpl_osc) : cmpl_mul <: +,- ;
oscs(imp) = osc1, osc2 : par(i, 2, _* env(imp));

// take the impulse as input
process(x) = trimod : par(i, 2, _ * env(x)), oscs(x) :> _,_ , nse(x)  :> bpass(_) ; 
