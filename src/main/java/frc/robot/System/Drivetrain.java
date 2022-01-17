package frc.robot.System;

import edu.wpi.first.wpilibj.Joystick;

public class Drivetrain 
{
    private Joystick _joystick;

    private boolean _isHorizontalOverride = false;
    private double _horizontalOverride = 0;

    private boolean _isVerticalOverride = false;
    private double _verticalOverride = 0;

    public Drivetrain()
    {
        _joystick = new Joystick(0);
    }

    public void Init()
    {

    }

    public void DoSystem()
    {
        var horizontal = _isHorizontalOverride ? _horizontalOverride : _joystick.getRawAxis(1);
        var vertical = _isVerticalOverride ? _verticalOverride : _joystick.getRawAxis(2);

        //Do the drivetrain code here

        _isHorizontalOverride = false;
        _isVerticalOverride = false;
    }

    public void OverrideHorizontal(double value)
    {
        _isHorizontalOverride = true;
        _horizontalOverride =  value;
    }
    public void OverrideVertical(double value)
    {
        _isVerticalOverride = true;
        _verticalOverride = value;
    }
}
