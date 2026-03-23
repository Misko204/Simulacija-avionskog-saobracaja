package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;
// Iskačući prozor koji iskače 5s do zatvaranja aplikacije
public class InactivityDialog extends Dialog {
	private int preostaloSekundi;
	private Timer countdown;
	private boolean nastaviti;
	private TextArea txtPoruka;
	Frame mainFrame;
	
	public InactivityDialog(Frame parent, int sekundi) {
		super(parent, "Upozorenje - Neaktivnost", true);
		this.preostaloSekundi = sekundi;
		this.nastaviti = false;
		this.mainFrame = parent;
		
		setLayout(new BorderLayout(10, 10));
		setSize(500, 150);
		setResizable(false);
		setLocationRelativeTo(parent);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (countdown != null) {
					countdown.cancel();
				}
				dispose();
				mainFrame.dispose();
				System.exit(0);
			}
		});
		
		populateWindow();
	}
	
	private void populateWindow() {
		txtPoruka = new TextArea("", 4, 40, TextArea.SCROLLBARS_NONE);
		txtPoruka.setEditable(false);
		txtPoruka.setFont(new Font("Arial", Font.BOLD, 14));
		azurirajPoruku();
		add(txtPoruka, BorderLayout.CENTER);
		
		Button nastaviBtn = new Button("Nastavi sa radom");
		nastaviBtn.setFont(new Font("Arial", Font.PLAIN, 14));
		nastaviBtn.addActionListener((ae) -> {
			nastaviti = true;
			if (countdown != null) {
                countdown.cancel();
            }
			dispose();
		});
		
		Panel dugmePanel = new Panel();
		dugmePanel.add(nastaviBtn);
		add(dugmePanel, BorderLayout.SOUTH);
		
		countdown = new Timer(true);
		countdown.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				preostaloSekundi--;
				if (preostaloSekundi <= 0) {
					countdown.cancel();
					dispose();
					mainFrame.dispose();
					System.exit(0);
				} else {
					azurirajPoruku();				
				}
			}
		}, 1000, 1000);
		
	}
	
	// Ažurira se poruka
	private void azurirajPoruku() {
        txtPoruka.setText("Aplikacija će se zatvoriti za " + preostaloSekundi
        		+ "s zbog neaktivnosti.\nKliknite na dugme da nastavite rad.");
    }
    
    public boolean zelimNastaviti() {
        return nastaviti;
    }
}
