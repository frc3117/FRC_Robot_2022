package frc.robot;

import java.nio.ByteBuffer;

import edu.wpi.first.hal.CANData;
import edu.wpi.first.wpilibj.CAN;

public class TestCanDevice 
{
    public TestCanDevice(int deviceID)
    {
        _deviceID = deviceID;
        Open();
    }

    private int _deviceID;
    private CAN _can;
    private long _currentNumber = 0;

    public long GetNumber()
    {
        var data = new CANData();
        if (_can.readPacketNew(0, data))
        {
            var bb = ByteBuffer.wrap(data.data);
            _currentNumber = bb.getLong();
        }

        return _currentNumber;
    }

    public void Open()
    {
        _can = new CAN(_deviceID);
    }
    public void Close()
    {
        _can.close();
        _can = null;
    }

    public boolean IsOpen()
    {
        return _can != null;
    }
}
