/*
 *  MusicTag Copyright (C)2003,2004
 *
 *  This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
 *  General Public  License as published by the Free Software Foundation; either version 2.1 of the License,
 *  or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 *  the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this library; if not,
 *  you can get a copy from http://www.opensource.org/licenses/lgpl-license.php or write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package ealvatag.tag.id3.framebody;

import java.nio.ByteBuffer;

import ealvatag.tag.InvalidTagException;
import ealvatag.tag.datatype.DataTypes;
import ealvatag.tag.datatype.NumberHashMap;
import ealvatag.tag.datatype.NumberVariableLength;
import ealvatag.tag.id3.ID3v24Frames;
import ealvatag.tag.id3.valuepair.EventTimingTimestampTypes;
import okio.Buffer;

/**
 * Position synchronisation frame.
 * <p>
 * <p>
 * This frame delivers information to the listener of how far into the
 * audio stream he picked up; in effect, it states the time offset of
 * the first frame in the stream. The frame layout is:
 * <p>
 * Head for 'Position synchronisation', ID:
 * Time stamp format $xx
 * Position          $xx (xx ...)
 * <p>
 * Where time stamp format is:
 * <p>
 * $01 Absolute time, 32 bit sized, using MPEG frames as unit
 * $02 Absolute time, 32 bit sized, using milliseconds as unit
 * <p>
 * and position is where in the audio the listener starts to receive,
 * i.e. the beginning of the next frame. If this frame is used in the
 * beginning of a file the value is always 0. There may only be one
 * "POSS" frame in each tag.
 * <p>
 * <p>
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 *
 * @author : Paul Taylor
 * @author : Eric Farng
 * @version $Id$
 */
public class FrameBodyPOSS extends AbstractID3v2FrameBody implements ID3v24FrameBody, ID3v23FrameBody {
    /**
     * Creates a new FrameBodyPOSS datatype.
     */
    public FrameBodyPOSS() {
        //        this.setObject(ObjectNumberHashMap.OBJ_TIME_STAMP_FORMAT, new Byte((byte) 0));
        //        this.setObject("Position", new Long(0));
    }

    public FrameBodyPOSS(FrameBodyPOSS body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyPOSS datatype.
     *
     * @param timeStampFormat
     * @param position
     */
    public FrameBodyPOSS(byte timeStampFormat, long position) {
        setObjectValue(DataTypes.OBJ_TIME_STAMP_FORMAT, timeStampFormat);
        setObjectValue(DataTypes.OBJ_POSITION, position);
    }

    /**
     * Creates a new FrameBodyPOSS datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException if unable to create framebody from buffer
     */
    public FrameBodyPOSS(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException {
        super(byteBuffer, frameSize);
    }

    public FrameBodyPOSS(Buffer byteBuffer, int frameSize) throws InvalidTagException {
        super(byteBuffer, frameSize);
    }

    /**
     * The ID3v2 frame identifier
     *
     * @return the ID3v2 frame identifier  for this frame type
     */
    public String getIdentifier() {
        return ID3v24Frames.FRAME_ID_POSITION_SYNC;
    }


    /**
     *
     */
    protected void setupObjectList() {
        addDataType(new NumberHashMap(DataTypes.OBJ_TIME_STAMP_FORMAT, this, EventTimingTimestampTypes.TIMESTAMP_KEY_FIELD_SIZE));
        addDataType(new NumberVariableLength(DataTypes.OBJ_POSITION, this, 1));
    }
}
