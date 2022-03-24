package frc.robot;

import java.util.LinkedHashMap;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.Vector2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Library.FRC_3117_Tools.Component.Swerve;
import frc.robot.Library.FRC_3117_Tools.Component.Data.Input;
import frc.robot.Library.FRC_3117_Tools.Component.Data.InputManager;
import frc.robot.Library.FRC_3117_Tools.Component.Data.MotorController;
import frc.robot.Library.FRC_3117_Tools.Component.Data.WheelData;
import frc.robot.Library.FRC_3117_Tools.Component.Data.MotorController.MotorControllerType;
import frc.robot.Library.FRC_3117_Tools.Component.Data.MotorControllerGroup;
import frc.robot.Library.FRC_3117_Tools.Component.Data.Tupple.Pair;
import frc.robot.Library.FRC_3117_Tools.Component.FRC_Robot_Server.RobotServerClient;
import frc.robot.Library.FRC_3117_Tools.Component.Swerve.DrivingMode;
import frc.robot.Library.FRC_3117_Tools.Interface.Component;
import frc.robot.Library.FRC_3117_Tools.Math.SimplePID;
import frc.robot.Library.FRC_3117_Tools.Math.Timer;
import frc.robot.System.ConveyorBelt;
import frc.robot.System.Feeder;
import frc.robot.System.Shooter;
import frc.robot.Wrapper.ADIS16448_IMU_Gyro;

public class Robot extends TimedRobot {

  public enum AutonomousMode
  {

  }

  public static Robot instance;
  public static RobotServerClient serverClient;
  public static AutonomousMode currentAutonomous;

  private SendableChooser<AutonomousMode> _autoChooser;
  private LinkedHashMap<String, Component> _componentList;
  private boolean _hasBeenInit;

  @Override
  public void robotInit()
  {
    instance = this;

    _autoChooser = new SendableChooser<>();
    _componentList = new LinkedHashMap<>();
    _hasBeenInit = false;

    for(var mode : AutonomousMode.values())
    {
      _autoChooser.addOption(mode.toString(), mode);
    }
    SmartDashboard.putData("AutonomousSelector", _autoChooser);

    CreateInput();
    CreateComponentInstance();
    for(var component : _componentList.values())
    {
      component.Awake();
    }
    
    serverClient = new RobotServerClient("10.31.17.14");
    serverClient.Connect(() -> 
    {
      serverClient.Register("rio");

      serverClient.Subscribe("imu", x ->
      {
        System.out.println(x.Data);
      });
    });
  }

  @Override
  public void robotPeriodic() 
  {

  }

  @Override
  public void autonomousInit() 
  {
    currentAutonomous = _autoChooser.getSelected();

    Init();
  }

  @Override
  public void autonomousPeriodic() 
  {
    ComponentLoop();
  }

  @Override
  public void teleopInit() 
  {
    if(!_hasBeenInit)
    {
      Init();
    }

    //Reset the init state for the next time the robot is eneabled
    _hasBeenInit = false;
  }

  @Override
  public void teleopPeriodic() 
  {
    ComponentLoop();
  }

  @Override
  public void disabledInit()
  {
    Timer.ClearEvents();

    for(var component : _componentList.values())
    {
      component.Disabled();
    }
  }

  @Override
  public void disabledPeriodic() 
  {
    
  }

  @Override
  public void testPeriodic() 
  {

  }

