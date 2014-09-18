package main;

//EXPANSION
//V.1.0.0.5.4
//Planet info is displayed when hovering over a planet
//

/*
 PROGRAM DESCRIPTION:
 Take over the galaxy by capturing planets and expanding your control
 Each turn you get a chance to coordinate your forces strategically

 OTHER INFORMATION:
 Made by
 Skylar Hoffert
 Zen Bug
 Eero Kelly
 */

//
//stage 0 is running stage
//stage 1 is start up menu
//stage 2 is pause menu
//stage 3 is planet menu
//stage 4 is turn report
//stage 5 is fight screen
//stage 6 is about screen
//stage 7 is about ships screen
//stage 8 is help screen
//stage 9 is help ships screen
//stage 10 is settings, out of game
//
//used on planets
//BUILD TYPES:
//1 - Expander
//2 - Scout
//3 - Defender
//4 - Destroyer
//5 - Town
//6 - Mine
//7 - Academy
//8 - Quantum Tech
//9 - Turret
//10 - Radar Tech
//11 - War Factory
//12 - Plasma Tech
//13 - fighter
//14 - tank
//15 - disabler
//16 - excavate
//17 - clone
//18 - harvester
//19 - engineer
//20 - carrier
//21 - EMP Tech
//22 - Molecular Fusion
//23 - Skid

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import planets.*;
import ships.DeadShip;
import ships.Ship;
import ships.allied.*;
import ships.enemy.*;

public class Expansion extends JPanel implements Runnable {
	// for init phase
	private static final int WIDTH = 300; // Width of screen
	private static final int HEIGHT = 200; // Height of screen
	private static final int SCALE = 4; // Scale
	private static final String NAME = "Expansion"; // Title

	// panel
	public static Panel panel;
	private static Container pane;
	private static JFrame theG;

	// running variables
	private static boolean running = false; // main loop variable
	private int tickCount = 0;

	// public variables
	public static int xMouse, yMouse, stage = 1, numSelected = 0,
			indexSelected_ship,
			indexOnPlanet_Expander,
			indexOnPlanet_Destroyer,
			indexOnPlanet_Carrier, // number of ships selected;
			indexSelected_planet, planetType, planetsColonized = 0,
			indexFighting_1, indexFighting_2, materials = 1000, people = 1000,
			knowledge = 100, turnNumber = 0, menuType, stage_Objective = 0, difficulty = 0;
	//0 is easy
	//1 is medium
	//2 is hard

	// values that are used in game
	private static int knowledgeFreq = 5, homePlanetHarvestAmount = 1,
			homePlanetPopulationGrowth = 1, fightersTrained = 0, enemiesKilled = 0;

	/*
	 * 0 planet type is home planet 1 planet type is uncolonized, to be home
	 * planet 2 planet type is any uncolonized planet needing expander 3 planet
	 * type is any uncolonized planet with expander on it 4 planet type is
	 * attack planet 5 planet type is mining planet
	 */

	// constants
	public static int edge = 25000;
	private static final int NUM_PLANETS = 78, RESTRICT_PLANET_SIZE = 2000;

	// privates
	// ints for actual components of game
	private static int xSelection, ySelection; // start of the selection box

	private static boolean isScreenMovable = true, // Dictates if the screen is
													// movable
			isMB1Down = false, // Dictates if the left mouse button is down
			isMouseLocked = false, // locks cursor
			isDDown = false, isADown = false, isWDown = false,
			isSDown = false,
			isShipMoving = false,
			isTurnReportPrimed = false,
			isPlanetMenuUp = false,
			isAcademyBought = false,
			isQuantumTechBought = false,
			isEMPTechBought = false,
			isMolecularFusionBought = false, isAFight = false;

	// arrays
	public static ArrayList<Planet> planets = new ArrayList<Planet>(); // Creates planets array
	public static ArrayList<Ship> ships = new ArrayList<Ship>(); // Creates the ship array
	public static Clickable[] clickables = new Clickable[8]; // clickable array
	public static Clickable[] menuClickables = new Clickable[8]; // clickable array
	public static ArrayList<String> news = new ArrayList<String>();

	// the main game this makes it
	// run************************************************************************************************
	private Expansion() {
		// Init phase
		theG = new JFrame(NAME);
		theG.setUndecorated(true);
		theG.setSize(WIDTH * SCALE, HEIGHT * SCALE);
		theG.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		theG.setResizable(false);
		theG.setLocationRelativeTo(null);
		panel = new Panel(Color.black);
		pane = theG.getContentPane();
		pane.add(panel);
		theG.setVisible(true);

		// listeners
		mLis ml = new mLis();
		mMotLis mml = new mMotLis();
		KL kl = new KL();

		// adding listeners
		panel.addMouseListener(ml);
		panel.addMouseMotionListener(mml);
		theG.addKeyListener(kl);

		// Cursor things
		BufferedImage cursor = new BufferedImage(16, 16,
				BufferedImage.TYPE_INT_ARGB);
		Cursor invisible = Toolkit.getDefaultToolkit().createCustomCursor(
				cursor, new Point(0, 0), "Blank cursor");
		theG.getContentPane().setCursor(invisible);

		// startup clickables
		createStartUpClickables();
	}

	// main loop
	public static void main(String[] args) {
		// Creates an Expansion and calls the start method
		new Expansion().start();
	}

	// called to start, runs in a thread
	private synchronized void start() {
		running = true;
		// calls the run method
		new Thread(this).start();
	}

	// stop - obv not used
	private synchronized void stop() {
		running = false;
	}

	// the game loop
	public void run() {
		// for frame rate etc
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000.0 / 60.0;
		int frames = 0;
		int ticks = 0;
		long lastTimer = System.currentTimeMillis();
		long now;
		double delta = 0;

		// Main Game Loop
		while (running) {
			// for fps purposes
			now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;
			boolean shouldRender = true;

			// more fps
			while (delta >= 1) {
				ticks++;
				tick();
				delta -= 1;
				shouldRender = true;
			}

			// sleep
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// every time a frame happens
			if (shouldRender) {
				frames++;
				render();
			}

			// for fps again
			if (System.currentTimeMillis() - lastTimer >= 1000) {
				lastTimer += 1000;
				System.out.println(frames + " frames , " + ticks + " ticks");
				frames = 0;
				ticks = 0;
			}
		}

		// Closes when main loop is over.
		theG.dispatchEvent(new WindowEvent(theG, WindowEvent.WINDOW_CLOSING));
	}

	// ticks control
	private void tick() {
		tickCount++;
	}

	// *****************************************************************************************************************************
	// Called all of the time
	private void render() {
		// running
		if (stage == 0) {
			// screen movement
			if (isDDown) {moveObjects(-2, 0);}
			if (isADown) {moveObjects(2, 0);}
			if (isWDown) {moveObjects(0, 2);}
			if (isSDown) {moveObjects(0, -2);}

			// calculates if a ship is still moving
			isShipMoving = false;
			for (int i = 0; i < ships.size(); i++) {
				if (ships.get(i).getIsMoving()) {
					ships.get(i).move();
					isShipMoving = true;
				}

				for (int j = 0; j < ships.size(); j++) {
					if (i != j) {
						// fighting
						if (ships.get(j) != null){
							if (findDistance(ships.get(i).getX(), ships.get(i)
									.getY(), ships.get(j).getX(), ships.get(j)
									.getY()) <= 10 * ships.get(i).getRange()
									|| findDistance(ships.get(i).getX(),
											ships.get(i).getY(), ships.get(j)
													.getX(), ships.get(j).getY()) <= 10 * ships
											.get(j).getRange()) {
								if (!(ships.get(i) instanceof DeadShip)
										&& !(ships.get(j) instanceof DeadShip)) {
									if (ships.get(i) instanceof EnemyFighter || 
										ships.get(i) instanceof EnemyScout){
										if (!(ships.get(j) instanceof EnemyFighter_Moving) &&
											!(ships.get(j) instanceof EnemyScout_Moving) &&
											!(ships.get(j) instanceof EnemyScout && 
											!(ships.get(j) instanceof EnemyFighter))){
											
											indexFighting_1 = i;
											indexFighting_2 = j;
											// fight menu stage
											clickables[0] = new Clickable("FightMenu", panel.getWidth() / 2,panel.getHeight() / 2, 900, 600);
											clickables[1] = new Clickable("FightButton", 3 * panel.getWidth() / 4, panel.getHeight() / 3, 200, 100);
											clickables[2] = new Clickable("FleeButton", 3 * panel.getWidth() / 4, 2 * panel.getHeight() / 3, 200, 100);
											panel.setShipsFighting(true, ships.get(i), ships.get(j));
											isAFight = true;
											stage = 5;
										}
									}
								}
							}
						}
					}
				}
			}

			if (!isShipMoving && isTurnReportPrimed) {
				openTurnReport();
			}
		}

		// always done
		if (isMouseLocked) {
			screenCollision();
		}
		
		// Tells the panel to draw everything else.
		panel.drawing();
	}

