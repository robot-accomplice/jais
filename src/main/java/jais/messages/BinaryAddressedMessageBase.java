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

package jais.messages;

import jais.AISSentence;
import jais.messages.enums.AISMessageType;
import jais.messages.enums.BinaryAddressedMessageType;
import jais.messages.enums.FieldMap;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Constructor;
import java.util.BitSet;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
@Setter
public abstract class BinaryAddressedMessageBase extends AISMessageBase {

    private int seqno;
    private int destMmsi;
    private boolean retransmit;
    private int dac; // designated area code
    private int fid; // functional id
    private BitSet data;
    private BinaryAddressedMessageType subType;

    /**
     *
     * @param source    String denoting the source of the packet
     * @param sentences AISSentence[] from which the message is composed
     */
    public BinaryAddressedMessageBase(String source, AISSentence... sentences) {
        super(source, AISMessageType.BINARY_ADDRESSED_MESSAGE, sentences);
    }

    /**
     *
     * @param source  String denoting the source of the packet
     * @param subType BinaryAddressedMessageType
     * @param packets AISPacket[] from which the message is composed
     */
    public BinaryAddressedMessageBase(String source, BinaryAddressedMessageType subType, AISSentence... packets) {
        this(source, packets);
        this.subType = subType;
    }

    /**
     * 
     * @return int
     */
    public int getSourceMmsi() {
        return super.getMmsi();
    }

    /**
     *
     * @return boolean
     */
    @Override
    public boolean hasSubType() {
        return true;
    }

    /**
     *
     * @return BinaryAddressedMessageType
     */
    public BinaryAddressedMessageType getSubType() {
        if (subType == null) {
            subType = BinaryAddressedMessageType.fetch(dac, fid, bits.size());
        }

        return subType;
    }

    /**
     *
     * @param subType BinaryAddressedMessageType
     */
    public void setSubType(BinaryAddressedMessageType subType) {
        this.subType = subType;
    }

    /**
     *
     * @return BinaryAddressedMessageBase
     *         subtype
     */
    @Override
    public BinaryAddressedMessageBase getSubTypeInstance() {
        BinaryAddressedMessageBase message = null;

        if (this.subType == null) {
            decode(); // we need the dac and fid
            getSubType();
        }

        if (this.subType != null) {
            try {

                Constructor<? extends BinaryAddressedMessageBase> con = this.subType.getMsgClass()
                        .getDeclaredConstructor(AISSentence[].class);
                con.setAccessible(true);

                if (this.sentences.length == 1) {
                    message = con.newInstance((Object) this.sentences);
                } else {
                    message = con.newInstance(new Object[] { this.sentences });
                }

                message.setType(super.getType());
                message.setSubType(this.subType);
            } catch (ReflectiveOperationException | SecurityException e) {
                // do nothing
            }
        }

        return message;
    }

    /**
     *
     */
    @Override
    public void decode() {
        super.decode();

        for (BinaryAddressedMessageFieldMap field : BinaryAddressedMessageFieldMap.values()) {
            if (bits.size() > field.getEndBit()) {
                switch (field) {
                    case SEQUENCE_NUMBER -> seqno = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case DESTINATION_MMSI -> destMmsi = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case RETRANSMIT -> retransmit = bits.get(field.getStartBit());
                    case DAC -> dac = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case FID -> fid = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case DATA, SPARE -> {}
                }
            }
        }
    }

    /**
     *
     */
    @Getter
    private enum BinaryAddressedMessageFieldMap implements FieldMap {

        SEQUENCE_NUMBER(38, 39),
        DESTINATION_MMSI(40, 69),
        RETRANSMIT(70, 70),
        SPARE(71, 71),
        DAC(72, 81), // designated area code
        FID(82, 87), // Functional ID
        DATA(88, -1) // -1 means from startBit to end of bitArray
        ;

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit the first bit of the target field
         * @param endBit the last bit of the target field
         */
        BinaryAddressedMessageFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }

        @Override
        public int getStartBit() {
            return startBit;
        }

        @Override
        public int getEndBit() {
            return endBit;
        }
    }
}
