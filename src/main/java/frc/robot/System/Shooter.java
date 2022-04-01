package frc.robot.System;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Robot;
import frc.robot.Library.FRC_3117_Tools.Component.LimeLight;
import frc.robot.Library.FRC_3117_Tools.Component.Data.InputManager;
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
    }

    public ShooterData Data;
    public ShooterDataInternal DataInternal;

    @Override
    public void Awake() 
    {
        Data.SpeedMotorGroup.SetBrake(false);

        DataInternal.Swerve = Robot.instance.GetComponent("Swerve");
    }

    @Override
    public void Init() 
    {
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

        DataInternal.ShooterRPMAverage.Evaluate(currentSpeed);

        //Handle Input
        if (InputManager.GetButtonDown("Align"))
        {
            DataInternal.IsAllign = !DataInternal.IsAllign;

            /*
            Might Be Added
            if (DataInternal.IsAllign)
                DataInternal.Swerve.SetCurrentMode(DrivingMode.Local);
            else
                DataInternal.Swerve.SetCurrentMode(DrivingMode.World);
            */
        }

        if (InputManager.GetButton("Shooter"))
            SetShooterRPM(3000);
        else
            SetShooterRPM(0);

        //Handle Alignment
        if (DataInternal.IsAllign)
        {
            var currentLimelight = LimeLight.GetCurrent();

            if(currentLimelight.IsTarget())
            {
                DataInternal.Swerve.OverrideRotationAxis(Data.DirectionController.Evaluate(-1 * currentLimelight.GetAngleX()));
            }
        }

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
        if (DataInternal.ShooterTargetAngle > 0)
        {
            var error = DataInternal.ShooterTargetAngle - Data.AngleEncoder.GetValueDegree();
            var max = 1;
            var min = -1;

            if (Data.AngleTopLimit.GetValue())
            {
                max = 0;
            }
            if (Data.AngleBotomLimit.GetValue())
            {
                min = 0;
            }

            Data.AngleMotor.Set(Mathf.Clamp(Data.AngleController.Evaluate(error), min, max));
        }
        else
        {
            Data.AngleMotor.Set(0);
        }
    }    

    public void SetShooterRPM(int targetRPM)
    {
        DataInternal.TargerRPM = targetRPM;
        Data.SpeedController.SetFeedForward(DataInternal.TargerRPM * 0.000075);
    }
    public void SetShooterAngle(double targetAngle)
    {
        DataInternal.ShooterTargetAngle = targetAngle;
    }

    public void CalibrateShooter()
    {
        
    }

    private boolean IsShooterReady()
    {
        return (Math.abs(DataInternal.ShooterRPMAverage.GetCurrent() - DataInternal.TargerRPM) / DataInternal.TargerRPM) <= 0.05;
    }
}
