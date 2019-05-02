(
ServerOptions.devices;
s = Server.local;
Server.local = Server.default;
o = Server.local.options; // Get the local server's options
// o.device = "JackRouter";
o.device = "Built-in Output";
o.numInputBusChannels = 2; // Set Input to number of Inputs
o.numOutputBusChannels = 2; // lets start after chan 36 so as not to see the mic input
o.numAudioBusChannels = 16;
o.blockSize = 512;
o.numWireBufs = 1024 * 16;
o.memSize = 2.pow(18);
o.sampleRate = 48000;
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
).play(clock: tClock);
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

