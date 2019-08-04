// -*- compile-command: "cd .. && make jack src=o_dispersed_snare.dsp && cd -"; -*-&& cd -"; -*-

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
// Generating an impulse and feeding it to a generic_snarefs and on to a disperser.
// disperse.dsp doe not pass on the impules as generic_snarefs does.
//
// 18 Juli 2019	Henrik Frisk	mail@henrikfrisk.com
//---------------------------------------------------

impgrp(x) = vgroup("impulse", x);
imp = impgrp(button("play")) : ba.impulsify;
				       
process = imp : component("generic_snarefs.dsp") : component("disperse.dsp") ;

