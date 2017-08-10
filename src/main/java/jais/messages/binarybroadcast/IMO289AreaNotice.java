/*
 * Copyright 2016 Jonathan Machen <jon.machen@robotaccomplice.com>.
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

package jais.messages.binarybroadcast;

import jais.AISPacket;
import jais.messages.BinaryBroadcastMessageBase;
import jais.messages.enums.BinaryBroadcastMessageType;

/**
 *
 * @author Jonathan Machen
 */
public class IMO289AreaNotice extends BinaryBroadcastMessageBase {

    /**
     * 
     * @param source
     * @param packets 
     */
    public IMO289AreaNotice( String source, AISPacket... packets ) {
        super( source, packets );
    }

    /**
     * 
     * @return 
     */
    @Override
    public BinaryBroadcastMessageType getSubType() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }
    
}
