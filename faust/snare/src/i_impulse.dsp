// -*- compile-command: "cd .. && make jack src=i_impulse.dsp && cd -"; -*-&& cd -"; -*-

declare version " 0.1 ";
declare author " Henrik Frisk " ;
declare author " henrikfr ";
declare license " BSD ";
declare copyright "(c) dinergy 2018 ";

import("stdfaust.lib");
import("math.lib") ; // for PI definition
import("music.lib") ; // for osci definition

//---------------`General impulse` --------------------------
//
// Generating an impulse to be fed into the likes of 'snare.dsp'
//
// 18 Juli 2019	Henrik Frisk	mail@henrikfrisk.com
//---------------------------------------------------

impgrp(x) = vgroup("impulse", x);
imp = ba.pulse(impgrp(hslider("tempo", 5000, 500, 10000, 1)));
				       
process = imp;

