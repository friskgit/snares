b(
ServerOptions.devices;
s = Server.local;
Server.local = Server.default;
o = Server.local.options; // Get the local server's options
//o.device = "JackRouter";
o.device = "Built-in Output";
o.numInputBusChannels = 2; // Set Input to number of Inputs
o.numOutputBusChannels = 2; // lets start after chan 36 so as not to see the mic input
o.numAudioBusChannels = 16;
o.blockSize = 512;
o.numWireBufs = 1024 * 16;
o.memSize = 2.pow(18);
o.sampleRate = 44100;
s.makeWindow;
s.boot;
)

//////////////////////////////
// Snare synth
//////////////////////////////
(
SynthDef(\snare, { | gain=0, freq=200, vol=1 |
    var snd;
    var env = Env([0, 1, 0], [0.0001, 0.5]);
    snd = Snare.ar(0.00001, freq, gain, 1, 10, 0.01, vol) * EnvGen.kr(env, doneAction: Done.freeSelf);
    Out.ar(0, snd);
}).add;
)

//////////////////////////////
// Player
//////////////////////////////

Pdefn(\x, Pbrown(0, 6, 0.1, inf));
Pbind(\note, Pdefn(\x), \dur, 0.3).play;
Pbind(\note, Pdefn(\x), \dur, 0.1, \ctranspose, 15).play;
// now change the definition
Pdefn(\x, Pseq([0, 3, 5, 7, 9, 11], inf));
Pdefn(\x, Pseq([0, 3, 3, 7], inf) + Pseq([0, [0, 3], [0, 5, 7]], inf));


// Stream of notes
(
~stream = Pbind(
    \instrument, \snare,
    \gain, 0.1,
    \dur, 0.25
).play;
)
~stream.stop;

// Accents
(
Pdefn(\duration, Pn(0.5, inf));
Pdefn(\accents, Pseq([1, 0.1, 0.1, 0.1, 0.1], inf));
~accent = Pbind(
    \instrument, \snare,
    \gain, Pdefn(\accents),
    \dur, Pdefn(\duration)
);
)

Pdefn(\accents, Pseq([1, 0.1], inf));
~accent.play(quant: 0);
// Stream with accents
Ppar([~stream, ~accent]).play;

// Change accent
Pdefn(\duration, Pn(0.5, inf));

~tClock = TempoClock(1); //start time is 1
// Make changes to the parameters of the Pbind.
Pdef(\x, Pbind(\instrument, \snare, \gain, Pseq([1, 0.1, 0.1, 0.1], 40)));
Pdef(\x).quant = 0;
Pdef(\x).play(~tClock);
Pdef(\x).play;
Pdef(\x).stop;
Pdef(\x).source.postcs;
Pdef(\x, Pbind(\instrument, \snare, \dur, Pseq([0.3, 0.1, 0.5], inf), \gain, Pseq([1, 0.1, 0.5], inf)));

Pdef(\x, Pbind(\instrument, \snare, \dur, Pseq([0.1, 0.5, 1])));
Pbindf(Pdef(\a), \dur, 0.1).play;
Pbindf(Pdef(\a), \dur, 0.1, \ctranspose, 15).play;

// Accelerando
(
var multiplier = 1.1; //the multiplier, lower than one for deaccelerando
var seq = { |length=4|
    { |x=0| x+1; if(x==0, {1.0}, {0.1})} ! length;
};

~tClock = TempoClock(1); //start time is 1
~crntBeatsPerBar = 4;
~accent.play(quant: 0, clock: ~tClock);
fork {
    loop {
        var barDur = 4; // Duration in seconds
        var beatsPerBar = ~crntBeatsPerBar;
        ~tClock.tempo = ~tClock.tempo * multiplier;
        "Tempo:".postln;
        ~tClock.tempo.postln;
        if((~tClock.beatDur*(beatsPerBar+1)) < barDur,
            {
                var beatsToAdd = 1;
                "Length of bar + 1 beat".postln;
                (~tClock.beatDur*(beatsPerBar+1)).postln;
                (
                    i = 1;
                    while( {(~tClock.beatDur*(beatsPerBar+i)) < barDur }, {i = i+1; beatsToAdd = i});
                );
                beatsToAdd.postln;
                ~crntBeatsPerBar = beatsPerBar + beatsToAdd;
                Pdefn(\accents, Pseq(seq.value(~crntBeatsPerBar), 1))
            },
            {
                "Length of bar".postln;
                (~tClock.beatDur*beatsPerBar).postln;
                ((~tClock.beatDur*beatsPerBar)/~tClock.beatDur).postln
            });
        (~tClock.beatDur*~crntBeatsPerBar).wait;
        ~accent.play(quant: 0, clock: ~tClock);
    }
};
)
(
var seq = { |length=4|
    { |x=0| x+1; if(x==0, {1.0}, {0.1})} ! length;
};

~accent = Pbind(
    \instrument, \snare,
    \gain, Pdefn(\accents),
    \dur, Pdefn(\duration)
);
Pdefn(\accents, Pseq(seq.value(4), 1));
Pdefn(\duration, 1);
)

