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

package jais.messages.binaryaddressed;

import jais.AISSentence;
import jais.messages.AISMessageDecoder;
import jais.messages.BinaryAddressedMessageBase;
import jais.messages.enums.AreaNoticeType;
import jais.messages.enums.BinaryAddressedMessageType;
import jais.messages.enums.FieldMap;
import jais.messages.enums.SubareaType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
public class IMO289AreaNotice extends BinaryAddressedMessageBase {

    private static final int SUBAREA_SIZE_BITS = 87;

    private int linkageId;
    private AreaNoticeType noticeType;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int durationMinutes;
    private final List<AreaSubarea> subareas = new ArrayList<>();

    /**
     *
     * @param source the name of the source of this message
     * @param sentences the AIS Sentences from which this message was composed
     */
    public IMO289AreaNotice(String source, AISSentence... sentences) {
        super(source, BinaryAddressedMessageType.AREA_NOTICE, sentences);
    }

    /**
     */
    @Override
    public final void decode() {
        super.decode();

        linkageId = AISMessageDecoder.decodeUnsignedInt(bits,
                IMO289AreaNoticeFieldMap.LINKAGE_ID.getStartBit(),
                IMO289AreaNoticeFieldMap.LINKAGE_ID.getEndBit());
        noticeType = AreaNoticeType.getForCode(AISMessageDecoder.decodeUnsignedInt(bits,
                IMO289AreaNoticeFieldMap.NOTICE_TYPE.getStartBit(),
                IMO289AreaNoticeFieldMap.NOTICE_TYPE.getEndBit()));
        month = AISMessageDecoder.decodeUnsignedInt(bits,
                IMO289AreaNoticeFieldMap.MONTH.getStartBit(),
                IMO289AreaNoticeFieldMap.MONTH.getEndBit());
        day = AISMessageDecoder.decodeUnsignedInt(bits,
                IMO289AreaNoticeFieldMap.DAY.getStartBit(),
                IMO289AreaNoticeFieldMap.DAY.getEndBit());
        hour = AISMessageDecoder.decodeUnsignedInt(bits,
                IMO289AreaNoticeFieldMap.HOUR.getStartBit(),
                IMO289AreaNoticeFieldMap.HOUR.getEndBit());
        minute = AISMessageDecoder.decodeUnsignedInt(bits,
                IMO289AreaNoticeFieldMap.MINUTE.getStartBit(),
                IMO289AreaNoticeFieldMap.MINUTE.getEndBit());
        durationMinutes = AISMessageDecoder.decodeUnsignedInt(bits,
                IMO289AreaNoticeFieldMap.DURATION_MINUTES.getStartBit(),
                IMO289AreaNoticeFieldMap.DURATION_MINUTES.getEndBit());

        subareas.clear();
        int subareaCount = maxSubareasAvailable();
        for (int i = 0; i < subareaCount; i++) {
            int startBit = IMO289AreaNoticeFieldMap.SUBAREAS.getStartBit() + (i * SUBAREA_SIZE_BITS);
            int typeCode = AISMessageDecoder.decodeUnsignedInt(bits, startBit, startBit + 2);
            SubareaType subareaType = SubareaType.getForCode(typeCode);
            String text = (subareaType == SubareaType.ASSOCIATED_TEXT)
                    ? AISMessageDecoder.decodeString(bits, startBit + 3, startBit + 86)
                    : null;

            subareas.add(new AreaSubarea(
                    subareaType,
                    bits.get(startBit, startBit + SUBAREA_SIZE_BITS),
                    text));
        }
    }

    /**
     *
     * @return immutable list of subareas contained in the notice
     */
    public List<AreaSubarea> getSubareas() {
        return Collections.unmodifiableList(subareas);
    }

    private int maxSubareasAvailable() {
        int availableBits = bits.length() - IMO289AreaNoticeFieldMap.SUBAREAS.getStartBit();
        if (availableBits <= 0) {
            return 0;
        }
        return availableBits / SUBAREA_SIZE_BITS;
    }

    /**
     *
     */
    @Getter
    public static class AreaSubarea {
        private final SubareaType type;
        private final java.util.BitSet rawBits;
        private final String associatedText;

        AreaSubarea(SubareaType type, java.util.BitSet rawBits, String associatedText) {
            this.type = type;
            this.rawBits = rawBits;
            this.associatedText = associatedText;
        }
    }

    /**
     *
     */
    @Getter
    private enum IMO289AreaNoticeFieldMap implements FieldMap {

        LINKAGE_ID(88, 97),
        NOTICE_TYPE(98, 104),
        MONTH(105, 108),
        DAY(109, 113),
        HOUR(114, 118),
        MINUTE(119, 124),
        DURATION_MINUTES(125, 142),
        SUBAREAS(143, -1);

        private final int startBit;
        private final int endBit;

        IMO289AreaNoticeFieldMap(int startBit, int endBit) {
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
