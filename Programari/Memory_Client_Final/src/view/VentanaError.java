package view;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JLabel;

import controller.ButtonController;

public class VentanaError extends JFrame implements WindowListener{

	/**
	 * La classe VentanaError és la classe encarregada de generar una finestra gràfica la qual, mostrarà com a títol
	 * i contingut les dues Strings que se li passen. 
	 */
	private ButtonController controler;
	private JLabel jlEtiqueta;

	public VentanaError(String title, String etiqueta) {
		
		jlEtiqueta = new JLabel(title);
		jlEtiqueta.setHorizontalAlignment(JLabel.CENTER);

		getContentPane().add(jlEtiqueta, BorderLayout.CENTER);

		setSize(350,100);
		setTitle(etiqueta);
		setLocationRelativeTo(null);
	}

	public void setControler(ButtonController controller){
		this.controler = controller;
	}
	
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		controler.setButtonsActive();
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}