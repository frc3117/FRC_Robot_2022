package frc.robot.System;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Robot;
import frc.robot.Library.FRC_3117_Tools.Component.LimeLight;
import frc.robot.Library.FRC_3117_Tools.Component.Data.InputManager;
import frc.robot.Library.FRC_3117_Tools.Debug.CsvLogger;
import frc.robot.Library.FRC_3117_Tools.Interface.Component;
import frc.robot.Library.FRC_3117_Tools.Math.MovingAverage;
import frc.robot.System.Data.ShooterData;
import frc.robot.System.Data.Internal.ShooterDataInternal;

public class Shooter implements Component
{
    public Shooter(ShooterData data, ShooterDataInternal dataInternal)
    {
        Data = data;
        DataInternal = dataInternal;

        DataInternal.ErrorMovingAverageFeedforward = new MovingAverage(15);
        DataInternal.ShooterInputMovingAverage = new MovingAverage(60);
    }

    public ShooterData Data;
    public ShooterDataInternal DataInternal;

    @Override
    public void Awake() 
    {
        Data.SpeedMotorGroup.SetBrake(false);

        DataInternal.Swerve = Robot.GetComponent("Swerve");
    }

    @Override
    public void Init() 
    {
        DataInternal.IsfeedforwardCalculation = false;
        DataInternal.TargerRPM = 0;
        DataInternal.IsAllign = false;
    }

    @Override
    public void Disabled() 
    {

    }

    @Override
    public void DoComponent()
    {
        var currentSpeed = (Data.SpeedEncoder.getRate() / 2048) * 60;
        SmartDashboard.putNumber("shooterRPM", currentSpeed);
        
        if (InputManager.GetButtonDown("Align"))
        {
            DataInternal.IsAllign = !DataInternal.IsAllign;
        }

        if (DataInternal.IsAllign)
        {
            var currentLimelight = LimeLight.GetCurrent();

            if(currentLimelight.IsTarget())
            {
                DataInternal.Swerve.OverrideRotationAxis(Data.DirectionController.Evaluate(-1 * currentLimelight.GetAngleX()));
            }
        }

        /*
        switch (_tempJoystock.getPOV())
        {
            //Go Up
            case 0:
                if (!ShooterData.ShooterAngleTopLimit.GetValue())
                    ShooterData.AngleMotor.Set(0.25);
                else
                    ShooterData.AngleMotor.Set(0);
                break;

            //Go Down
            case 180:
                if (!ShooterData.ShooterAngleBotomLimit.GetValue())
                    ShooterData.AngleMotor.Set(-0.17);
                else
                    ShooterData.AngleMotor.Set(0);
                break;

            //Nothing
            default:
                ShooterData.AngleMotor.Set(0);
                break;
        }*/

        if (!DataInternal.IsfeedforwardCalculation)
        {
            Data.SpeedController.SetFeedForward(DataInternal.TargerRPM * 0.00015);

            if (InputManager.GetButton("Shooter"))
            {
                SetShooterRPM(3000);
                DataInternal.ShooterInputMovingAverage.Evaluate(1);
            }
            else
            {
                SetShooterRPM(0);
                DataInternal.ShooterInputMovingAverage.Evaluate(0);
            }

            if (DataInternal.TargerRPM > 0)
            {
                var error = DataInternal.TargerRPM - currentSpeed;
            
                if (DataInternal.ShooterInputMovingAverage.GetCurrent() > 0.8)
                    Data.IntakeMotor.Set(-0.5);
                else
                    Data.IntakeMotor.Set(0);

                Data.SpeedMotorGroup.Set(Data.SpeedController.Evaluate(error) * -1);
            }
            else
            {
                Data.SpeedMotorGroup.Set(0);
                Data.IntakeMotor.Set(0);
            }
        }
        else
        {
            DataInternal.FrameTotalFeedforwardCalculation++;
            DataInternal.ErrorMovingAverageFeedforward.Evaluate(currentSpeed - DataInternal.TargerRPM);

            if (DataInternal.CurrentFeedforwardCalculation > 1)
                DataInternal.FrameOverMaxFeedforwardCalculation++;
            else
                DataInternal.FrameOverMaxFeedforwardCalculation = 0;

            if ((DataInternal.CurrentFeedforwardCalculation > 1 && DataInternal.FrameOverMaxFeedforwardCalculation >= 100) || DataInternal.FrameTotalFeedforwardCalculation >= 1000)
            {
                //Failed
                DataInternal.FeedforwardCalculationLogger.SetValue("RPM", DataInternal.TargerRPM);
                DataInternal.FeedforwardCalculationLogger.SetValue("Integral", -9999);

                SetShooterRPM(DataInternal.TargerRPM + 100);
                DataInternal.ErrorMovingAverageFeedforward.Clear();
            }
            else if (Math.abs(DataInternal.ErrorMovingAverageFeedforward.GetCurrent()) <= 250 )
            {
                //Success
                DataInternal.FeedforwardCalculationLogger.SetValue("RPM", DataInternal.TargerRPM);
                DataInternal.FeedforwardCalculationLogger.SetValue("Integral", DataInternal.CurrentFeedforwardCalculation);

                SetShooterRPM(DataInternal.TargerRPM + 100);
                DataInternal.ErrorMovingAverageFeedforward.Clear();
            }

            if (DataInternal.TargerRPM >= 6000)
            {
                StopFeedforwardCalculator();
                return;
            }

            Data.SpeedMotorGroup.Set(DataInternal.CurrentFeedforwardCalculation);
        }
    }    

    public void StartFeedforwardCalculator()
    {
        DataInternal.FeedforwardCalculationLogger = new CsvLogger();

        DataInternal.FeedforwardCalculationLogger.AddColumn("RPM");
        DataInternal.FeedforwardCalculationLogger.AddColumn("Integral");

        DataInternal.CurrentFeedforwardCalculation = 0;
        DataInternal.ErrorMovingAverageFeedforward.Clear();
        DataInternal.IsfeedforwardCalculation = true;
    }
    public void StopFeedforwardCalculator()
    {
        DataInternal.FeedforwardCalculationLogger.SaveToFile("FeedforwardCalculation");

        DataInternal.IsfeedforwardCalculation = false;
    }

    public void SetShooterRPM(int targetRPM)
    {
        DataInternal.TargerRPM = targetRPM;
    }
    public void SetShooterAngle(double targetAngle)
    {
        DataInternal.ShooterTargetAngle = targetAngle;
    }
}
