/**
 *  Amended @author : Paul Taylor
 *  Initial @author : Eric Farng
 *
 *  Version @version:$Id$
 *
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
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Description:
 * Relative Volume Adjustment
 *
 */
package org.jaudiotagger.tag.id3.framebody;

import org.jaudiotagger.audio.mp3.*;
import org.jaudiotagger.tag.datatype.*;
import org.jaudiotagger.tag.InvalidTagException;

import java.io.*;
import java.nio.ByteBuffer;

public class FrameBodyRVA2 extends AbstractID3v2FrameBody implements ID3v24FrameBody
{

    /**
     * Creates a new FrameBodyRVA2 datatype.
     */
    public FrameBodyRVA2()
    {
    }

    public FrameBodyRVA2(FrameBodyRVA2 body)
    {
        super(body);
    }

    /**
     * Convert from V3 to V4 Frame
     */
    public FrameBodyRVA2(FrameBodyRVAD body)
    {
        setObjectValue(DataTypes.OBJ_DATA, body.getObject(DataTypes.OBJ_DATA));
    }


    /**
     * Creates a new FrameBodyRVAD datatype.
     *
     * @param file DOCUMENT ME!
     * @throws IOException         DOCUMENT ME!
     * @throws InvalidTagException DOCUMENT ME!
     */
    public FrameBodyRVA2(ByteBuffer byteBuffer, int frameSize)
        throws IOException, InvalidTagException
    {
        super(byteBuffer, frameSize);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getIdentifier()
    {
        return "RVA2";
    }

    /**
     * Retrieve the next String @todo why is this needed.
     *
     * @return DOCUMENT ME!
     */
    public String getBriefDescription()
    {
        return this.getIdentifier();
    }

    /**
     * Setup the Object List. A byte Array which will be read upto frame size
     * bytes.
     */
    protected void setupObjectList()
    {
        objectList.add(new ByteArraySizeTerminated(DataTypes.OBJ_DATA, this));
    }


}