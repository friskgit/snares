// -*- compile-command: "cd .. && make jack src=i_filtered_snare_dispersed.dsp && cd -"; -*-&& cd -"; -*-

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
// Taking an impulse as input and feeding it to a generic_snarefs and on to a disperser which
// outputs each filter band on a separate channel. The input control 'port' offsets the filterbands with
// the given amount.
//
// Main controls:
// - tempo: (unless impulse is taken from input)
// - random: if uncecked the output control is done by 'output', else it is randomly distributed
// - output: the offset for the distributed filterbands, if 0 then the lowest band is output to
//   the 0th speaker (if the checkbox above is checked this does nothing)
//
// Other controls are inherited from 'generic_snarefs' and 'filter_bank'.
// 
// 18 Juli 2019	Henrik Frisk	mail@henrikfrisk.com
//---------------------------------------------------

// Number of bands and number of output channels. Offset is if channels < bands.
bands=16;
channels = 2;
offset = outgrp(hslider("offset", 0, 0, channels, 1));

// Impulse control
impgrp(x) = vgroup("impulse", x);
imp = ba.pulse(impgrp(hslider("tempo", 5000, 50, 48000, 1)));

// Output control
outgrp(x) = vgroup("[1]output", x);
port = outgrp(hslider("output port", 0, 0, 16, 1));
ongrellls
// Two distribution possibilities, wrapped or wrapped_rnd
ch_wrapped_rnd(x) = ma.modulo(+(outputctrl, x), channels);
ch_wrapped(x) = ma.modulo(+(port, x), channels);
disperser(x) = ch_wrapped(x), ch_wrapped_rnd(x) : ba.selectn(2, outgrp(checkbox("[0]random")));

// Generate a random signal triggered by the impulse
rate = ma.SR/1000.0;
rndctrl = (no.lfnoise(rate) * (channels + 1)) : ma.fabs : int ;
outputctrl = rndctrl : ba.sAndH(imp);

// Main process
process = imp : component("generic_snarefs.dsp") :
	  component("filter_bank.dsp")[bands=bands;] :
	  par(i, bands, ba.selectoutn(bands, disperser(i))) :>
	  par(i, bands, _);


