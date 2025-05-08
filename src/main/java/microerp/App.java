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


package microerp;

import microerp.controller.ConnectionController;
import microerp.controller.ControllerManager;
import microerp.dao.Database;
import microerp.util.Setting;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class App extends Application
{	
    public static void main(String[] args) 
    {
		launch(args);
    	
    	shutdown();
    }

    private static void setup() 
    {
    	try 
    	{
    		//Connect DB
			Database.setupDriver(Config.driver);
			
			ControllerManager.switchController(ConnectionController.getInstance());
		} 
    	catch (Exception e) 
    	{
			System.out.println("Error while setting up");
			e.printStackTrace();
			System.out.println("Setup finished Unsuccessfully");
			System.out.println("Terminating the Application ...");
			Platform.exit();
	        System.exit(0);
		}
    	
    	System.out.println("Setup finished Successfully");
    }
    
    private static void shutdown() 
    {
    	try 
    	{
			//
		} 
    	catch (Exception e) 
    	{
			System.out.println("Error while setting up");
			e.printStackTrace();
			System.out.println("Shutdown finished Unsuccessfully");
		}
    	
		System.out.println("Shutdown finished Successfully");
    }

	@Override
	public void start( Stage primaryStage) throws Exception 
	{
		primaryStage.setMinWidth(Config.windowWidth);
		primaryStage.setMinHeight(Config.windowHeight);
		
		Setting.loadSettings();
		
		
		new ControllerManager(primaryStage, Setting.APP_THEME);		
		
		setup();
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    @Override
		    public void handle(WindowEvent t) 
		    {
		    	Platform.exit();
		    	
		    	shutdown();
		    	
		        System.exit(0);
		    }
		});
	}
}