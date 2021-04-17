f = { arg mul=1, tpo=1, player=~accent;
	var multiplier = mul; //the multiplier, lower than one for deaccelerando
	var tClock, beatsPB=2;
	var seq = { |length=4|
		{ |x=0| x+1; if(x==0, {1.0}, {rrand(0.2, 0.27)})} ! length;
	};

	tClock = TempoClock(tpo); //start time is 1
	player.play(quant: 0, clock: tClock);
	fork {
		loop {
			var barDur = 4; // Duration in seconds
			var beatsPerBar = ~beatsPerBar;
			tClock.tempo = tClock.tempo * multiplier;
			"Tempo:".postln;
			tClock.tempo.postln;
			if((tClock.beatDur*(beatsPerBar+1)) < barDur,
				{
					var beatsToAdd = 1;
					"Length of bar + 1 beat".postln;
					(tClock.beatDur*(beatsPerBar+1)).postln;
					(
						i = 1;
						while( {(tClock.beatDur*(beatsPerBar+i)) < barDur }, {i = i+1; beatsToAdd = i});
					);
					beatsToAdd.postln;
					beatsPerBar = beatsPerBar + beatsToAdd;
					Pdefn(\accents, Pseq(seq.value(beatsPerBar), 1))
				},
				{
					"Length of bar".postln;
					(tClock.beatDur*beatsPerBar).postln;
					((tClock.beatDur*beatsPerBar)/tClock.beatDur).postln
				});
			(tClock.beatDur*beatsPerBar).wait;
			player.play(quant: 0, clock: tClock);
		}
	};
}
