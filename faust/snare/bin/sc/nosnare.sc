Nosnare : MultiOutUGen
{
  *ar { | attack(1.0e-08), rel(0.1), tempo(5000.0) |
      ^this.multiNew('audio', attack, rel, tempo)
  }

  *kr { | attack(1.0e-08), rel(0.1), tempo(5000.0) |
      ^this.multiNew('control', attack, rel, tempo)
  } 

  init { | ... theInputs |
      inputs = theInputs
      ^this.initOutputs(4, rate)
  }

  name { ^"Nosnare" }


  info { ^"Generated with Faust" }
}

