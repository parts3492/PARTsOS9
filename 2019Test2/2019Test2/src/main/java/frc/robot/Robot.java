
/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;

import java.io.File;
import java.io.IOException;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.commands.ArmPosition;
import frc.robot.commands.AutoCenterCmdGrp;
import frc.robot.commands.AutoDoNothingCmdGrp;
import frc.robot.commands.AutoDriveFwdCmdGrp;
import frc.robot.commands.AutoLeftLV2CmdGrp;
import frc.robot.commands.AutoRightLV2CmdGrp;
import frc.robot.commands.GearShift;
import frc.robot.subsystems.Encoders;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.LimeLight;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.PathfinderFRC;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Trajectory.Config;
import jaci.pathfinder.followers.EncoderFollower;
import java.nio.file.Path;







public class Robot extends TimedRobot {

  private static final int kTicksPerRev = 750;
  private static final double kWheelDiameter = 6.25;
  private static final double kMaxVelocity = 18 * 12;

  private static final int kLeftChannel = 0;
  private static final int kRightChannel = 1;

  private static final int kGyroPort = 0;

  private static final String kPath = "RightRocket.right.pf1.csv";

  private AnalogGyro gyro;
  private EncoderFollower leftFollower;
  private EncoderFollower rightFollower;
  private Notifier followerNotifier;

  public static ExampleSubsystem m_subsystem = new ExampleSubsystem();
  public static OI m_oi;
  Command m_autonomousCommand;
  SendableChooser<Command> m_chooser = new SendableChooser<>();
  double setPositionRight;
  LimeLight limeLight = new LimeLight();
  Encoders encoder = new Encoders();
  double KpDistance = -0.1;
  boolean firstPress8 = true;
  double startPosition;
  GearShift shiftGears = new GearShift(); 
//------------------------------PATHFINDER CODE------------------------------------------------------------------------------------------------
/* private void followPath(){
    if (leftFollower.isFinished() ||rightFollower.isFinished()){
      followerNotifier.stop();
    }
    else{
      double leftSpeed = leftFollower.calculate((int)RobotMap.encoder1.getPosition());
      double rightSpeed = leftFollower.calculate((int)RobotMap.encoder0.getPosition());
      double heading = gyro.getAngle();
      double desiredHeading = Pathfinder.r2d(leftFollower.getHeading());
      double headingDifference = Pathfinder.boundHalfDegrees(desiredHeading - heading);
      double turn = 0.8 * (-1.0/80.0) * headingDifference;
      RobotMap.dDrive.tankDrive((leftSpeed + turn), (rightSpeed - turn));
    }
  } */
//---------------------------------------------------------------------------------------------------------------------------------------------
  @Override
  public void robotInit() {
    gyro = new AnalogGyro(kGyroPort);
    
    RobotMap.liftMotor2.set(ControlMode.Follower, 14);
    RobotMap.intake2.set(ControlMode.Follower, 11);
    m_oi = new OI();
    SmartDashboard.putData("Auto mode", m_chooser);
    m_chooser.setDefaultOption("Default Auto", new AutoDriveFwdCmdGrp());
    m_chooser.addOption("RightRocketLV2Auto", new AutoRightLV2CmdGrp());
    m_chooser.addOption("LeftRocketLV2Auto", new AutoLeftLV2CmdGrp());
    m_chooser.addOption("CenterCargoAuto", new AutoCenterCmdGrp());
    m_chooser.addOption("DoNothingAuto", new AutoDoNothingCmdGrp());    
    RobotMap.c.setClosedLoopControl(true);
    RobotMap.solenoidStan.set(DoubleSolenoid.Value.kReverse); //TODO: check if forward is low gear
  }
    
