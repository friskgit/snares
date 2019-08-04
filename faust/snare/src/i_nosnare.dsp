// -*- compile-command: "cd .. && make jack src=src/i_nosnare.dsp && cd -"; -*-&& cd -"; -*-

declare version " 0.1 ";
declare author " Henrik Frisk " ;
declare author " henrikfr ";
declare license " BSD ";
declare copyright "(c) dinergy 2018 ";

import("stdfaust.lib");

//---------------`Snare drum dispersing over X channels` --------------------------
//
// Generating an impulse and feeding it to a generic_snarefs and on to a disperser.
// disperse.dsp doe not pass on the impules as generic_snarefs does.
//
// 18 Juli 2019	Henrik Frisk	mail@henrikfrisk.com
//---------------------------------------------------

// impgrp(x) = vgroup("impulse", x);
// imp = ba.pulse(impgrp(hslider("tempo", 5000, 500, 10000, 1)));
// //imp = os.impulse;

process = component("i_impulse.dsp") : component("bass_snare.dsp") ;

