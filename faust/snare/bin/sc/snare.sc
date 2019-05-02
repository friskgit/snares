Snare : MultiOutUGen
{
  *ar { | attack(1.0e-08), freq(200.0), gain(0.0), gate(0.0), q(1.0), rel(0.1), vol(0.5) |
      ^this.multiNew('audio', attack, freq, gain, gate, q, rel, vol)
  }

  *kr { | attack(1.0e-08), freq(200.0), gain(0.0), gate(0.0), q(1.0), rel(0.1), vol(0.5) |
      ^this.multiNew('control', attack, freq, gain, gate, q, rel, vol)
  } 

  init { | ... theInputs |
      inputs = theInputs
      ^this.initOutputs(4, rate)
  }

  name { ^"Snare" }


  info { ^"Generated with Faust" }
}

