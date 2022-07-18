// -*- compile-command: "cd .. && make jack src=src/snarefs.dsp && cd -"; -*-&& cd -"; -*-

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
// Where:
// * midi note 67-89
// * stiffness 0-0.55 (mapped to note as in note 67 -> 0)
// * midi velocity 75-127	    
// * midi velocity is mapped to pressure
// A useful parameter setting is:
//
//
// 30 Juni 2018	Henrik Frisk	mail@henrikfrisk.com
//---------------------------------------------------

osc1f = hslider("osc 1 freq", 330, 50, 2000, 0.1);
osc2f = hslider("osc 2 freq", 180, 50, 2000, 0.1);
tri1f = hslider("triangle freq", 111, 50, 2000, 0.1);
osc1 = os.osc(osc1f) *(0.1);
osc2 = os.osc(osc2f) *(0.1);
tri1 = os.triangle(tri1f) *(0.1);

imp = ba.pulse(hslider("tempo", 5000, 500, 10000, 1));

env = en.ar(attack, rel, imp)
with {
  attack = hslider("attack", 0.00000001, 0, 0.1, 0.000000001) : si.smooth(0.1);
  rel = hslider("rel", 0.1, 0.0000001, 0.5, 0.0000001) : si.smooth(0.2);
};

noiseenv = en.ar(attack, rel, imp)
with {
  attack = hslider("noise attack", 0.00000001, 0, 0.1, 0.000000001) : si.smooth(0.1);
  rel = hslider("noise rel", 0.1, 0.0000001, 0.5, 0.0000001) : si.smooth(0.2);
};

// Noise
n = no.multinoise(8) : par(i, 8, _ * env * hslider("noise lvl", 0.1, 0, 1.5, 0.0001));
// Reduce to stereo
nse = n :> _ * noiseenv ;

// filt = fi.resonbp(frq, q, gain)
// with {
//   frq = hslider("frq", 200, 50, 5000, 0.1);
//   q = hslider("q", 1, 0.01, 10, 0.01);
//   gain = hslider("gn", 0, 0, 2, 0.00001);
// };

// Frequence shift
mSR = fconstant(int fSamplingFreq , <math.h>);
f2smp(freq) = (mSR, freq : / ) ;
phasor(smp) =   1 : +~_ : _,smp : fmod : _,smp : / ;
				       
unit(v1) = (_ <: *(v1) , _'' : - ) : + ~ (_', v1 : *);
filters = _ <: _,_' :( unit(0.161758): unit(.733029) : unit (.94535) : unit(.990598) ), (unit(.479401) : unit(.876218) : unit (.976599) : unit(.9975) ) ;
cmpl_osc(freq) = f2smp(freq) : phasor : _, 6.2831853 : *<: sin,cos; 

cmpl_mul(in1,in2,in3,in4) = in1*(in3), in2*(in4) ;

trimod = tri1, tri1 : (filters, cmpl_osc) : cmpl_mul <: +,- ;
oscs = osc1, osc2 : par(i, 2, _* env);
process = trimod : par(i, 2, _ * env), oscs :> _+nse ,_+nse :> _;
//process = nse;
