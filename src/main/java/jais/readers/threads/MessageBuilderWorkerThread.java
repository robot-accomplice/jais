/*
 * Copyright 2018 jmachen.
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
package jais.readers.threads;

import jais.AISPacket;
import jais.exceptions.AISException;
import jais.messages.AISMessage;
import jais.messages.AISMessageFactory;
import java.util.Optional;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author jmachen
 */
public class MessageBuilderWorkerThread implements Callable<Optional<AISMessage>> {
    
    private final static Logger LOG = LogManager.getLogger( MessageBuilderWorkerThread.class );
    
    private volatile boolean _idle = true;
    private final String _source;
    private AISPacket [] _packets;
    
    /**
     * 
     * @param source
     */
    public MessageBuilderWorkerThread( String source ) {
       _source = source; 
    }
    
    /**
     * 
     * @return 
     */
    public boolean isIdle() {
        return _idle;
    }
    
    /**
     * 
     * @param packets 
     * @return  
     */
    public MessageBuilderWorkerThread init( AISPacket [] packets ) {
        _packets = packets;
        
        return this;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public Optional<AISMessage> call() {
        _idle = false;
        
        try {
            Optional<AISMessage> message =  AISMessageFactory.create( _source, _packets );
            if( message.isPresent() ) {
                System.out.println( "Decoded a message of type " + message.get().getType() );
                return message;
            } else {
                System.err.println( "Failed to decode message!" );
            }
        } catch( AISException ae ) {
            System.err.println( "Unable to decode message: {}" + ae.getMessage() );
        } finally {
            _idle = true;
        }
     
        return Optional.empty();
    }
}
