/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
// stfu FIRST

package org.usfirst.frc.team6506.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic; 				// are we using this
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */

/**
 * CONTROLS:
 * joystick - movement
 * POV (joystick on the top of the joystick) - lift
 * trigger/thumb button - intake cube/spit out cube, respectively
 * slider - modify movement speed
 * every single other button - self destruct
 * 
 * THIS STUPID-ASS LAPTOP IS A POS
 */

public class Robot extends IterativeRobot {
	
	/**
	 * defining variables
	 * 
	 * motor ports list:
	 * 0, 1: drive motors
	 * 2: intake
	 * 3: lift
	 * 8-9: test
	 * 
	 * YOU FOOLS! YOU LET A FRESHMAN INTO THE CODE!
	 * made by VexPRO (c)
	 * (not really pls no sue)
	 */
	
	// drive
	private Spark m_left = new Spark(0); 		// left wheels
	private Spark m_right = new Spark(1); 		// right wheels
	private DifferentialDrive m_robotDrive; 	// prebuilt class by FRC for drivetrains
	
	// spools
	private Spark spools = new Spark(2);
	private double spoolDirection; 				// PWM value for spools
	
	// intakes
	private Spark intakeMotors = new Spark(3);
	private double intakeDirection;				// PWM value for intake
	
	// etc
	private Joystick m_stick;
	private Timer m_timer;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	
	@Override
	public void robotInit() {
		// CameraServer.getInstance().startAutomaticCapture();		// record video; it's this easy lmao
		m_stick = new Joystick(0);
		m_timer = new Timer();
		m_robotDrive = new DifferentialDrive(m_left, m_right);	// setup drivetrain with left and right wheels
		
		CameraServer.getInstance().startAutomaticCapture();
		
		System.out.println("robot initialized!");
	}
	
	/**
	 * This function is run once each time the robot enters autonomous mode.
	 */
	@Override
	public void autonomousInit() {
		m_timer.reset();
		System.out.println("timer reset");
		m_timer.start();
		System.out.println("timer started!");
		
		System.out.println("auto initialized!");
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		/**
		 * I might add modes
		 * modes:
		 * 0 = cross line
		 * 1 = aggro (full forwards to enemy's side of scale)
		 * 2 = 
		 * 
		 * 
		 */
		// drive for 8(?) seconds
		
		// don't place our bot behind the switch lmao
		if (m_timer.get() < 2.5) {
			m_robotDrive.arcadeDrive(0.75, 0.0); // drive forwards 3/4 speed
		} else {
			m_robotDrive.stopMotor(); 			// stop robot
		}
		
		
	}

	/**
	 * This function is called once each time the robot enters teleoperated mode.
	 */
	@Override
	public void teleopInit() {
		// not sure what to put here
		System.out.println("teleop initialized!");
	}

	/**
	 * This function is called periodically during teleoperated mode.
	 */
	@Override
	public void teleopPeriodic() {
		double mulSpeed = (((m_stick.getRawAxis(3) * -1) + 1) / -2.0) - 0.5;
		/** ^^ computes sensitivity of the joystick
		 * m_stick.getRawAxis(3) gets the value of the slider
		 * it's multiplied by -1 to flip the direction its
		 * the slider returns a range of [-1.0,1.0] so we add 1 to convert it to [0,2.0]
		 * divide by 2 to normalize values to [0,1]
		 * add 0.5 so robot isn't annoyingly slow
		 */
		
		if (mulSpeed < -1) {
			mulSpeed = -1;
		}
		System.out.println(mulSpeed);
		
		double forwardSpeed = m_stick.getY() * mulSpeed;		// self explanatory
		double turnSpeed = m_stick.getX() * mulSpeed;			// ^^^^^^^^^^^^^^^^
		
		// threshold set to 0.9
		if (forwardSpeed > 0.9) {
			forwardSpeed = 0.9;
		} else if (forwardSpeed < -0.9){
			forwardSpeed = -0.9;
		}
		
		if (turnSpeed > 0.9) {
			turnSpeed = 0.9;
		} else if (turnSpeed < -0.9){
			turnSpeed = -0.9;
		}
		
		// test motor
		if (m_stick.getPOV() == 0) { // lift the... lift
			spoolDirection = 1; // speed of spool
			System.out.println("going up");
		} else if (m_stick.getPOV() == 180) { // lower the lift
			spoolDirection = -1;
			System.out.println("going down");
		} else {
			spoolDirection = 0;
		}
		
		
		if (m_stick.getTrigger() && !m_stick.getRawButton(2)) { // if trigger is pressed shoot out cube
			intakeDirection = 0.5;
		} else if (m_stick.getRawButton(2) && !m_stick.getTrigger())  { // if the thumb button thingy is pressed succ in cube
			intakeDirection = -0.5;
		} else { 
			intakeDirection = 0;
		}
		
		// movement
		m_robotDrive.arcadeDrive(forwardSpeed, -turnSpeed, true);	// see variable definitions above, bool at end squares movement
		spools.set(spoolDirection);						// set spools PWM rate to spoolDirection (see if/else above)
		intakeMotors.set(intakeDirection); 				// set intake's PWM rate to intakeDirection
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
		double mulSpeed = ((m_stick.getRawAxis(3) * -1) + 1) / -2.0;
		/** ^^ computes sensitivity of the joystick
		 * m_stick.getRawAxis(3) gets the value of the slider
		 * it's multiplied by -1 to flip the direction its
		 * the slider returns a range of [-1.0,1.0] so we add 1 to convert it to [0,2.0]
		 * divide by 2 to normalize values to [0,1]
		 */
		double forwardSpeed = m_stick.getY() * mulSpeed;		// self explanatory
		double turnSpeed = m_stick.getX() * mulSpeed;			// ^^^^^^^^^^^^^^^^
		
		// threshold
		if (forwardSpeed > 0.5) {
			forwardSpeed = 0.5;
		} else if (forwardSpeed < -0.5){
			forwardSpeed = -0.5;
		}
		
		if (turnSpeed > 0.5) {
			turnSpeed = 0.5;
		} else if (turnSpeed < -0.5){
			turnSpeed = -0.5;
		}
		
		// test motor
		if (m_stick.getPOV() == 0) { // lift the... lift
			spoolDirection = 1; // speed of spool
			System.out.println("going up");
		} else if (m_stick.getPOV() == 180) { // lower the lift
			spoolDirection = -1;
			System.out.println("going down");
		} else {
			spoolDirection = 0;
		}
		
		
		if (m_stick.getTrigger() && !m_stick.getRawButton(2)) { // if trigger is pressed shoot out cube
			intakeDirection = 0.5;
		} else if (m_stick.getRawButton(2) && !m_stick.getTrigger())  { // if the thumb button thingy is pressed succ in cube
			intakeDirection = -0.5;
		} else { 
			intakeDirection = 0;
		}
		
		// movement
		m_robotDrive.arcadeDrive(-forwardSpeed, turnSpeed, true);	// see variable definitions above, bool at end squares movement
		spools.set(spoolDirection);						// set spools PWM rate to spoolDirection (see if/else above)
		intakeMotors.set(intakeDirection); 				// set intake's PWM rate to intakeDirection
	}
}


























// Anjo was here.  Michael 3. FRC Power Up, 2018