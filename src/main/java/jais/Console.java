/*
 * 
 */
package jais;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class Console extends Application {

    private final static Logger LOG = LogManager.getLogger( "Console" );
    private final static String FXML_PATH = "/fxml/console.fxml";

    /**
     *
     * @param stage
     */
    @Override
    public void start( Stage stage ) {
        try {
            stage.setTitle( "AIS Decoder Console " );
            Scene scene = new Scene( FXMLLoader.<Parent>load( getClass().getResource( FXML_PATH ) ) );
            stage.setScene( scene );
            stage.show();
        } catch( Exception e ) {
            LOG.fatal( "Unanticipated fault when launching the interface: {}", e.getMessage() );
            // alert dialog?
            System.exit( 1 );
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main( String[] args ) {
        launch( args );
    }
}
