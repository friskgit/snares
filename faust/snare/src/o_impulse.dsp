// -*- compile-command: "cd .. && make jack src=o_impulse.dsp && cd -"; -*-&& cd -"; -*-

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
// Generating a single impulse to be fed into the likes of 'snare.dsp'.
// Use this in order to have a trigger programmatically available.
//
// 18 Juli 2019	Henrik Frisk	mail@henrikfrisk.com
//---------------------------------------------------

impgrp(x) = vgroup("impulse", x);
imp = impgrp(button("play")) : ba.impulsify;
				       
process = imp;

