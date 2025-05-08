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

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.sql.SQLException;

import microerp.dao.Database;
import microerp.util.Setting;

public class ConnectionController extends Controller 
{
	protected static Controller instance = null;
	
    @FXML private TextField dbAddressField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button connectButton;
    @FXML private Label errorLabel;

    private final String CSSPATH = "/styles/Connection.css", FXMLPATH = "/fxml/Connection.fxml";
    
    public static Controller getInstance() 
	{
		if(instance == null)
			instance = new ConnectionController();
		
		return instance;
	}

    @Override
    public String getName() 
	{
		return "Connection";
	}
    
    @Override
    public Parent loadRoot() throws IOException 
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPATH));
        loader.setController(this);
        return loader.load();
    }
    
    @FXML
    private void initialize() 
    {
    	errorLabel.setVisible(false);
    	
        dbAddressField.setOnKeyPressed(event -> {
        	if(event.getCode() == KeyCode.ENTER)
        	{
        		usernameField.requestFocus();
        	}
        	errorLabel.setVisible(false);
        });

        usernameField.setOnKeyPressed(event -> {
        	if(event.getCode() == KeyCode.ENTER)
        	{
        		passwordField.requestFocus();
        	}
        	errorLabel.setVisible(false);
        });

        passwordField.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER ) 
            {
            	connectButton.fire();
            }
            errorLabel.setVisible(false);
        });
    
        if(Database.checkConnection()) 
    	{
    		Platform.runLater(() -> 
    		{
    			try 
    			{
    				ControllerManager.switchController(LoginController.getInstance());
    			} 
    			catch (Exception e) 
    			{	
    				e.printStackTrace();
    			}
    		});
    	}
    }

    @FXML
    private void onConnect() 
    {
        String dbAddress = dbAddressField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        try 
        {
        	Database.connect(dbAddress, username, password);
        	errorLabel.setVisible(false);
        	
        	ControllerManager.switchController(LoginController.getInstance());
        	
        	Setting.DB_ADDRESS = dbAddress;
        	Setting.DB_USERNAME = username;
        	Setting.DB_PASSWORD = password;
        	
        	Setting.saveSettings();
        }
        catch(SQLException e) 
        {
        	System.err.println("Connection failed!");
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Message: " + e.getMessage());
            
            errorLabel.setText(e.getMessage());
            errorLabel.setVisible(true);
        }
        catch(Exception e) 
        {
        	e.printStackTrace();
        }
    }

    @Override
    public void onEntered() {
        ControllerManager.addCSS(CSSPATH);
    }

    @Override
    public void onExited() {
        ControllerManager.removeCSS(CSSPATH);
    }
    
    
}
