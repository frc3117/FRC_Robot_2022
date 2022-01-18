package frc.robot.System;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.motorcontrol.PWMTalonSRX;

public class Drivetrain 
{
    public static final int MOTOR_LEFT_PWM_CHANNEL = 2;
    public static final int MOTOR_RIGHT_PWM_CHANNEL = 3;

    private Joystick _joystick;

    private PWMTalonSRX _motorLeft;
    private PWMTalonSRX _motorRight;

    private boolean _isHorizontalOverride = false;
    private double _horizontalOverride = 0;

    private boolean _isVerticalOverride = false;
    private double _verticalOverride = 0;

    public Drivetrain()
    {
        _joystick = new Joystick(0);

        _motorLeft = new PWMTalonSRX(MOTOR_LEFT_PWM_CHANNEL);
        _motorRight = new PWMTalonSRX(MOTOR_RIGHT_PWM_CHANNEL);
    }

    public void Init()
    {

    }

    public void DoSystem()
    {
        var horizontal = _isHorizontalOverride ? _horizontalOverride : (-1 * _joystick.getRawAxis(0));
        var vertical = _isVerticalOverride ? _verticalOverride : _joystick.getRawAxis(1);

        //Do the drivetrain code here
        _motorLeft.set(vertical + horizontal);
        _motorRight.set(vertical - horizontal);


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
