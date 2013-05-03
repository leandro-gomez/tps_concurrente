package concubattles;

import ar.edu.unq.tpi.pconc.Channel;

public class Castle extends Place {

	boolean live;
	boolean winner;
	int count;	

	@Override
	public String toString() {
		return "Castle of team: " + this.id;
	}

	public Castle(Channel<String> controlChannel, int castleID) {
		super(controlChannel, castleID);
		this.live = true;
		this.winner = false;
		this.count = 0;		
	}

	public int getNextSoldierID() {
		int next = this.count;
		this.count++;
		return next;
	}

	public void createSoldier() {
		Soldier x = new Soldier(this, this, this.getNextSoldierID());
		this.getSoldiers().add(x);
		x.start();
		System.out.println(x.toString() + " Created");
	}

	@Override
	public void receive(Soldier soldier) {
		System.out.println(soldier.toString() + " In Castle");
		soldier.setPrevious_place(soldier.getMy_place());
		soldier.setMy_place(this);
		if (!(this == soldier.getTeam())) {
			this.startBattle(soldier);
		} else {
			this.getSoldiers().add(soldier);
		}

	}

	public void win() {
		System.out.println(this.toString() + " Win!");
		this.live = false;
		this.winner = true;
	}

	@Override
	public void conqueredBy(Soldier soldier) {
		System.out.println(this.toString() + " Conquered");
		this.live = false;
		soldier.getTeam().win();
	}

}
