
import("stdfaust.lib");

// Impulse for snare hits
imp = ba.beat(hslider("tempo", 100, 1, 2000, 1));
// GUI
snaregroup(x) = vgroup("snare", x);

// Main envelope
env = en.ar(0.00001, 0.05, imp) * amp

// Noise generation and filter
snare(n) = no.multinoise(8) : par(i, 8, _ * env * 0.1);

process = snare;
