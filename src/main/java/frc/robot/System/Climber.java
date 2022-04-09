package frc.robot.System;

import frc.robot.Robot;
import frc.robot.Library.FRC_3117_Tools.Component.FunctionScheduler;
import frc.robot.Library.FRC_3117_Tools.Component.Data.InputManager;
import frc.robot.Library.FRC_3117_Tools.Interface.Component;
import frc.robot.Library.FRC_3117_Tools.Math.Mathf;
import frc.robot.System.Data.ClimberData;
import frc.robot.System.Data.Internal.ClimberDataInternal;

public class Climber implements Component
{
    public Climber(ClimberData data, ClimberDataInternal dataInternal)
    {
        Data = data;
        DataInternal = dataInternal;
    }

    public enum ArmType
    {
        Moving,
        Fixed
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

        SetArmAngle(0);
    }

    @Override
    public void Disabled() 
    {
        StopSequence();
    }

    @Override
    public void DoComponent() 
    {
        HandleMovingArm();
        HandleFixedArm();

        if (InputManager.GetButtonDown("ClimberSequence"))
        {
            StartSequence();
        }

        if(InputManager.GetButtonDown("ClimberSetAngleTargetCurrent"))
        {
            SetArmAngle(Data.MovingArmAngleEncoder.getDistance());
        }

        if (InputManager.GetButtonDown("ClimberZeroAngle"))
        {
            SetArmAngle(0);
        }

        if (DataInternal.CurrentSequence != null)
        {
            DataInternal.CurrentSequence.DoComponent();
        }
    }

