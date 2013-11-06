package UI;
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.UIManager;


public abstract class UIElement extends JFrame {

	private JPanel contentPane;
	private JFrame frame;



	public UIElement() {
		setTitle("titleBar");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 500);
		this.setResizable(false);
//		this.setContentPane(new JLabel(new ImageIcon("Res/bg.png")));
//		http://placehold.it/400x400
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnGame = new JMenu("Game");
		menuBar.add(mnGame);
		
		JMenuItem mntmNewGame = new JMenuItem("New Game");
		mnGame.add(mntmNewGame);
		
		JMenuItem mntmEndGame = new JMenuItem("End Game");
		mnGame.add(mntmEndGame);
		
		JMenu mnOption = new JMenu("Option");
		menuBar.add(mnOption);
		
		JMenuItem mntmMusic = new JMenuItem("Music");
		mnOption.add(mntmMusic);
		
		JMenuItem mntmMapSelection = new JMenuItem("Map Selection");
		mnOption.add(mntmMapSelection);
		
		JMenu mnAbout = new JMenu("About");
		menuBar.add(mnAbout);
		
		JMenuItem mntmPlayerState = new JMenuItem("player Statistics");
		mnAbout.add(mntmPlayerState);
		
		JMenuItem mntmTeam = new JMenuItem("Team");
		mnAbout.add(mntmTeam);
		
		JMenuItem mntmProject = new JMenuItem("Project");
		mnAbout.add(mntmProject);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.add(new JLabel(new ImageIcon("Res/bg.png")));
		setContentPane(contentPane);
	}
}