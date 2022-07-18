(
var select, spat_positions, task;
(
SynthDef("test", { arg freq = 440, out = 10, amp = 0.2;
    Out.ar(out, SinOsc.ar(freq, 0, amp));
}).add;
);

f = { arg fund=200, sel = [1,2], delta = 2;

        var harmonics, amp, index = 0;

        /* Fill arrays for pitches */
        harmonics = Array.fill(32, { arg i; i*fund; });
        amp = Array.fill(32, { arg i; 1/(i+1); });
        // amp = Array.fill(32, { arg i; 1; });	    

        /* Run the loop */
        t = Task({
                (1..32).do({ |pulse|
                        var bus;
                        if(pulse == sel[index],
                                { index = index + 1;
                                        bus = Bus.audio(s, 1);
                                        ~audioBusses.add(bus);
                                        Synth("test", [\freq, harmonics[pulse], \amp, amp[pulse], \out, bus]);
                                        Post << "Harmonic " <<< pulse << ": " <<< harmonics[pulse] <<  ", " <<< amp[pulse] << " at Audio bus " <<< bus.index << Char.nl;
                                },
                                {  });
                        delta.wait;
                });
        });
        /* Return the task */
        t;
};

~allRings = [[-24.036688, 23.800417], [21.279257, 23.800417], [55.741675, 23.800417], [101.49442, 23.800417], [147.91193, 23.800417], [-167.82013, 23.800417], [ -123.784, 23.800417], [-79.887731, 23.800417], [ 0.247203, 56.476405], [69.013292, 56.476405], [158.89992, 56.476405], [-114.65354, 56.476405], [-90, 86.424489], [90, 86.424489]];

//spat = Array.fill(sel.size, { 0; });
//select = [5,6,7,8,9,10];
//spat_positions = [5,6,7,8,9,10 ];
select = [ 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25];
spat_positions = [0, 1, 5, 6, 3, 2, 7, 4, 9, 11, 8, 10];
~audioBusses = Array.new(select.size);
~spatializations = Array.new(spat_positions.size);
if(select.size == spat_positions.size,
        {
                i = 0;	
                while( {i < select.size},
                        { ~spatializations.add(~allRings[spat_positions[i]]);
                                i = i + 1;
                        });
        },
        { "'sel' and 'spat' have unequal number of elements".postln; });

/* Call the harmonics function and play it.*/
task = f.value(300, select, 0.1);
task.play;
)

~allRings = [[-24.036688, 23.800417], [21.279257, 23.800417], [55.741675, 23.800417], [101.49442, 23.800417], [147.91193, 23.800417], [-167.82013, 23.800417], [ -123.784, 23.800417], [-79.887731, 23.800417], [ 0.247203, 56.476405], [69.013292, 56.476405], [158.89992, 56.476405], [-114.65354, 56.476405], [-90, 86.424489], [90, 86.424489]];

~spkrArray = VBAPSpeakerArray.new(3, [[ -34.689614 , 12.910417 ], [ -13.383763 , 12.910417 ], [ 10.440725 , 12.910417 ], [ 32.117788 , 12.910417 ],
        [ 55.741675 , 12.910417 ], [ 78.207673 , 12.910417 ], [ 101.49442 , 12.910417 ], [ 124.85167 , 12.910417 ],
        [ 147.91193 , 12.910417 ], [ 169.17789 , 12.910417 ], [ -167.82013 , 12.910417 ], [ -145.63454 , 12.910417 ],
        [ -123.784 , 12.910417 ], [ -102.64182 , 12.910417 ], [ -79.887731 , 12.910417 ], [ -57.926139 , 12.910417 ],
        [ -22.349553 , 34.696822 ], [ 22.843958 , 34.696822 ], [ 69.013292 , 34.696822 ], [ 115.56544 , 34.696822 ],
        [ 158.89992 , 34.696822 ], [ -158.89763 , 34.696822 ], [ -114.65354 , 34.696822 ], [ -68.170128 , 34.696822 ],
        [ -45 , 69.185799 ], [ 45 , 69.185799 ], [ 135 , 69.185799 ], [ -135 , 69.185799 ], [ 0 , 90 ]]);
~spkrArray.speakers[1].dump;
~spkrBuffer = Buffer.loadCollection(s, ~spkrArray.getSetsAndMatrices);

