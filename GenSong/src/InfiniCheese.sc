/*	
====================================================================================
2011 | Jonathan Baker  | GenSong  |  http://www.jonny-baker.com | http://github.com/JSBaker

All source code licenced under The MIT Licence
====================================================================================  
*/


	InfiniCheese	{
	
	classvar s, buffers,tempo;

	var scale;


	
	*new {|s,buff,tempo|
		
		^super.new.initInfiniCheese(s,buff,tempo)
		
	}
	
	initInfiniCheese {|server, buff,tmp|
	
		s = server ? Server.default;
		buffers = buff;
		tempo = tmp;
	
		{

	////////////////////////////////////	
	// SynthDefs
	////////////////////////////////////
	
	//kick drum
	
	SynthDef(\kik, { |basefreq = 50, envratio = 3, freqdecay = 0.02, ampdecay = 0.5, amp = 1|
	
	   	var filterenv, ampenv;
	   	
		  		filterenv = EnvGen.kr(Env([envratio, 1], [freqdecay], \exp), 1) * basefreq;
	      		ampenv = EnvGen.kr(Env.perc(0.005, ampdecay), 1, doneAction:2);
	      		
	   	Out.ar(0,Pan2.ar(SinOsc.ar(filterenv, 0.5pi, ampenv)*amp));
	}).send(s);
	
	//hit hat
	
	SynthDef(\hat, {|hatfreq = 13500, shape = -10, peak = 1|
	
			var sound;
			
				sound = BPF.ar(WhiteNoise.ar(EnvGen.ar(Env.perc(0.01,0.2,peak,shape),1, doneAction:2)),hatfreq);
				
		Out.ar(0,Pan2.ar(sound)) 
	}).send(s);
	
	
	
	//pingy synth
	
	SynthDef(\sawPing, {|freq, decay = 0.5, shape = -10|
	
		var sound;
	
			sound = Saw.ar([1,4,5]*freq, EnvGen.ar(Env.perc(0.01, decay, 0.2, shape),1, doneAction:2));
	
		Out.ar(0,Pan2.ar(sound));
	}).send(s);
	
	//bass synth
	
	SynthDef(\bassSaw, {|freq, amp = 0.12|
	
		var sound; 
		
			sound= LFSaw.ar([1,2,4]*freq,0,EnvGen.ar(Env.triangle(0.5,amp),1,doneAction:2));
			
		Out.ar(0,Pan2.ar(sound));
	}).send(s);


	//fm synth
	
	SynthDef(\chime, {|note = 36, modDepth = 5000, modRate = 600, shape = 4, att = 1, rel = 0.01|
	
		var sound;
	
			sound = SinOsc.ar(note.midicps +(modDepth*SinOsc.ar(modRate)),0,EnvGen.ar(Env.perc(att,rel,0.2,shape),1,doneAction:2));
	
		Out.ar(0,Pan2.ar(sound));
	}).send(s);
	
	
	
	//chordy synth
	SynthDef(\chord, {|note = 30, shape = -5|
	
		var sound, saw, env;
	
			env = EnvGen.ar(Env.perc(0.1,2,0.1,shape),1,doneAction:2);
			sound = LFPar.ar([1,2,4]*(note.midicps*4),0,env);
	
		Out.ar(0,Pan2.ar(sound));
	}).send(s);

	
	//snare
	
	SynthDef(\snare, {|amp = 0.5|
	
		var sound, env;
			sound = SinOsc.ar(110) - WhiteNoise.ar(0.5, 0.5);
			env = EnvGen.kr(Env.perc(0.001,0.4,1,-5),1,doneAction:2);
	
		Out.ar(0,Pan2.ar(sound*env));
	}).send(s);

	s.sync;
	
	}.fork;
	}


	drums {
		^{
			if(0.9.coin, // drums will play?
			{
				if(0.9.coin, // all drums will play, or just kick?
				{
					{
						8.do{
						if(0.9.coin, // kick on the first beat or skips and plays on second?
						{
							Synth(\kik, [\basefreq, 50, \freqdecay, 0.05, \ampdecay, 1]);
							0.5.wait;
			
								Synth(\hat, [\peak, 0.5]);
								if(0.1.coin, // kick plays on first and second?
								{
									Synth(\kik, [\basefreq, 50, \freqdecay, 0.05, \ampdecay, 1]);
									0.5.wait;
									},
								{
									0.5.wait;
									}
								)
							},
							{
								0.5.wait;
								Synth(\kik, [\basefreq, 50, \freqdecay, 0.05, \ampdecay, 1]);
								Synth(\hat, [\peak, 0.5]);
								0.5.wait
								}
						);
					
							Synth(\kik, [\basefreq, 50, \freqdecay, 0.05, \ampdecay, 1]);
							Synth(\snare);
							Synth(\hat, [\hatfreq, 2000, \shape, -1, \peak, 0.5]);
							0.5.wait;
						
							Synth(\hat, [\peak, 0.5]);
							0.5.wait;
							
						}
					}.fork(tempo);
					},
					
				{
					{
						16.do{
							Synth(\kik, [\basefreq, 50, \freqdecay, 0.05, \ampdecay, 1]);
							if(0.8.coin, // don't play on next half beat?
							{
								1.wait;
								},
							{
								0.5.wait;
								Synth(\kik, [\basefreq, 50, \freqdecay, 0.05, \ampdecay, 1]);
								0.5.wait;
								}
							);
						}
					}.fork(tempo)
				}
				)
				},
			{
				16.wait
				}
			)
		}.fork(tempo);
	}
	
	
	pingy {
	
		^{
			[0,1,2].wchoose([0.25,0.5,0.25]).do{ // will not play, play half or play all of phrase?
			var pingDec = rrand(0.3,0.9);
			
				Synth(\sawPing, [\freq, (scale[0].midicps)*8, \decay, pingDec]); // plays root on first beat
				0.5.wait;
				Synth(\sawPing, [\freq, (scale[0].midicps)*8, \decay, pingDec]);
				0.5.wait;
				
					{
						7.do{
						var note, pause;
						
						note = [0.25,0.5,0.75].choose; // next note to play
						
							Synth(\sawPing, [\freq, (scale.choose.midicps)*8, \decay, pingDec]);
							note.wait;
							Synth(\sawPing, [\freq, (scale.choose.midicps)*8, \decay, pingDec]);
							
							if(note <= 0.5, 
								{
									if (0.25.coin, // if first note short play another?
									{
										pause = 0.25*rrand(1,2);
										pause.wait;
										Synth(\sawPing, [\freq, (scale.choose.midicps)*8, \decay, pingDec]);
										
											if(pause == 0.25,
											{
												if(0.4.coin, // second note short play another?
												{
												0.25.wait;
												Synth(\sawPing, [\freq, (scale.choose.midicps)*8, \decay, pingDec]);
												(1.0-(pause+note+0.25)).wait
												},
													
												{
												(1.0-(pause+note)).wait
												}
												);
												},
											{
												(1.0-(pause+note)).wait
												}
											);
											
										(1.0-(pause+note)).wait
										},
									{	
										(1.0-note).wait
										}
									)
									},
								{
									(1.0-note).wait
									}
							)
						}
					}.fork(tempo);
			7.wait
			}
		}.fork(tempo);
	
	}
	
	
	chords {
		^{
			2.do{
				if(0.8.coin, // play at beginning or first or fifth bar?
					{
						Synth(\chord, [\note, scale.choose]);
						7.wait
						},
					{
						7.wait
						}
				);
				
				if(0.5.coin, // play halfway through third or seventh bar?
					{
						Synth(\chord, [\note, scale.choose]);
						1.wait
						},
					{
						1.wait
						}
				);
		
			}
		}.fork(tempo);
	
	}
	
	// method for playing bassline function
	bassline {
	
		^{
			if(0.9.coin,
			{
				{
					2.do{
						Synth(\bassSaw, [\freq, scale[0].midicps]); // always play root on first bar
						2.wait;
						{
							3.do{
								if(0.75.coin, // play note this bar?
								{
									if(0.1.coin, // play skippy rhythm or straight?
										{
											0.4.wait;
											Synth(\bassSaw, [\freq, scale.choose.midicps]);
											1.6.wait
											},
										{
											Synth(\bassSaw, [\freq, scale.choose.midicps]);
											if(0.08.coin, // play another note quickly?
											{
												1.wait;
												Synth(\bassSaw, [\freq, scale.choose.midicps]);
												1.wait
												},
											{	
												2.wait
												}
											)
											}
										)
									},
								{
									2.wait
									}
								)
							}
						}.fork(tempo);
						
					6.wait;
					}
				}.fork(tempo);},
					
			{
				16.wait
				}
			)
		
		}.fork(tempo);
	
	}
	
	
	revchime  {
	
		^{
			4.do{
				if(0.9.coin, // play every other bar?
					{
						Synth(\chime, [\note, scale.choose, \modDepth, rrand(2000,6000), \modRate, rrand(300,700)]);
						4.wait
						},
					{
						4.wait
						}
				);
			}
		}.fork(tempo);
	
	}
	
	fmglitch  {
	
		^{
			if(0.95.coin, // play this phrase?
				{
					{
						16.do{
						0.25.wait;
						{
							3.do{ // play sound with varying carrier freq, mod depth and mod rate every time
								Synth(\chime, [\note, rrand(10,70),\modDepth, rrand(2000,6000), \modRate, rrand(300,700), \att, 0.001, \rel, 0.125, \shape, -4]);
								0.25.wait
							}
						}.fork(tempo);
						
						0.75.wait
						}
					}.fork(tempo);},
				{
					16.wait
					}
			);
		}.fork(tempo);
	
	}
	

	
	dfill {
	
		^{
			if(0.5.coin, // play drum fill leading to next phrase?
				{
					14.5.wait;
					{
						3.do{|i| // plays tom fill with decreasing freqs
							Synth(\kik, [\basefreq, 160-(i*10), \freqdecay, 0.02, \ampdecay, 0.2, \amp, 0.3]);
							0.25.wait;
							Synth(\kik, [\basefreq, 160-(i*10), \freqdecay, 0.02, \ampdecay, 0.2, \amp, 0.3]);
							0.25.wait
						}
					}.fork(tempo)
					},
				{
					16.wait
					}
			);
		}.fork(tempo);
	
	}	
		

	play {
	
		^{	
			inf.do{
				{
					// chooses random scale every other phrase
					scale = [[31,33,35,36,38,40,42,43],[29,31,33,34,36,38,40,41],[33,35,37,38,40,42,44,45]].choose;
			
					2.do{
						
						this.drums;
						this.pingy;
						this.chords;
						this.bassline;
						this.revchime;
						this.fmglitch;
						this.dfill;
						
						16.wait;
						
					}
						
				}.fork(tempo);
					
			32.wait;
			}
		}.fork(tempo)
	}
}
