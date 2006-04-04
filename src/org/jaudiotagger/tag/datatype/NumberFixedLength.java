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
 *  Represents a Number of a fixed number of decimal places.
 */
package org.jaudiotagger.tag.datatype;

import org.jaudiotagger.tag.id3.ID3Tags;
import org.jaudiotagger.tag.AbstractTagFrameBody;
import org.jaudiotagger.audio.mp3.*;


public class NumberFixedLength
    extends AbstractDataType
{
    /**
     * Creates a new ObjectNumberFixedLength datatype.
     *
     * @param identifier DOCUMENT ME!
     * @param size       DOCUMENT ME!
     * @throws IllegalArgumentException DOCUMENT ME!
     */
    public NumberFixedLength(String identifier, AbstractTagFrameBody frameBody, int size)
    {
        super(identifier, frameBody);
        if (size < 0)
        {
            throw new IllegalArgumentException("Length is less than zero: " + size);
        }
        this.size = size;

    }

    public NumberFixedLength(NumberFixedLength copy)
    {
        super(copy);
        this.size = copy.size;
    }


    /**
     * Set Size in Bytes of this Object
     *
     * @param length DOCUMENT ME!
     */
    public void setSize(int size)
    {
        if (size > 0)
        {
            this.size = size;
        }
    }

    /**
     * Return size
     *
     * @return DOCUMENT ME!
     */
    public int getSize()
    {
        return size;
    }

    /**
     * DOCUMENT ME!
     *
     * @param obj DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    public boolean equals(Object obj)
    {
        if ((obj instanceof NumberFixedLength) == false)
        {
            return false;
        }
        NumberFixedLength object = (NumberFixedLength) obj;
        if (this.size != object.size)
        {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * Read the number from file.
     *
     * @param arr    DOCUMENT ME!
     * @param offset DOCUMENT ME!
     * @throws NullPointerException      DOCUMENT ME!
     * @throws IndexOutOfBoundsException DOCUMENT ME!
     */
    public void readByteArray(byte[] arr, int offset)
    {
        if (arr == null)
        {
            throw new NullPointerException("Byte array is null");
        }
        if ((offset < 0) || (offset >= arr.length))
        {
            throw new IndexOutOfBoundsException("Offset to byte array is out of bounds: offset = " + offset +
                ", array.length = " + arr.length);
        }
        long lvalue = 0;
        for (int i = offset; i < (offset + size); i++)
        {
            lvalue <<= 8;
            lvalue += arr[i];
        }
        value = new Long(lvalue);
    }


    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString()
    {
        if (value == null)
        {
            return "";
        }
        else
        {
            return value.toString();
        }
    }

    /**
     * Write data to byte array
     *
     * @return DOCUMENT ME!
     */
    public byte[] writeByteArray()
    {
        byte[] arr;
        arr = new byte[size];
        if (value != null)
        {
            long temp = ID3Tags.getWholeNumber(value);
            for (int i = size - 1; i >= 0; i--)
            {
                arr[i] = (byte) (temp & 0xFF);
                temp >>= 8;
            }
        }
        return arr;
    }
}