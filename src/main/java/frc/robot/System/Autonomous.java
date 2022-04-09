package frc.robot.System;

import frc.robot.Library.FRC_3117_Tools.Interface.Component;
import frc.robot.Robot.AutonomousMode;
import frc.robot.System.Data.AutonomousData;
import frc.robot.System.Data.Internal.AutonomousDataInternal;

public class Autonomous implements Component 
{
    public Autonomous(AutonomousData data, AutonomousDataInternal dataInternal)
    {
        Data = data;
        DataInternal = dataInternal;
    }
    
    public AutonomousData Data;
    public AutonomousDataInternal DataInternal;

    @Override
    public void Awake() 
    {

    }

    @Override
    public void Init() 
    {

    }

    @Override
    public void Disabled() 
    {
        if (DataInternal.CurrentSequence != null)
        {
            DataInternal.CurrentSequence.Stop();
            DataInternal.CurrentSequence = null;
        }
    }

    @Override
    public void DoComponent() 
    {
        if (DataInternal.CurrentSequence != null && !DataInternal.CurrentSequence.IsCompleted())
        {
            DataInternal.CurrentSequence.DoComponent();
        }
    }
    
    public void Start(AutonomousMode auto)
    {
        DataInternal.CurrentMode = auto;
        DataInternal.CurrentSequence = DataInternal.AutonomousModes.get(auto);

        if (DataInternal.AutonomousModes.containsKey(auto))
        {
            DataInternal.CurrentSequence = DataInternal.AutonomousModes.get(auto).Copy();
            DataInternal.CurrentSequence.Copy();
        }
    }
}