~spatChannels = Array.new(~audioBusses.size);
~spatNdefs = Array.new(~audioBusses.size);
~controlBus = Array.new(~audioBusses.size);
~audioBusses.do({ arg bus, i;
        m = "ch" ++ i.asString.asSymbol;
        n = Bus.control(s, 2);
        ~controlBus.add(n);
        ~spatNdefs.add(
                Ndef.new(m, { arg azi = 0, ele = 0, spr = 0;
                        VBAP.ar(29, In.ar(bus), ~spkrBuffer.bufnum, In.kr(n), In.kr(n.index + 1), spr)}););
        Ndef(m).play(addAction: \addToTail);
});
~spatNdefs.do({ arg channel, i;
        var ae, as, es = 0, ee = 1;
        as = channel.get(\azi);
        es = channel.get(\ele);
        ae = ~spatializations[i][0];
        ae = ~spatializations[i][1];
        ~interPan.value(i, ~controlBus[i], as, ae, es, ee);
});
~interPan = { arg int, bus, astart, aend, estart, eend;
        {Out.kr(bus.index, Line.kr(astart, aend, 4, doneAction: 2))}.play(addAction: \addToHead);
        {Out.kr(bus.index + 1, Line.kr(estart, eend, 4, doneAction: 2))}.play(addAction: \addToHead);	
};
~spatNdefs.do({ arg channel, i;
        var ae, as, es = 0, ee = 1;
        as = channel.get(\azi);
        es = channel.get(\ele);
        ae = ~spatializations[i][0];
        ae = ~spatializations[i][1];
        ~interPan.value(i, ~controlBus[i], as, ae, es, ee);
});

~interPan = { arg int, bus, astart, aend, estart, eend;
        {Out.kr(bus.index, Line.kr(astart, aend, 4, doneAction: 2))}.play(addAction: \addToHead);
        {Out.kr(bus.index + 1, Line.kr(estart, eend, 4, doneAction: 2))}.play(addAction: \addToHead);	
};

~spatNdefs.do({ arg channel, i;
        var ae, as, es = 0, ee = 1;
        as = channel.get(\azi);
        es = channel.get(\ele);
        ae = ~spatializations[i][0];
        ae = ~spatializations[i][1];
        ~interPan.value(i, ~controlBus[i], as, ae, es, ee);
});

var spat_positions =  Array.rand(12, 0, 13);
~spatializations.do({ arg item, i;
        ~spatializations.put(i, ~allRings[spat_positions[i]]);
        "Putting ".post;  spat_positions[i].post; " at index ".post; i.postln;
});
~spatNdefs.do({ arg channel, i;
        var ae, as, es = 0, ee = 1;
        as = channel.get(\azi);
        es = channel.get(\ele);
        ae = ~spatializations[i][0];
        ae = ~spatializations[i][1];
        ~interPan.value(i, ~controlBus[i], as, ae, es, ee);
});

~spkrArray = VBAPSpeakerArray.new(3, [[ -34.689614 , 12.910417 ], [ -13.383763 , 12.910417 ], [ 10.440725 , 12.910417 ], [ 32.117788 , 12.910417 ],
        [ 55.741675 , 12.910417 ], [ 78.207673 , 12.910417 ], [ 101.49442 , 12.910417 ], [ 124.85167 , 12.910417 ],
        [ 147.91193 , 12.910417 ], [ 169.17789 , 12.910417 ], [ -167.82013 , 12.910417 ], [ -145.63454 , 12.910417 ],
        [ -123.784 , 12.910417 ], [ -102.64182 , 12.910417 ], [ -79.887731 , 12.910417 ], [ -57.926139 , 12.910417 ],
        [ -22.349553 , 34.696822 ], [ 22.843958 , 34.696822 ], [ 69.013292 , 34.696822 ], [ 115.56544 , 34.696822 ],
        [ 158.89992 , 34.696822 ], [ -158.89763 , 34.696822 ], [ -114.65354 , 34.696822 ], [ -68.170128 , 34.696822 ],
        [ -45 , 69.185799 ], [ 45 , 69.185799 ], [ 135 , 69.185799 ], [ -135 , 69.185799 ], [ 0 , 90 ]]);
~spkrArray.speakers[1].dump;
~spkrBuffer = Buffer.loadCollection(s, ~spkrArray.getSetsAndMatrices);
