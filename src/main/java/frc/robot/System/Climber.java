package frc.robot.System;

import frc.robot.Library.FRC_3117_Tools.Component.FunctionScheduler;
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
    }

    @Override
    public void Disabled() 
    {
        
    }

    @Override
    public void DoComponent() 
    {
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
        DataInternal.CalibrationSequence.AddFunction(() ->
        {
            
        });
    }
    private void CreateClimbSequence()
    {

    }
    private void CreateClimbSequenceSafe()
    {

    }
}
