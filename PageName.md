# Points to Ponder #

Add your content here.  Format your content with:


We talked about these different values that determine the worth/value/debt/etc. of a move or player. Maybe we can call them heuristics.
--> heuristics to determine how to help/hurt a player:
  * worth of opponents moves (almost implemented, but still buggy)
    * amount we are in "debt" to each player
  * potential to help vs. actual helped?
  * total points gained/lost? as in, don't just use the distance equation

And then we need to look at when cooperations should be started/ended/maintained
--> "cooperistic" measures (?)
  * when that player can no longer help us
  * when another player can help us more
  * towards the end of a game?


---


So there are several things we talked about that we should definitely consider implementing by Monday. I ordered them in what I think is the most crucial:

  * make sure that we're not hurting ourselves when we move (obviously)
  * make a better equation to determine if a move was helpful/hurtful/neutral
  * end game strategy
  * short game strategy
  * error on the side of too cooperative - help more than our partner helped us so partners know we're trying to be helpful
  * track how many coins/points our opponents have at each turn
  * dynamically change how many rounds we look back? dependent on numRounds?
  * avoid building huge stacks
  * change strategy based on ranking
  * never cooperate with immediate neighbor? (i don't know about this one...)