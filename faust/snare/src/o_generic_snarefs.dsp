// -*- compile-command: "cd .. && make jack src=o_generic_snarefs.dsp && cd -"; -*-&& cd -"; -*-

declare version " 0.1 ";
declare author " Henrik Frisk " ;
declare author " henrikfr ";
declare license " BSD ";
declare copyright "(c) dinergy 2018 ";

import("stdfaust.lib");
import("math.lib") ; // for PI definition
import("music.lib") ; // for osci definition

//---------------`Single snare drum` --------------------------
//
// Taking an impulse as input and feeding it to a generic_snarefs.
//
// Paramters
// - tempo: tempo of impulse
// - see inherited parameters from generic_snarefs.dsp
//
// 18 Juli 2019	Henrik Frisk	mail@henrikfrisk.com
//---------------------------------------------------

impgrp(x) = vgroup("impulse", x);
imp = ba.pulse(impgrp(hslider("tempo", 5000, 5, 10000, 1)));
//imp = os.imptrain(impgrp(hslider("tempo", 1, 0.01, 10000, 1)));
//imp = os.impulse;g

process = component("generic_snarefs.dsp") : !,_ :> _;

