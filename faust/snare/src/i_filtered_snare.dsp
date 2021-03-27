// -*- compile-command: "cd .. && make jack src=i_filtered_snare.dsp && cd -"; -*-&& cd -"; -*-

declare version " 0.1 ";
declare author " Henrik Frisk " ;
declare author " henrikfr ";
declare license " BSD ";
declare copyright "(c) dinergy 2018 ";

import("stdfaust.lib");
import("math.lib") ; // for PI definition
import("music.lib") ; // for osci definition

//---------------`Snare drum split up in X channels` --------------------------
//
// Taking an impulse as input and feeding it to a generic_snarefs and on to a disperser.
// disperse.dsp does not pass on the impules as generic_snarefs does.
//
// 18 Juli 2019	Henrik Frisk	mail@henrikfrisk.com
//---------------------------------------------------

impgrp(x) = vgroup("impulse", x);
imp = ba.pulse(impgrp(hslider("tempo", 5000, 20, 10000, 2))) : si.smooth(0.999);
//imp = os.impulse;

process = imp : component("generic_snarefs.dsp") : component("filter_bank.dsp")[bands = 16;] ;

