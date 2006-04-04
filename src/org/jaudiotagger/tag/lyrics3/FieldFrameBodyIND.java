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
 *
 */
package org.jaudiotagger.tag.lyrics3;

import org.jaudiotagger.tag.InvalidTagException;
import org.jaudiotagger.tag.datatype.BooleanString;
import org.jaudiotagger.tag.InvalidTagException;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;


public class FieldFrameBodyIND extends AbstractLyrics3v2FieldFrameBody
{
    /**
     * Creates a new FieldBodyIND datatype.
     */
    public FieldFrameBodyIND()
    {
        //        this.setObject("Lyrics Present", new Boolean(false));
        //        this.setObject("Timestamp Present", new Boolean(false));
    }

    public FieldFrameBodyIND(FieldFrameBodyIND body)
    {
        super(body);
    }

    /**
     * Creates a new FieldBodyIND datatype.
     *
     * @param lyricsPresent    DOCUMENT ME!
     * @param timeStampPresent DOCUMENT ME!
     */
    public FieldFrameBodyIND(boolean lyricsPresent, boolean timeStampPresent)
    {
        this.setObjectValue("Lyrics Present", new Boolean(lyricsPresent));
        this.setObjectValue("Timestamp Present", new Boolean(timeStampPresent));
    }

    /**
     * Creates a new FieldBodyIND datatype.
     *
     * @param file DOCUMENT ME!
     * @throws InvalidTagException DOCUMENT ME!
     * @throws java.io.IOException DOCUMENT ME!
     */
    public FieldFrameBodyIND(ByteBuffer byteBuffer)
        throws InvalidTagException, java.io.IOException
    {
        this.read(byteBuffer);
    }

    /**
     * DOCUMENT ME!
     *
     * @param author DOCUMENT ME!
     */
    public void setAuthor(String author)
    {
        setObjectValue("Author", author);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getAuthor()
    {
        return (String) getObjectValue("Author");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getIdentifier()
    {
        return "IND";
    }

    /**
     * DOCUMENT ME!
     */
    protected void setupObjectList()
    {
        objectList.add(new BooleanString("Lyrics Present", this));
        objectList.add(new BooleanString("Timestamp Present", this));
    }
}