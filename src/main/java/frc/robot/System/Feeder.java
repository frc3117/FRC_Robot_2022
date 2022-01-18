package frc.robot.System;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj.motorcontrol.PWMTalonSRX;

public class Feeder 
{
    public static final int SERVO_FEEDER_PWM_CHANNEL = 4;
    public static final int WHEEL_FEEDER_PWM_CHANNEL = 5;

    private PWMTalonSRX _motorFeeder;
    
    private Servo _servo;
    
    private Joystick _joystick;
    
    private boolean _isdown = false;

    public Feeder()
    {
        _joystick = new Joystick(0);
        _motorFeeder = new PWMTalonSRX(WHEEL_FEEDER_PWM_CHANNEL);
        _servo = new Servo(SERVO_FEEDER_PWM_CHANNEL);
    }

    public void Init()
    {
        
    }

    public void DoSystem()
    {
        var feeder = _joystick.getRawButton(0);
        var servo = _joystick.getRawButtonPressed(1);

        if (feeder) 
            _motorFeeder.set(1);
        else
            _motorFeeder.set(0);
        
        if (servo) 
        {
            if (_isdown)
                _servo.set(0);
            else
                _servo.set(90);
            _isdown = !_isdown;
        }
    }
}
