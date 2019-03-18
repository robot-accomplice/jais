/*
 * Copyright 2016-2019 Jonathan Machen <jonathan.machen@robotaccomplice.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jais;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author Jonathan Machen
 */
public class Console extends Application {

    private final static Logger LOG = LogManager.getLogger( Console.class );
    private final static String FXML_PATH = "/fxml/console.fxml";

    /**
     *
     * @param stage
     */
    @Override
    public void start( Stage stage ) {
        try {
            stage.setTitle( "AIS Decoder Console " );
            Scene scene = new Scene( FXMLLoader.load( getClass().getResource( FXML_PATH ) ) );
            stage.setScene( scene );
            stage.show();
        } catch( IOException ioe ) {
            LOG.error( "Unanticipated fault when launching the interface: {}", ioe.getMessage() );
            // alert dialog?
            System.exit( 1 );
        }
    }
    
    /**
     * 
     * @param args 
     */
    public static void draw( String [] args ) {
        Console.launch( args );
    }
}
