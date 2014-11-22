	/*	
	====================================================================================
	2011 | Jonathan Baker  | GenSong  |  http://www.jonny-baker.com | http://github.com/JSBaker

	All source code licenced under The MIT Licence
	====================================================================================  
	*/

	GenSong 	{
	
	var <s;
	var <>buffers, filenames, samplenames, filenames;
	var <> filepath;
	var <tempo, <>bps;
	var <kickTimes, <hatTimes;
	var ridehat, hatsnare, bass, build; 
	var kickMarkov, current;
	var low, bar;
	var <melodNotes, <introNotes, <introTimes, <introLength;
	var basses,bassFlag;
	var manualLoad;
	
	*new { |filepath,s,buffers|
		
		^super.new.initGenWobble(filepath,s,buffers)
	}
	
	initGenWobble {|path,server,buffs|
	
	s = server ? Server.default;
	filepath = path ? "You must enter the file path to the GenSong Samples folder !!! ";
	filepath.postln;
	
		tempo = TempoClock(bps);
		s.latency = 0.04; //0.001 //ensures the patterns start at the same time as .fork functions
		current = 0;
		kickTimes = [[2],[0.5,1.5],[0.875,1.125],[0.25,1.75],[0.25,0.625,1.125],[0.25,0.25,1.5]];
		hatTimes = [[0.25,0.25],[0.25,0.25],[0.25,0.125,0.125],[0.125,0.125,0.125,0.125],[0.125,0.125,0.25],[0.25,0.0625,0.0625,0.0625,0.0625]];
		melodNotes = [64,65,67,69,71,72,74,76,77,79];
		kickMarkov = 	[
					[0.1,0.1,0.2,0.3,0.3,0.0],
					[0.4,0.0,0.3,0.05,0.25,0.0],
					[0.0,0.2,0.0,0.3,0.2,0.3],
					[0.1,0.4,0.4,0.0,0.1,0.0],
					[0.1,0.4,0.0,0.0,0.1,0.3],
					[0.1,0.2,0.5,0.0,0.2,0.0]
					];
		kickTimes = [[2],[0.5,1.5],[0.875,1.125],[0.25,1.75],[0.5,1,0.5],[0.25,0.25,1.5]];
		low = bassFlag = false;
		bar = 8;
		
		this.loadSynthDefs;
	}
	
	setTempo{|newbps|
		
		bps = newbps;
		tempo = TempoClock(bps);
	}		
	
	wobble {|norm=true,lcltempo|
	
		var tmp;
		this.stop();
		tmp = lcltempo ? 1.17;
		this.setTempo(tmp);
		
		if(norm==false, 
		{
			this.setTempo(lcltempo)
		});
		this.intro;
	}
	
	cheeseit {|norm=true,lcltempo|
	
		var cheesetune, tmp;
		this.stop();
		tmp = lcltempo ? 2;
		this.setTempo(tmp);
		
		if(norm==false, 
		{
			this.setTempo(lcltempo)
		});
		cheesetune = InfiniCheese(s,buffers,tempo);
		cheesetune.play;
	}
		
	random {
		
		if(0.5.coin,
		{
			this.wobble
		},{
			this.cheeseit
		});
	}
	
	crazy {
	
		if(0.5.coin,
		{
			this.wobble(false,rrand(1.3,1.9).postln)
		},{
			this.cheeseit(false,rrand(2.0,2.6).postln)
		});				
	}
	
	stop {
	
		if(bassFlag,
		{
			basses.do{arg i; i.release(0.1)}
		});
		
		tempo.clear;
		bassFlag = false;
	}	
}