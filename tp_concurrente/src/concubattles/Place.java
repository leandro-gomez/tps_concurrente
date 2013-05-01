package concubattles;

import java.util.ArrayList;

import concubattles.Soldier;

public abstract class Place {
	private Channel<String> controlChannel;
	private ArrayList<Soldier>soldiers = new ArrayList<Soldier>();
	private ArrayList<Place>roads = new ArrayList<Place>();

	public ArrayList<Place> getRoads() {
		return roads;
	}

	public void setRoads(ArrayList<Place> roads) {
		this.roads = roads;
	}

	public Channel<String> getControlChannel() {
		return controlChannel;
	}

	public void setControlChannel(Channel<String> controlChannel) {
		this.controlChannel = controlChannel;
	}

	public ArrayList<Soldier> getSoldiers() {
		return this.soldiers;
	}

	public void setSoldiers(ArrayList<Soldier> soldiers) {
		this.soldiers = soldiers;
	}

	public Place(Channel<String> controlChannel) {
		this.controlChannel = controlChannel;
	}

	public void getPermission() {
		this.controlChannel.receive();
	}

	public void returnPermission() {
		this.controlChannel.send("");
	}

	/**
	 * debe retornar el siguiente lugar a donde el soldado puede ir, 
	 * teniendo en cuenta el lugar previo (puede ser null)
	 *  
	 * @param previous_place
	 * @return Place subclass instance
	 */
	 // SE ME OCURRIO QUE CUANDO SE CREA UN SOLDADO EN EL CASTILLO, SU LUGAR PREVIO ES EL CASTILLO
	// ASI NOS AHORRAMOS EL TEMA DE PENSAR QUE PASA SI SU LUGAR PREVIO ES "NULL"
	// Va a retornar un lugar por medio de random
	// ELIJO UN NUMERO RANDOM ENTRE EL 0 Y LA LONGITUD DE LA LISTA PLACES (QUE ES LA MISMA LISTA QUE 
	// LA LISTA QUE POSEE EL LUGAR DE TODOS LOS LUGARES PERO SIN EL LUGAR 
	// PREVIO DONDE ESTUVO EL SOLDADO.. Y OBTENGO EL LUGAR QUE OCUPA 
	// EL LUGAR "X" DE MI LISTA Y ESE VA A SER EL PROXIMO LUGAR A MOVERME..
	public Place getNextPlace(Place previous_place){
		ArrayList<Place>places = this.getRoads();
		places.remove(previous_place);
		int x = (int) (Math.random() * (places.size()));
		return this.getRoads().get(x);
	}

	/**
	 * La primera version de este metodo debe resolver batallas si las hubiera.
	 * agregar al soldado a si mismo, etc
	 * Debe setear el valor de la variable live de soldado en caso de estar muerto.
	 * TODO: Que lo haga un thread aparte.
	 * @param soldier
	 */
	 //  NO PUEDO CREAR UN THREAD EN UN METODO.. SOLO ME DEJA EN EL MAIN
	// ME TIRA EL ERROR DE QUE EL THREAD NO CONOCE LOS GETTERS POR EJEMPLO DE LA CLASE
	// SIN EMBARGO AGREGE EN STARTBATTLE EL TEMA DE SETEAR LA VARIABLE DE VIDA
	// Y ENVIO AL CASTILLO EL PERMISO DE CREAR SOLDADO (LO HIZE CON MENSAJES (A LO OBJETOS))
	// SI QUERES HACERLO CON CANALES SOLO HAY QUE MODIFICAR UNA BOLUDEZ
	public void receive(Soldier soldier){
		this.startBattle(soldier);
	}

	/**
	 * Talavez simplemente remueve el soldado de la lista de soldados
	 * @param soldier
	 */
	// TE CREO EL METODO PERO NO ENTIENDO PORQUE LO QUERES
	// SIMPLEMENTE AL GETTER LE REMOVES EL SOLDADO
	public void remove(Soldier soldier){
		this.getSoldiers().remove(soldier);
	}

	/**
	 * Toma 2 soldados les hace un random a cada uno de acuerdo al nivel y
	 * retorna el que tiene el numero mas grande
	 * 
	 * @param soldier
	 * @param soldierEnemy
	 * @return soldado ganador
	 */
	public Soldier fight(Soldier soldier, Soldier soldierEnemy) {
		int x = (int) (Math.random() * soldier.getLevel());
		int y = (int) (Math.random() * soldierEnemy.getLevel());
		if (x < y) {
			return soldier;
		} else {
			return soldierEnemy;
		}
	}

	/**
	 * Llega un soldado enemigo.. Si este le gana a todos los soldados enemigos,
	 * se agrega el soldado a la lista de soldados del castillo o ciudad ya que
	 * posee un nuevo ocupante sino el soldado pierde y se interrumpe la
	 * iteracion. (ver este tema de la batalla) se debe ver el tema de la
	 * concurrencia.
	 */
	 // se fija si el soldado muerto era mayor a level 1, si lo era
	 // le envio al castillo un permiso para crear un soldado
	// HAY QUE AGREGAR LOS METODOS PARA QUE SE VEAN EN LA INTERFAZ
	public void startBattle( Soldier soldierEnemy) {
		Soldier x = soldierEnemy;
		if (this.getSoldiers().isEmpty()) {
			this.getSoldiers().add(x);
		} else {
			for (Soldier s : this.getSoldiers()) {
				if (s.equals(this.fight(s, x))) {
					s.experienceUp();
					s.getTeam().createSoldier();             // envio de mensaje para crear un soldado
					x.setLive(false);                        // modificacion del seteo de vida
					x.checkForLevel();                       // verificacion del soldado
					break;
				} else {
					x.experienceUp();
					x.getTeam().createSoldier();
					s.checkForLevel();
					s.setLive(false);
					this.getSoldiers().remove(s);
				}
			}
		}
	}	

}
