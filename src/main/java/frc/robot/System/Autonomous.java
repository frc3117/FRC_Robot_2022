package frc.robot.System;

import java.util.HashMap;

import frc.robot.Robot;
import frc.robot.Library.FRC_3117_Tools.Component.FunctionScheduler;
import frc.robot.Library.FRC_3117_Tools.Component.Swerve;
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
        CreateSequences();
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

        if (DataInternal.AutonomousModes.containsKey(auto))
        {
            DataInternal.CurrentSequence = DataInternal.AutonomousModes.get(auto).Copy();
            DataInternal.CurrentSequence.Copy();

            DataInternal.CurrentSequence.Start();
        }
    }

    private void CreateSequences()
    {
        DataInternal.AutonomousModes = new HashMap<>();
        DataInternal.AutonomousModes.put(AutonomousMode.SimpleBackward, CreateSimpleBackward());
    }

    private FunctionScheduler CreateSimpleBackward()
    {
        var scheduer = new FunctionScheduler();

        scheduer.AddRunFor(() ->
        {
            ((Swerve)Robot.instance.GetComponent("Swerve")).OverrideVerticalAxis(0.5);
            ((Shooter)Robot.instance.GetComponent("Shooter")).Align(true);
        }, 3);

        return scheduer;
    }
}
