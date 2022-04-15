package frc.robot.System;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Robot;
import frc.robot.Library.FRC_3117_Tools.Component.LimeLight;
import frc.robot.Library.FRC_3117_Tools.Component.Data.InputManager;
import frc.robot.Library.FRC_3117_Tools.Component.Swerve.DrivingMode;
import frc.robot.Library.FRC_3117_Tools.Interface.Component;
import frc.robot.Library.FRC_3117_Tools.Math.Mathf;
import frc.robot.Library.FRC_3117_Tools.Math.MovingAverage;
import frc.robot.System.Data.ShooterData;
import frc.robot.System.Data.Internal.ShooterDataInternal;

public class Shooter implements Component
{
    public Shooter(ShooterData data, ShooterDataInternal dataInternal)
    {
        Data = data;
        DataInternal = dataInternal;

        DataInternal.ShooterRPMAverage = new MovingAverage(15);
        DataInternal.DistanceAverager = new MovingAverage(4);
    }

    public ShooterData Data;
    public ShooterDataInternal DataInternal;

    @Override
    public void Awake() 
    {
        Data.SpeedMotorGroup.SetBrake(false);

        SmartDashboard.putNumber("ShooterRPM", 0);
        SmartDashboard.putNumber("ShooterFeedForward", 0);

        DataInternal.Swerve = Robot.instance.GetComponent("Swerve");
    }

    @Override
    public void Init() 
    {
        DataInternal.TargerRPM = 0;
        DataInternal.IsAllign = false;

        //CalibrateShooter();
    }

    @Override
    public void Disabled() 
    {

    }

    @Override
    public void DoComponent()
    {
        var currentSpeed = (Data.SpeedEncoder.getRate() / 2048) * 60;
        SmartDashboard.putNumber("shooterRPM", DataInternal.ShooterRPMAverage.GetCurrent());

        DataInternal.ShooterRPMAverage.Evaluate(currentSpeed);

        if (DataInternal.IsCalibrating)
        {
            if (Data.AngleBottomLimit.GetValue())
            {
                DataInternal.IsCalibrating = false;
                DataInternal.ShooterAngleOffset = Data.AngleEncoder.GetValueDegree();
                
                SetShooterAngle(-2);
            }
            else
            {
                Data.AngleMotor.Set(-0.4);
                return;
            }
        }

        //Handle Input
        if (InputManager.GetButtonDown("Align"))
        {
            DataInternal.IsAllign = !DataInternal.IsAllign;
        }

        if (InputManager.GetButtonDown("Shooter"))
        {
            Data.SpeedController.Reset();
        }

        //Handle Alignment
        if (DataInternal.IsAllign)
        {
            var currentLimelight = LimeLight.GetCurrent();

            if(currentLimelight.IsTarget())
            {
                var distance = (2.56-0.85) / Math.tan(Math.toRadians((31 + currentLimelight.GetAngleY())));

                DataInternal.DistanceAverager.Evaluate(distance);

                //SetShooterAngle(3.723 * DataInternal.DistanceAverager.GetCurrent() + 7.175);
                DataInternal.TargerRPM = (int)((143 * DataInternal.DistanceAverager.GetCurrent() + 877) * 1.95);
                //DataInternal.TargerRPM = (int)((84.35 * DataInternal.DistanceAverager.GetCurrent() + 1110.545) * 2.2);

                DataInternal.Swerve.OverrideRotationAxis(Data.DirectionController.Evaluate(-1 * currentLimelight.GetAngleX()));
            }
        }

        if (InputManager.GetButton("Shooter") || DataInternal.IsManualShooter)
        {
            DataInternal.Swerve.OverrideRotationAxis(0);
            SetShooterRPM(DataInternal.TargerRPM);
        }
        else
            SetShooterRPM(0);

        //Handle Target RPM
        if (DataInternal.TargerRPM > 0)
        {
            var error = DataInternal.TargerRPM - currentSpeed;
        
            if (IsShooterReady())
                Data.IntakeMotor.Set(-0.5);
            else
                Data.IntakeMotor.Set(0);

            Data.SpeedMotorGroup.Set(Data.SpeedController.Evaluate(error));
        }
        else
        {
            Data.SpeedMotorGroup.Set(0);
            Data.IntakeMotor.Set(0);
        }

        //Handle Target Angle
        /*if (DataInternal.ShooterTargetAngle > 0)
        {
            var error = DataInternal.ShooterTargetAngle - GetCurrentAngle();
            var max = 1;
            var min = -1;

            if (Data.AngleTopLimit.GetValue())
            {
                max = 0;
            }
            if (Data.AngleBottomLimit.GetValue())
            {
                min = 0;
            }

            if (Math.abs(error) >= 3)
            {
                Data.AngleMotor.Set(Mathf.Clamp(0.35 * Math.signum(error), min, max));
            }
            else
            {
                Data.AngleMotor.Set(0);
            }
        }
        else
        {
            Data.AngleMotor.Set(0);
        }*/

        DataInternal.IsManualShooter = false;
    }    

    public void SetShooterRPM(int targetRPM)
    {
        DataInternal.TargerRPM = targetRPM;
        Data.SpeedController.SetFeedForward(0.00021  * targetRPM + 0.00976);
    }
    public void SetShooterAngle(double targetAngle)
    {
        DataInternal.ShooterTargetAngle = targetAngle;
    }

    public void Align()
    {
        DataInternal.IsAllign = !DataInternal.IsAllign;
    }
    public void Align(boolean state)
    {
        DataInternal.IsAllign = state;
    }

    public void ManualShoot(boolean state)
    {
        DataInternal.IsManualShooter = state;
    }

    public void CalibrateShooter()
    {
        DataInternal.IsCalibrating = true;
    }

    public double GetCurrentAngle()
    {
        return (Data.AngleEncoder.GetValueDegree() - DataInternal.ShooterAngleOffset) / 4;
    }

    private boolean IsShooterReady()
    {
        return (Math.abs(DataInternal.ShooterRPMAverage.GetCurrent() - DataInternal.TargerRPM) / DataInternal.TargerRPM) <= 0.05;
    }
}
