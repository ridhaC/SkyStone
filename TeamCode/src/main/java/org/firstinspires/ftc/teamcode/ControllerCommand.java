package org.firstinspires.ftc.teamcode;

import java.util.ArrayList;
import java.util.ResourceBundle;

public abstract class ControllerCommand {
    boolean buttonDown = false;
    actionable type;
    public static enum actionable {
            onPress, whileDown, onRelease
    }
    public static ArrayList<ControllerCommand> allControllerCommands = new ArrayList<>();
    public ControllerCommand(actionable type) {
        this.type = type;
    }

    public boolean operate(boolean input) {
        switch (type) {
            case onPress:
                if (input) {
                    if (!buttonDown) {
                        defineOperation();
                    }
                    buttonDown = true;
                }
                if (!input) {
                    buttonDown = false;
                }
                break;

            case whileDown:
                if (input) {
                    defineOperation();
                    buttonDown = true;
                }
                if (!input) {
                    buttonDown = false;
                }
                break;

            case onRelease:
                if(input)   {
                    buttonDown = true;
                }
                if(!input)  {
                    if(buttonDown)
                        defineOperation();
                    buttonDown = false;
                }
                break;

        }
        return buttonDown;
    }
    public abstract void defineOperation();
}