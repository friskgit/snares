(
// Settings
~signalBus = 52;
~reverbChan = 63;
~order = 3;
~binaural = 1;
~hoaNumChannels = (~order+1).pow(2);
~decoderNumChannels = 29;
//s.scope(~hoaNumChannels);

// Create the input bus and the encoder
~hoaSignal = NodeProxy.new(s, \audio, ~hoaNumChannels);
//~hoaSignal.play;

// Handling the reverb send
~reverbSend = NodeProxy.new(s, \audio, 1);

// hoaSignal.source och hoaSignal.add verkar fungera lika bra.
~hoaSignal[0] = {
  HOAEncoder.ar(~order,
        In.ar(~signalBus),
        \azpana.kr(-3.14),
        \elpana.kr(0),
        \gaina.kr(0),
        \planespherical.kr(1),
        \rada.kr(2),
        \speakerrad.ir(1.07))
};
~hoaSignal[1] = {
  HOAEncoder.ar(~order,
        In.ar(54),
        \azpanb.kr(-3.14),
        \elpanb.kr(0),
        \gainb.kr(0),
        \planespherical.kr(1),
        \radb.kr(2),
        \speakerrad.ir(1.07)) };

~hoaSignal.do({ arg obj, i;
  obj.fadeTime = 0.1;
});

// Merely mapping and scaling the audio
~reverbSend.source = { arg scale, ctrl;
  //    in = In.ar(~reverbChan);
  //  in = in * \reva.kr(0);
  ctrl = \reva.kr(0) / 20;
  scale = ControlSpec(0, 1, \lin).map(ctrl);
  In.ar(~signalBus) * scale;
};
~reverbSend.play(~reverbChan, 1);

/* Create windows for panning */
~windows = Environment(know: true);

// With phasor
// trig = Impulse.kr(0.001);
//~azimuthA.source = { LinLin.kr(Phasor.kr(Impulse.kr(0.5), 0.5/ControlRate.ir), 0, 1, 0, 360); };

// Load the decoder:
~decoder = NodeProxy.new(s, \audio, ~decoderNumChannels);
~decoder.fadeTime = 1;
if(~binaural == 0,
  //////////////////
  /* If decoded */
  {
        if(~order == 1, {
          "First order".postln;	
          ~decoder.source = {
                var in; in = \in.ar(0!~hoaNumChannels);
                in.add(-10);
                KMHLSDome1h1pNormal6.ar(*in);
          };
        });
        if(~order == 3, {
          "Third order".postln;
          ~decoder.source = {
                var in; in = \in.ar(0!~hoaNumChannels);
                in.add(-10);
                KMHLSDome3h3pNormal6.ar(*in);
          };
        });
        if(~order == 5, {
          "Fifth order".postln;
          ~decoder.source = {
                var in; in = \in.ar(0!~hoaNumChannels);
                in.add(-10);
                KMHLSDome5h5pNormal6.ar(*in);
          };
        });
        //////////////////
        /* If binaural */
  }, {
        "Binaural version".postln;
        HOADecLebedev26.loadHrirFilters (
          s,
          "/home/henrikfr/Dropbox/Music/faust/ambi/ambitools/FIR/hrir/hrir_christophe_lebedev50"
        );
        if(~order == 1, {
          ~decoder.source = {
                var in; in = \in.ar(0!~hoaNumChannels);
                HOADecLebedev06.ar(~order.asInteger, in, hrir_Filters:1)
          };
        });
        /* 3 and 5 are actually the same */
        if(~order == 3, {
          ~decoder.source = {
                var in; in = \in.ar(0!~hoaNumChannels);
                HOADecLebedev26.ar(~order.asInteger, in, hrir_Filters:1)
          };
        });
        /* 3 and 5 are actually the same */
        if(~order == 5, {
          ~decoder.source = {
                var in; in = in.ar(0!~hoaNumChannels);
                HOADecLebedev26.ar(~order.asInteger, in, hrir_Filters:1)
          };
        });
  });

  ~snareSimple = SynthDef(\snare, {
    var snd;
    var env = Env([0, 1, 0], [0.0001, \length.ir(0.5)]);
    var imp = Impulse.ar(\impf.ir(1), 0.0, 0.5, 0);
    var frq1=\freq.kr(300), frq2=frq1-100;
    snd = OGenericSnarefs.ar(imp, 0.00001, 0.001, \noiselvl.kr(0.1), \nrel.ir(0.1), frq1, frq2, \rel.ir(0.1), \trifrq.kr(111)) * EnvGen.kr(env, doneAction: Done.freeSelf);
    Out.ar(\out.kr(0), snd*\gain.ir(1));
  }).add;

)