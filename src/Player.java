import java.util.ArrayList;
import java.util.List;

public class Player implements Runnable
{
	private static final int NUM_TURNS = 5;

	private enum State
	{
		A, B, C;
	}

	State state;

	public static void main(String[] args)
	{
		// Create all the players, and give each a turn
		final int numStates = State.values().length;

		Player[] players = new Player[numStates];
		for (State s : State.values())
		{
			players[s.ordinal()] = new Player(s);
		}

		for (int i = 0; i < NUM_TURNS; i++)
		{
			for (Player p : players)
			{
				p.giveTurn();
			}
		}
		for (Player p : players)
		{
			p.stopPlayer();
		}
	}

	private final State myJob;
	private final Thread t;
	private volatile boolean running;
	private boolean myTurn;
	private int turnCount;
	
	private static final int NUM_PLAYERS = 14;
	private List<Note> player_Bellnote = new ArrayList<>();

	Player(State myJob)
	{
		this.myJob = myJob;
		turnCount = 1;
		t = new Thread(this, myJob.name());
		t.start();
		
		for (int i = 0; i < NUM_PLAYERS; i++)
		{
			player_Bellnote.add(Note.A4);
			player_Bellnote.add(Note.A4S);
			player_Bellnote.add(Note.A5);
			player_Bellnote.add(Note.B4);
			player_Bellnote.add(Note.C4);
			player_Bellnote.add(Note.C4S);
			player_Bellnote.add(Note.D4);
			player_Bellnote.add(Note.D4S);
			player_Bellnote.add(Note.E4);
			player_Bellnote.add(Note.F4);
			player_Bellnote.add(Note.F4S);
			player_Bellnote.add(Note.G4);
			player_Bellnote.add(Note.G4S);
			player_Bellnote.add(Note.REST);
		}
		System.out.println(player_Bellnote.size());
		
	}

	public void stopPlayer()
	{
		running = false;
	}

	public void giveTurn()
	{
		synchronized (this)
		{
			if (myTurn)
			{
				throw new IllegalStateException(
						"Attempt to give a turn to a player who's "
						+ "hasn't completed the current turn");
			}
			myTurn = true;
			notify();
			while (myTurn)
			{
				try
				{
					wait();
				}
				catch (InterruptedException ignored)
				{
				}
			}
		}
	}

	public void run()
	{
		running = true;
		synchronized (this)
		{
			do
			{
				// Wait for my turn
				while (!myTurn)
				{
					try
					{
						wait();
					}
					catch (InterruptedException ignored)
					{
					}
				}

				// My turn!
				doTurn();
				turnCount++;

				// Done, complete turn and wake-up the waiting process
				myTurn = false;
				notify();
			}
			while (running);
		}
	}

	private void doTurn()
	{
		System.out.println("Player[" + myJob.name() + "] taking turn " + turnCount);
	}
}
