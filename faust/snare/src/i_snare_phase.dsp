// -*- compile-command: "cd .. && make app src=i_snare_phase.dsp && cd -"; -*-&& cd -"; -*-

declare version " 0.1 ";
declare author " Henrik Frisk " ;
declare author " henrikfr ";
declare license " BSD ";
declare copyright "(c) dinergy 2018 ";

import("stdfaust.lib");
import("math.lib") ; // for PI definition
import("music.lib") ; // for osci definition

//---------------`Snare drum dispersing over X channels` --------------------------
//
// Generating an impulse and feeding it to a generic_snarefs. Each impulse is delayed by 25%
// and sent to a separate instance of generic_snarefs. This allows for faster impulses 4X the
// speed of the pulse which is in samples.
//
// disperse.dsp doe not pass on the impules as generic_snarefs does.
//
// 18 Juli 2019	Henrik Frisk	mail@henrikfrisk.com
//---------------------------------------------------

p = hslider("pulse", 48000, 30, 60000, 1);
imp = ba.pulse(p);
delA = p : *(0.25);
delB = p : *(0.5);
delC = p : *(0.75);
imp_delA = imp : de.sdelay(48000, 256, delA);
imp_delB = imp : de.sdelay(48000, 256, delB);
imp_delC = imp : de.sdelay(48000, 256, delC);
//imp = os.impulse;

process = ((imp : component("generic_snarefs.dsp")),
	   (imp_delA : component("generic_snarefs.dsp")),
	   (imp_delB : component("generic_snarefs.dsp")),
	   (imp_delC : component("generic_snarefs.dsp"))) :> _;
