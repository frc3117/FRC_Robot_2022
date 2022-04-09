package frc.robot.System.Data.Internal;

import java.util.HashMap;

import frc.robot.Library.FRC_3117_Tools.Component.FunctionScheduler;
import frc.robot.Robot.AutonomousMode;

public class AutonomousDataInternal 
{
    public AutonomousMode CurrentMode;

    public FunctionScheduler CurrentSequence;

    public HashMap<AutonomousMode, FunctionScheduler> AutonomousModes;
}
