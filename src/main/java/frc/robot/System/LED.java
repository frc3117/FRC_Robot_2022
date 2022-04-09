package frc.robot.System;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.CANifier.LEDChannel;

import frc.robot.Library.FRC_3117_Tools.Math.Mathf;
import frc.robot.System.Data.Color;

public class LED
{
    public LED(CANifier canifier)
    {
        _canifier = canifier;
    }

    private CANifier _canifier;
    private Color _currentColor;
    private double _currentPriority;

    public void Refresh()
    {
        _canifier.setLEDOutput(Mathf.Clamp(_currentColor.G * _currentColor.A, 0, 1), LEDChannel.LEDChannelA);
        _canifier.setLEDOutput(Mathf.Clamp(_currentColor.R * _currentColor.A, 0, 1), LEDChannel.LEDChannelB);
        _canifier.setLEDOutput(Mathf.Clamp(_currentColor.B * _currentColor.A, 0, 1), LEDChannel.LEDChannelC);
    
        _currentPriority = 0;
    }

    public void SetColor(Color color, double priority)
    {
        if (priority >= _currentPriority)
        {
            _currentColor = color;
        }
    }
    public void SetColor(double r, double g, double b, double priority)
    {
        SetColor(new Color(r, g, b, 1), priority);
    }
    public void SetColor(double r, double g, double b, double a, double priority)
    {
        SetColor(new Color(r, g, b, a), priority);
    }
}