	// *******************************************************************************************************************************
	// game functions
	// *******************************************************************************************************************************
	// function called whenever user left clicks
	private static void leftClick(int x, int y) {
		// if the game is running
		if (stage == 0) {
			// resests the cursor type and selects nothing
			numSelected = 0;
			// if the click is not the start of the selection
			if (x != xSelection && y != ySelection) {
				// goes thorugh all ships
				for (int i = 0; i < ships.size(); i++) {
					if (ships.get(i) != null) {
						// checks to see if ship collides with click
						if (ships.get(i).collidesWith(x, y)) {
							ships.get(i).select(true);
							numSelected++;
							indexSelected_ship = i;
							//panel.setCursorType(getCursorType());
						} else {
							// deselects
							ships.get(i).select(false);
						}
					}
				}
				// if the game is not running, at a menu or other
			} else {
				// deselects all ships
				for (int i = 0; i < ships.size(); i++) {
					if (ships.get(i) != null) {
						ships.get(i).select(false);
					}
				}
				// checks for planet collision
				for (int i = 0; i < planets.size(); i++) {
					if (planets.get(i) != null) {
						if (findDistance(x, y, planets.get(i).getX(), planets
								.get(i).getY()) < planets.get(i).getRadius()) {
							// planet menu stuff
							stage = 3;
							indexSelected_planet = i;
							// note:
							// the first clickable is just the background, not
							// actually functioning as a clickable
							clickables[0] = new Clickable(
									"PlanetMenuBackground",
									panel.getWidth() / 2,
									panel.getHeight() / 2,
									6 * panel.getWidth() / 8,
									6 * panel.getHeight() / 8);
							clickables[1] = new Clickable("ResumeButton",
									panel.getWidth() / 8 + 185,
									7 * panel.getHeight() / 8 - 75, 200, 100);
							if (planets.get(indexSelected_planet) instanceof HomePlanet) {
								clickables[0] = new Clickable(
										"PlanetMenuBackground_Home",
										panel.getWidth() / 2,
										panel.getHeight() / 2,
										6 * panel.getWidth() / 8,
										6 * panel.getHeight() / 8);
								// clickable 1 is resume button
								clickables[2] = new Clickable(
										"BuildMenuButton",
										7 * panel.getWidth() / 8 - 400,
										7 * panel.getHeight() / 8 - 75, 200,
										100);
								clickables[3] = new Clickable(
										"TrainMenuButton",
										7 * panel.getWidth() / 8 - 175,
										7 * panel.getHeight() / 8 - 75, 200,
										100);
								// for...
								if (carrierCollidesWithPlanet()) {
									clickables[4] = new Clickable(
											"ExtraBottomMenu",
											panel.getWidth() / 2,
											7 * panel.getHeight() / 8 + 25,
											900, 100);
									clickables[5] = new Clickable(
											"LandPeopleOnCarrierButton",
											panel.getWidth() / 2,
											7 * panel.getHeight() / 8 + 25,
											700, 60);
								}
								// planet is a home planet
								planetType = 0;
							} else if (planets.get(indexSelected_planet) instanceof AttackPlanet) {
								clickables[0] = new Clickable(
										"PlanetMenuBackground_Attack",
										panel.getWidth() / 2,
										panel.getHeight() / 2,
										6 * panel.getWidth() / 8,
										6 * panel.getHeight() / 8);
								// clickable 1 is resume button
								clickables[2] = new Clickable(
										"BuildMenuButton",
										7 * panel.getWidth() / 8 - 400,
										7 * panel.getHeight() / 8 - 75, 200,
										100);
								clickables[3] = new Clickable(
										"TrainMenuButton",
										7 * panel.getWidth() / 8 - 175,
										7 * panel.getHeight() / 8 - 75, 200,
										100);
								// for.....
								if (carrierCollidesWithPlanet()) {
									clickables[4] = new Clickable(
											"ExtraBottomMenu",
											panel.getWidth() / 2,
											7 * panel.getHeight() / 8 + 25,
											900, 100);
									clickables[5] = new Clickable(
											"LandPeopleOnCarrierButton",
											panel.getWidth() / 2,
											7 * panel.getHeight() / 8 + 25,
											700, 60);
								}
								// planet is a attack planet
								planetType = 4;

							} else if (planets.get(indexSelected_planet) instanceof MiningPlanet) {
								clickables[0] = new Clickable(
										"PlanetMenuBackground_Mining",
										panel.getWidth() / 2,
										panel.getHeight() / 2,
										6 * panel.getWidth() / 8,
										6 * panel.getHeight() / 8);
								// clickable 1 is resume button
								clickables[2] = new Clickable(
										"BuildMenuButton",
										7 * panel.getWidth() / 8 - 400,
										7 * panel.getHeight() / 8 - 75, 200,
										100);
								clickables[3] = new Clickable(
										"TrainMenuButton",
										7 * panel.getWidth() / 8 - 175,
										7 * panel.getHeight() / 8 - 75, 200,
										100);
								// planet is a mining planet
								planetType = 5;

							} else if (planets.get(indexSelected_planet) instanceof TechPlanet) {
								clickables[0] = new Clickable(
										"PlanetMenuBackground_Tech",
										panel.getWidth() / 2,
										panel.getHeight() / 2,
										6 * panel.getWidth() / 8,
										6 * panel.getHeight() / 8);
								// clickable 1 is resume button
								clickables[2] = new Clickable(
										"BuildMenuButton",
										7 * panel.getWidth() / 8 - 400,
										7 * panel.getHeight() / 8 - 75, 200,
										100);
								clickables[3] = new Clickable(
										"TrainMenuButton",
										7 * panel.getWidth() / 8 - 175,
										7 * panel.getHeight() / 8 - 75, 200,
										100);
								// for....
								if (carrierCollidesWithPlanet()) {
									clickables[4] = new Clickable(
											"ExtraBottomMenu",
											panel.getWidth() / 2,
											7 * panel.getHeight() / 8 + 25,
											900, 100);
									clickables[5] = new Clickable(
											"LandPeopleOnCarrierButton",
											panel.getWidth() / 2,
											7 * panel.getHeight() / 8 + 25,
											700, 60);
								}
								// planet is a tech planet
								planetType = 6;

							} else {
								// makes sure the next planet will be a home
								// planet
								if (planetsColonized == 0) {
									// checks for collision
									if (expanderCollidesWithPlanet()) {
										// creates the colonization clickable
										clickables[2] = new Clickable(
												"ColonizeAsHomePlanetButton",
												7 * panel.getWidth() / 8 - 275,
												7 * panel.getHeight() / 8 - 75,
												400, 100);
										planetType = 1;
									} else {
										// tells the user they need an expander
										clickables[2] = new Clickable(
												"ExpanderNeededButton",
												7 * panel.getWidth() / 8 - 275,
												7 * panel.getHeight() / 8 - 75,
												400, 100);
										planetType = 2;
									}
								} else {
									// for anything not the first planet being
									// colonized
									if (expanderCollidesWithPlanet()) {
										clickables[2] = new Clickable(
												"ColonizeAsAttackPlanetButton",
												7 * panel.getWidth() / 8 - 475,
												7 * panel.getHeight() / 8 - 75,
												150, 100);
										clickables[3] = new Clickable(
												"ColonizeAsMiningPlanetButton",
												7 * panel.getWidth() / 8 - 300,
												7 * panel.getHeight() / 8 - 75,
												150, 100);
										clickables[4] = new Clickable(
												"ColonizeAsTechPlanetButton",
												7 * panel.getWidth() / 8 - 125,
												7 * panel.getHeight() / 8 - 75,
												150, 100);
										planetType = 3;
									} else if (destroyerCollidesWithPlanet()) {
										clickables[2] = new Clickable(
												"DestroyPlanetButton",
												7 * panel.getWidth() / 8 - 275,
												7 * panel.getHeight() / 8 - 75,
												400, 100);
										planetType = 7;
									} else {
										clickables[2] = new Clickable(
												"ExpanderNeededButton",
												7 * panel.getWidth() / 8 - 275,
												7 * panel.getHeight() / 8 - 75,
												400, 100);
										planetType = 2;
									}
								}
							}
							panel.setDrawingSelectionBox(false);
						}
					}

					/*
					 * 0 planet type is home planet 1 planet type is
					 * uncolonized, to be home planet 2 planet type is any
					 * uncolonized planet needing expander 3 planet type is any
					 * uncolonized planet with expander on it 4 planet type is
					 * attack planet 5 planet type is mining planet 6 planet
					 * type is tech planet 7 planet type is planet with
					 * destroyer on it
					 */

				}
			}
			// if the stage is not stage 0 (running stage)
		} else {
			// checks clickables
			// which only are there when the stage is not 0
			for (int i = clickables.length - 1; i >= 0; i--) {
				// goes through the build menu clickables
				// simultaneously with the actual clickables
				// this is because they are the same length
				// efficiency gentlemen
				if (menuClickables[i] != null) {
					if (rectPointCollision(
							menuClickables[i].getX()
									- menuClickables[i].getWidth() / 2,
							menuClickables[i].getY()
									- menuClickables[i].getHeight() / 2,
							menuClickables[i].getWidth(),
							menuClickables[i].getHeight(), x, y)) {
						// planet menu stage
						if (stage == 3) {
							// build menu
							if (menuType == 0) {
								if (i == 1) {
									// the 'X' Button
									deleteMenuClickables();
									isPlanetMenuUp = false;
								} else if (i == 2) {
									if (planetType == 0 || planetType == 5) {
										// the build town button
										if (planets.get(indexSelected_planet)
												.getBuildTime() == 0) {
											if (materials >= 60) {
												materials -= 60;
												planets.get(
														indexSelected_planet)
														.setToBeMade(5, 2);
												deleteMenuClickables();
												isPlanetMenuUp = false;
												return;
											}
										}
									} else if (planetType == 4) {
										// the build Turret button
										if (planets.get(indexSelected_planet)
												.getBuildTime() == 0) {
											if (materials >= 70) {
												materials -= 70;
												planets.get(
														indexSelected_planet)
														.setToBeMade(9, 2);
												deleteMenuClickables();
												isPlanetMenuUp = false;
												return;
											}
										}
									} else if (planetType == 6) {
										// the build Academy button
										if (planets.get(indexSelected_planet)
												.getBuildTime() == 0) {
											if (knowledge >= 15) {
												if (materials >= 90) {
													materials -= 90;
													planets.get(
															indexSelected_planet)
															.setToBeMade(7, 3);
													deleteMenuClickables();
													isPlanetMenuUp = false;
													return;
												}
											}
										}
									}
								} else if (i == 3) {
									if (planetType == 0 || planetType == 5) {
										// the build mine button
										if (planets.get(indexSelected_planet)
												.getBuildTime() == 0) {
											if (materials > 60) {
												materials -= 60;
												planets.get(
														indexSelected_planet)
														.setToBeMade(6, 2);
												deleteMenuClickables();
												isPlanetMenuUp = false;
												return;
											}
										}
									} else if (planetType == 4) {
										// the build Radar Tech button
										if (planets.get(indexSelected_planet)
												.getBuildTime() == 0) {
											if (!((AttackPlanet) planets
													.get(indexSelected_planet))
													.getIsRadarTechBought()) {
												if (knowledge >= 10) {
													if (materials >= 80) {
														materials -= 80;
														planets.get(
																indexSelected_planet)
																.setToBeMade(
																		10, 3);
														deleteMenuClickables();
														isPlanetMenuUp = false;
														return;
													}
												}
											}
										}
									} else if (planetType == 6) {
										// the build EMP Tech button
										if (planets.get(indexSelected_planet)
												.getBuildTime() == 0) {
											if (knowledge >= 60) {
												if (isQuantumTechBought) {
													if (materials >= 100) {
														materials -= 100;
														planets.get(
																indexSelected_planet)
																.setToBeMade(
																		21, 4);
														deleteMenuClickables();
														isPlanetMenuUp = false;
														return;
													}
												}
											}
										}
									}
								} else if (i == 4) {
									if (planetType == 0) {
										// the build academy button
										if (planets.get(indexSelected_planet)
												.getBuildTime() == 0) {
											if (planets.get(indexSelected_planet) instanceof HomePlanet){
												if (!isAcademyBought) {
													if (knowledgeFreq > 1) {
														if (knowledge >= 15) {
															if (materials > 90) {
																materials -= 90;
																planets.get(
																		indexSelected_planet)
																		.setToBeMade(
																				7,
																				3);
																deleteMenuClickables();
																isPlanetMenuUp = false;
																return;
															}
														}
													}
												}
											} else if (!((TechPlanet) planets.get(indexSelected_planet)).getIsAcademyBought()){
												if (knowledgeFreq > 1) {
													if (knowledge >= 15) {
														if (materials > 90) {
															materials -= 90;
															planets.get(
																	indexSelected_planet)
																	.setToBeMade(
																			7,
																			3);
															deleteMenuClickables();
															isPlanetMenuUp = false;
															return;
														}
													}
												}
											}
										}
									} else if (planetType == 4) {
										// the build War Factory button
										if (planets.get(indexSelected_planet)
												.getBuildTime() == 0) {
											if (!((AttackPlanet) planets
													.get(indexSelected_planet))
													.getIsWarFactoryBought()) {
												if (knowledge >= 20) {
													if (materials >= 120) {
														materials -= 120;
														planets.get(
																indexSelected_planet)
																.setToBeMade(
																		11, 4);
														deleteMenuClickables();
														isPlanetMenuUp = false;
														return;
													}
												}
											}
										}
									} else if (planetType == 5) {
										// the build Excavate button
										if (planets.get(indexSelected_planet)
												.getBuildTime() == 0) {
											planets.get(indexSelected_planet)
													.setToBeMade(16, 2);
											deleteMenuClickables();
											isPlanetMenuUp = false;
											return;
										}
									} else if (planetType == 6) {
										// the build molecular Fusion button
										if (planets.get(indexSelected_planet)
												.getBuildTime() == 0) {
											if (knowledge >= 95) {
												if (isQuantumTechBought) {
													if (materials >= 200) {
														materials -= 200;
														planets.get(
																indexSelected_planet)
																.setToBeMade(
																		22, 4);
														deleteMenuClickables();
														isPlanetMenuUp = false;
														return;
													}
												}
											}
										}
									}
								} else if (i == 5) {
									if (planetType == 0) {
										// the build quantum tech button
										if (planets.get(indexSelected_planet)
												.getBuildTime() == 0) {
											if (!isQuantumTechBought) {
												if (knowledge >= 30) {
													if (materials > 100) {
														materials -= 100;
														planets.get(
																indexSelected_planet)
																.setToBeMade(8,
																		5);
														deleteMenuClickables();
														isPlanetMenuUp = false;
														return;
													}
												}
											}
										}
									} else if (planetType == 4) {
										// the build Plasma Tech button
										if (planets.get(indexSelected_planet)
												.getBuildTime() == 0) {
											if (!((AttackPlanet) planets
													.get(indexSelected_planet))
													.getIsPlasmaTechBought()) {
												if (knowledge >= 80) {
													if (materials >= 150) {
														materials -= 150;
														planets.get(
																indexSelected_planet)
																.setToBeMade(
																		12, 5);
														deleteMenuClickables();
														isPlanetMenuUp = false;
														return;
													}
												}
											}
										}
									} else if (planetType == 5) {
										// the build Turret button
										if (planets.get(indexSelected_planet)
												.getBuildTime() == 0) {
											if (knowledge > 40) {
												if (materials >= 100) {
													materials -= 100;
													planets.get(
															indexSelected_planet)
															.setToBeMade(17, 2);
													deleteMenuClickables();
													isPlanetMenuUp = false;
													return;
												}
											}
										}
									}
								}
								// train menu
							} else if (menuType == 1) {
								if (i == 1) {
									// the 'X' Button
									deleteMenuClickables();
									isPlanetMenuUp = false;
								} else if (i == 2) {
									if (planetType == 0) {
										// the build expander button
										if (planets.get(indexSelected_planet)
												.getBuildTime() == 0) {
											if (materials >= 100
													&& planets
															.get(indexSelected_planet)
															.getPopulation() >= 40) {
												materials -= 100;
												planets.get(
														indexSelected_planet)
														.takePeople(40);
												planets.get(
														indexSelected_planet)
														.setToBeMade(1, 3);
												deleteMenuClickables();
												isPlanetMenuUp = false;
												return;
											}
										}
									} else if (planetType == 4) {
										// the build fighter button
										if (planets.get(indexSelected_planet)
												.getBuildTime() == 0) {
											if (materials >= 120
													&& planets
															.get(indexSelected_planet)
															.getPopulation() >= 15) {
												materials -= 120;
												planets.get(
														indexSelected_planet)
														.takePeople(15);
												planets.get(
														indexSelected_planet)
														.setToBeMade(13, 4);
												deleteMenuClickables();
												isPlanetMenuUp = false;
												return;
											}
										}
										// the train harvester button
									} else if (planetType == 5) {
										if (planets.get(indexSelected_planet)
												.getBuildTime() == 0) {
											if (materials >= 100
													&& planets
															.get(indexSelected_planet)
															.getPopulation() >= 40) {
												materials -= 100;
												planets.get(
														indexSelected_planet)
														.takePeople(40);
												planets.get(
														indexSelected_planet)
														.setToBeMade(18, 3);
												deleteMenuClickables();
												isPlanetMenuUp = false;
												return;
											}
										}
									} else if (planetType == 6) {
										// the build skid button
										if (planets.get(indexSelected_planet)
												.getBuildTime() == 0) {
											if (materials >= 50
													&& planets
															.get(indexSelected_planet)
															.getPopulation() >= 10) {
												materials -= 50;
												planets.get(
														indexSelected_planet)
														.takePeople(10);
												planets.get(
														indexSelected_planet)
														.setToBeMade(23, 1);
												deleteMenuClickables();
												isPlanetMenuUp = false;
												return;
											}
										}
									}
									// the train harvester button
								} else if (i == 3) {
									if (planetType == 0 || planetType == 4) {
										// the build scout button
										if (knowledge >= 5) {
											if (planets.get(
													indexSelected_planet)
													.getBuildTime() == 0) {
												if (materials >= 75
														&& planets
																.get(indexSelected_planet)
																.getPopulation() >= 15) {
													materials -= 75;
													planets.get(
															indexSelected_planet)
															.takePeople(15);
													planets.get(
															indexSelected_planet)
															.setToBeMade(2, 3);
													deleteMenuClickables();
													isPlanetMenuUp = false;
													return;
												}
											}
										}
									} else if (planetType == 5) {
										// the build engineer button
										if (knowledge >= 20) {
											if (planets.get(
													indexSelected_planet)
													.getBuildTime() == 0) {
												if (materials >= 150
														&& planets
																.get(indexSelected_planet)
																.getPopulation() >= 35) {
													materials -= 150;
													planets.get(
															indexSelected_planet)
															.takePeople(35);
													planets.get(
															indexSelected_planet)
															.setToBeMade(19, 4);
													deleteMenuClickables();
													isPlanetMenuUp = false;
													return;
												}
											}
										}
									}
								} else if (i == 4) {
									if (planetType == 0) {
										// the build defender button
										if (knowledge >= 35) {
											if (planets.get(
													indexSelected_planet)
													.getBuildTime() == 0) {
												if (materials >= 200
														&& planets
																.get(indexSelected_planet)
																.getPopulation() >= 40) {
													materials -= 200;
													planets.get(
															indexSelected_planet)
															.takePeople(40);
													planets.get(
															indexSelected_planet)
															.setToBeMade(3, 5);
													deleteMenuClickables();
													isPlanetMenuUp = false;
													return;
												}
											}
										}
									} else if (planetType == 4) {
										// the build Tank button
										if (knowledge >= 45) {
											if (planets.get(
													indexSelected_planet)
													.getBuildTime() == 0) {
												if (materials >= 220
														&& planets
																.get(indexSelected_planet)
																.getPopulation() >= 30) {
													materials -= 220;
													planets.get(
															indexSelected_planet)
															.takePeople(15);
													planets.get(
															indexSelected_planet)
															.setToBeMade(14, 6);
													deleteMenuClickables();
													isPlanetMenuUp = false;
													return;
												}
											}
										}
									} else if (planetType == 5) {
										// the build carrier button
										if (knowledge >= 30) {
											if (planets.get(
													indexSelected_planet)
													.getBuildTime() == 0) {
												if (materials >= 120
														&& planets
																.get(indexSelected_planet)
																.getPopulation() >= 100) {
													materials -= 120;
													planets.get(
															indexSelected_planet)
															.takePeople(100);
													planets.get(
															indexSelected_planet)
															.setToBeMade(20, 5);
													deleteMenuClickables();
													isPlanetMenuUp = false;
													return;
												}
											}
										}
									}
								} else if (i == 5) {
									if (planetType == 0) {
										// the build destroyer button
										if (knowledge >= 100) {
											if (((TechPlanet) planets.get(indexSelected_planet)).getIsMolecularFusionBought()) {
												if (planets.get(
														indexSelected_planet)
														.getBuildTime() == 0) {
													if (materials >= 500
															&& planets
																	.get(indexSelected_planet)
																	.getPopulation() >= 80) {
														materials -= 500;
														planets.get(
																indexSelected_planet)
																.takePeople(80);
														planets.get(
																indexSelected_planet)
																.setToBeMade(4,
																		8);
														deleteMenuClickables();
														isPlanetMenuUp = false;
														System.out
																.println(":::"
																		+ isPlanetMenuUp);
														return;
													}
												}
											}
										}
									} else if (planetType == 4) {
										// the build disabler button
										if (knowledge >= 65) {
											if (isEMPTechBought) {
												if (planets.get(
														indexSelected_planet)
														.getBuildTime() == 0) {
													if (materials >= 200
															&& planets
																	.get(indexSelected_planet)
																	.getPopulation() >= 25) {
														materials -= 200;
														planets.get(
																indexSelected_planet)
																.takePeople(25);
														planets.get(
																indexSelected_planet)
																.setToBeMade(
																		15, 8);
														deleteMenuClickables();
														isPlanetMenuUp = false;
														return;
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}

				if (!isPlanetMenuUp) {
					if (clickables[i] != null) {
						if (rectPointCollision(
								clickables[i].getX() - clickables[i].getWidth()
										/ 2,
								clickables[i].getY()
										- clickables[i].getHeight() / 2,
								clickables[i].getWidth(),
								clickables[i].getHeight(), x, y)) {
							// so
							// i will be the clickable colliding
							// there are ifs determining what the stage is,
							// which will determine the course of action
							if (stage == 0) {

								// title menu
							} else if (stage == 1) {
								// starting the game
								if (i == 0) {
									stage = 0;
									panel.setBackground("GameBackground");
									createPlanets();
									isMouseLocked = true;
									deleteClickables();
									stage_Objective = 1;
									materials = 500 - 150*difficulty;
								} else if (i == 1) {
									//about screen
									deleteClickables();
									panel.setBackground("AboutScreen");
									stage = 6;
									clickables[0] = new Clickable(
											"AboutShipsButton",
											3 * panel.getWidth() / 4 + 100,
											14 * panel.getHeight() / 16,
											200, 100);
									clickables[1] = new Clickable(
											"ReturnButton",
											45, 45, 50, 50);
								} else if (i == 2) {
									deleteClickables();
									stage = 10;
									panel.setBackground("SettingsBackground");
									clickables[0] = new Clickable("EasyButton", 100, 335, 150, 100);
									clickables[1] = new Clickable("MediumButton", 275, 335, 150, 100);
									clickables[2] = new Clickable("HardButton", 450, 335, 150, 100);
									clickables[3] = new Clickable("SmallButton", 200, 535, 150, 100);
									clickables[4] = new Clickable("LargeButton", 400, 535, 150, 100);
									clickables[5] = new Clickable("ReturnButton", 60, 740, 50, 50);
								} else if (i == 3) {
									// closing the game
									System.exit(0);
								}
								// pause menu
							} else if (stage == 2) {
								if (i == 0) {
									// the resume button
									stage = 0;
									isMouseLocked = true;
									deleteClickables();
								} else if (i == 2){
									stage = 8;
									deleteClickables();
									panel.setBackground("AboutScreen");
									clickables[0] = new Clickable("AboutScreen", panel.getWidth()/2, panel.getHeight()/2, 1200, 800);
									clickables[1] = new Clickable(
											"AboutShipsButton",
											3 * panel.getWidth() / 4 + 100,
											14 * panel.getHeight() / 16,
											200, 100);
									clickables[2] = new Clickable(
											"ReturnButton",
											45, 45, 50, 50);
								} else if (i == 3) {
									// the menu button
									stage = 1;
									panel.setBackground("Title");
									isMouseLocked = false;
									deleteClickables();
									deletePlanets();
									deleteShips();
									createStartUpClickables();
									stage_Objective = 0;
								} else if (i == 4) {
									// the exit button
									System.exit(0);
								}
								// planet selection menu thing
							} else if (stage == 3) {
								// do nothign for 0
								if (i == 1) {
									// the resume button
									stage = 0;
									deleteClickables();
									panel.setDrawingSelectionBox(true);
								} else if (i == 2) {
									// the Colonizing planet button
									if (planetType == 0) {
										// home planet stuff
										// 0 menu type for build menu
										menuType = 0;
										menuClickables[0] = new Clickable(
												"BuildMenu_Home",
												3 * panel.getWidth() / 4,
												panel.getHeight() / 2,
												3 * panel.getWidth() / 8,
												7 * panel.getHeight() / 8);
										menuClickables[1] = new Clickable("X",
												7 * panel.getWidth() / 8 + 25,
												panel.getHeight() / 8 - 10, 50,
												50);
										menuClickables[2] = new Clickable(
												"BuyTownButton",
												3 * panel.getWidth() / 4 - 100,
												4 * panel.getHeight() / 16,
												200, 100);
										menuClickables[3] = new Clickable(
												"BuyMineButton",
												3 * panel.getWidth() / 4 - 100,
												7 * panel.getHeight() / 16,
												200, 100);
										menuClickables[4] = new Clickable(
												"BuyAcademyButton",
												3 * panel.getWidth() / 4 - 100,
												10 * panel.getHeight() / 16,
												200, 100);
										menuClickables[5] = new Clickable(
												"BuyQuantumTechButton",
												3 * panel.getWidth() / 4 - 100,
												13 * panel.getHeight() / 16,
												200, 100);
										isPlanetMenuUp = true;
									} else if (planetType == 1) {
										// creating the home planet
										planetsColonized++;
										news.add(new String(
												"Home Planet Colonized"));
										stage = 0;
										deleteClickables();
										planets.set(
												indexSelected_planet,
												new HomePlanet(
														planets.get(indexSelected_planet)));
										if (stage_Objective == 1){
											stage_Objective = 2;
										}
										// simulating dropping people off at
										// planet
										people = 0;
										people += planets.get(
												indexSelected_planet)
												.getPopulation();
										// removes expander
										ships.remove(indexOnPlanet_Expander);
									} else if (planetType == 2) {
									} else if (planetType == 3) {
										// the attack planet
										planetsColonized++;
										news.add(new String(
												"Attack Planet Colonized"));
										stage = 0;
										deleteClickables();
										planets.set(
												indexSelected_planet,
												new AttackPlanet(
														planets.get(indexSelected_planet)));
										ships.remove(indexOnPlanet_Expander);
										if (stage_Objective == 5){
											stage_Objective = 6;
										}
									} else if (planetType == 4) {
										// attack planet stuff
										// 0 menu type for build menu
										menuType = 0;
										menuClickables[0] = new Clickable(
												"BuildMenu_Attack",
												3 * panel.getWidth() / 4,
												panel.getHeight() / 2,
												3 * panel.getWidth() / 8,
												7 * panel.getHeight() / 8);
										menuClickables[1] = new Clickable("X",
												7 * panel.getWidth() / 8 + 25,
												panel.getHeight() / 8 - 10, 50,
												50);
										menuClickables[2] = new Clickable(
												"BuyTurretButton",
												3 * panel.getWidth() / 4 - 100,
												4 * panel.getHeight() / 16,
												200, 100);
										menuClickables[3] = new Clickable(
												"BuyRadarTechButton",
												3 * panel.getWidth() / 4 - 100,
												7 * panel.getHeight() / 16,
												200, 100);
										menuClickables[4] = new Clickable(
												"BuyWarFactoryButton",
												3 * panel.getWidth() / 4 - 100,
												10 * panel.getHeight() / 16,
												200, 100);
										menuClickables[5] = new Clickable(
												"BuyPlasmaTechButton",
												3 * panel.getWidth() / 4 - 100,
												13 * panel.getHeight() / 16,
												200, 100);
										isPlanetMenuUp = true;
									} else if (planetType == 5) {
										// mining planet stuff
										// 0 menu type for build menu
										menuType = 0;
										menuClickables[0] = new Clickable(
												"BuildMenu_Mining",
												3 * panel.getWidth() / 4,
												panel.getHeight() / 2,
												3 * panel.getWidth() / 8,
												7 * panel.getHeight() / 8);
										menuClickables[1] = new Clickable("X",
												7 * panel.getWidth() / 8 + 25,
												panel.getHeight() / 8 - 10, 50,
												50);
										menuClickables[2] = new Clickable(
												"BuyTownButton",
												3 * panel.getWidth() / 4 - 100,
												4 * panel.getHeight() / 16,
												200, 100);
										menuClickables[3] = new Clickable(
												"BuyMineButton",
												3 * panel.getWidth() / 4 - 100,
												7 * panel.getHeight() / 16,
												200, 100);
										menuClickables[4] = new Clickable(
												"BuyExcavateButton",
												3 * panel.getWidth() / 4 - 100,
												10 * panel.getHeight() / 16,
												200, 100);
										menuClickables[5] = new Clickable(
												"BuyCloneButton",
												3 * panel.getWidth() / 4 - 100,
												13 * panel.getHeight() / 16,
												200, 100);
										isPlanetMenuUp = true;
									} else if (planetType == 6) {
										// Tech planet stuff
										// 0 menu type for build menu
										menuType = 0;
										menuClickables[0] = new Clickable(
												"BuildMenu_Tech",
												3 * panel.getWidth() / 4,
												panel.getHeight() / 2,
												3 * panel.getWidth() / 8,
												7 * panel.getHeight() / 8);
										menuClickables[1] = new Clickable("X",
												7 * panel.getWidth() / 8 + 25,
												panel.getHeight() / 8 - 10, 50,
												50);
										menuClickables[2] = new Clickable(
												"BuyAcademyButton",
												3 * panel.getWidth() / 4 - 100,
												4 * panel.getHeight() / 16,
												200, 100);
										menuClickables[3] = new Clickable(
												"BuyEMPTechButton",
												3 * panel.getWidth() / 4 - 100,
												7 * panel.getHeight() / 16,
												200, 100);
										menuClickables[4] = new Clickable(
												"BuyMolecularFusionButton",
												3 * panel.getWidth() / 4 - 100,
												10 * panel.getHeight() / 16,
												200, 100);
										isPlanetMenuUp = true;
									} else if (planetType == 7) {
										// gain half the materials on the planet
										materials += planets.get(
												indexSelected_planet)
												.getMaterials() / 2;
										planets.remove(indexSelected_planet);
										if (stage_Objective == 10){
											stage_Objective = 11;
										}
										news.add(new String("Planet Destroyed!"));
										deleteClickables();
										stage = 0;
										panel.setDrawingSelectionBox(true);
									}
								} else if (i == 3) {
									// the Colonizing planet button
									if (planetType == 0) {
										// home planet stuff
										// 1 menu type for training
										menuType = 1;
										menuClickables[0] = new Clickable(
												"TrainMenu_Home",
												3 * panel.getWidth() / 4,
												panel.getHeight() / 2,
												3 * panel.getWidth() / 8,
												7 * panel.getHeight() / 8);
										menuClickables[1] = new Clickable("X",
												7 * panel.getWidth() / 8 + 25,
												panel.getHeight() / 8 - 10, 50,
												50);
										menuClickables[2] = new Clickable(
												"BuyExpanderButton",
												3 * panel.getWidth() / 4 - 100,
												4 * panel.getHeight() / 16,
												200, 100);
										menuClickables[3] = new Clickable(
												"BuyScoutButton",
												3 * panel.getWidth() / 4 - 100,
												7 * panel.getHeight() / 16,
												200, 100);
										menuClickables[4] = new Clickable(
												"BuyDefenderButton",
												3 * panel.getWidth() / 4 - 100,
												10 * panel.getHeight() / 16,
												200, 100);
										menuClickables[5] = new Clickable(
												"BuyDestroyerButton",
												3 * panel.getWidth() / 4 - 100,
												13 * panel.getHeight() / 16,
												200, 100);
										isPlanetMenuUp = true;
									} else if (planetType == 1) {
									} else if (planetType == 2) {
									} else if (planetType == 3) {
										// the mining planet
										planetsColonized++;
										news.add(new String(
												"Mining Planet Colonized"));
										stage = 0;
										deleteClickables();
										planets.set(
												indexSelected_planet,
												new MiningPlanet(
														planets.get(indexSelected_planet)));
										if (stage_Objective == 3){
											stage_Objective = 4;
										}
										ships.remove(indexOnPlanet_Expander);
									} else if (planetType == 4) {
										// attack planet stuff
										// 1 menu type for traim menu
										menuType = 1;
										menuClickables[0] = new Clickable(
												"TrainMenu_Attack",
												3 * panel.getWidth() / 4,
												panel.getHeight() / 2,
												3 * panel.getWidth() / 8,
												7 * panel.getHeight() / 8);
										menuClickables[1] = new Clickable("X",
												7 * panel.getWidth() / 8 + 25,
												panel.getHeight() / 8 - 10, 50,
												50);
										menuClickables[2] = new Clickable(
												"BuyFighterButton",
												3 * panel.getWidth() / 4 - 100,
												4 * panel.getHeight() / 16,
												200, 100);
										menuClickables[3] = new Clickable(
												"BuyScoutButton",
												3 * panel.getWidth() / 4 - 100,
												7 * panel.getHeight() / 16,
												200, 100);
										menuClickables[4] = new Clickable(
												"BuyTankButton",
												3 * panel.getWidth() / 4 - 100,
												10 * panel.getHeight() / 16,
												200, 100);
										menuClickables[5] = new Clickable(
												"BuyDisablerButton",
												3 * panel.getWidth() / 4 - 100,
												13 * panel.getHeight() / 16,
												200, 100);
										isPlanetMenuUp = true;
									} else if (planetType == 5) {
										// mining planet stuff
										// 1 menu type for train menu
										menuType = 1;
										menuClickables[0] = new Clickable(
												"TrainMenu_Mining",
												3 * panel.getWidth() / 4,
												panel.getHeight() / 2,
												3 * panel.getWidth() / 8,
												7 * panel.getHeight() / 8);
										menuClickables[1] = new Clickable("X",
												7 * panel.getWidth() / 8 + 25,
												panel.getHeight() / 8 - 10, 50,
												50);
										menuClickables[2] = new Clickable(
												"BuyHarvesterButton",
												3 * panel.getWidth() / 4 - 100,
												4 * panel.getHeight() / 16,
												200, 100);
										menuClickables[3] = new Clickable(
												"BuyEngineerButton",
												3 * panel.getWidth() / 4 - 100,
												7 * panel.getHeight() / 16,
												200, 100);
										menuClickables[4] = new Clickable(
												"BuyCarrierButton",
												3 * panel.getWidth() / 4 - 100,
												10 * panel.getHeight() / 16,
												200, 100);
										isPlanetMenuUp = true;
									} else if (planetType == 6) {
										// Tech planet stuff
										// 1 menu type for train menu
										menuType = 1;
										menuClickables[0] = new Clickable(
												"TrainMenu_Tech",
												3 * panel.getWidth() / 4,
												panel.getHeight() / 2,
												3 * panel.getWidth() / 8,
												7 * panel.getHeight() / 8);
										menuClickables[1] = new Clickable("X",
												7 * panel.getWidth() / 8 + 25,
												panel.getHeight() / 8 - 10, 50,
												50);
										menuClickables[2] = new Clickable(
												"BuySkidButton",
												3 * panel.getWidth() / 4 - 100,
												4 * panel.getHeight() / 16,
												200, 100);
										isPlanetMenuUp = true;
									}
								} else if (i == 4) {
									if (planetType == 3) {
										// the tech planet
										planetsColonized++;
										news.add(new String(
												"Tech Planet Colonized"));
										stage = 0;
										deleteClickables();
										planets.set(
												indexSelected_planet,
												new TechPlanet(
														planets.get(indexSelected_planet)));
										ships.remove(indexOnPlanet_Expander);
										if (stage_Objective == 8){
											stage_Objective = 9;
										}
									}
								} else if (i == 5) {
									ships.remove(indexOnPlanet_Carrier);
									planets.get(indexSelected_planet)
											.addPeople(100, true);
									deleteClickables();
									stage = 0;
								}
							} else if (stage == 4) {
								// resume button
								if (i == 1) {
									stage = 0;
									deleteClickables();
									deleteNews();
									for (int j = 0; j<ships.size(); j++){
										if (ships.get(j) instanceof EnemyFighter_Moving ||
											ships.get(j) instanceof EnemyScout_Moving){
											if (ships.get(j).getX() > planets.get(0).getX()){
												ships.get(j).setDest(ships.get(j).getX() - 300, ships.get(j).getY());
											} else {
												ships.get(j).setDest(ships.get(j).getX() + 300, ships.get(j).getY());												
											}
										}
									}
								}
							} else if (stage == 5) {
								if (i == 1) {
									// for fighting, ships to be removed
									ArrayList<Integer> toBeRem = new ArrayList<Integer>();
									toBeRem.add(fight(indexFighting_1,
											indexFighting_2));
									// sets the dead ships to dead ships
									for (int j = 0; j < toBeRem.size(); j++) {
										if (toBeRem.get(j) != -1) {
											ships.set(
													toBeRem.get(j),
													new DeadShip(ships.get(
															toBeRem.get(j))
															.getX(), ships.get(
															toBeRem.get(j))
															.getY()));
										}
									}
									toBeRem = null;
									deleteClickables();
									// back to running stage
									stage = 0;
								} else if (i == 2) {
									ships.get(indexFighting_1).moveBack();
									ships.get(indexFighting_2).moveBack();
									deleteClickables();
									stage = 0;
								}
							} else if (stage == 6){
								if (i == 0){
									panel.setBackground("AboutShipsScreen");
									stage = 7;
									clickables[0] = new Clickable("ReturnButton", 45, 45, 50, 50);
								} else if (i == 1){
									// the return button
									stage = 1;
									panel.setBackground("Title");
									isMouseLocked = false;
									deleteClickables();
									deletePlanets();
									deleteShips();
									createStartUpClickables();
								}
							} else if (stage == 7){
								if (i == 0){
									stage = 6;
									panel.setBackground("AboutScreen");
									clickables[0] = new Clickable(
											"AboutShipsButton",
											3 * panel.getWidth() / 4 + 100,
											14 * panel.getHeight() / 16,
											200, 100);
									clickables[1] = new Clickable(
											"ReturnButton",
											45, 45, 50, 50);
								}
							} else if (stage == 8){
								if (i == 1){
									deleteClickables();
									clickables[0] = new Clickable("AboutShipsScreen", panel.getWidth()/2, panel.getHeight()/2, 1200, 800);
									stage = 9;
									clickables[1] = new Clickable("ReturnButton", 45, 45, 50, 50);
								} else if (i == 2){
									// the return button
									stage = 0;
									panel.setBackground("GameBackground");
									isMouseLocked = true;
									deleteClickables();
								}
							} else if (stage == 9){
								if (i == 1){
									deleteClickables();
									stage = 8;
									panel.setBackground("AboutScreen");
									clickables[0] = new Clickable("AboutScreen", panel.getWidth()/2, panel.getHeight()/2, 1200, 800);
									clickables[1] = new Clickable(
											"AboutShipsButton",
											3 * panel.getWidth() / 4 + 100,
											14 * panel.getHeight() / 16,
											200, 100);
									clickables[2] = new Clickable(
											"ReturnButton",
											45, 45, 50, 50);
								}
							} else if (stage == 10){
								if (i == 0){
									//easy
									difficulty = 0;
								} else if (i == 1){
									//medium
									difficulty = 1;
								} else if (i == 2){
									difficulty = 2;
								} else if (i == 3){
									edge = 25000;
								} else if (i == 4){
									edge = 30000;
								} else if (i == 5){
									// the return button
									stage = 1;
									panel.setBackground("Title");
									isMouseLocked = false;
									deleteClickables();
									deletePlanets();
									deleteShips();
									createStartUpClickables();
									stage_Objective = 0;
								}
							}
						}
					}
				}
			}
		}
	}

	// right
	// just sets the destination
	private static void rightClick(int x, int y) {
		if (numSelected == 1) {
			if (!ships.get(indexSelected_ship).getIsMoving()) {
				if (findDistance(x, y, ships.get(indexSelected_ship).getX(),
						ships.get(indexSelected_ship).getY()) <= ships.get(
						indexSelected_ship).getMovement()) {
					ships.get(indexSelected_ship).setDest(x, y);
				}
			}
		}
	}

	// space
	// ends turn
	private static void space() {
		// deselects ships
		numSelected = 0;
		panel.setCursorType(0);

		// moves ships
		for (int i = 0; i < ships.size(); i++) {
			if (ships.get(i) != null) {
				ships.get(i).setIsMoving(true);
				ships.get(i).setIsSelected(false);
			}
		}

		// opens turn report after movement stops
		isTurnReportPrimed = true;
	}

	// finds what ships are selected
	// goes off click and current mouse position
	private static void selection() {
		for (int i = 0; i < ships.size(); i++) {
			if (ships.get(i) != null) {
				if (rectPointCollision(xSelection, ySelection, xMouse
						- xSelection, yMouse - ySelection, ships.get(i).getX(),
						ships.get(i).getY())
						|| rectPointCollision(xSelection, yMouse, xMouse
								- xSelection, ySelection - yMouse, ships.get(i)
								.getX(), ships.get(i).getY())
						|| rectPointCollision(xMouse, yMouse, xSelection
								- xMouse, ySelection - yMouse, ships.get(i)
								.getX(), ships.get(i).getY())
						|| rectPointCollision(xMouse, ySelection, xSelection
								- xMouse, yMouse - ySelection, ships.get(i)
								.getX(), ships.get(i).getY())
								|| rectPointCollision(ships.get(i).getX() - ships.get(i).getWidth() / 2, ships.get(i).getY() - ships.get(i).getHeight() / 2, ships.get(i).getWidth(), ships.get(i).getHeight(),
								        xMouse, yMouse)) {
					if (!(ships.get(i) instanceof DeadShip)){
						ships.get(i).select(true);
						numSelected++;
						indexSelected_ship = i;
					}
				}
			}
		}
	}

	// Moves objects drawn on screen when shifted
	private static void moveObjects(int x, int y) {
		// moving planets
		for (int i = 0; i < planets.size(); i++) {
			if (planets.get(i) != null) {
				planets.get(i).move(x, y);
			}
		}

		// moving ships
		for (int i = 0; i < ships.size(); i++) {
			if (ships.get(i) != null) {
				ships.get(i).move(x, y);
			}
		}
	}

	// checks for collision when placing a ship
	private static boolean shipCollision(Ship newShip) {
		for (int i = 0; i < ships.size(); i++) {
			Ship oldShip = ships.get(i);
			if (rectRectCollision(oldShip.getX() - oldShip.getWidth() / 2,
					oldShip.getY() - oldShip.getHeight() / 2,
					oldShip.getWidth(), oldShip.getHeight(), newShip.getX()
							- newShip.getWidth() / 2,
					newShip.getY() - newShip.getHeight() / 2,
					newShip.getWidth(), newShip.getHeight())) {
				return true;
			}
		}
		return false;
	}

	// fighting
	private static int fight(int index1, int index2) {
		int ship1Dmg = (int) (ships.get(index1).getDamage()
				+ ships.get(index1).getRange()
				+ ships.get(index1).getMovement() / 100 + 5 * Math.random());
		int ship2Dmg = (int) (ships.get(index2).getDamage()
				+ ships.get(index2).getRange()
				+ ships.get(index2).getMovement() / 100 + 5 * Math.random());
		int ship1Hp = 10 * ships.get(index1).getHealth();
		int ship2Hp = 10 * ships.get(index2).getHealth();

		// explanation for fighting algorithm
		// - each ship has damage amount based off stats
		// - each ship has a health
		// - each ship also has a random 1-5 damage increase
		// - health is multiplied by ten
		// - from there, each damage is done to each ship until one ship dies
		// - then the winning ships health is divided by ten again
		// - this way, the winner ship has an appropriate amount of health

		while (ship1Hp > 0 && ship2Hp > 0) {
			ship1Hp -= ship2Dmg;
			ship2Hp -= ship1Dmg;
		}

		// destroying and setting health of winner/loser
		if (ship1Hp <= 0) {
			if (ships.get(index1) instanceof EnemyFighter
					|| ships.get(index1) instanceof EnemyBasic ||
					ships.get(index1) instanceof EnemyScout) {
				news.add(new String("Enemy Ship Destroyed"));
				enemiesKilled++;
				if (stage_Objective == 7){
					if (enemiesKilled >= 5){
						stage_Objective = 8;
					}
				}
			} else {
				news.add(new String("Allied Ship Lost"));
			}
			ships.get(index2).setHealth(ship2Hp / 10);
			ships.get(index1).setHealth(0);
			return index1;
		} else if (ship2Hp <= 0) {
			if (ships.get(index2) instanceof EnemyFighter
					|| ships.get(index2) instanceof EnemyBasic ||
					ships.get(index2) instanceof EnemyScout) {
				news.add(new String("Enemy Ship Destroyed"));
				enemiesKilled++;
				if (stage_Objective == 7){
					if (enemiesKilled >= 5){
						stage_Objective = 8;
					}
				}
			} else {
				news.add(new String("Allied Ship Lost"));
			}
			ships.get(index1).setHealth(ship1Hp / 10);
			ships.get(index2).setHealth(0);
			return index2;
		} else {
			System.out.println("Something went wrong with fighting");
			return -1;
		}
	}

	// the turn report stuff
	private static void openTurnReport() {
		// opens the turn report
		// adds every turn
		// knowledge earned according to frequency
		if (turnNumber % knowledgeFreq == 0 && knowledge < 100) {
			if (planetsColonized > 0) {
				knowledge++;
			}
		}

		// makes people the summation of all people on planets
		people = 0;
		// goes through the planets and does stuff
		for (int i = 0; i < planets.size(); i++) {
			// if something is on build queue
			if (planets.get(i).getToBeMade() != 0) {
				// if something is ready to be made
				if (planets.get(i).getBuildTime() == 1) {
					// decides what to make
					int n;
					switch (planets.get(i).getToBeMade()) {
					// expander
					case 1:
						n = 0;
						while (shipCollision(new Expander(
								planets.get(i).getX(), planets.get(i).getY()
										- planets.get(i).getRadius()
										- (100 - n), 40))) {
							n -= 60;
						}
						ships.add(new Expander(planets.get(i).getX(), planets
								.get(i).getY()
								- planets.get(i).getRadius()
								- (100 - n), 40));
						planets.get(i).setToBeMade(0, 0);
						news.add(new String("Expander Trained"));
						break;
					// scout
					case 2:
						n = 0;
						while (shipCollision(new Scout(planets.get(i).getX(),
								planets.get(i).getY()
										- planets.get(i).getRadius()
										- (100 - n), 15))) {
							n -= 60;
						}
						ships.add(new Scout(planets.get(i).getX(), planets.get(
								i).getY()
								- planets.get(i).getRadius() - (100 - n), 15));
						planets.get(i).setToBeMade(0, 0);
						news.add(new String("Scout Trained"));
						if (stage_Objective == 4){
							stage_Objective = 5;
						}
						break;
					// defender
					case 3:
						n = 0;
						while (shipCollision(new Defender(
								planets.get(i).getX(), planets.get(i).getY()
										- planets.get(i).getRadius()
										- (100 - n), 40))) {
							n -= 60;
						}
						ships.add(new Defender(planets.get(i).getX(), planets
								.get(i).getY()
								- planets.get(i).getRadius()
								- (100 - n), 15));
						planets.get(i).setToBeMade(0, 0);
						news.add(new String("Defender Trained"));
						break;
					// defender
					case 4:
						n = 0;
						while (shipCollision(new Destroyer(planets.get(i)
								.getX(), planets.get(i).getY()
								- planets.get(i).getRadius() - (100 - n), 80))) {
							n -= 60;
						}
						ships.add(new Destroyer(planets.get(i).getX(), planets
								.get(i).getY()
								- planets.get(i).getRadius()
								- (100 - n), 80));
						planets.get(i).setToBeMade(0, 0);
						news.add(new String("Destroyer Trained!"));
						if (stage_Objective == 9){
							stage_Objective = 10;
						}
						break;
					// Town
					case 5:
						homePlanetPopulationGrowth++;
						planets.get(i).setToBeMade(0, 0);
						news.add(new String("Town Built"));
						if (stage_Objective == 2){
							stage_Objective = 3;
						}
						break;
					// Mine0
					case 6:
						homePlanetHarvestAmount++;
						planets.get(i).setToBeMade(0, 0);
						news.add(new String("Mine Built"));
						break;
					// Academy
					case 7:
						knowledgeFreq--;
						planets.get(i).setToBeMade(0, 0);
						news.add(new String("Academy Built"));
						isAcademyBought = true;
						if (planets.get(i) instanceof TechPlanet){
							((TechPlanet) planets.get(i)).setIsAcademyBought();
						}
						break;
					// Quantum tech
					case 8:
						isQuantumTechBought = true;
						planets.get(i).setToBeMade(0, 0);
						news.add(new String("Quantum Technology Researched"));
						break;
					// turret
					case 9:
						((AttackPlanet) planets.get(i)).incDefenseDamage();
						planets.get(i).setToBeMade(0, 0);
						news.add(new String("Turret Built"));
						break;
					// radar tech
					case 10:
						((AttackPlanet) planets.get(i)).incOrbitRadius();
						planets.get(i).setToBeMade(0, 0);
						news.add(new String("Radar Technology Researched"));
						break;
					// war factory
					case 11:
						((AttackPlanet) planets.get(i)).incDamageModifier();
						planets.get(i).setToBeMade(0, 0);
						news.add(new String("War Factory Built"));
						break;
					// Plasma Tech
					case 12:
						((AttackPlanet) planets.get(i)).incDefenseHealth();
						planets.get(i).setToBeMade(0, 0);
						news.add(new String("Plasma Technology Researched"));
						break;
					// Fighter
					case 13:
						n = 0;
						while (shipCollision(new Fighter(planets.get(i).getX(),
								planets.get(i).getY()
										- planets.get(i).getRadius()
										- (100 - n),
								((AttackPlanet) planets.get(i))
										.getDamageModifier(), 15))) {
							n -= 60;
						}
						ships.add(new Fighter(planets.get(i).getX(), planets
								.get(i).getY()
								- planets.get(i).getRadius()
								- (100 - n), ((AttackPlanet) planets.get(i))
								.getDamageModifier(), 15));
						planets.get(i).setToBeMade(0, 0);
						news.add(new String("Fighter Trained"));
						if (stage_Objective == 6){
							if (fightersTrained >= 2){
								stage_Objective = 7;
							} else {
								fightersTrained++;
							}
						}
						break;
					// Tank
					case 14:
						n = 0;
						while (shipCollision(new Tank(planets.get(i).getX(),
								planets.get(i).getY()
										- planets.get(i).getRadius()
										- (100 - n),
								((AttackPlanet) planets.get(i))
										.getDamageModifier(), 15))) {
							n -= 60;
						}
						ships.add(new Tank(planets.get(i).getX(), planets
								.get(i).getY()
								- planets.get(i).getRadius()
								- (100 - n), ((AttackPlanet) planets.get(i))
								.getDamageModifier(), 15));
						planets.get(i).setToBeMade(0, 0);
						news.add(new String("Tank Trained"));
						break;
					// Disabler
					case 15:
						n = 0;
						while (shipCollision(new Disabler(
								planets.get(i).getX(), planets.get(i).getY()
										- planets.get(i).getRadius()
										- (100 - n),
								((AttackPlanet) planets.get(i))
										.getDamageModifier(), 15))) {
							n -= 60;
						}
						ships.add(new Disabler(planets.get(i).getX(), planets
								.get(i).getY()
								- planets.get(i).getRadius()
								- (100 - n), ((AttackPlanet) planets.get(i))
								.getDamageModifier(), 15));
						planets.get(i).setToBeMade(0, 0);
						news.add(new String("Disabler Trained"));
						break;
					// excavate
					case 16:
						materials += 250;
						planets.get(i).setToBeMade(0, 0);
						news.add(new String("Materials Excavated"));
						break;
					// Clone
					case 17:
						// the overloaded method, to bypass the max people
						planets.get(i).addPeople(100, true);
						planets.get(i).setToBeMade(0, 0);
						news.add(new String("Clones Developed"));
						break;
					// Harvester
					case 18:
						n = 0;
						while (shipCollision(new Harvester(planets.get(i)
								.getX(), planets.get(i).getY()
								- planets.get(i).getRadius() - (100 - n), 40))) {
							n -= 60;
						}
						ships.add(new Harvester(planets.get(i).getX(), planets
								.get(i).getY()
								- planets.get(i).getRadius()
								- (100 - n), 40));
						planets.get(i).setToBeMade(0, 0);
						news.add(new String("Harvester Trained"));
						break;
					// Engineer
					case 19:
						n = 0;
						while (shipCollision(new Engineer(
								planets.get(i).getX(), planets.get(i).getY()
										- planets.get(i).getRadius()
										- (100 - n), 35))) {
							n -= 60;
						}
						ships.add(new Engineer(planets.get(i).getX(), planets
								.get(i).getY()
								- planets.get(i).getRadius()
								- (100 - n), 35));
						planets.get(i).setToBeMade(0, 0);
						news.add(new String("Engineer Trained"));
						break;
					// Carrier
					case 20:
						n = 0;
						while (shipCollision(new Carrier(planets.get(i).getX(),
								planets.get(i).getY()
										- planets.get(i).getRadius()
										- (100 - n), 100))) {
							n -= 60;
						}
						ships.add(new Carrier(planets.get(i).getX(), planets
								.get(i).getY()
								- planets.get(i).getRadius()
								- (100 - n), 100));
						planets.get(i).setToBeMade(0, 0);
						news.add(new String("Carrier Trained"));
						break;
					// EMP Tech
					case 21:
						if (planets.get(i) instanceof TechPlanet){
							((TechPlanet) planets.get(i)).setIsEMPTechBought();
						}
						planets.get(i).setToBeMade(0, 0);
						news.add(new String("EMP Technology Researched"));
						break;
					// Molecular Fusion
					case 22:
						if (planets.get(i) instanceof TechPlanet){
							((TechPlanet) planets.get(i)).setIsMolecularFusionBought();
						}
						planets.get(i).setToBeMade(0, 0);
						news.add(new String("Molecular Fusion Researched"));
						break;
					// Skid
					case 23:
						n = 0;
						while (shipCollision(new Skid(planets.get(i).getX(),
								planets.get(i).getY()
										- planets.get(i).getRadius()
										- (100 - n), 10))) {
							n -= 60;
						}
						ships.add(new Skid(planets.get(i).getX(), planets
								.get(i).getY()
								- planets.get(i).getRadius()
								- (100 - n), 10));
						planets.get(i).setToBeMade(0, 0);
						news.add(new String("Skid Trained"));
						break;
					}
				}
				// decrements build time after checking
				planets.get(i).decBuildTime();
			}

			// adds people and materials
			if (planets.get(i) instanceof HomePlanet) {
				// makes sure planet is not filled, and adds people
				// 2 for home planet only
				planets.get(i).addPeople(homePlanetPopulationGrowth);
				// makes sure there is enough materials
				// takes amount, only from home planet
				if (planets.get(i).decMaterials(homePlanetHarvestAmount)) {
					materials += homePlanetHarvestAmount;
				}
			} else if (planets.get(i) instanceof MiningPlanet) {
				((MiningPlanet) planets.get(i)).growPopulation();
				if (((MiningPlanet) planets.get(i)).harvestMaterials()) {
					materials += ((MiningPlanet) planets.get(i))
							.getMaterialsRate();
				}
			} else if (planets.get(i) instanceof AttackPlanet
					|| planets.get(i) instanceof TechPlanet) {
				planets.get(i).addPeople(2);
			}
			// sums up the people
			people += planets.get(i).getPopulation();
		}

		int indexToBeRemoved = -1;
		// moving ships because of collision
		for (int i = 0; i < ships.size(); i++) {
			for (int j = 0; j < ships.size(); j++) {
				if (i != j) {
					while (findDistance(ships.get(i).getX(), ships.get(i)
							.getY(), ships.get(j).getX(), ships.get(j).getY()) <= 3 * ships
							.get(i).getWidth() / 2
							|| findDistance(ships.get(i).getX(), ships.get(i)
									.getY(), ships.get(j).getX(), ships.get(j)
									.getY()) <= 3 * ships.get(j).getWidth() / 2) {
						ships.get(i).move(0, 5);
						ships.get(j).move(0, -5);
					}

					// healing
					// fighting
					// simultaneous with moving
					// again, efficiency gentlemen
					if (findDistance(ships.get(i).getX(), ships.get(i).getY(),
							ships.get(j).getX(), ships.get(j).getY()) <= 10 * ships
							.get(i).getRange()
							|| findDistance(ships.get(i).getX(), ships.get(i)
									.getY(), ships.get(j).getX(), ships.get(j)
									.getY()) <= 10 * ships.get(j).getRange()) {
						if (!(ships.get(i) instanceof DeadShip)
								&& !(ships.get(j) instanceof DeadShip)) {
							if (ships.get(i) instanceof Engineer) {
								ships.get(j).addHealth(10);
							}
						} else if (ships.get(i) instanceof Harvester) {
							if (indexToBeRemoved == -1){
								indexToBeRemoved = j;
								materials += 100;
								news.add(new String("Materials Harvested"));
							}
						}
					}
				}
			}
		}
		if (indexToBeRemoved != -1) {
			ships.remove(indexToBeRemoved);
		}

		// opens turn report
		isTurnReportPrimed = false;
		turnNumber++;
		stage = 4;
		clickables[0] = new Clickable("TurnReport", panel.getWidth() / 2,
				panel.getHeight() / 2, 6 * panel.getWidth() / 8,
				6 * panel.getHeight() / 8);
		clickables[1] = new Clickable("ContinueButton", panel.getWidth() / 2,
				25 * panel.getHeight() / 32, 200, 100);
	}

	// creating
	// stuff*************************************************************************************************************************
	private static void createStartUpClickables() {
		panel.setBackground("Title");
		planetsColonized = 0;
		clickables[0] = new Clickable("StartButton", 5 * panel.getWidth() / 16,
				5 * panel.getHeight() / 32, 200, 100);
		clickables[1] = new Clickable("AboutButton", 6 * panel.getWidth() / 16,
				10 * panel.getHeight() / 32, 200, 100);
		clickables[2] = new Clickable("SettingsButton",
				6 * panel.getWidth() / 16, 22 * panel.getHeight() / 32, 200,
				100);
		clickables[3] = new Clickable("ExitButton", 5 * panel.getWidth() / 16,
				27 * panel.getHeight() / 32, 200, 100);
	}

	private static void createPauseClickables() {
		clickables[0] = new Clickable("ResumeButton", panel.getWidth() / 2,
				2 * panel.getHeight() / 16, 200, 100);
		clickables[2] = new Clickable("HelpButton", panel.getWidth() / 2,
				6 * panel.getHeight() / 16, 200, 100);
		clickables[3] = new Clickable("MenuButton", panel.getWidth() / 2,
				10 * panel.getHeight() / 16, 200, 100);
	}

	private static void createPlanets() {
		// creates the home planet
		planets.add(new Planet(600, 450));
		planets.add(new Planet(50, 50));
		// creates the other planets
		for (int i = 2; i < NUM_PLANETS; i++) {
			planets.add(new Planet());
			// checks collision of planets
			while (planetCollision(i)) {
				planets.get(i).setNewLocation();
				System.out.println("Reseting");
			}
			switch ((int) Math.random()){
			case 0:
				ships.add(new EnemyFighter(planets.get(i).getX() + planets.get(i).getRadius() + 50, planets.get(i).getY(), 0));
				break;
			case 1:
				ships.add(new EnemyScout(planets.get(i).getX() - planets.get(i).getRadius() - 50, planets.get(i).getY(), 0));				
				break;
			}
		}

		//allied expander
		ships.add(new Expander(580, 300, 100));
		
		//enemy ships, approaching the home planet
		ships.add(new EnemyScout_Moving(-16000, 400, 0));
		ships.add(new EnemyFighter_Moving(-19000, 100, 0));
		ships.add(new EnemyFighter_Moving(-21000, 700, 0));
		ships.add(new EnemyFighter_Moving(-24000, -300, 0));
		//second wave
		ships.add(new EnemyScout_Moving(30000, 400, 0));
		ships.add(new EnemyFighter_Moving(33000, 100, 0));
		ships.add(new EnemyFighter_Moving(36000, 700, 0));
		ships.add(new EnemyFighter_Moving(39000, -300, 0));
	}

	// *******************************************************************************************************************************
	// Deleting stuff
	// *******************************************************************************************************************************
	private static void deleteClickables() {
		for (int i = 0; i < clickables.length; i++) {
			clickables[i] = null;
		}
	}

	private static void deleteMenuClickables() {
		for (int i = 0; i < menuClickables.length; i++) {
			menuClickables[i] = null;
		}
	}

	private static void deletePlanets() {
		while (planets.size() > 0) {
			planets.remove(0);
		}
	}

	private static void deleteShips() {
		while (ships.size() > 0) {
			ships.remove(0);
		}
	}

	private static void deleteNews() {
		while (news.size() > 0) {
			news.remove(0);
		}
	}

	// *******************************************************************************************************************************
	// calcultions n shtuff
	// *******************************************************************************************************************************

	// ************************************************************************************************************************
	// Collisions between points and a rectangle
	public static boolean rectPointCollision(int x, int y, int xx, int yy,
			int xP, int yP) {
		// x, y are top left point of rect
		// xx, yy are size of rect
		// xP, yP are coordinates of point

		if (xP > x) {
			if (xP < (x + xx)) {
				if (yP > y) {
					if (yP < (y + yy)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	// Collision between two
	// rectangles***************************************************************************************
	public static boolean rectRectCollision(int x, int y, int xx, int yy,
			int x1, int y1, int xx1, int yy1) {
		// x and y are top left
		// xx and yy are size
		// x1 etc is same

		// x1,y1 x1+xx1,y1
		//
		//
		// x1,y1+yy1 x1+xx1,y1

		if (x1 > xx1 || y1 > yy1) {
			if (rectPointCollision(x, y, xx, yy, x1, y1)) {
				return true;
			}
			if (rectPointCollision(x, y, xx, yy, x1 + xx1, y1)) {
				return true;
			}
			if (rectPointCollision(x, y, xx, yy, x1, y1 + yy1)) {
				return true;
			}
			if (rectPointCollision(x, y, xx, yy, x1 + xx1, y1 + yy1)) {
				return true;
			}
			if (rectPointCollision(x, y, xx, yy, x1 + xx1 / 2, y1 + yy1 / 2)) {
				return true;
			}
		} else {
			if (rectPointCollision(x1, y1, xx1, yy1, x, y)) {
				return true;
			}
			if (rectPointCollision(x1, y1, xx1, yy1, x + xx, y)) {
				return true;
			}
			if (rectPointCollision(x1, y1, xx1, yy1, x, y + yy)) {
				return true;
			}
			if (rectPointCollision(x1, y1, xx1, yy1, x + xx, y + yy)) {
				return true;
			}
			if (rectPointCollision(x1, y1, xx1, yy1, x + xx / 2, y + yy / 2)) {
				return true;
			}
		}
		return false;
	}

	// finds the distance between
	// points*********************************************************************************************
	public static int findDistance(int x, int y, int xx, int yy) {
		return (int) Math.sqrt(Math.pow((xx - x), 2) + Math.pow((yy - y), 2));
	}

	// /checks collision amongst planets
	private static boolean planetCollision(int x) {
		boolean ifCollides = false;

		// for to check collision
		// x is current planet
		// i is previous planet
		for (int i = 0; i < planets.size(); i++) {
			if (planets.get(x) != null && planets.get(i) != null) {
				if (i != x) {
					if (findDistance(planets.get(i).getX(), planets.get(i)
							.getY(), planets.get(x).getX(), planets.get(x)
							.getY()) >= RESTRICT_PLANET_SIZE) {
						ifCollides = false;
					} else {
						ifCollides = true;
						break;
					}
				}
			}
		}
		return ifCollides;
	}

	// checks to see if an expander is on the planet
	// for colonization
	private static boolean expanderCollidesWithPlanet() {
		for (int i = 0; i < ships.size(); i++) {
			if (ships.get(i) instanceof Expander) {
				if (findDistance(ships.get(i).getX(), ships.get(i).getY(),
						planets.get(indexSelected_planet).getX(),
						planets.get(indexSelected_planet).getY()) <= planets
						.get(indexSelected_planet).getRadius()) {
					indexOnPlanet_Expander = i;
					return true;
				}
			}
		}
		return false;
	}

	// checks to see if a carrier is in the planets orbit range
	// for depositing people
	private static boolean carrierCollidesWithPlanet() {
		for (int i = 0; i < ships.size(); i++) {
			if (ships.get(i) instanceof Carrier) {
				if (findDistance(ships.get(i).getX(), ships.get(i).getY(),
						planets.get(indexSelected_planet).getX(),
						planets.get(indexSelected_planet).getY()) <= planets
						.get(indexSelected_planet).getRadius()) {
					indexOnPlanet_Carrier = i;
					return true;
				}
			}
		}
		return false;
	}

	// checks to see if a destroyer is on the planet
	// for colonization
	private static boolean destroyerCollidesWithPlanet() {
		for (int i = 0; i < ships.size(); i++) {
			if (ships.get(i) instanceof Destroyer) {
				if (findDistance(ships.get(i).getX(), ships.get(i).getY(),
						planets.get(indexSelected_planet).getX(),
						planets.get(indexSelected_planet).getY()) <= planets
						.get(indexSelected_planet).getRadius()) {
					indexOnPlanet_Destroyer = i;
					return true;
				}
			}
		}
		return false;
	}

	// checks what type of cursor should be drawn
	private static int getCursorType() {
		// 1 is green cursor
		// 2 is red cursor
		if (findDistance(xMouse, yMouse, ships.get(indexSelected_ship).getX(),
				ships.get(indexSelected_ship).getY()) <= ships.get(
				indexSelected_ship).getMovement()) {
			return 1;
		} else {
			return 2;
		}
	}

	// Screen motion
	// detection*********************************************************************************************
	// Catch has to be used for robot
	private static void screenCollision() {
		// Right side of the screen
		if (xMouse > theG.getWidth() - 10) {
			try {
				// Move the cursor
				Robot robot = new Robot();
				Point pt = new Point();
				pt = theG.getLocation();
				robot.mouseMove(xMouse + (int) pt.getX(),
						yMouse + (int) pt.getY());
			} catch (AWTException e) {
			}
		}

		// Left side of the screen
		else if (xMouse < 10) {
			try {
				// Move the cursor
				Robot robot = new Robot();
				Point pt = new Point();
				pt = theG.getLocation();
				robot.mouseMove(xMouse + (int) pt.getX(),
						yMouse + (int) pt.getY());
			} catch (AWTException e) {
			}
		}

		// Bottom of screen
		else if (yMouse > theG.getHeight() - 10) {
			try {
				// Move the cursor
				Robot robot = new Robot();
				Point pt = new Point();
				pt = theG.getLocation();
				robot.mouseMove(xMouse + (int) pt.getX(),
						yMouse + (int) pt.getY());
			} catch (AWTException e) {
			}
		}

		// Top of screen
		else if (yMouse < 10) {
			try {
				// Move the cursor
				Robot robot = new Robot();
				Point pt = new Point();
				pt = theG.getLocation();
				robot.mouseMove(xMouse + (int) pt.getX(),
						yMouse + (int) pt.getY());
			} catch (AWTException e) {
			}
		}
	}

	// *************************************************************************************************************************
	// Used for general mouse position in methods
	public static int getMouseX() {
		return xMouse;
	}

	// Used for general mouse position in methods
	public static int getMouseY() {
		return yMouse;
	}

	// Mouse listener
	// class********************************************************************************************************
	public static class mLis implements MouseListener {
		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				isMB1Down = true;
				if (stage == 0) {
					xSelection = e.getX();
					ySelection = e.getY();
					panel.setDrawingSelectionBox(true, e.getX(), e.getY());
				}
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				rightClick(e.getX(), e.getY());
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				isMB1Down = false;
				leftClick(e.getX(), e.getY());
				if (stage == 0) {
					selection();
					panel.setDrawingSelectionBox(false);
				}
			}
		}

		public void mouseClicked(MouseEvent e) {
		}
	}

	// Motion listnener
	// class******************************************************************************************************
	public class mMotLis implements MouseMotionListener {
		// Mouse Motion Events
		public void mouseDragged(MouseEvent e) {
			xMouse = e.getX();
			yMouse = e.getY();
		}

		public void mouseMoved(MouseEvent e) {
			xMouse = e.getX();
			yMouse = e.getY();
		}
	}

	// Key listener
	// class*************************************************************************************************************
	public static class KL implements KeyListener {
		public void keyTyped(KeyEvent e) {
		}

		/** Handle the key-pressed event from the text field. */
		public void keyPressed(KeyEvent e) {
			// System.out.println("KEY Pressed: " + e.getKeyChar());

			if (e.getKeyCode() == KeyEvent.VK_D) {
				isDDown = true;
			} else if (e.getKeyCode() == KeyEvent.VK_A) {
				isADown = true;
			} else if (e.getKeyCode() == KeyEvent.VK_W) {
				isWDown = true;
			} else if (e.getKeyCode() == KeyEvent.VK_S) {
				isSDown = true;
			}
		}

		// Handle the key-released event from the text field.
		public void keyReleased(KeyEvent e) {
			// System.out.println("KEY Released: " + e.getKeyChar());

			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				if (stage == 0) {
					space();
				}
			}

			else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				isScreenMovable = !isScreenMovable;
				isMouseLocked = !isMouseLocked;
				if (stage == 0) {
					stage = 2;
					createPauseClickables();
				}
			}

			else if (e.getKeyCode() == KeyEvent.VK_DELETE) {

			}

			if (e.getKeyCode() == KeyEvent.VK_I) {
				ships.add(new Expander(xMouse, yMouse, 40));
			}
			else if (e.getKeyCode() == KeyEvent.VK_O) {
				ships.add(new Carrier(xMouse, yMouse, 100));
			}
			else if (e.getKeyCode() == KeyEvent.VK_P) {
				ships.add(new Defender(xMouse, yMouse, 40));
			}
			else if (e.getKeyCode() == KeyEvent.VK_K) {
				ships.add(new Destroyer(xMouse, yMouse, 40));
			}
			else if (e.getKeyCode() == KeyEvent.VK_L) {
				ships.add(new Disabler(xMouse, yMouse, 0, 40));
			}
			else if (e.getKeyCode() == KeyEvent.VK_M) {
				ships.add(new Engineer(xMouse, yMouse, 40));
			}
			else if (e.getKeyCode() == KeyEvent.VK_J) {
				ships.add(new Fighter(xMouse, yMouse, 0, 40));
			}
			else if (e.getKeyCode() == KeyEvent.VK_U) {
				ships.add(new Harvester(xMouse, yMouse, 40));
			}
			else if (e.getKeyCode() == KeyEvent.VK_0) {
				ships.add(new Scout(xMouse, yMouse, 40));
			}
			else if (e.getKeyCode() == KeyEvent.VK_9) {
				ships.add(new Skid(xMouse, yMouse, 40));
			}
			else if (e.getKeyCode() == KeyEvent.VK_8) {
				ships.add(new Tank(xMouse, yMouse, 0, 40));
			}
			else if (e.getKeyCode() == KeyEvent.VK_N) {
				ships.add(new EnemyFighter(xMouse, yMouse, 40));
			}
			else if (e.getKeyCode() == KeyEvent.VK_B) {
				ships.add(new EnemyScout(xMouse, yMouse, 40));
			}
			else if (e.getKeyCode() == KeyEvent.VK_7) {
				ships.add(new DeadShip(xMouse, yMouse));
			}

			else if (e.getKeyCode() == KeyEvent.VK_D) {
				isDDown = false;
			}

			else if (e.getKeyCode() == KeyEvent.VK_A) {
				isADown = false;
			}

			else if (e.getKeyCode() == KeyEvent.VK_W) {
				isWDown = false;
			}

			else if (e.getKeyCode() == KeyEvent.VK_S) {
				isSDown = false;
			}
		}
	}
}