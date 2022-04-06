package frc.robot.System;

import frc.robot.Library.FRC_3117_Tools.Component.FunctionScheduler;
import frc.robot.Library.FRC_3117_Tools.Component.Data.Input;
import frc.robot.Library.FRC_3117_Tools.Component.Data.InputManager;
import frc.robot.Library.FRC_3117_Tools.Interface.Component;
import frc.robot.System.Data.ClimberData;
import frc.robot.System.Data.Internal.ClimberDataInternal;

public class Climber implements Component
{
    public Climber(ClimberData data, ClimberDataInternal dataInternal)
    {
        Data = data;
        DataInternal = dataInternal;
    }

    public ClimberData Data;
    public ClimberDataInternal DataInternal;

    @Override
    public void Awake() 
    {
        DataInternal.CalibrationSequence = new FunctionScheduler();
        DataInternal.ClimbSequence = new FunctionScheduler();
        DataInternal.ClimbSequenceSafe = new FunctionScheduler();

        CreateCalibrateSequence();
        CreateClimbSequence();
        CreateClimbSequenceSafe();
    }

    @Override
    public void Init() 
    {
        Calibrate();

        SetArmAngle(-30);
    }

    private boolean _isNegative = true;

    @Override
    public void Disabled() 
    {
        
    }

    @Override
    public void DoComponent() 
    {
        var error = DataInternal.MovingArmTargetAngle - Data.MovingArmAngleEncoder.getDistance();

        if (Math.abs(error) >= 1)
        {
            Data.MovingArmAngleMotor.Set(0.3 * Math.signum(error));
        }
        else
        {
            Data.MovingArmAngleMotor.Set(0);
        }

        if (InputManager.GetButtonDown("TestClimber"))
        {
            _isNegative = !_isNegative;

            if (_isNegative)
            {
                SetArmAngle(-30);
            }
            else
            {
                SetArmAngle(30);
            }
        }

        System.out.println(Data.MovingArmAngleEncoder.getDistance());

        if (InputManager.GetButtonDown("ClimberSequence"))
        {
            StartSequence();
        }
        else if (InputManager.GetButtonDown("ClimberSequenceSafe"))
        {
            StartSequenceSafe();
        }

        if (DataInternal.CurrentSequence != null)
        {
            DataInternal.CurrentSequence.DoComponent();
        }
    }

    public void SetArmLenght(boolean fixed, double lenght)
    {
        if (fixed)
        {
            DataInternal.FixedArmTargetLenght = lenght;
        }
        else
        {
            DataInternal.MovingArmTargetLenght = lenght;
        }
    }
    public void SetArmAngle(double angle)
    {
        DataInternal.MovingArmTargetAngle = angle;
    }

    public void Calibrate()
    {
        Data.MovingArmAngleEncoder.reset();

        if (DataInternal.CurrentSequence == null)
        {
            DataInternal.CurrentSequence = DataInternal.CalibrationSequence.Copy();
            DataInternal.CurrentSequence.Start();
        }
    }

    public void StartSequence()
    {
        if (DataInternal.CurrentSequence == null)
        {
            DataInternal.CurrentSequence = DataInternal.ClimbSequence.Copy();
            DataInternal.CurrentSequence.Start();
        }
    }
    public void StartSequenceSafe()
    {
        if (DataInternal.CurrentSequence == null)
        {
            DataInternal.CurrentSequence = DataInternal.ClimbSequenceSafe.Copy();
            DataInternal.CurrentSequence.Start();
        }
    }

    public void StopSequence()
    {
        if (DataInternal.CurrentSequence != null)
        {
            DataInternal.CurrentSequence.Stop();
            DataInternal.CurrentSequence = null;
        }
    }

    private void CreateCalibrateSequence()
    {
        /*DataInternal.CalibrationSequence.
        AddWaituntil(() ->
        {
            var allSwitch = true;

            if (Data.FixedArmBotomSwitch.GetValue())
            {
                Data.FixedArmLenghtMotor.Set(0);
            }
            else
            {
                Data.FixedArmLenghtMotor.Set(-0.1);
                allSwitch = false;
            }

            if (Data.MovingArmBotomSwitch.GetValue())
            {
                allSwitch = allSwitch && true;
                Data.MovingArmLenghtMotor.Set(0);
            }
            else
            {
                Data.MovingArmLenghtMotor.Set(-0.1);
                allSwitch = false;
            }

            return true;
        });*/
    }
    private void CreateClimbSequence()
    {

    }
    private void CreateClimbSequenceSafe()
    {

    }
}
