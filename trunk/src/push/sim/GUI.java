/* 
 * 	$Id: GUI.java,v 1.4 2007/11/14 22:02:59 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package push.sim;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

//import colony.Tournament;
//import colony.Tournament.TournamentResult;
/**
 * 
 * @author Bell
 * 
 */
public final class GUI extends JFrame implements ActionListener, GameListener
{
	private GameEngine engine;
	private static final long serialVersionUID = 1L;


	private ConfigurationPanel configPanel;
	private ControlPanel controlPanel;
	private BoardPanel boardPanel;

	private BoardFrame boardFrame;

	private volatile boolean fast;

	public GUI(GameEngine engine)
	{
		this.engine = engine;
		engine.addGameListener(this);
		JPanel topPanel = new JPanel(new BorderLayout());

		JPanel eastPanel = new JPanel(new GridLayout(1, 1));
		configPanel = new ConfigurationPanel(engine.getConfig());
		eastPanel.add(configPanel);

		controlPanel = new ControlPanel();

		boardPanel = new BoardPanel();
		JScrollPane boardScroller = new JScrollPane();
		boardScroller.getViewport().add(boardPanel);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("COMS 4444 - Push");
		setName("GUI");
		setPreferredSize(new Dimension(1000, 850));
		setMinimumSize(new Dimension(500, 500));
		getContentPane().add(topPanel);

		JPanel gamePanel = new JPanel(new BorderLayout());
		gamePanel.setName("Game Play");
		gamePanel.add(eastPanel, BorderLayout.EAST);
		gamePanel.add(controlPanel, BorderLayout.NORTH);
		gamePanel.add(boardScroller, BorderLayout.CENTER);

		
		this.add(gamePanel);
		this.pack();
		this.setVisible(true);

		setupListeners();

		boardFrame = new BoardFrame(engine.getBoard());
	}

	private void setupListeners()
	{
		controlPanel.addListener(this);
		// controlPanel.play.addActionListener(this);
		// controlPanel.begin.addActionListener(this);
		// controlPanel.step.addActionListener(this);
		// controlPanel.stop.addActionListener(this);
		// controlPanel.pause.addActionListener(this);
		// controlPanel.tournament.addActionListener(this);
	}

	public void actionPerformed(ActionEvent arg0)
	{
		String command = arg0.getActionCommand();
		if (command.compareToIgnoreCase("Begin") == 0)
		{
			

			// disable the begin button and the configPanel (freeze the config)
			controlPanel.begin.setEnabled(false);
			configPanel.setEnabled(false);
			controlPanel.tournament.setEnabled(false);
			fast = false;
			if (engine.setUpGame())
			{
				boardPanel.setEngine(engine);
				boardFrame.setEngine(engine);
				boardPanel.setBoard(engine.getBoard(), false);
				boardFrame.setBoard(engine.getBoard(), false);
				// boardFrame.playerLabel.setText(engine.getPlayerName());
				// boardFrame.playerLabel.setForeground(engine.getPlayerColor());
				boardFrame.round.setText("Round: 0");
				controlPanel.stop.setEnabled(true);
				controlPanel.play.setEnabled(true);
				controlPanel.pause.setEnabled(false);
				controlPanel.step.setEnabled(true);
			} else
			{
				// game set up failed. Turn the right buttons on/off.
				JOptionPane.showMessageDialog(this, "Error creating game: Please check that you have selected exactly 6 players");
				controlPanel.begin.setEnabled(true);
				configPanel.setEnabled(true);
				controlPanel.tournament.setEnabled(true);
			}
			this.repaint();
		} else if (command.compareToIgnoreCase("Step") == 0)
		{
			engine.step();
		} else if (command.compareToIgnoreCase("Play") == 0)
		{
			controlPanel.step.setEnabled(false);
			controlPanel.play.setEnabled(false);
			controlPanel.pause.setEnabled(true);
			controlPanel.begin.setEnabled(false);
			controlPanel.tournament.setEnabled(false);
			controlPanel.stop.setEnabled(true);
			fast = true;
			GameRunner runner = new GameRunner(configPanel.getSpeedSlider());
			runner.setName("Game Runner");
			runner.start();
		} else if (command.equalsIgnoreCase("Pause"))
		{
			fast = false;
			controlPanel.step.setEnabled(true);
			controlPanel.play.setEnabled(true);
			controlPanel.pause.setEnabled(false);
			controlPanel.begin.setEnabled(false);
			controlPanel.stop.setEnabled(true);
			controlPanel.tournament.setEnabled(false);
		} else if (command.compareToIgnoreCase("Stop") == 0)
		{
			fast = false;
			controlPanel.stop.setEnabled(false);
			controlPanel.play.setEnabled(false);
			controlPanel.pause.setEnabled(false);
			controlPanel.step.setEnabled(false);
			controlPanel.begin.setEnabled(true);
			controlPanel.tournament.setEnabled(true);
			configPanel.setEnabled(true);
		} else if (command.compareToIgnoreCase("Tournament") == 0)
		{

		} else if (command.compareToIgnoreCase("BoardFrame") == 0)
		{
			boardFrame.setVisible(true);
		} else
		{
			throw new RuntimeException("Unknow Action Command: " + command);
		}
	}

	private class GameRunner extends Thread implements ChangeListener
	{
		private JSlider slider;
		private int delay;

		public GameRunner(JSlider slider)
		{
			this.slider = slider;
		}

		public void run()
		{
			delay = slider.getValue();
			slider.addChangeListener(this);
			while (fast && engine.step())
			{
				try
				{
					Thread.sleep(delay);
				} catch (InterruptedException e)
				{
					// this should not happen!
					e.printStackTrace();
				}
			}
			slider.removeChangeListener(this);
		}

		public void stateChanged(ChangeEvent arg0)
		{
			if (arg0.getSource().equals(slider))
			{
				delay = ((JSlider) arg0.getSource()).getValue();
			}
		}
	}

	public void gameUpdated(GameUpdateType type)
	{
		// find our super parent frame -- needed for dialogs
		Component c = this;
		while (null != c.getParent())
			c = c.getParent();

		switch (type)
		{
		case GAMEOVER:
			fast = false;
			controlPanel.play.setEnabled(false);
			controlPanel.step.setEnabled(false);
			controlPanel.pause.setEnabled(false);
			controlPanel.stop.setEnabled(false);
			controlPanel.begin.setEnabled(true);
			controlPanel.tournament.setEnabled(true);
			configPanel.setEnabled(true);
			configPanel.setScores(engine.getScores(), engine.players);
			String s = "Game Over";
			JOptionPane.showMessageDialog((Frame) c, s, "Game Over", JOptionPane.INFORMATION_MESSAGE);

			break;
		case MOVEPROCESSED:
			controlPanel.roundText.setText("" + engine.getCurrentRound());
			boardFrame.round.setText("Round: " + engine.getCurrentRound());
			boardFrame.bp.repaint();
			boardPanel.repaint();
			configPanel.setScores(engine.getScores(), engine.players);
//			configPanel.updateScores(engine.getScores());
			break;
		case STARTING:
			controlPanel.roundText.setText("0");
			configPanel.setScores(engine.getScores(), engine.players);
			break;
			default:
			// nothing.
		}
	}


}
