// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import frc.robot.System.Climber;
import frc.robot.System.Drivetrain;
import frc.robot.System.Feeder;
import frc.robot.System.Part.Timer;

public class Robot extends TimedRobot 
{
  public static Climber Climber;
  public static Drivetrain Drivetrain;
  public static Feeder Feeder;

  @Override
  public void robotInit() 
  {
    Climber = new Climber();
    Drivetrain = new Drivetrain();
    Feeder = new Feeder();
  }

  @Override
  public void robotPeriodic() {}

  @Override
  public void autonomousInit() {}

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() 
  {
    Timer.Init();

    Climber.Init();
    Drivetrain.Init();
    Feeder.Init();
  }

  @Override
  public void teleopPeriodic() 
  {
    Timer.Calculate();

    Climber.DoSystem();
    Drivetrain.DoSystem();
    Feeder.DoSystem();
  }
}