  public void CreateComponentInstance()
  {
    //Swerve
    var wheelsData = new WheelData[] 
    {
      new WheelData(new MotorController(MotorControllerType.TalonFX, 22, true), new MotorController(MotorControllerType.SparkMax, 16, true), new Pair<>(0, 0), 1, new Vector2d(-0.62320, 0.78206), 0.17640258),
      new WheelData(new MotorController(MotorControllerType.TalonFX, 23, true), new MotorController(MotorControllerType.SparkMax, 15, true), new Pair<>(0, 0), 2, new Vector2d(0.62320, 0.78206), 5.1831684 - Math.PI),
      new WheelData(new MotorController(MotorControllerType.TalonFX, 21, true), new MotorController(MotorControllerType.SparkMax, 17, true), new Pair<>(0, 0), 3, new Vector2d(0.62320, -0.78206), 4.19378 - Math.PI),
      new WheelData(new MotorController(MotorControllerType.TalonFX, 20, true), new MotorController(MotorControllerType.SparkMax, 14, true), new Pair<>(0, 0), 0, new Vector2d(-0.62320, -0.78206), 3.2458076)
    };

    var swerve = new Swerve(wheelsData, new ADIS16448_IMU_Gyro());
    swerve.SetPIDGain(0, 1, 0, 0);
    swerve.SetPIDGain(1, 1, 0, 0);
    swerve.SetPIDGain(2, 1, 0, 0);
    swerve.SetPIDGain(3, 1, 0, 0);

    swerve.SetCurrentMode(DrivingMode.Local);

    //Shooter
    var shooterMotorGroup = new MotorControllerGroup();
    shooterMotorGroup.AddPositiveController(new MotorController(MotorControllerType.SparkMax, 6, true));
    shooterMotorGroup.AddNegativeController(new MotorController(MotorControllerType.SparkMax, 4, true));

    var shooterIntakeMotor = new MotorController(MotorControllerType.TalonSRX, 3, false);
    shooterIntakeMotor.SetInverted(true);

    var shooterAngleMotor = new MotorController(MotorControllerType.TalonSRX, 2, false);
    shooterAngleMotor.SetBrake(true);
    shooterAngleMotor.SetInverted(true);

    var shooterEncoder = new Encoder(0, 1);
    var shooterPID = new SimplePID(0.001, 0, 0, "Shooter");
    var directionPID = new SimplePID(0.03, 0, 0.002, "Direction");

    var shooter = new Shooter(shooterMotorGroup, shooterIntakeMotor, shooterAngleMotor,shooterEncoder, shooterPID, directionPID);
    
    //Conveyor
    var towerConveyorMotor = new MotorController(MotorControllerType.SparkMax, 5, true);

    var towerConveyorEncoder = new Encoder(2, 3);
    var towerConveyorPID = new SimplePID(0.001, 0, 0, "TowerConveyor");

    var conveyor = new ConveyorBelt(towerConveyorMotor, towerConveyorPID, towerConveyorEncoder);

    //Feeder
    var feederAngleMotor = new MotorController(MotorControllerType.SparkMax, 10, true);
    feederAngleMotor.SetInverted(true);

    var feeder = new Feeder(feederAngleMotor);

    AddComponent("Swerve", swerve);
    AddComponent("Shooter", shooter);
    AddComponent("Conveyor", conveyor);
    AddComponent("Feeder", feeder);
  }

  public void CreateInput()
  {
    Input.CreateAxis("Horizontal", 0, 0, false);
    Input.CreateAxis("Vertical", 0, 1, true);
    Input.CreateAxis("Rotation", 0, 2, false);

    Input.SetAxisNegative("Rotation", 0, 3, false);

    Input.SetAxisDeadzone("Horizontal", 0.15);
    Input.SetAxisDeadzone("Vertical", 0.15);
    Input.SetAxisDeadzone("Rotation", 0.15);

    Input.CreateButton("Shooter", 0, 1);
    Input.CreateButton("Align", 0, 2);

    Input.CreateButton("ConveyorBackward", 0, 5);
    Input.CreateButton("ConveyorForward", 0, 6);

    Input.CreateButton("FeederUpAnalog", 1, 6);
    Input.CreateButton("FeederDownAnalog", 1, 5);
  }

  public void Init()
  {
    Timer.Init();
    InputManager.Init();

    for(var component : _componentList.values())
    {
      component.Init();
    }

    _hasBeenInit = true;
  }

  public void ComponentLoop()
  {
    Timer.Evaluate();
    InputManager.DoInputManager();

    for(var component : _componentList.values())
    {
      component.DoComponent();
    }
  }

  public static void AddComponent(String name, Component component)
  {
    instance._componentList.put(name, component);
  }

  @SuppressWarnings("unchecked")
  public static <T> T GetComponent(String name)
  {
    try
    {
      return (T)instance._componentList.get(name);
    }
    catch (Exception ex)
    {
      return null;
    }
  }
}