(
var tmp = TempoClock(2);
tmp.tempo = 1;
~accent.play(clock: tmp);
)

(
c = TempoClock.new;
fork {
    loop {
        c.beats.postln; // updates, because ".wait" calls the thread
        1.wait;
    }
};
)

(
// Create an array of length. Call like this: seq.value(6); 
var seq = { |length=4|
    { |x=0| x+1; if(x==0, {1.0}, {0.1})} ! length;
};
seq.value(6);
)

(
~sis = Pbind(
    \instrument, \snare,
    \gain, Pseq([0.1, 0.1, 0.1, 0.1], 40),
    \dur, 0.2; 
).play(clock: Clock);
)
)

(
~bro.play;
~sis.play;
)
// Updates tempo every four beats
(
~bro = Pbind(
    \instrument, \snare,
    \gain, Pseq([1, 0.1, 0.1, 0.1], 10),
    \dur, Pstutter(4, Pseries(10, -1, 10)),
).play;
)

// sequence two bro's
Pseq([~bro, ~bro], 1).play;

~bro.stop;
~bro.play;

TempoClock.default.tempo = 1;

// stuff
(Pwhite(48, 72, inf) +.x Pseq(#[0, 4, 7, 11], 1)).asStream.nextN(5);
p.stop;

Pstutter(4, Pseries(1, -0.1, 10)).asStream.nextN(20);

Pstutter(4, 1 / (Pseries(1, 1, 10))).asStream.nextN(20);

Pn(Pseries(10, -1, 10) * 0.1, 4).asStream.nextN(20);

Pn(Pseries(1, 0, 4), 4).asStream.nextN(10);

p = (Pseries(10, -1, 10) * 0.1).asStream;
Pn(Pn(p.next, 4), 4).asStream.nextN(20);

Pwhite(1, 5, inf).asStream.nextN(10);

5.do({ arg i; i.postln});

Pn(5.do, 4).asStream.nextN(20)

///////////////////////////////
// Plambda
//////////////////////////////

p = Plambda(
    Pbind(
        \a, Plet(\z, Pseries(0, 1, inf), Pseries(100, -1, inf)),
        \b, Pget(\z, 0, inf) * 2
    ).trace(key: \eventScope, prefix: "\nscope: ")
).asStream;

p.next(());

///////////////////////////////
// Events
//////////////////////////////

(
Event.addEventType(\happyEvent, {
    "I am so happy to be silent sometimes, says %.\n".postf(~who)
})
)
(type: \happyEvent, who: "Alice").play;

// using protoEvent
(
Event.addEventType(\happyEvent, {
    "I am so happy to be silent sometimes, says %.\n".postf(~who)
}, (who: "Alice"))
)

(type: \happyEvent).play; // use default
(type: \happyEvent, who: "Eve").play; // overrride default

// in a Pbind:
Pbind(\type, \happyEvent, \who, Prand(["Alice", "Bob", "Eve"], inf), \dur, Pwhite(0.1, 1.0, inf)).play;

// parent event type for indirect calls:
Event.addEventType(\test, { ("x was" + ~x.value).postln }, (x: { ~y + 1 }, y: 0));

(type: \test).play; // use defaults
(type: \test, \y: 7).play; // set the value that x refers to
(type: \test, \x: 10).play; // override x by a different one


// It is possible to reuse some of another event type's functionality:
(
Event.addEventType(\happyEvent, { |server|
    ~octave = [5, 6, 7]; // always play three octaves
    ~detune = 10.0.rand2; // always play a bit out of tune
    ~type = \note; // now set type to a different one
    currentEnvironment.play;
});

Pbind(\type, \happyEvent, \degree, Pseq([0, 1, 2, 3, 4, 4, 5, 5, 5, 5, 4, 2, 3, 2, 3, 1], inf), \dur, Pwhite(0.1, 1.0, inf)).play;
);


Pdef(\x, Pbind(\note, Pbrown(0, 6, 0.1, inf)));
Pdef(\x).quant = 0; // no waiting.
Pbindf(Pdef(\x), \dur, 0.03).play;
Pbindf(Pdef(\x), \dur, 0.1, \ctranspose, 15).play;
Pbindf(Pdef(\x), \dur, 0.3, \ctranspose, 2).play;
// now change the definition
Pdef(\x, Pbind(\note, Pseq([0, 3, 5, 7, 9, 11], inf)));
Pdef(\x, Pbind(\freq, Pseq([1000, 1923, 245.2, 1718] / 1.2 + 0.1, inf)));

/// Pdef help
(
SynthDef(\Pdefhelp, { arg out, freq, sustain=1, amp=1, pan;
    var env = EnvGen.kr(Env.perc(0.01, sustain), 1, doneAction: Done.freeSelf);
    Out.ar(out, Pan2.ar(SinOsc.ar(freq, 0.5pi, amp * env), pan));
}).add;
)

// function as an argument, create a new pattern each time it is called
Pdef(\x, { Pbind(\note, Pseries(10.rand, 5.rand, 8.rand + 1), \dur, 1 / (8.rand + 1)) });
Pn(Pdef(\x)).play;

// the function is called in the incoming event as current environment, so parameters can be passed:
Pdef(\stut, { Pstutter(~stutter ? 1, ~pattern) });
Pdef(\x, Pbind(\instrument, \Pdefhelp, \note, Pseq([0, 4, 7, 3, 0, 1, 0], inf)));
Pdef(\y, Pdef(\stut) <> (pattern: Pdef(\x), stutter: Pseq([2, 2, 4, 3], inf)) <> (dur: 0.1, legato: 0.2)).play;

// tempo stuff
(
SynthDef(\bleep,{ arg out=0, note=60, amp=0.5, pan=0.0;
	var freq, env; 
	freq = note.midicps;
	env = EnvGen.ar(
					Env([0,1,1,0],[0.01, 0.1, 0.2]),
					levelScale:amp, 
					doneAction:2
				);
	Out.ar(out,
		Pan2.ar(Blip.ar(freq) * env, pan)
	)
}).add;
)



var u;
u = TempoClock(2);
(
u.schedAbs(0.0, { arg beat, sec; 
		Synth(\bleep, [\note, rrand(60.0,67.0)]);   
		0.5
});
)
u.schedAbs(8.0, { u.tempo_(2); nil }); // just schedule tempo change
u.schedAbs(12.0, { u.tempo_(7); nil }); // just schedule tempo change
u.schedAbs(17.2, { u.tempo_(1); nil }); // just schedule tempo change
SystemClock.sched(7.0, { u.clear; }); // schedule a stop for 7 seconds from now.

(
var updateRate = 1;
{
	10.do {
        u.tempo = u.tempo * 1.1;
        u.tempo.postln;
		updateRate.wait;
	};
}.fork;
)

var acc = Routine({
    u.tempo = u.tempo * 1.1
    
})
(
10.do {    
    u.tempo = u.tempo * 1.1;
    u.tempo.postln;
    1.wait;
}
)


f = { 3.yield };
x = Routine({
    delta = 1;
    f.loop;
    delta.yield; });
10.do({ x.next.postln })


{ Poll.kr (Impulse.kr(10), Line.kr(0, 1, 5)) }.play;

{ Poll.kr (Impulse.kr(10), Phasor.kr(Impulse.kr(0.01), 1.0, -pi, pi, 0)) }.play;
    
{ Poll.kr(Impulse.kr(10), Sweep.kr(0, 1)) }.play(s);


(
SynthDef(\lineReset, { |out, start= 0, end= 1, dur= 1, t_trig= 1, run= 1|
    var phasor = Sweep.ar(t_trig, run / dur).linlin(0, 1, start, end, \minmax);
    phasor.poll;
    Out.ar(out, SinOsc.ar(phasor, 0, 0.2));
}).add;
)
a = Synth(\lineReset, [\start, 400, \end, 800, \dur, 2])
a.set(\t_trig, 1)
a.set(\run, 0)
a.set(\run, 1)
a.set(\t_trig, 1)
a.free

//shorter duration and downwards...
a= Synth(\lineReset, [\start, 1000, \end, 500, \dur, 0.5])
a.set(\t_trig, 1)
a.set(\run, 0)
a.set(\run, 1)
a.set(\t_trig, 1)
a.free

List[0.1, 0.2, 0.3].at(2).postln;

a = Array.iota(0, 10).normalize;
a.postln;
a.normalize;

(0..100).normalize;

Array.fill([2, 2, 3], { arg i, j, k;  i * 100 + (j * 10) + k });
Array.fill(10, { arg i; i * 0.1 });

a.postln;
a.do({ arg item; item * 0.1; });

['a', 'b', 'c'].do({ arg item, i; [i, item].postln; });

[(1..10)].do({ arg item, i; [item/10].postln; });

(
var a, x;
a = Pfunc({ exprand(0.1, 2.0) + #[1, 2, 3, 6].choose }, { \reset.postln });
x = a.asStream;
x.nextN(20).postln;
x.reset;
)

(
SynthDef("help-out", { arg out=0, freq=440;
    var source, source2;
        source = SinOsc.ar(freq, 0, 0.1);
source = SinOsc.ar(freq+10, 0, 0.1);
        // write to the bus, adding to previous contents
        Out.ar(out, [source, source2]);

}).add;
)


Synth("help-out", [\freq, 500]);
Synth("help-out", [\freq, 600]);
Synth("help-out", [\freq, 700]);

(

	SynthDef.new("tutorial-SinOsc-stereo", { var outArray;

		outArray = [SinOsc.ar(440, 0, 0.2), SinOsc.ar(700, 0, 0.2)];

		Out.ar(0, outArray); // writes to busses 0 and 1 

	}).play;

)

Line.ar(start: 0, end: 1, dur: 1, mul: 1, add: 0, doneAction: 0).plot;

(
{
    //    
    // var mod = Line.kr(1, 10, 10);
    // LinExp.kr(mod, -1,1, 100, 900);
        Linen.kr(Impulse.kr(0), 0.01, 0.6, 1.0, doneAction: Done.freeSelf)
}.plot;
)

{ LinLin.kr(Phasor.kr(Impulse.kr(2), 2/ControlRate.ir), 0, 1, -pi, pi); }.plot(duration: 1);

(
{ var trig, rate, x, sr;
    trig = Impulse.ar(rate);
    sr = SampleRate.ir;
    x = Phasor.ar(trig, rate / sr);
}.plot(duration: 1);
)

(
{
    EnvGen.kr(
        Env(
            levels: [0, 1], 
            times: [1],
            curve: \lin,
            releaseNode: 0,
            loopNode: 1,
        ),
        gate: Impulse.kr(1),
        levelScale: 2pi,
        levelBias: -pi,
        timeScale: 1,
        doneAction: 0
    );
}.plot(duration: 1);
)



// Envelope in Pbind
(
Pbind(
    \note,  Pseg( Pseq([1, 5],inf), Pseq([4, 1],inf), \sin),
    \dur, 0.1
).play;
)

(
r = Routine({
    var delta;
    loop {
        [60, 62, 64, 65, 67, 69, 71, 72].do({ |midi|
midi.postln;
        "Will wait ".post; midi.postln;
        1.yield;
        })
    }
});
)


r.next;

TempoClock.default.sched(0, r);

r.stop;

(
t = Task({
    loop {
        [60, 62, 64, 65, 67, 69, 71, 72].do({ |midi|
midi.postln;
            0.125.wait;
        });
    }
}).play;
)

r = Routine { "hi".yield; "bye".yield };
r.next.postln;    // "hi" posted

q = r.p.asStream; // or just: r.p.iter
q.next.postln;    // "hi" again

r.next.postln;    // "bye" because r kept its own state, separate from q

(
r = Routine { arg inval;
    loop {
        // thisThread refers to the routine.
        postf("beats: % seconds: % time: % \n",
            thisThread.beats, thisThread.seconds, Main.elapsedTime
        );
        1.0.yield;

    }
}.play;
)

r.stop;
r.beats;
r.seconds;
r.clock;

r = Routine {

}

r.next;

(
r = Routine {
        199.yield;
        189.yield;
        Routine { 100.do { |i| i.yield } }.idle(6);
        199.yield;
        189.yield;
};

fork {
    loop {
        r.value.postln;
        1.wait;
    }
}
);

(

{

	

	4.do {
        "hej".postln;
		1.0.wait;	
	};

	

}.fork; 	

)

(
b = Bus.control(s, 1);

x = SynthDef(\snaredisp4, { | dur=20, out=0, pos=0, disp=0, pulse=2000, att=0.00001, n_attack=0.01, n_level=0.1, n_rel=0.01, osc1_f=100, osc2_f=130, release=0.01, tri_f=300 |
    var snd, env;
    env = Env.new(levels: [0, 1, 1, 0], times: [0.01, dur, 0.01]);
    snd = IDispersedSnare.ar(pos, disp, pulse, att, n_attack, n_level, n_rel, osc1_f, osc2_f, release, tri_f) * EnvGen.kr(env, doneAction: Done.freeSelf);
    Out.ar(out, snd);
}).play(s);
)

(
x.map(\pos, b);
y = { Out.kr(b, SinOsc.kr(0.1, 0, 10, 10))};
y.play(addAction: \addToHead);
s.meter;
)