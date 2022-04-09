package frc.robot.System;

import frc.robot.Library.FRC_3117_Tools.Component.Data.Input;
import frc.robot.Library.FRC_3117_Tools.Component.Data.InputManager;
import frc.robot.Library.FRC_3117_Tools.Interface.Component;
import frc.robot.System.Data.FeederData;
import frc.robot.System.Data.Internal.FeederDataInternal;

public class Feeder implements Component
{
    public Feeder(FeederData data, FeederDataInternal dataInternal)
    {
        Data = data;
        DataInternal = dataInternal;
    }

    public enum AngleTarget
    {
        Up,
        Down,
        Manual,
        Climb
    }

    public FeederData Data;
    public FeederDataInternal DataInternal;

    @Override
    public void Awake() 
    {
        DataInternal.FeederSpeed = 0.4;
    }

    @Override
    public void Init() 
    {
    }

    @Override
    public void Disabled() 
    {
    }

    @Override
    public void DoComponent() 
    {
        /*if (Data.EdgeLimitSwitch.GetValue())
        {
            DataInternal.FeederSpeed = 0.1;
        }
        else
        {
            DataInternal.FeederSpeed = 0.3;
        }*/
        
        if (InputManager.GetButtonDown("FeederToggle"))
        {
            if (DataInternal.Target == AngleTarget.Up)
            {
                DataInternal.Target = AngleTarget.Down;
            }
            else
            {
                DataInternal.Target = AngleTarget.Up;
            }
        }


        if (Input.GetButton("FeedForward"))
        {
            Data.FeedMotor.Set(-0.55);
        }
        else if (Input.GetButton("FeedBackward"))
        {
            Data.FeedMotor.Set(0.55);
        }
        else
        {
            Data.FeedMotor.Set(0);
        }

        switch(DataInternal.Target)
        {
            case Up:
                HandleUp();
                break;

            case Down:
                HandleDown();
                break;

            case Manual:
                HandleManual();
                break;
        }
    }

    private void Calibrate()
    {
    }

    private void HandleUp()
    {
        if (!Data.TopLimitSwitch.GetValue())
        {
            Data.AngleMotor.Set(DataInternal.FeederSpeed);
        }
        else
        {
            Data.AngleMotor.Set(0);
        }
    }
    private void HandleDown()
    {   
        if (!Data.BottomLimitSwitch.GetValue())
        {
            Data.AngleMotor.Set(DataInternal.FeederSpeed * -1);
        }
        else
        {
            Data.AngleMotor.Set(0);
        }
    }
    private void HandleManual()
    {
        if (Input.GetButton("FeederUpAnalog"))
        {
            Data.AngleMotor.Set(0.25);
        }
        else if (Input.GetButton("FeederDownAnalog"))
        {
            Data.AngleMotor.Set(-0.20);
        }
        else
        {
            Data.AngleMotor.Set(0);
        }
    }
}
