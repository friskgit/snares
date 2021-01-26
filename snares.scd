(
(
~snare_simple = SynthDef(\snare, {
    var snd;
    var env = Env([0, 1, 0], [0.0001, \length.ir(0.5)]);
    var imp = Impulse.ar(\impf.ir(1), 0.0, 0.5, 0);
    var frq1=\freq.kr(300), frq2=frq1-100;
    snd = OGenericSnarefs.ar(imp, 0.00001, 0.001, \noiselvl.kr(0.1), \nrel.ir(0.1), frq1, frq2, \rel.ir(0.1), \trifrq.kr(111)) * EnvGen.kr(env, doneAction: Done.freeSelf);
    Out.ar(\out.ir(0), snd);
}).add;
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
var multiplier = 1.1; //the multiplier, lower than one for deaccelerando
var seq = { |length=4|
	{ |x=0| x+1; if(x==0, {1.0}, {0.1})} ! length;
};

~tClock = TempoClock(1); //start time is 1
~crntBeatsPerBar = 2;
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
)