Pbind(\instrument, \isnaredisp,
\dur, Pseq([0.05263157894736842, 0.10526315789473684, 0.15789473684210525, 0.21052631578947367, 0.2631578947368421, 0.3157894736842105, 0.3684210526315789, 0.42105263157894735, 0.47368421052631576, 0.5263157894736842, 0.5789473684210527, 0.631578947368421, 0.6842105263157895, 0.7368421052631579, 0.7894736842105263, 0.8421052631578947, 0.8947368421052632, 0.9473684210526315, 1.0, ], inf),
\freq, 1,
\freq_mod, 0,
\smth, Pseq([1, 1, 1, 1, ], inf),
\del_mod, 0,
\b1, Pshuf([-0.0, -56.568542500724995, -80.0, -56.5685424775202, ], inf),
\b2, Pshuf([-56.568542500724995, -80.0, -56.5685424775202, -0.0, ], inf),
\b3, Pshuf([-80.0, -56.5685424775202, -0.0, -56.568542500724995, ], inf),
\b4, Pshuf([-56.5685424775202, -0.0, -56.568542500724995, -80.0, ], inf),
\b5, Pshuf([-0.0, -56.568542500724995, -80.0, -56.5685424775202, ], inf),
\b6, Pshuf([-56.568542500724995, -80.0, -56.5685424775202, -0.0, ], inf),
\b7, Pshuf([-80.0, -56.5685424775202, -0.0, -56.568542500724995, ], inf),
\b8, Pshuf([-56.5685424775202, -0.0, -56.568542500724995, -80.0, ], inf),
).play;

s.meter;

(
SynthDef(\isnare, { | outBus=0, cBus1=1, gain=0.5, freq=200, osc1=330, osc2=180, tri=111, noise=0.1, position=0, disperse=0, impf=1, dur=1, distance=0 |
    var snd, rev;
    var env = Env([0, 1, 0], [0.0001, 0.5]);
    snd = IDispersedSnare.ar(position, disperse, tempo: impf) * EnvGen.kr(env, timeScale: 0.08, doneAction: Done.freeSelf);
    rev = FreeVerb.ar(snd * gain, mix: distance, room: 1, damp: 0.5);
    Out.ar(outBus, snd);
}).add;
)

~sisnare = Synth.new("isnare", [\position, 29, \disperse, 0, \impf, 10000, \dur, 0.1] );

(
a = Array.fill(16, { arg i; (i + 1) / 16 });
Pbind (\instrument, \isnare,
    \position, Pseq((0..1), inf),
    \disperse, 0.0,
    \impf, 0, //Pfunc({30.rand2}),
    \osc1, Pseq([330,320,310,300,290,250,220,250,280,300,320], inf),
    \gain, Pseq(a, 16),
    \distance, Pseq(a.reverse, 16),
    \dur, 1
).play;
)

(
Pbind (\instrument, \isnare,
    \position, 1,
    \distance, Pseq([0, 0.1, 0.2, 0.3, 0.4, 0.5], 10),
    \disperse, 0,
    \impf, 10000,
    \dur, 1,
    \delta, 0
).play;
)

s.meter;

a = Array.fill(16, { arg i; (i + 1) - 1 });

//////////////////////////////
// Convolution
//////////////////////////////

// preparation; essentially, allocate an impulse response buffer, then
// follow a special buffer preparation step to set up the data the plugin needs.
// Different options are provided commented out for loading impulse responses from soundfiles.
(
​
 // also 4096 works on my machine; 1024 too often and amortisation too pushed, 8192 more high load FFT
~fftsize = 2048;
​
s.waitForBoot {
    ​
    {
        var ir, irbuffer, bufsize;
        ​
        // // MONO ONLY
        // pre-existing impulse response sound files
        // (could also use any general soundfile too for cross-synthesis effects)
        // irbuffer = Buffer.read(s, "/Volumes/data/audio/ir/ir2.wav");
        ​
        // synthesise the honourable 'Dan Stowell' impulse response
        ​
        ir = [1] ++ 0.dup(100) ++ (
            (1, 0.99998 .. 0)
            .collect {|f|
                f = f.squared.squared;
                f = if(f.coin) { 0 }{ f.squared };
                f = if(0.5.coin) { 0 - f } { f }
            } * 0.1
        );
        ir = ir.normalizeSum;
        ​
        irbuffer = Buffer.loadCollection(s, ir);
        ​
        s.sync;
        ​
        bufsize = PartConv.calcBufSize(~fftsize, irbuffer);
        ​
        // ~numpartitions= PartConv.calcNumPartitions(~fftsize, irbuffer);
        ​
        ~irspectrum = Buffer.alloc(s, bufsize, 1);
        ~irspectrum.preparePartConv(irbuffer, ~fftsize);
        ​
        s.sync;
        ​
        irbuffer.free; // don't need time domain data anymore, just needed spectral version
    }.fork;
​}
)
​
~target = Buffer.read(s, Platform.resourceDir +/+ "sounds/a11wlk01.wav");

(
{
    var input = PlayBuf.ar(1, ~target, loop:1);
    var rev = PartConv.ar(input, ~fftsize, ~irspectrum.bufnum, 0.5);
    var mix = (input * 0) + (rev * 1);
    Out.ar(0, mix);
}.play
)

(
{
    PlayBuf.ar(1, ~target, loop:1);
}.play
)