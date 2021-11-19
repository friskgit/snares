// -*- compile-command: "cd .. && make sc src=i_snare_phase_disp.dsp && cd -"; -*-&& cd -"; -*-

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

p = hslider("pulse[scale:exp]", 1, 1, 2000, 0.1) : si.smooth(0.5);
//per = hslider("pulse", 192000, 10, 192000, 1) : *(4);
per = ma.SR / p : int : *(4);
//p = hslider("pulse", 1, 1, 1000, 0.01);
imp = ba.pulse(per);
impsp = ba.pulse(per / 4);
// imp = os.imptrain(p);
delA = per : *(0.25);
delB = per : *(0.5);
delC = per : *(0.75);
imp_delA = imp : de.sdelay(192000, 64, delA);
imp_delB = imp : de.sdelay(192000, 64, delB);
imp_delC = imp : de.sdelay(192000, 64, delC);
//imp = os.impulse;
// divisor = 0.25;
// snares(d) = imp : de.sdelay(192000, 64, (per : *(divisor * d))) : component("generic_snarefs.dsp");
// process = par(i, 4, snares(i+1));
// n = (p / 20000) - 1 : ma.fabs : ma.log1p;

process = impsp, ((imp : component("generic_snarefs.dsp")),
	   (imp_delA : component("generic_snarefs.dsp")),
	   (imp_delB : component("generic_snarefs.dsp")),
	   (imp_delC : component("generic_snarefs.dsp")) :> _ ) : component("disperse.dsp")[channels = 29;];

