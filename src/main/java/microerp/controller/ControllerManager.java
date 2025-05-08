/*
 * The MIT License (MIT)
 *
 * Copyright © 2025 Ayush Samantaray (@Ayush4441)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished
 * to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package microerp.controller;

import java.io.IOException;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import atlantafx.base.theme.Dracula;
import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public final class ControllerManager 
{
    public enum Theme 
    {
    	NONE,
        CUPERTINO_DARK,
        CUPERTINO_LIGHT,
        DRACULA,
        NORD_DARK,
        NORD_LIGHT,
        PRIMER_DARK,
        PRIMER_LIGHT
    }
    
    private static ControllerManager instance = null;
    
    Controller activeController = null;
    private Scene scene = null;
    
    public ControllerManager(Stage stage) throws Exception, IOException
    {
        this(stage, null, Theme.PRIMER_LIGHT);
    }
    
    public ControllerManager(Stage stage, Controller controller) throws Exception, IOException 
    {
        this(stage, controller, Theme.PRIMER_LIGHT);
    }
    
    public ControllerManager(Stage stage, Theme theme) throws Exception, IOException 
    {
        this(stage, null, theme);
    }
    
    public ControllerManager(Stage stage, Controller controller, Theme theme) throws Exception, IOException
    {
    	if (instance == null)
            instance = this;
        else
            throw new Exception("There are more than one instance of Controller Manager");
    
        scene = new Scene(new GridPane());
        
        activeController = controller;
        
        if(activeController != null)
        	switchController(activeController);
        
        stage.setScene(scene);
        stage.show();
        
        setTheme(theme);
    }
    
    public static void switchController(Controller controller) throws IllegalStateException
    {
    	if (instance == null)
            throw new IllegalStateException("ControllerManager instance is not initialized!");
    	
        if(instance.activeController != null)
            instance.activeController.onExited();

        instance.activeController = controller;
        instance.activeController.onEntered();
        
        boolean success = true;
        
        try 
        {
        	Parent root = controller.loadRoot();
        	if (root != null)
        		instance.scene.setRoot(root);
        }
        catch(IOException e)
        {
        	success = false;
            e.printStackTrace();
        }
        finally 
        {
        	if(success)
        		System.out.println("Switching to " + controller.getName());
        	else
        		System.out.println("Can't Switching to " + controller.getName());
        }
    }
    
    public static void setTheme(Theme theme) 
    {
        switch (theme) 
        {
            case CUPERTINO_DARK:
                Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
                break;
            case CUPERTINO_LIGHT:
                Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
                break;
            case DRACULA:
                Application.setUserAgentStylesheet(new Dracula().getUserAgentStylesheet());
                break;
            case NORD_DARK:
                Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());
                break;
            case NORD_LIGHT:
                Application.setUserAgentStylesheet(new NordLight().getUserAgentStylesheet());
                break;
            case PRIMER_DARK:
                Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
                break;
            case PRIMER_LIGHT:
                Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
                break;
            default:
                break;
        }
    }
    
    public static void addCSS(String path) 
    {
    	if(instance == null)
    		return;
    	
    	String css = ControllerManager.class.getResource(path).toExternalForm();
        instance.scene.getStylesheets().add(css);
    }

    public static void removeCSS(String path){
        if(instance == null)
    		return;
    	
    	String css = ControllerManager.class.getResource(path).toExternalForm();
        instance.scene.getStylesheets().remove(css);
    }
}
