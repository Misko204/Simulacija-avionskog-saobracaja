package gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import logic.InactivityTimer;
import logic.SistemManager;
import model.Aerodrom;
// Panel sa listom aerodroma pored mape
public class FilterPanel extends Panel {
	private SistemManager manager;
	private MapaPanel mapaPanel;
    private InactivityTimer timer;
    private List<Checkbox> checkboxLista;
    private Panel listaPanel;
    
    public FilterPanel(SistemManager manager, MapaPanel mapaPanel, 
    		InactivityTimer timer) {
    	this.manager = manager;
    	this.mapaPanel = mapaPanel;
    	this.timer = timer;
    	this.checkboxLista = new ArrayList<>();
    	
    	setLayout(new BorderLayout(5, 5));
    	
    	Label naslov = new Label("Prikaz aerodroma:", Label.LEFT);
        naslov.setFont(new Font("Arial", Font.BOLD, 12));
        add(naslov, BorderLayout.NORTH);
        
        listaPanel = new Panel();
        listaPanel.setLayout(new GridLayout(0, 1, 5, 5));
        
        ScrollPane scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
        scrollPane.add(listaPanel);
        add(scrollPane, BorderLayout.CENTER);
        
        Panel kontrolePanel = new Panel(new GridLayout(2, 1, 5, 5));
        
        Button btnSvi = new Button("Prikaži sve");
        btnSvi.addActionListener(e -> {
            timer.resetTimer();
            oznacSve(true);
        });
        
        Button btnNista = new Button("Sakrij sve");
        btnNista.addActionListener(e -> {
            timer.resetTimer();
            oznacSve(false);
        });
        
        kontrolePanel.add(btnSvi);
        kontrolePanel.add(btnNista);
        add(kontrolePanel, BorderLayout.SOUTH);
        
        osveziListu();
    }
    
    // Osvežava listu unutar filter panela
	public void osveziListu() {
		listaPanel.removeAll();
        checkboxLista.clear();
        
        List<Aerodrom> aerodromi = manager.getAerodromi();
        
        for (int i = 0; i < aerodromi.size(); i++) {
            final Aerodrom aerodrom = aerodromi.get(i);
            
            Checkbox cb = new Checkbox(aerodrom.toString());
            
            cb.addItemListener(e -> {
                timer.resetTimer();
                aerodrom.setPrikazan(cb.getState());
                mapaPanel.repaint();
            });
            
            checkboxLista.add(cb);
            listaPanel.add(cb);
        }
        
        oznacSve(true);
        listaPanel.revalidate();
        listaPanel.doLayout();
	}

	private void oznacSve(boolean stanje) {
		List<Aerodrom> aerodromi = manager.getAerodromi();
        
        for (int i = 0; i < aerodromi.size(); i++) {
            aerodromi.get(i).setPrikazan(stanje);
            if (i < checkboxLista.size()) {
                checkboxLista.get(i).setState(stanje);
            }
        }
        
        mapaPanel.repaint();
		
	}

}
