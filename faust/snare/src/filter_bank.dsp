 // -*- compile-command: "cd .. && make jack src=src/filter_bank.dsp && cd -"; -*-

declare version " 0.1 ";
declare author " Henrik Frisk " ;
declare author " henrikfr ";
declare license " BSD ";
declare copyright "(c) dinergy 2018 ";

import("stdfaust.lib");

//---------------`Filterbank for snaredrum` --------------------------
//
// A filterbank for use with snare drum synths and channel disperser. It
// takes a trigger and a signal as input and outputs as many channels as
// there are filterbands.
//
// Parameter 'bands' is set at compile time.
//
// 18 Juli 2019	Henrik Frisk	mail@henrikfrisk.com
//---------------------------------------------------

bands = 16;
     
// Control the output channel
del_group(x) = vgroup("delay", x);
del = del_group(hslider("delay", 0, 0, 1024, 1));

fb = fi.mth_octave_filterbank(order,M,ftop,bands) : par(i, bands, (*(ba.db2linear(fader(bands-i))))) :
     par(i, bands, de.sdelay(8192, 512, *(del, i)))
with {
  M = 2;
  order = 1;
  ftop = 10000;
  bp1 = ba.bypass1;
  slider_group(x) = mofb_group(hgroup("[1]", x));
  mofb_group(x) = vgroup("constant-q filter bank (Butterworth dyadic tree) [tooltip: See Faust's filters.lib for more info", x);  
  fader(i) = slider_group(vslider("Band%2i [unit:dB] [tooltip: Bandpass filter gain in dB]", -10, -70, 10, 0.1)) : si.smoo;
  bp = bypass_group(checkbox("[0] Bypassc[tooltip: When this is checked, the filter-bank has no effect]"));
  };

ch_wrapped(x) = ma.modulo((offset+x), bands);
offset = hslider("offset", 0, 0, bands, 1);

process(trig, sig) = sig : fb : par(i, bands, ba.selectoutn(bands, ch_wrapped(i))) :> par(i, bands, _);
