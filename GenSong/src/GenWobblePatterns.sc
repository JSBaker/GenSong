/*	
====================================================================================
2011 | Jonathan Baker  | GenSong  |  http://www.jonny-baker.com | http://github.com/JSBaker

All source code licenced under The MIT Licence
====================================================================================  
*/

+ GenSong {

	chordPat  {|freq, timing|
	
	Pbind(
		\dur, Pseq(timing,1),
		\amp, 0.5,
		\freq, Pseq(freq,1),
		\instrument, \stabby
	).play(tempo);
	}
	
	// kick
	kickPat  {|pat|
	
	Pbind(
		\dur, Pseq(pat,1),
		\amp, 0.7,
		\bufnum, buffers[0],
		\instrument, \buff
	).play(tempo);
	
	}
	
	// synthesised snare
	snarePat  {
	Pbind(
		\dur, Pseq([1],2),
		\amp, Pseq([0,0.15],1),
		\instrument, \snare2
	).play(tempo);
	}

	// constant hat
	hatPat  {|pat|
	Pbind(
		\dur, Pseq(pat,1),
		\peak, 0.2,
		\shape, -20,
		\instrument, \hat
	).play(tempo);
	
	}

	// sampled hat
	offPat2  {
	Pbind(
		\dur, Pseq([0.5,0.25,0.25],2),
		\amp, Pseq([0,0.1,0.1,0],2),
		\bufnum, buffers[2],
		\instrument, \buff
	).play(tempo);
	}
	
	// fill hat
	offPat3  {
	Pbind(
		\dur, Pseq([1.25,0.25,0.25,0.25],1),
		\amp, Pseq([0,0.6,0.6,0.6,0],1),
		\bufnum, buffers[4],
		\instrument, \buff
	).play(tempo);
	}
	
	// fill snare
	snarePat2  {
	Pbind(
		\dur, Pseq([1.375,0.25,0.25,0.125],1),
		\amp, Pseq([0,0.6,0.6,0.6,0],1),
		\bufnum, buffers[5],
		\instrument, \buff
	).play(tempo);
	}
	
//	// offbeat hat
//	offPat = {
//	Pbind(
//		\dur, Pseq([0.5],4),
//		\peak, Pseq([0,0.4],2),
//		\hatfreq, 5000,
//		\instrument, \hat
//	).play(tempo);
//	};
	
	// offbeat hat
	offPat  {|cycles = 4, dpat, ppat, freqs|
	Pbind(
		\dur, Pseq(dpat,cycles),
		\peak, Pseq(ppat,(cycles/2)),
		\hatfreq, Pseq(freqs,(cycles/2)),
		\instrument, \hat
	).play(tempo);
	}
	
	// offbeat ride
	ridePat  {
	Pbind(
		\dur, Pseq([0.5],2),
		\amp, Pseq([[0,0.15,0.25].wchoose([0.4,0.3,0.3]),0.3],1),
		\bufnum, buffers[6],
		\instrument, \buff
	).play(tempo);
	}
	
	// cymbal
	cymPat  {
	Pbind(
		\dur, Pseq([[2],[1.5,0.5]].wchoose([0.8,0.2]),1),
		\amp, 0.5,
		\bufnum, buffers[3],
		\instrument, \buff
	).play(tempo);
	}
}