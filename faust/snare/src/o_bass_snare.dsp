// -*- compile-command: "cd .. && make jack src=o_bass_snare.dsp && cd -"; -*-&& cd -"; -*-

declare version " 0.1 ";
declare author " Henrik Frisk " ;
declare author " henrikfr ";
declare license " BSD ";
declare copyright "(c) dinergy 2018 ";

import("stdfaust.lib");
import("math.lib") ; // for PI definition
import("music.lib") ; // for osci definition

//---------------`Bass snare drum` --------------------------
//
// Generating an impulse and feeding it to a bass_drum synth.
// o_bass_snare.dsp does not pass on the impules as generic_snarefs does.
//
// 03 Maj 2020	Henrik Frisk	mail@henrikfrisk.com
//---------------------------------------------------

impgrp(x) = vgroup("impulse", x);
imp = ba.pulse(impgrp(hslider("tempo", 5000, 500, 10000, 1)));

process = component("bass_snare.dsp");

