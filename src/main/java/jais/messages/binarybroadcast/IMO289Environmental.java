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

package jais.messages.binarybroadcast;

import jais.AISSentence;
import jais.messages.BinaryBroadcastMessage;
import jais.messages.enums.BinaryBroadcastMessageType;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
public class IMO289Environmental extends BinaryBroadcastMessage {

    /**
     *
     * @param source the name of the source of this message
     * @param sentences the AIS sentences from which this sentence was composed
     */
    public IMO289Environmental(String source, AISSentence... sentences) {
        super(source, sentences);
    }

    /**
     * 
     * @return
     */
    @Override
    public BinaryBroadcastMessageType getSubType() {
        return BinaryBroadcastMessageType.IMO289_ENVIRONMENTAL;
    }
}
