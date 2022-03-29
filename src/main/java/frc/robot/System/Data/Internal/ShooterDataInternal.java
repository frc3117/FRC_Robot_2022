package frc.robot.System.Data.Internal;

import frc.robot.Library.FRC_3117_Tools.Component.Swerve;
import frc.robot.Library.FRC_3117_Tools.Debug.CsvLogger;
import frc.robot.Library.FRC_3117_Tools.Math.MovingAverage;

public class ShooterDataInternal 
{
    public ShooterDataInternal() { }

    public MovingAverage ShooterInputMovingAverage;
    public MovingAverage ErrorMovingAverageFeedforward;

    public boolean IsAllign;
    public boolean IsfeedforwardCalculation;

    public int TargerRPM;
    public int FrameTotalFeedforwardCalculation;
    public int FrameOverMaxFeedforwardCalculation;

    public double ShooterTargetAngle;
    public double CurrentFeedforwardCalculation;
    
    public CsvLogger FeedforwardCalculationLogger;

    public Swerve Swerve;
}
