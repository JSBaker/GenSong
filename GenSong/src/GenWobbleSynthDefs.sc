/*	
====================================================================================
2011 | Jonathan Baker  | GenSong  |  http://www.jonny-baker.com | http://github.com/JSBaker

All source code licenced under The MIT Licence
====================================================================================  
*/

+ GenSong {

	loadSynthDefs {
	{	
	// buffers
	SynthDef(\buff, {|bufnum, rate = 1, trigrate = 0.0001, startpos = 0, amp = 0.4, sustain = 0.125, release = 0.01, out = 0|
	
		var trig, sound, filt;
	
			trig = Impulse.ar(trigrate);
			sound = PlayBuf.ar(1,bufnum,rate*BufRateScale.kr(bufnum),trig,startpos,doneAction:2);
	
		Out.ar(out,Pan2.ar(sound*amp))
	}).store;
	
	
	//delay
	SynthDef(\delay, {|max = 0.5, time = 0.1, dec = 3, in = 16, out = 0, amp = 1|
	
		var fx, env, input;
	
			input= In.ar(in,1);
			env = EnvGen.ar(Env.new([0,amp,0],[0.001,dec]),1,doneAction:2);
			fx = CombL.ar(input*0.3,max,time,dec, 1.0,input);
			
		Out.ar(out,Pan2.ar(fx*env))
	}).store;
	
	//reverb
	SynthDef(\reverb, {|mix = 0.9, room = 1, damp = 0.5, in = 16, out = 0, amp = 1|
		var fx, env,input;
		
			input = In.ar(in,1);
			env = EnvGen.ar(Env.new([0,amp,0],[0.001,3]),1,doneAction:2);
			fx = FreeVerb.ar(input*0.4, mix, room, damp, 1.0, input);
			
		Out.ar(out,Pan2.ar(fx*env))
		
	}).store;
	
	// build up
	SynthDef(\buildup, {|amp = 0.2, length = 10|
	
		var sound, line1, line2, env, rel = 0.9, gate = 1;
		
			line1 = XLine.kr(200,17000,length,doneAction:2);
			line2 = XLine.kr(100,17000,length,doneAction:2);
			sound = LFSaw.ar([line1,line2]);
			env = EnvGen.ar(Env.new([0,amp,amp,0],[1,(length-2),rel]),gate,doneAction:2);
	
		Out.ar(0,(sound*env))
		
	}).store;
	
	
	// wobble
	SynthDef(\wobble, {|freq = 50, botfreq = 20, topfreq = 150, amp = 0.25, lfoDepth = 10, lfoRate = 2, lfoAmount = 0.9, cutOff = 100, gate = 1, att = 0.0001|
	
		var sound, freqmult, rate, freqSlew, rateSlew, amountSlew, depthSlew, ampSlew, rqSlew, env;
	
			freqmult = 1+SinOsc.ar(LFNoise1.kr(rrand(0.25,0.5),4,5),pi.rand,0.01);
			freqSlew = Slew.kr(freq, 100, 100);
			rateSlew = Slew.kr(lfoRate, 5, 5);
			amountSlew = Slew.kr(lfoAmount,15,15);
			env = EnvGen.ar(Env.asr(0.001,amp),gate,doneAction:2);
	
			sound = Resonz.ar(
				Mix.fill(8,{
					LFPulse.ar([10+freqSlew,(10+freqSlew)*2]*(freqmult),pi.rand,0.2)}),
					lfoDepth*(cutOff+((cutOff-1)*SinOsc.kr(1+(lfoRate),0,amountSlew))),0.9);
	
		Out.ar(0,Pan2.ar(sound*env))
	}).store;
	
	// sub
	SynthDef(\sub, {|freq = 50, amp = 0.3,gate = 1, slew = 200|
	
		var sound, freqSlew,env;
		
			freqSlew = Slew.kr(freq, slew, slew);
			sound = SinOsc.ar(freqSlew);
			env = EnvGen.ar(Env.asr(0.01,amp),gate,doneAction:2);
	
		Out.ar(0,Pan2.ar(sound*env))
	}).store;
	
	
	// hat
	SynthDef(\hat, {|hatfreq = 13500, shape = -10, peak = 1|
	
		var env, sound;
		
			env = EnvGen.ar(Env.perc(0.01,0.2,peak,shape),1, doneAction:2);
			sound = BPF.ar(WhiteNoise.ar(env),hatfreq);
	
		Out.ar(0,Pan2.ar(sound)) 
	}).store;
	
	// snare
	 SynthDef(\snare2, {|amp = 0.4|
	 
		var sound, env;
	
	  		sound = SinOsc.ar(50) - WhiteNoise.ar(0.2, 0.1);
			env = EnvGen.kr(Env.perc(0.001,0.4,amp,-5),1,doneAction:2);
			
		Out.ar(0,Pan2.ar(sound*env));
	}).store;

	
	//pingy synth	
	SynthDef(\sawPing, {|freq, decay = 0.5, shape = -10|
	
		var sound, env;
		
			env = EnvGen.ar(Env.perc(0.01, decay, 0.2, shape),1, doneAction:2);
			sound = Saw.ar([1,4,5]*freq, env);
			
		Out.ar(0,Pan2.ar(sound));
	}).store;
	
	
	//chordy synth
	SynthDef(\chord, {|note = 30, shape = -5|
	
		var sound, saw, env;
	
			env = EnvGen.ar(Env.perc(0.1,2,0.1,shape),1,doneAction:2);
			sound = LFPar.ar([1,2,4]*(note.midicps*4),0,env);
	
		Out.ar(0,Pan2.ar(sound));
	}).store;
	//Synth(\chord);
	
	
	// stabby synth
	SynthDef(\stabby,{|freq = 440, amp = 0.5,rhythm = 0.25|
	
		var filter, env, sound, note;
	
			env = EnvGen.ar(Env.perc(0.05,4,amp,-2),1,doneAction:2);
			
			sound = Mix.new([
			 		LFTri.ar(Array.fill(2,{rrand(0.95,1.1)})*freq),
					 SinOsc.ar(Array.fill(2,{rrand(0.95,1.1)})*freq)])*env;
					 
			filter = Resonz.ar(sound,XLine.kr(10000,10,3,doneAction:2),0.9);
			
		Out.ar(0,Pan2.ar(filter*amp))
	}).store;	
	
	s.sync;
	}.fork
	
	}
	
}