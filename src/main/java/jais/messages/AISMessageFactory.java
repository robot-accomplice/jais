/*
 * Copyright 2016 Jonathan Machen <jon.machen@gmail.com>.
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

package jais.messages;

import jais.AISPacket;
import jais.exceptions.AISException;
import jais.messages.enums.AISMessageType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.BitSet;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class AISMessageFactory {

    private final static Logger LOG = LogManager.getLogger( AISMessageFactory.class );

    /**
     *
     * @param strict
     * @param packets
     * @return
     * @throws jais.exceptions.AISException
     */
    public static AISMessage create( boolean strict, AISPacket... packets ) throws AISException {
        AISMessage message = null;

        String compositeMsg = new String();

        try {
            LOG.debug( "Decoding message from {} packet(s).", packets.length );
            for( AISPacket packet : packets ) {
                if( packet.isValid() ) {
                    compositeMsg += packet.getRawMessage();
                }
            }

            LOG.fatal( "Composite String is: {}", compositeMsg );
            BitSet bits = AISMessageDecoder.stringToBitSet( compositeMsg );

            // we need the message type in order to invoke the reflective constructor
            AISMessageType mType = AISMessageDecoder.decodeMessageType( bits );

            if( mType != null ) {
                LOG.debug( "Creating a new {} instance.", mType.getDescription() );

                Constructor con = mType.getMessageClass().getDeclaredConstructor( AISPacket[].class );
                con.setAccessible( true );

                // reflection won't work properly for singletons if we don't do this
                if( packets.length == 1 ) {
                    message = ( AISMessage ) con.newInstance( ( Object ) packets );
                } else {
                    message = ( AISMessage ) con.newInstance( new Object[]{packets} );
                }

                message.setType( mType );
                while( message.hasSubType() ) {
                    message = message.getSubTypeInstance();
                }

                // decode message
                message.decode();
            } else {
                throw new AISException( "MessageType is null for message String: "
                        + compositeMsg );
            }
        } catch( AISException | NoSuchMethodException | SecurityException |
                InstantiationException | IllegalAccessException |
                IllegalArgumentException | InvocationTargetException t ) {
            // repackage any and all throwables as AISExceptions
            if( strict ) {
                throw new AISException( "Unable to create a new AISMessage: "
                        + t.getMessage(), t );
            } else {
                LOG.warn( "Unable to create a new AISMessage: {}", t.getMessage() );
                LOG.trace( "Decode Failure: {}", t.getMessage(), t );
            }
        }

        return message;
    }

    /**
     *
     * @param packets
     * @return
     * @throws jais.exceptions.AISException
     */
    public static AISMessage create( AISPacket... packets ) throws AISException {
        return create( true, packets );
    }

    /**
     *
     * @param packetStrings
     * @return
     * @throws AISException
     */
    public static AISMessage create( String... packetStrings ) throws AISException {
        return create( true, packetStrings );
    }

    /**
     *
     * @param strict
     * @param packetStrings
     * @return
     * @throws AISException
     */
    public static AISMessage create( boolean strict, String... packetStrings ) throws AISException {
        AISPacket[] packets = new AISPacket[packetStrings.length];

        for( int i = 0; i < packetStrings.length; i++ ) {
            packets[i] = new AISPacket( packetStrings[i] );
        }

        return create( strict, packets );
    }

    /**
     * 
     * @param packets
     * @return 
     * @throws jais.exceptions.AISException 
     */
    public static AISMessage create( List<AISPacket> packets ) throws AISException {
        AISPacket [] packetArray = new AISPacket[ packets.size() ];
        packets.toArray( packetArray );
        return create( packetArray );
    }
}
