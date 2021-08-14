// -*- compile-command: "cd .. && make jack src=i_snare_phase.dsp && cd -"; -*-&& cd -"; -*-

declare version " 0.1 ";
declare author " Henrik Frisk " ;
declare author " henrikfr ";
declare license " BSD ";
declare copyright "(c) dinergy 2018 ";

import("stdfaust.lib");
import("math.lib") ; // for PI definition
import("music.lib") ; // for osci definition

//---------------`Four drum instances phased equally` --------------------------
//
// Generating an impulse and feeding it to a generic_snarefs. Each impulse is delayed by 25%
// and sent to a separate instance of generic_snarefs. This allows for faster impulses 4X the
// speed of the pulse which is in samples.
//
// disperse.dsp doe not pass on the impules as generic_snarefs does.
//
// 18 Juli 2019	Henrik Frisk	mail@henrikfrisk.com
//---------------------------------------------------

p = hslider("pulse", 1, 1, 10000, 1);// : si.smooth(0.999);
per = ma.SR / p : int : *(4);

//This is to avoid lagging when modulating the pulse 
hit(t) = (diff(ba.period(t)) < 0) + impulse
with {
  diff(x) = x <: _ - _';
  impulse = 1 - 1';
};

delA = per : *(0.25);
delB = per : *(0.5);
delC = per : *(0.75);

imp_delA = hit(per) : de.sdelay(192000, 64, delA);
imp_delB = hit(per) : de.sdelay(192000, 64, delB);
imp_delC = hit(per) : de.sdelay(192000, 64, delC);

process = ((hit(per) : component("generic_snarefs.dsp")[accent = 2;]),
	   (imp_delA : component("generic_snarefs.dsp")),
	   (imp_delB : component("generic_snarefs.dsp")),
	   (imp_delC : component("generic_snarefs.dsp"))) :> _;
