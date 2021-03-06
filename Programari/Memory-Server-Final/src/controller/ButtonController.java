package controller;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import model.GestioPartida;
import model.Operacions;
import model.classThread;
import network.Network;
import view.MainWindow;

public class ButtonController implements ActionListener {
	
	/**
	 * La classe ButtonController és l'encarregada de gestionar les accions que es donen a terme a través de la finestra gràfica
	 * principal del nostre programa.
	 */
	private MainWindow vista;
	private GestioPartida partida;
	private classThread timer;
	private boolean initPartida;
	private boolean canRegist=false;
	private Network network;
	private int numPartides;
	
	public ButtonController(MainWindow vista, GestioPartida model) {
		this.vista = vista;
		this.partida = model;
		initPartida=false;
		canRegist=true;
	}
	
	public void actionPerformed(ActionEvent event) {
		String quinBoto = ((JButton)event.getSource()).getText();
		
		if (quinBoto.equals("Inserir")) {
			Frame frame = new Frame();
			eliminarTablasBaseDatos();
			String[] s;
			Date d = new Date();
			timer = new classThread(this);
			partida.actualizarTablas();
			s = vista.horaIniciMinutsDurada();
			s = partida.comprovacioTemps(s);
			timer.setTempsTotal(s);
			partida.actualizarTablas();
			vista.repaint();
			setCanRegist(true);
			if(!network.getIsOn()){
				network.turnOn();
				new Thread(network).start();
			}	
			if(s[0]!=null){
				//vista.resetTableRegister();
				try {
					d = partida.getTimeIniciPartida(s);
					if(d!=null){
						vista.enableBotoInserir();
						timer.setDate(d);
						timer.start();
					}else{
						JOptionPane.showMessageDialog(frame,"El temps no pot ser m�s petit del actual","Error",JOptionPane.ERROR_MESSAGE);
					}
					
				} catch (ParseException e) {
					
					JOptionPane.showMessageDialog(frame,"Error en els camps del temps","Error",JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		if(quinBoto.equals("Registrar")){
			String nickname, contrasena, nom;
			Frame frame = new Frame();
			nickname = vista.getNickname();
			contrasena = vista.getContrasena();
			nom = vista.getNom();
			if(canRegist==true){
				if(nom.isEmpty() && contrasena.isEmpty() && nickname.isEmpty()){
					JOptionPane.showMessageDialog(frame,"Error tots els camps estan buits!","Error",JOptionPane.ERROR_MESSAGE);
				}else{
					if(nom.isEmpty()){
						JOptionPane.showMessageDialog(frame,"Error el camp nom est� buit!","Error",JOptionPane.ERROR_MESSAGE);
					}else if(nickname.isEmpty()){
						JOptionPane.showMessageDialog(frame,"Error el camp de nickname est� buit!","Error",JOptionPane.ERROR_MESSAGE);
	
					}else if(contrasena.isEmpty()){
						JOptionPane.showMessageDialog(frame,"Error el camp de contrase�a est� buit!","Error",JOptionPane.ERROR_MESSAGE);
						
					}else{
						partida.inserirUsuari(nickname,nom,contrasena);							
					}
				}
			}else{
				JOptionPane.showMessageDialog(frame,"No et pots registrar!","Registrar",JOptionPane.INFORMATION_MESSAGE);
			}
		
		}
		
		if(quinBoto.equals("Memoria")){
			String nickname=vista.getNicknameComboBox();
			ArrayList<String> memoria = selectGraficMemoria(nickname);
			ArrayList<String> concentracio = selectGraficConcentracio(nickname);
			setPuntsGrafica(memoria,concentracio,false);		}
		
		if(quinBoto.equals("Concentracio")){
			String nickname=vista.getNicknameComboBox();
			ArrayList<String> memoria = selectGraficMemoria(nickname);
			ArrayList<String> concentracio = selectGraficConcentracio(nickname);
			setPuntsGrafica(memoria,concentracio,true);
		}

	}
	
	/**
	 * Aquest procediment s'encarrega de actualitzar l'hora del joc, obtenint la hora real de la informació pròpia de l'ordinador.
	 * @param d: Variable del tipus Date, la qual és pròpia de Java i s'encarrega d'emmagatzemar la dada actual extreta de 
	 * l'ordinador.
	 */
	public void updateHoraInici(Date d){
		vista.setTempsInici(d);
	}
	
	/**
	 * Aquest procediment permet concretar la dada exata a través del sistema de l'ordinador per a després calcular la hora
	 * en la que el torneig finalitzarà.
	 * @param d: Variable del tipus Date, la qual és pròpia de Java i s'encarrega d'emmagatzemar la dada actual extreta de 
	 * l'ordinador.
	 */
	public void updateMinutsDurada(Date d){
		vista.setTempsDurada(d);
	}
	
	/**
	 * Com el seu nom indica, aquest procediment és l'encarregat d'inicialitzar la partida.
	 */
	public void iniciarPartida(){
		this.initPartida = true;
	}
	
	/**
	 * Com el seu nom indica, aquest procediment és l'encarregat d'aturar la partida.
	 */
	public void aturarPartida(){
		this.initPartida = false;
	}
	
	public boolean getinitPartida(){
		return initPartida;
	}
	
	/**
	 * El procediment enableTabs activa les finestres de la finestra principal que permeten la inserció de les dades que 
	 * especifiquen el moment en el que començarà el torneig i el temps que queda per a que aquest comenci.
	 */
	public void enableTabs(){
		vista.enableWindows();
	}
	
	public boolean getCanRegist(){
		return canRegist;
	}
	
	public void setCanRegist(boolean canRegist){
		this.canRegist = canRegist;
	}
	
	/**
	 * La funció getTimePartida s'encarrega de crear una variable tipus Data mitjançant la informació obtinguda a través de 
	 * la finestra gràfica. Aquesta informació serà la que ens permetrà definir la hora d'inici del torneig i el temps de duració
	 * d'aquest. 
	 * @param s: Variable tipus String que conté la informació omplerta en els camps de hora de començament de partida, i genera 
	 * una variable tipus Date, per tal poder tractar aquesta informació en altres apartats. 
	 * @return d: Variable tipus data que conté la hora en la que començarà el torneig.
	 * @throws ParseException
	 */
	public Date getTimePartida(String[] s) throws ParseException{
    	DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date d = new Date();

		String data = ("00:"+s[3]+":"+s[4]);
		
		d = dateFormat.parse(data);
		return d;
	}
	
	/**
	 * Com el seu nom indica el procediment cleanVistaRegister ens permet col·locar tots els camps de la finestra gràfica
	 * en blanc per tal de que l'usuari pugui inserir les dades sense problemes. 
	 */
	public void cleanVistaRegister(){
		vista.cleanRegister();
	}
	
	/**
	 *  Aquest procediment és l'encarregat de omplir la taula amb els usuari registrats fins el moment per tal de mostrar-los
	 *  en la opció de gestió d'usuaris del nostre menú
	 * @param data: Array de Objects que conté tota la informació de tots els usuaris enregistrats fins el moment.
	 */
	public void setDataTableRegister(ArrayList<Object[]> data){
		vista.resetTableRegister();
		for(int i=0;i<data.size();i++){
			vista.setDataTableRegister(data.get(i));
		}
	}
	
	/**
	 * Aquest procediment ens permet eliminar una de les files de la taula mostrada a la opció rànquing de la opció de la 
	 * finestra gràfica principal. 
	 */
	public void deleteSelectedRow(){
		vista.deleteSelectedRow();
	}	
	
	/**
	 * Aquest procediment s'encarrega d'omplir la taula del rànquing del menú principal amb la informació que conté l'array 
	 * data. 
	 * @param data: Variable del tipus ArrayList<Object[]> omplerta amb la informació de la taula que es mostrarà al menú.
	 */
	public void setDataTablePoints(ArrayList<Object[]> data){
		vista.resetTablePoints();
		for(int i=0;i<data.size();i++){
			vista.setDataTablePoints(data.get(i));
		}
		if(data.size()!=0){
			vista.actualizarJugador();
		}
	}
	
	/**
	 * Procediment que ens permet omplir el submenú de selecció de l'apartat de mostrar la gràfica amb els noms dels usuaris
	 * registrats per tal de que siguin accessibles.
	 * @param nicknames: Array de String que conté tots els nicknames dels usuaris
	 */
	public void setDataComboBox(ArrayList<String> nicknames){
		String[] names = new String[nicknames.size()];
		for(int i=0;i<nicknames.size();i++){
			names[i] = nicknames.get(i);
		}
		vista.setNoms(names);
	}
	
	/**
	 * Aquesta funció ens permet inserir un nou client a la bases de dades del nostre sistema. Aquesta funció reb el nom, 
	 * el nickname i la contrasenya empleats per l'usuari per tal de després enviar-li a la classe GestioPartida.
	 * @param nom: Variable del tipus String que conté el nom de l'usuari
	 * @param nickname: Variable del tipus String que conté el Nickname del usuari
	 * @param contrasena: Variable del tipus String que conté la contrasenya de l'usuari.
	 * @return resultat de la inserció de les dades a la bbdd.
	 */
	public boolean inserirUsuariCLient(String nom,String nickname,String contrasena){
			return partida.inserirUsuariClient(nickname, nom, contrasena);	
	}
	
	public void actualitzaUsuari(){
		vista.actualizarJugador();

	}
	
	/**
	 * Aquesta funció s'encarrega de tramitar l'accés al sistema com a jugador registrat a la base de dades. Aquesta rep un nickname
	 * i una contrasenya i els passa a la classe GestioPartida per a que aquesta comprovi si coincideixen amb les dades de la bbdd.
	 * @param nickname: Variable del tipus String que conté el Nickname del usuari
	 * @param contrasena: Variable del tipus String que conté la contrasenya de l'usuari.
	 * @return resultat de la consulta a la bbdd.
	 */
	public int loginClient(String nickname,String contrasena){
		return partida.loginClient(nickname, contrasena);
	}
	
	public void setNetwork(String IP,int port){
		network = new Network(port,this,IP);
	}
	
	/**
	 * Aquesta funió com el seu nom indica s'encarrega d'actualitzar la puntuació d'un jugador a la base de dades. Aquesta rep
	 * com a paràmetres el nickname del jugador i la puntuació per així trobar-lo dintre de la bbdd i poder modificar la seva
	 * puntuació.
	 * @param punts: Variable de tipus enter que conté la puntuació obtinguda per l'usuari a la última partida.
	 * @param nickname: Variable del tipus String que conté el Nickname del usuari
	 * @return resultat de la operació a la bbdd. 
	 */
	public int updatePuntos(int punts,String nickname,int modoJuego){
		return partida.actualitzarPunts(punts,nickname,modoJuego);
	}
	
	/**
	 * Aquest procedmient com el seu nom indica és l'encarregat de actualitzar les taules de la base de dades.
	 */
	public void updateTable(){
		partida.actualizarTablas();
	}
	
	/**
	 * Aquesta funció ens permet obtenir la taula que es mostra al apartat de ranking del nostre menú principal.
	 * @return: variable tipus JTable que conté les dades del ranking
	 */
	public JTable getTableView(){
		return vista.getTablaClassificacio();
	}
	
	
	public ArrayList<String> selectGraficMemoria(String nickname){
		ArrayList<String> memoria = partida.selecPartidasMemoriaUser(nickname);
		return memoria;
	}
	
	public ArrayList<String> selectGraficConcentracio(String nickname){
		ArrayList<String> concentracio = partida.selectPartidasConcentracioUser(nickname);
		return concentracio;
	}
	
	public void setPuntsGrafica(ArrayList<String> memoria, ArrayList<String> concentracio,boolean select){
		if(select==true){
			vista.cambiarGrafic(concentracio);
		}else{
			vista.cambiarGrafic(memoria);
		}
	}
	
	public void eliminarTablasBaseDatos(){
		partida.dropDataBase();
	}
	
	public int getNumPartides(){
		return numPartides;
	}
	
	public void setNumPartides(int numPartides){
		this.numPartides = numPartides;
		vista.setNumPartides(numPartides);
	}
}