  @Override
  public void robotPeriodic() {
  }

  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
    Scheduler.getInstance().run();
    
  }

  @Override
  public void autonomousInit() {
    if(shiftGears != null){
      shiftGears.cancel();
    }
    m_autonomousCommand = m_chooser.getSelected();
    
      if (m_autonomousCommand != null) { m_autonomousCommand.start(); }
     
  
//----------------------------------------------PATHFINDER CODE-----------------------------------------------------------------------------------
 /*   Trajectory left_trajectory = new Trajectory(1);
    Trajectory right_trajectory = new Trajectory(1);

    try {
    
      left_trajectory = PathfinderFRC.getTrajectory(kPath + ".right.pf1.csv");
  
  
      right_trajectory = PathfinderFRC.getTrajectory(kPath + ".left.pf1.csv");
    
    }
    catch(IOException e){
      e.printStackTrace();
    }
    
    leftFollower = new EncoderFollower(left_trajectory);
    rightFollower = new EncoderFollower(right_trajectory);
    //double position = Math.round(RobotMap.encoder0.getPosition());

    leftFollower.configureEncoder((int) RobotMap.encoder1.getPosition(), kTicksPerRev, kWheelDiameter / 254);
    leftFollower.configurePIDVA(1.0, 0.0, 0.0, 1 / kMaxVelocity, 0);

    rightFollower.configureEncoder((int) RobotMap.encoder0.getPosition(), kTicksPerRev, kWheelDiameter / 254);
    rightFollower.configurePIDVA(1.0, 0.0, 0.0, 1 / kMaxVelocity, 0);

    followerNotifier = new Notifier(this::followPath);
    if(followerNotifier != null){
    followerNotifier.startPeriodic(left_trajectory.get(0).dt);
    } */
//-----------------------------------------------------------------------------------------------------------------------------    

  }



  @Override
  public void autonomousPeriodic() {
    Scheduler.getInstance().run();
    SmartDashboard.putNumber("DriveDistanceAuto", encoder.getEncoder0Distance());
  }

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
    if(followerNotifier != null)
    {
    followerNotifier.stop();
    }
    RobotMap.dDrive.tankDrive(0,0);

    shiftGears.start(); //continually check to see if gears need to be shifted. May need to go in Teleop Periodic.

    startPosition = RobotMap.liftMotor.getSelectedSensorPosition();
  }
  ArmPosition armPosition = ArmPosition.home;
  @Override
  public void teleopPeriodic() {

    
    /*
    if (RobotMap.liftEncoder.getDistance() >= 1 && RobotMap.liftEncoder.getDistance() <= 1 && armPosition == ArmPosition.home){
      new ArmPivot(ArmPosition.miss).start();
      armPosition = ArmPosition.miss;
    }
    else if (RobotMap.liftEncoder.getDistance() <= 1 && RobotMap.liftEncoder.getDistance() >= 1 && armPosition == ArmPosition.miss){
      new ArmPivot(ArmPosition.home).start();
      armPosition = ArmPosition.home;
    }
    */
    Scheduler.getInstance().run();
    double x = limeLight.getX();
    double y = limeLight.getY();
    double area = limeLight.getArea();
    double v = limeLight.getV();
    SmartDashboard.putNumber("LimelightX", x);
    SmartDashboard.putNumber("LimelightY", y);
    SmartDashboard.putNumber("LimelightArea", area);
    SmartDashboard.putNumber("Target Valid", v);
    SmartDashboard.putNumber("Drive Distance Teleop", encoder.getEncoder0Distance());
    SmartDashboard.putNumber("Lift Encoder Distance", RobotMap.liftMotor.getSelectedSensorPosition());
    SmartDashboard.putNumber("RPMs ", RobotMap.encoder0.getVelocity());
    
  

    RobotMap.dDrive.arcadeDrive(-m_oi.driveStick.getRawAxis(1)*.8,m_oi.driveStick.getRawAxis(4)*.5);
   
    double Kp = -0.06;
    double min_command = -0.03;
    double leftCommand = 0;
    double rightCommand = 0;
    double steering_adjust = 0;double startPos;
    double distanceAdjust = 0;
    
    //AIMING AIMING AIMING AIMING
   if(m_oi.driveStick.getRawButton(4)){  //Y
      double heading_error = -x;
      if(x > 1.0){
        steering_adjust = Kp * heading_error - min_command; 
      }
      else if (x < -1.0){
        steering_adjust = Kp * heading_error + min_command; 
      }
      leftCommand += steering_adjust;
      rightCommand -= steering_adjust;
      RobotMap.dDrive.tankDrive(leftCommand*.75,rightCommand*.75);      
    } //END AIMING 

    //SEEKING SEEKING SEEKING SEEKING
    double kP2 = -0.03;
    if(m_oi.driveStick.getRawButton(3)){        
        if(v==0.0){
          //we don't see the target, seek for the target by spinning in place at a safe speed
          steering_adjust = 0.4;
        }
        else {
          //we do see the target, execute aiming code
          double heading_error = -x;      
          if(x > 1.0){
            steering_adjust = kP2 * heading_error - min_command;         
          }
          else if (x < -1.0){
            steering_adjust = kP2 * heading_error + min_command;
          }
        }
        leftCommand += steering_adjust;
        rightCommand -= steering_adjust;
        //if (m_oi.driveStick.getRawAxis(4) < 0){
         // RobotMap.dDrive.arcadeDrive(-0.3,rightCommand*.75);
        //}
        //if (m_oi.driveStick.getRawAxis(4) > 0){
         // RobotMap.dDrive.arcadeDrive(-0.3,leftCommand*.75);
        //}
        RobotMap.dDrive.tankDrive(leftCommand*.75,rightCommand*.75);  
    } 
    //END SEEKING
    
    double KpAim = -0.1;
    double minAimCommand = 0.05;
    if (m_oi.driveStick.getRawButton(5))
    {
      double headingError = -x;
      double distanceError = -y;
      double steeringAdjust = 0.0;      
      if(x > 1.0){
        steeringAdjust = KpAim * headingError - minAimCommand;
      }
      else if(x < -1.0){
      steeringAdjust = KpAim*headingError+minAimCommand; 
      }
      distanceAdjust = KpDistance * distanceError;
      leftCommand += steeringAdjust + distanceAdjust;
      rightCommand -= steeringAdjust + distanceAdjust;
      //The multiplier for the speed here does NOT make the actual speed go down by that much.
      RobotMap.dDrive.tankDrive((leftCommand)*.3,(-rightCommand)*.3);
    } 
  // end aim, seek, destroy
    if(m_oi.driveStick.getRawButton(1)){
      RobotMap.solenoidSteve.set(DoubleSolenoid.Value.kForward);
    } 

    if(m_oi.driveStick.getRawButton(2)){
      RobotMap.solenoidSteve.set(DoubleSolenoid.Value.kReverse);
    }  
    
    //if(m_oi.driveStick.getRawButton(3)){
    //   RobotMap.armMotor.set(ControlMode.PercentOutput, 1);
    // }
    // else if(m_oi.driveStick.getRawButton(4)){
    //   RobotMap.armMotor.set(ControlMode.PercentOutput, -1);
    // }
    // else{
    //   RobotMap.armMotor.set(ControlMode.PercentOutput, 0);
    // }

    // if(m_oi.driveStick.getRawButton(5)){
    //   RobotMap.intake.set(ControlMode.PercentOutput, 1);
    // }
    else if(m_oi.driveStick.getRawButton(6)){
      RobotMap.intake.set(ControlMode.PercentOutput, -.775);
    }
    else{
      RobotMap.intake.set(ControlMode.PercentOutput, 0);
    }
    if(m_oi.launchPad.getRawButton(1)){
      RobotMap.liftMotor.set(ControlMode.PercentOutput, 1);
    }
    else if(m_oi.launchPad.getRawButton(3)){
      RobotMap.liftMotor.set(ControlMode.PercentOutput, -1);
    }
    else{
      RobotMap.liftMotor.set(ControlMode.PercentOutput, 0);
    }
    
    if (m_oi.launchPad.getRawButton(8)){
      if((RobotMap.liftMotor.getSelectedSensorPosition() - startPosition) < -34206){
        RobotMap.liftMotor.set(ControlMode.PercentOutput, 1); //down
      } else if(RobotMap.liftMotor.getSelectedSensorPosition() - startPosition > -34206){
        RobotMap.liftMotor.set(ControlMode.PercentOutput, -1); //up
      }
      else RobotMap.liftMotor.set(ControlMode.PercentOutput, 0);
    }
    
    if (m_oi.launchPad.getRawButton(9)){
      if((RobotMap.liftMotor.getSelectedSensorPosition() - startPosition) < -70270){
        RobotMap.liftMotor.set(ControlMode.PercentOutput, 1); //down
      }
      else if((RobotMap.liftMotor.getSelectedSensorPosition() - startPosition) > -70270){
        RobotMap.liftMotor.set(ControlMode.PercentOutput, -1); //up
      }
      else{
        RobotMap.liftMotor.set(ControlMode.PercentOutput, 0);
      }
    }

    if(m_oi.launchPad.getRawButton(5)){
      if(RobotMap.liftMotor.getSelectedSensorPosition() > (startPosition - 360)){
        RobotMap.liftMotor.set(ControlMode.PercentOutput, 0); //stop
      }
      else if (RobotMap.liftMotor.getSelectedSensorPosition() < (startPosition - 360)){
        RobotMap.liftMotor.set(ControlMode.PercentOutput, 1); //down
      }
    }
  }

  

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}