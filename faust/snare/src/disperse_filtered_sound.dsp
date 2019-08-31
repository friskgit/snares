 // -*- compile-command: "cd .. && make jack src=src/disperse_filtered_sound.dsp && cd -"; -*-

declare version " 0.1 ";
declare author " Henrik Frisk " ;
declare author " henrikfr ";
declare license " BSD ";
declare copyright "(c) dinergy 2018 ";

import("stdfaust.lib");

//---------------`takes a filtered sound and disperses it` --------------------------
//
// Take the output of a filterbankj and alter the routing of the channels. Takes the impulse as its first input
//
// Parameter 'bands' is set at compile time.
//
// 18 Juli 2019	Henrik Frisk	mail@henrikfrisk.com
//---------------------------------------------------

bands = 8;
channels = 16;

t = 0;
// Extract the impulse
trigger(x) = t;

// Control the output channel
distribute = 

process(x) = par(i, bands, ba.selectoutn(bands, i));
