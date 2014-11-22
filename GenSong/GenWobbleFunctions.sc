/*	
====================================================================================
2011 | Jonathan Baker  | GenSong  |  http://www.jonny-baker.com | http://github.com/JSBaker

All source code licenced under The MIT Licence
====================================================================================  
*/

+ GenSong {

	ridehat  {
		{
			8.do{|l|
				if((low)&&(l==(bar+4)), // creates a pause if the bass repeats a low note during the drop
					{
						Synth(\buff, [\bufnum, 7, \amp, 0.1, \rate, 1.5]);
						1.wait;
					},
					{
					this.ridePat;
						{
							2.do{
								this.hatPat(hatTimes.wchoose([0.75,0.05,0.05,0.05,0.05,0.05]));
								0.5.wait;
							}
						}.fork(tempo);
					
					1.wait;
					}
				)
			}
		}.fork(tempo);
	}
	
	// function for intro and breakdown hat and snare fill pattern		
	hatsnare  {
		{
			4.do{
				if(0.8.coin,{this.snarePat2; this.offPat3});
				
				{
					4.do{
						this.hatPat([0.25,0.25]);
						0.5.wait;
					}
				}.fork(tempo);
				
			2.wait;
			}
		}.fork(tempo);
	}	
	
	bass  {|rel = 0.2|
	var freqs, rates, bharm, releaseTime;
	
	bassFlag = true;
	bharm = 1.5;
	freqs = Array.fill(4,{rrand(1,10)}); // creates values for freq every section
	{
	
		4.do{|k|
		rates = Array.fill(4,{rrand(1,10)}); // creates values for rate every 8 bar phrase	    			   
		
			{
			var wob, sub, amounts, rpt; 
			
				rpt = false;   // resets the boolean
				wob = Synth(\wobble, [\amp,0.2]); 	 // wobble and sub instances
				sub = Synth(\sub);  
						   
				basses = [wob,sub];
				
				2.do{|j|					     									 // creates values for lfo amounts every 4 bar phrase,
				amounts = Array.fill(4,{(1+(k/20))*rrand(0.1,0.8)}); // with more chance of wobble as the section progresses
			
					{
					4.do{|i|
		    		         
						if((j==1)&&(low)&&(i==bar), // checks if it is the second half of the 8 bar phrase, if there has been a low note, and if it was in this bar
								
							{
								wob.set(\freq, 20, \lfoRate, [0.5,1,2,4].choose, \lfoAmount, 0.9);
								sub.set(\freq, 20*bharm);
								low = false;
								rpt = true;
								1.wait;
							},
							{	
								// decides with increasing probability whether to follow
								// the melody or play a low note if there has not been one
								if(low==true||rpt==true||(1-(k/20)).coin, 
									
									{
										if((1-(i/8)).coin, // decides with increasing probability whether to have a fluctuation in the note		   						
											{
												wob.set(\freq, 10*((freqs[i]/2)+2), \lfoRate, rates[i], \lfoAmount, amounts[i]);
												sub.set(\freq, bharm*(10*((freqs[i]/2)+2))); // assigns the next freq between 25-70Hz
												1.wait;
											},
											{
												wob.set(\freq, 10*((freqs[i]/2)+2), \lfoRate, rates[i], \lfoAmount, amounts[i]);
												sub.set(\freq, bharm*(10*((freqs[i]/2)+2)));
												0.5.wait;
												
												wob.set(\freq, (10*((freqs[i]/2)+2))*rrand(0.7,1.3), \lfoRate, rates[i]*rrand(0.7,1.3));
												sub.set(\freq, bharm*(10*((freqs[i]/2)+2)));
												0.5.wait;
											}
											)
									},
									{
										wob.set(\freq, 17.59, \lfoRate, [0.5,1,2,4].choose, \lfoAmount, 0.9);
										sub.set(\freq, 20*bharm);										
										if(j==0, {bar = i; low = true});// plays a low note and then stores the bar number and sets the boolean 
										1.wait
									}
									)
							}
							);
			
						}
					}.fork(tempo);
					4.wait;
				};
					
				if(k==3,{releaseTime = rel},{releaseTime = 0.2});
				wob.release(releaseTime);
				sub.release(0.1);
				
			}.fork(tempo);
		8.wait;
		
		
		}
	
	}.fork(tempo);
	}	
	
	////////////////////////////////////////
	//  main structure
	////////////////////////////////////////
	
	//intro section
				
	intro 	 {|intro = true|
	var third;
	if(intro,{"Intro Length:".postln},{"Outro Length:".postln}); //  posts the legnth of the intro/outro
	introLength = [1,2,3].wchoose([0.3,0.4,0.3]).postln; // chooses length of section
	if(introLength == 3,{third = 0.5.coin},{third = false}); // if the intro legnth is 3 then decideds whether the beat will play in the last phrase
	introNotes = Array.fill(introLength,{Array.fill(4,{[64,65,67,69,71,72,74,76,77,79].choose.midicps})});
	introTimes = Array.fill(introLength,{[[2,2,2,2],[2,2,4],[2,2,1.5,0.5]].choose}); // notes and rhythm of the intro chords
	
	
		{
			introLength.do{|i|
				{
					2.do{|j|
					var current;

						this.chordPat(introNotes[i],introTimes[i]); // plays chord and percussion patterns
						if(0.2.coin,{this.hatsnare});
						if(intro && (j==1),{Synth(\buildup, [\length, 9.5/bps, \amp, (0.02*(i+1))])}); // plays a buildup every other cycle of j, with increasing amplitude
						current = 0;
						{
							4.do{|k|
							var beat = false; // boolean to determine if beat is playing

								if(intro,{ // drums do not play on the outro
								
									if((introLength==1) || (i==1) || ((i == 2) && third), // decides whether drums should play, if the length is 1, its is the second phrase or if third is true
									
										{
										
										if((i==(introLength-1))&&(j==1)&&(k==3), // if it is the last bar before the drop the drums will not play
											{
											"The drop!".postln
											},
											{
											this.kickPat(kickTimes[current]); 
											current = [0,1,2,3,4,5].wchoose(kickMarkov[current]); 
											this.snarePat;
											this.offPat(4,[0.5],[0,0.4],[5000,5000]);
											this.cymPat;
											beat = true;
											}
										)
										},
										{
										if((k==0), // if not playing a beat - sparse kicks and snares with fx
											{
											Synth(\delay, [\time, rrand(0.1,0.3), \in, 18]); Synth(\reverb, [\out, 18]); Synth(\buff, [\bufnum, buffers[0], \out, 16, \amp, 0.8])
											},
											{
											if(0.3.coin, 
												{
												Synth(\delay, [\time, rrand(0.1,0.3), \in, 18]); Synth(\reverb, [\out, 18]); Synth(\buff, [\bufnum, buffers[[0,1].wchoose([0.3,0.7])], \out, 16, \amp, 0.8])
												}
											)
											}
											)
										}
									)
									}
								);
									if(intro, // if outro all light percussion plays through whole section
									 	{
									 		if(0.8.coin,{this.offPat2})
									 		},
									 	{
									 		if(0.8.coin,{this.offPat2});
									 		this.offPat(4,[0.5],[0,0.4],[5000,5000]);
											this.cymPat
											}
									);
									 		
									
									1.wait;
									if(beat, // if beat is playing, regular snare hits with varied fx
										{
										if(0.8.coin, 
											{
											if (0.5.coin,
												{
												Synth(\delay, [\time, rrand(0.1,0.3)]); Synth(\buff, [\bufnum, buffers[1], \out, 16, \amp, 0.8])
												},
												{
												Synth(\reverb); Synth(\buff, [\bufnum, buffers[1], \out, 16, \amp, 0.8])
												}
												)
											},
											{
									 		Synth(\buff, [\bufnum, buffers[1], \amp, 0.8])
									 		}
									 		)
									 	}
									 );
					 			
									
							1.wait;
							}
						}.fork(tempo);
			
					8.wait;
					}
				}.fork(tempo);
				
			16.wait;
			if(i==(introLength-1) && intro,{this.drop}); // triggers drop method
			}

		}.fork(tempo);
	}	
	
	//drop section
	drop 	{|firstDrop = true|
	var dropLength;
	"Drop Length:".postln;
	dropLength = [2,3,4].wchoose([0.6,0.25,0.15]).postln; // decides drop length
	
		{
			dropLength.do{|i|
				if(i==(dropLength-1),{this.bass(3)},{this.bass}); // starts the bass, if last phrase, increases the release value to smooth the transition
				Synth(\delay, [\time, rrand(0.2,0.3), \dec, 10, \in, 18]);Synth(\reverb, [\out, 18]); Synth(\buff, [\bufnum, buffers[7], \out, 16, \amp, 0.3]); // cymbal crash on first beat
				
				{
					4.do{
						this.ridehat;
						current = 0;
						
						{
							4.do{
								this.kickPat(kickTimes[current]); 
								current = [0,1,2,3,4,5].wchoose(kickMarkov[current]); 
								this.snarePat;
								this.cymPat;
								if(0.8.coin,{this.offPat2});
								
								1.wait;
								
								if(0.9.coin, // snare pattern with increased chance of fx, particularly reverb
									{
									if (0.1.coin,
										{
										Synth(\delay, [\time, rrand(0.1,0.3)]); Synth(\buff, [\bufnum, buffers[8], \out, 16, \amp, 0.65])
										}, 
										{Synth(\reverb); Synth(\buff, [\bufnum, buffers[8], \out, 16, \amp, 0.65])
										}
									)
									},
									{
									 Synth(\buff, [\bufnum, buffers[8], \amp, 0.65])
									 }
								);
							1.wait;
							}
						}.fork(tempo);
					
					8.wait;
					}
				}.fork(tempo);
			
			32.wait;
			if(i==(dropLength-1),
				{
				if(firstDrop, // triggers breakdown unless second drop, then triggers outro
					{
					this.breakdown; "Breakdown!".postln
					},
					{
					this.intro(false)
					
					}
				)
				}
			);
			}
		}.fork(tempo);
	
	}	
	
	
	// breakdown section
	breakdown 	 {
	var breakdownLength;
	if(introLength == 1,{breakdownLength = (introLength*2)},{breakdownLength = introLength}); // same length as intro unless introlength = 1, in which case it is doubled
	
	
		{
			breakdownLength.do{|i|
				{
				var index, bsub, subFreqs, rsub;
	
					2.do{|j|
						
						if(introLength == 1, {index = 0},{index = i}); // makes sure does not look in array index that doesnt exist!
						this.chordPat(introNotes[index],introTimes[index]); // plays same freqs and rhythms as intro
						if(j==1,{Synth(\buildup, [\length, 9.5/bps, \amp, (0.02*(i+1))])}); 
						this.hatsnare;
						subFreqs = introNotes[index]; // sub plyas along following same intro freqs
						bsub = Synth(\sub, [\slew, 160]);
						rsub = true; // boolean to release sub
						
						{
							4.do{|k|
								if(i==(breakdownLength-1)&&(k==3), // sub and beat will not play on last bars in the last phrase
									{
										bsub.release(0.5);
										this.kickPat(kickTimes[0]); 
										this.cymPat;
										rsub = false;
										if(j==1,{"Second drop!".postln});
										2.wait;
									},
									{
										bsub.set(\freq, ((subFreqs[k])/6));
										this.kickPat(kickTimes[current]); 
										current = [0,1,2,3,4,5].wchoose(kickMarkov[current]); 
										this.snarePat;
										this.cymPat;
										this.offPat(8,[0.25],[0.4,0.2],[5000,8000]);
										if(0.8.coin,{this.offPat2});
							
										1.wait;
										if(0.8.coin, // intro snare pattern with varied fx
											{
											if (0.5.coin,
											{
											Synth(\delay, [\time, rrand(0.1,0.3)]); Synth(\buff, [\bufnum, buffers[1], \out, 16, \amp, 0.8])}, {Synth(\reverb); Synth(\buff, [\bufnum, buffers[1], \out, 16, \amp, 0.8])})},
											
											{
											Synth(\buff, [\bufnum, buffers[1], \amp, 0.8])
											}
										);
							
									1.wait
									});
							}
						}.fork(tempo);
				
					8.wait;
					if(rsub,{bsub.release(0.1)});
					}
				}.fork(tempo);
		
			16.wait;
			if(i==(breakdownLength-1),{this.drop(false)});
			}
		}.fork(tempo);
	}		

}