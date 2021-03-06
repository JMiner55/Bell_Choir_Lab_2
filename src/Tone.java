import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * @author Jason Miner
 * 
 *         Tone class that was provided by Professor Nate Williams through
 *         Moodle. Plays songs from text files though the use of ant arguments.
 * 
 */
public class Tone
{
	/**
	 * Loads the song from a text file and changes the strings into the actual Note
	 * and NoteLegnth that can be added to the BellNote List.
	 */
	private static List<BellNote> loadSong(String filename)
	{
		final List<BellNote> song = new ArrayList<>();
		final File file = new File(filename);
		Boolean error = false;

		if (file.exists())
		{
			try (FileReader fileReader = new FileReader(file); BufferedReader br = new BufferedReader(fileReader))
			{
				String line;
				NoteLength nl = null;
				Note n = null;

				if (br.readLine() == null)
				{
					System.err.println("Error: File is Blank!");
					error = true;
				}
				else
				{
					while ((line = br.readLine()) != null)
					{
						final String[] fields = line.split(" ");

						if (fields != null)
						{
							if (fields[0].equals("A4"))
							{
								n = Note.valueOf(fields[0]);
							}
							else if (fields[0].equals("A4S"))
							{
								n = Note.valueOf(fields[0]);
							}
							else if (fields[0].equals("B4"))
							{
								n = Note.valueOf(fields[0]);
							}
							else if (fields[0].equals("C4"))
							{
								n = Note.valueOf(fields[0]);
							}
							else if (fields[0].equals("C4S"))
							{
								n = Note.valueOf(fields[0]);
							}
							else if (fields[0].equals("D4"))
							{
								n = Note.valueOf(fields[0]);
							}
							else if (fields[0].equals("D4S"))
							{
								n = Note.valueOf(fields[0]);
							}
							else if (fields[0].equals("E4"))
							{
								n = Note.valueOf(fields[0]);
							}
							else if (fields[0].equals("F4"))
							{
								n = Note.valueOf(fields[0]);
							}
							else if (fields[0].equals("F4S"))
							{
								n = Note.valueOf(fields[0]);
							}
							else if (fields[0].equals("G4"))
							{
								n = Note.valueOf(fields[0]);
							}
							else if (fields[0].equals("G4S"))
							{
								n = Note.valueOf(fields[0]);
							}
							else if (fields[0].equals("A5"))
							{
								n = Note.valueOf(fields[0]);
							}
							else if (fields[0].equals("REST"))
							{
								n = Note.valueOf(fields[0]);
							}
							else
							{
								System.err.println("Error: Invalid Note");
								error = true;
								break;
							}
							if (fields[1].equals("1"))
							{
								nl = NoteLength.WHOLE;
							}
							else if (fields[1].equals("2"))
							{
								nl = NoteLength.HALF;
							}
							else if (fields[1].equals("3"))
							{
								nl = NoteLength.EIGTH;
							}
							else if (fields[1].equals("4"))
							{
								nl = NoteLength.QUARTER;
							}
							else
							{
								System.err.println("Error: Invalid NoteLength");
								error = true;
								break;
							}

							BellNote bn = new BellNote(n, nl);
							song.add(bn);
						}
					}
				}
			}
			catch (FileNotFoundException e)
			{
				System.err.println("Error: File not Found!!");
				e.printStackTrace();
				error = true;
			}
			catch (IOException e)
			{
				System.err.println("IOException: File Doesn't exsit!");
				e.printStackTrace();
				error = true;
			}
		}
		else
		{
			System.err.println("Error: File Not Found!");
		}
		if (error)
		{
			return null;
		}
		return song;
	}

	/**
	 * main method that runs the class to actually play the music.
	 */
	public static void main(String[] args) throws Exception
	{
		// System.out.println("Main running");
		final AudioFormat af = new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, false);
		Tone t = new Tone(af);
		List<BellNote> song = null;

		for (int i = 0; i < args.length; i++)
		{
			switch (i)
			{
			case 0:
				song = loadSong(args[i]);
				break;
			}
			if (song != null)
			{
				t.playSong(song);
			}
			else
			{
				System.err.println("Error: Invalid File.");
				break;
			}
		}
	}

	private final AudioFormat af;

	/**
	 * sets the tone of the music.
	 */
	Tone(AudioFormat af)
	{
		this.af = af;
	}

	/**
	 * Plays all the notes to create a song.
	 */
	void playSong(List<BellNote> song) throws LineUnavailableException
	{	
		try (final SourceDataLine line = AudioSystem.getSourceDataLine(af))
		{
			line.open();
			line.start();

			for (BellNote bn : song)
			{	
				
				
				playNote(line, bn);
			}
			line.drain();
		}
	}

	/**
	 * plays the music note.
	 */
	private void playNote(SourceDataLine line, BellNote bn)
	{
		final int ms = Math.min(bn.length.timeMs(), Note.MEASURE_LENGTH_SEC * 1000);
		final int length = Note.SAMPLE_RATE * ms / 1000;
		line.write(bn.note.sample(), 0, length);
		line.write(Note.REST.sample(), 0, 50);
	}
}

/**
 * Class for combining Note and NoteLegnth to create a BellNote.
 */
class BellNote
{
	final Note note;
	final NoteLength length;

	/**
	 * Creates the actual BellNote by combining the Note and NoteLength.
	 */
	BellNote(Note note, NoteLength length)
	{
		this.note = note;
		this.length = length;
	}
}

/**
 * Class for the note length.
 */
enum NoteLength
{
	WHOLE(1.0f), HALF(0.5f), QUARTER(0.25f), EIGTH(0.125f);

	private final int timeMs;

	/**
	 * Creates the actual note lengths using timeMs and Math.
	 */
	private NoteLength(float length)
	{
		timeMs = (int) (length * Note.MEASURE_LENGTH_SEC * 1000);
	}

	/**
	 * Returns time in milliseconds
	 */
	public int timeMs()
	{
		return timeMs;
	}
}

/**
 * Class for the notes.
 */
enum Note
{
	// REST Must be the first 'Note'
	REST, A4, A4S, B4, C4, C4S, D4, D4S, E4, F4, F4S, G4, G4S, A5;

	public static final int SAMPLE_RATE = 48 * 1024; // ~48KHz
	public static final int MEASURE_LENGTH_SEC = 1;

	// Circumference of a circle divided by # of samples
	private static final double step_alpha = (2.0d * Math.PI) / SAMPLE_RATE;

	private final double FREQUENCY_A_HZ = 440.0d;
	private final double MAX_VOLUME = 127.0d;

	private final byte[] sinSample = new byte[MEASURE_LENGTH_SEC * SAMPLE_RATE];

	/**
	 * sets the actual notes through frequencies
	 */
	private Note()
	{
		int n = this.ordinal();
		if (n > 0)
		{
			// Calculate the frequency!
			final double halfStepUpFromA = n - 1;
			final double exp = halfStepUpFromA / 12.0d;
			final double freq = FREQUENCY_A_HZ * Math.pow(2.0d, exp);

			// Create sinusoidal data sample for the desired frequency
			final double sinStep = freq * step_alpha;
			for (int i = 0; i < sinSample.length; i++)
			{
				sinSample[i] = (byte) (Math.sin(i * sinStep) * MAX_VOLUME);
			}
		}
	}

	/**
	 * creates a byte sample.
	 */
	public byte[] sample()
	{
		return sinSample;
	}
}