    public void SetArmLenght(ArmType type, double lenght)
    {
        if (type == ArmType.Fixed)
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

    public void SetAngleSpeed(double speed)
    {
        DataInternal.AngleSpeed = speed;
    }
    public void SetLenghtSpeed(double speed)
    {
        DataInternal.LenghtSpeed = speed;
    }

    private void HandleMovingArm()
    {
        var error = DataInternal.MovingArmTargetAngle - Data.MovingArmAngleEncoder.getDistance();
        if (!IsMovingAngleTarget())
        {
            Data.MovingArmAngleMotor.Set(DataInternal.AngleSpeed * Math.signum(error));
        }
        else
        {
            Data.MovingArmAngleMotor.Set(0);
        }

        var min = -1;
        var max = 1;

        if (Data.MovingArmTopSwitch.GetValue())
        {
            max = 0;
        }
        if (Data.MovingArmBottomSwitch.GetValue())
        {
            min = 0;
        }

        error = DataInternal.MovingArmTargetLenght - Data.MovingArmLenghtEncoder.getDistance();
        if (!IsMovingLenghtTarget())
        {
            Data.MovingArmLenghtMotor.Set(Mathf.Clamp(DataInternal.LenghtSpeed * Math.signum(error), min, max));
        }
        else
        {
            Data.MovingArmLenghtMotor.Set(0);
        }
    }
    private void HandleFixedArm()
    {
        var min = -1;
        var max = 1;

        if (Data.FixedArmTopSwitch.GetValue())
        {
            max = 0;
        }
        if (Data.FixedArmBottomSwitch.GetValue())
        {
            min = 0;
        }

        var error = DataInternal.FixedArmTargetLenght - Data.FixedArmLenghtEncoder.getDistance();
        if (!IsFixedLenghtTarget())
        {
            Data.FixedArmLenghtMotor.Set(Mathf.Clamp(DataInternal.LenghtSpeed * Math.signum(error), min, max));
        }
        else
        {
            Data.FixedArmLenghtMotor.Set(0);
        }
    }

    private boolean IsMovingAngleTarget()
    {
        var error = DataInternal.MovingArmTargetAngle - Data.MovingArmAngleEncoder.getDistance();
        return Math.abs(error) <= 1;
    }
    private boolean IsMovingLenghtTarget()
    {
        var error = DataInternal.MovingArmTargetLenght - Data.MovingArmLenghtEncoder.getDistance();
        
        if (Math.abs(error) <= 0.003)
        {
            return true;
        }
        else if (Math.signum(error) > 0 && Data.MovingArmTopSwitch.GetValue())
        {
            return true;
        }
        else if (Math.signum(error) < 0 && Data.MovingArmBottomSwitch.GetValue())
        {
            return true;
        }

        return false;
    }
    private boolean IsFixedLenghtTarget()
    {
        var error = DataInternal.FixedArmTargetLenght - Data.FixedArmLenghtEncoder.getDistance();
        
        if (Math.abs(error) <= 0.003)
        {
            return true;
        }
        else if (Math.signum(error) > 0 && Data.FixedArmTopSwitch.GetValue())
        {
            return true;
        }
        else if (Math.signum(error) < 0 && Data.FixedArmBottomSwitch.GetValue())
        {
            return true;
        }

        return false;
    }

    private boolean WaitUntilAngle(double angle)
    {
        SetArmAngle(angle);

        return IsMovingAngleTarget();
    }
    private boolean WaitUntilLenght(ArmType type, double lenght)
    {
        SetArmLenght(type, lenght);

        if (type == ArmType.Fixed)
        {
            return IsFixedLenghtTarget();
        }
        else
        {
            return IsMovingLenghtTarget();
        }
    }

    private void CreateCalibrateSequence()
    {
        DataInternal.CalibrationSequence.
        AddFunction(() -> 
        { 
            Data.MovingArmAngleEncoder.reset();
        }).
        AddWaituntil(() ->
        {
            var allSwitch = true;

            SetLenghtSpeed(0.1);

            if (Data.FixedArmBottomSwitch.GetValue())
            {
                Data.FixedArmLenghtMotor.Set(0);
                Data.FixedArmLenghtEncoder.reset();

                allSwitch = allSwitch == true;
            }
            else
            {
                Data.FixedArmLenghtMotor.Set(-0.3);

                allSwitch = false;
            }

            if (Data.MovingArmBottomSwitch.GetValue())
            {
                Data.MovingArmLenghtMotor.Set(0);
                Data.MovingArmLenghtEncoder.reset();

                allSwitch = allSwitch == true;
            }
            else
            {
                Data.MovingArmLenghtMotor.Set(-0.3);

                allSwitch = false;
            }

            return allSwitch;
        }).
        AddFunction(() -> 
        { 
            System.out.println("Climber calibration over!");

            SetArmLenght(ArmType.Moving, 0);
            SetArmLenght(ArmType.Fixed, 0);

            SetAngleSpeed(0.5);
            SetLenghtSpeed(1);

            StopSequence();
        });
    }
    private void CreateClimbSequence()
    {
        DataInternal.ClimbSequence.AddWaituntil(() ->
        {
            return WaitUntilLenght(ArmType.Moving, 0.7) & WaitUntilLenght(ArmType.Fixed, 0.7);
        }).
        AddWait(5).
        AddWaituntil(() -> 
        {
            return WaitUntilLenght(ArmType.Fixed, 0.02);
        }).
        AddWaituntil(() -> 
        {
            return WaitUntilAngle(30);
        }).
        AddWaituntil(() ->
        {
            return WaitUntilLenght(ArmType.Moving, 0.53);
        }).
        AddWaituntil(() ->
        {
            SetLenghtSpeed(1);
            return WaitUntilLenght(ArmType.Fixed, 0.15) & WaitUntilAngle(30);
        }).
        AddWaituntil(() -> 
        {
            return WaitUntilLenght(ArmType.Fixed, 0.26) & WaitUntilAngle(35);
        }).
        AddWaituntil(() ->
        {
            return WaitUntilLenght(ArmType.Fixed, 0.4);
        }).
        AddWaituntil(() -> 
        {
            return WaitUntilLenght(ArmType.Fixed, 0.6) & WaitUntilAngle(25);
        }).
        AddWaituntil(() ->
        {
            if (!Data.FixedArmFrontLeftSwitch.GetValue() && !Data.FixedArmFrontRightSwitch.GetValue())
            {
                return WaitUntilAngle(10);
            }
            
            StopSequence();
            return false;
        }).
        AddWaituntil(() ->
        {
            return WaitUntilLenght(ArmType.Fixed, 0.4);
        }).
        AddWaituntil(() ->
        {
            return WaitUntilAngle(-10);
        }).
        AddWaituntil(() ->
        {
            SetLenghtSpeed(0.75);
            return WaitUntilLenght(ArmType.Moving, 0) & WaitUntilLenght(ArmType.Fixed, 0.7);
        }).
        AddWaituntil(() ->
        {
            SetAngleSpeed(0.3);
            return WaitUntilAngle(-28);
        }).
        AddWaituntil(() ->
        {
            ((Feeder)Robot.instance.GetComponent("Feeder")).DataInternal.Target = Feeder.AngleTarget.Down;
            return WaitUntilLenght(ArmType.Fixed, 0.53);
        }).
        AddWaituntil(() ->
        {
            return WaitUntilLenght(ArmType.Moving, 0.15) & WaitUntilAngle(-35);
        }).
        AddWaituntil(() ->
        {
            return WaitUntilLenght(ArmType.Moving, 0.7);
        }).
        AddFunction(this::StopSequence);
    }
    private void CreateClimbSequenceSafe()
    {

    }
}
