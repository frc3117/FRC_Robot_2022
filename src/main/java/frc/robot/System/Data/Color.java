package frc.robot.System.Data;

public class Color 
{
    public Color(double r, double g, double b)
    {
        R = r;
        G = g;
        B = b;
        A = 1;
    }
    public Color(double r, double g, double b, double a)
    {
        R = r;
        G = g;
        B = b;
        A = a;
    }
    public Color(String HexColor) 
    {
        R = Integer.valueOf( HexColor.substring( 1, 3 ), 16) / 255.;
        G = Integer.valueOf( HexColor.substring( 3, 5 ), 16) / 255.;
        B = Integer.valueOf( HexColor.substring( 5, 7 ), 16) / 255.;

        if(HexColor.length() >= 9)
        {
            A = Integer.valueOf( HexColor.substring( 7, 9 ), 16 ) / 255.;
        }
        else
        {
            A = 1;
        }
    }

    public final static Color RED = new Color(1, 0, 0);
    public final static Color GREEN = new Color(0, 1, 0);
    public final static Color BLUE = new Color(0, 0, 1);

    public final static Color ORANGE = new Color(1, 0.5, 0);
    public final static Color YELLOW = new Color(1, 1, 0);
    public final static Color PURPLE = new Color(0.5, 0, 0.5);
    public final static Color MAGENTA = new Color(1, 0, 1);
    public final static Color PINK = new Color(1, 0, 0.5);
    public final static Color CYAN = new Color(0, 1, 1);
    public final static Color LIME = new Color(0.5, 1, 0);
    public final static Color TURQOISE = new Color(0, 0.5, 0.5);

    public final static Color WHITE = new Color(1, 1, 1);
    public final static Color BLACK = new Color(0, 0, 0, 0);

    public double R;
    public double G;
    public double B;
    public double A;
}
