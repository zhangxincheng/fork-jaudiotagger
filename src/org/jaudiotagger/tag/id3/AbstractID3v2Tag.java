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
 * ID3v2 Description: This is the abstract base class for all
 * ID3v2 tags
 *
 */
package org.jaudiotagger.tag.id3;

import java.io.*;
import java.io.RandomAccessFile;

import org.jaudiotagger.audio.mp3.*;

import org.jaudiotagger.tag.id3.framebody.FrameBodyTDRC;
import org.jaudiotagger.tag.id3.framebody.FrameBodyUnsupported;


import java.util.*;
import java.nio.*;
import java.nio.channels.*;

public abstract class AbstractID3v2Tag
    extends AbstractID3Tag
{
    protected static final String TYPE_HEADER = "header";
    protected static final String TYPE_BODY = "body";

    //Tag ID as held in file
    protected static final byte[] TAG_ID =
        {'I', 'D', '3'};

    //The tag header is the same for ID3v2 versions
    protected static int TAG_HEADER_LENGTH = 10;
    protected static int FIELD_TAGID_LENGTH = 3;
    protected static int FIELD_TAG_MAJOR_VERSION_LENGTH = 1;
    protected static int FIELD_TAG_MINOR_VERSION_LENGTH = 1;
    protected static int FIELD_TAG_FLAG_LENGTH = 1;
    protected static int FIELD_TAG_SIZE_LENGTH = 4;

    protected static final int FIELD_TAGID_POS = 0;
    protected static final int FIELD_TAG_MAJOR_VERSION_POS = 3;
    protected static final int FIELD_TAG_MINOR_VERSION_POS = 4;
    protected static final int FIELD_TAG_FLAG_POS = 5;
    protected static final int FIELD_TAG_SIZE_POS = 6;

    protected static final int TAG_SIZE_INCREMENT = 100;

    /**
     * Map of all frames for this tag
     */
    public HashMap frameMap = null;

    /**
     * Holds the ids of invalid duplicate frames
     */
    protected static final String TYPE_DUPLICATEFRAMEID = "duplicateFrameId";
    protected String duplicateFrameId = "";

    /**
     * Holds byte count of invalid duplicate frames
     */
    protected static final String TYPE_DUPLICATEBYTES = "duplicateBytes";
    protected int duplicateBytes = 0;

    /**
     * Holds byte count of empty frames
     */
    protected static final String TYPE_EMPTYFRAMEBYTES = "emptyFrameBytes";
    protected int emptyFrameBytes = 0;

    /**
     * Holds the size of the tag as reported by the tag header
     */
    protected static final String TYPE_FILEREADSIZE = "fileReadSize";
    protected int fileReadSize = 0;

    /**
     * Holds byte count of invalid frames
     */
    protected static final String TYPE_INVALIDFRAMEBYTES = "invalidFrameBytes";
    protected int invalidFrameBytes = 0;

    /**
     * Empty Constructor
     */
    public AbstractID3v2Tag()
    {
    }

    /**
     * This constructor is used when a tag is created as a duplicate of another
     * tag of the same type and version.
     *
     */
    protected AbstractID3v2Tag(AbstractID3v2Tag copyObject)
    {
    }

    /**
     * Copy primitives apply to all tags
     */
    protected void copyPrimitives(AbstractID3v2Tag copyObject)
    {
        logger.info("Copying Primitives");
        //Primitives type variables common to all IDv2 Tags
        this.duplicateFrameId = new String(copyObject.duplicateFrameId);
        this.duplicateBytes = copyObject.duplicateBytes;
        this.emptyFrameBytes = copyObject.emptyFrameBytes;
        this.fileReadSize = copyObject.fileReadSize;
        this.invalidFrameBytes = copyObject.invalidFrameBytes;
    }

    /**
     * Copy frames from another tag, needs implemanting by subclasses
     */
    protected  abstract void copyFrames(AbstractID3v2Tag copyObject);




    /**
     * Returns the number of bytes which come from duplicate frames
     *
     * @return the number of bytes which come from duplicate frames
     */
    public int getDuplicateBytes()
    {
        return duplicateBytes;
    }

    /**
     * Return the string which holds the ids of all
     * duplicate frames.
     *
     * @return  the string which holds the ids of all duplicate frames.
     */
    public String getDuplicateFrameId()
    {
        return duplicateFrameId;
    }

    /**
     * Returns the number of bytes which come from empty frames
     *
     * @return the number of bytes which come from empty frames
     */
    public int getEmptyFrameBytes()
    {
        return emptyFrameBytes;
    }

    /**
     * Return  byte count of invalid frames
     *
     * @return byte count of invalid frames
     */
    public int getInvalidFrameBytes()
    {
        return invalidFrameBytes;
    }

    /**
     * Returns the tag size as reported by the tag header
     *
     * @return the tag size as reported by the tag header
     */
    public int getFileReadBytes()
    {
        return fileReadSize;
    }

    /**
     * Return whether tag has frame with this identifier
     *
     * Warning the match is only done against the identifier so if a tag contains a frame with an unsuported body
     * but happens to have an identifier that is valid for another version of the tag it will return true
     *
     * @param identifier frameId to lookup
     * @return true if tag has frame with this identifier
     */
    public boolean hasFrame(String identifier)
    {
        return frameMap.containsKey(identifier);
    }


    /**
     * Return whether tag has frame with this identifier and a related body. This is required to protect
     * against circumstances whereby a tag contains a frame with an unsuported body
     * but happens to have an identifier that is valid for another version of the tag which it has been converted to
     *
     * e.g TDRC is an invalid frame in a v23 tag but if somehow a v23tag has been created by another application
     * with a TDRC frame we construct an UnsupportedFrameBody to hold it, then this library constructs a
     * v24 tag, it will contain a frame with id TDRC but it will not have the expected frame body it is not really a
     * TDRC frame.
     *
     * @param identifier frameId to lookup
     * @return true if tag has frame with this identifier
     */
    public boolean hasFrameAndBody(String identifier)
    {
        if(hasFrame(identifier))
        {
            Object o = getFrame(identifier);
            if(o instanceof AbstractID3v2Frame)
            {
                if(((AbstractID3v2Frame)o).getBody() instanceof FrameBodyUnsupported)
                {
                    return false;
                }
                return true;
            }
            return true;
        }
        return false;
    }

    /**
     * Return whether tag has frame starting
     * with this identifier
     *
     * Warning the match is only done against the identifier so if a tag contains a frame with an unsuported body
     * but happens to have an identifier that is valid for another version of the tag it will return true

     * @param identifier start of frameId to lookup
     * @return tag has frame starting with this identifier
     */
    public boolean hasFrameOfType(String identifier)
    {
        Iterator iterator = frameMap.keySet().iterator();
        String key;
        boolean found = false;
        while (iterator.hasNext() && !found)
        {
            key = (String) iterator.next();
            if (key.startsWith(identifier))
            {
                found = true;
            }
        }
        return found;
    }


    /**
     * For single frames return the frame in this tag with given identifier if it exists, if multiple frames
     * exist with the same identifier it will return a list containing all the frames with this identifier
     *
     * Warning the match is only done against the identifier so if a tag contains a frame with an unsuported body
     * but happens to have an identifier that is valid for another version of the tag it will be returned.
     *
     * @param identifier is a ID3Frame identifier
     * @return matching frame, or list of matching frames
     */
    public Object getFrame(String identifier)
    {
        return frameMap.get(identifier);
    }

     /**
     * Add a frame to this tag
     *
     * @param frame the frame to add
     *
     * Warning if frame(s) already exists for this identifier thay are overwritten
      *
     * @TODO needs to ensure do not add an invalid frame for this tag
     */
    public void setFrame(AbstractID3v2Frame frame)
    {
        frameMap.put(frame.getIdentifier(), frame);
    }

    /**
     * Used for setting multiple frames for a single frame Identifier
     *
     * Warning if frame(s) already exists for this identifier thay are overwritten
     *
     * @TODO needs to ensure do not add an invalid frame for this tag
     */
    public void setFrame(String identifier, ArrayList multiFrame)
    {
        frameMap.put(identifier, multiFrame);
    }

    /**
     * Return the number of frames in this tag of a particular type, multiple frames
     * of the same time will only be counted once
     *
     * @return a count of different frames
     */
    public int getFrameCount()
    {
        if (frameMap == null)
        {
            return 0;
        }
        else
        {
            return frameMap.size();
        }
    }

    /**
     * Return all frames which start with the identifier, this
     * can be more than one which is useful if trying to retrieve
     * similar frames e.g TIT1,TIT2,TIT3 ... and dont know exaclty
     * which ones there are.
     *
     * Warning the match is only done against the identifier so if a tag contains a frame with an unsuported body
     * but happens to have an identifier that is valid for another version of the tag it will be returned.
     *
     * @param identifier DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    public Iterator getFrameOfType(String identifier)
    {
        Iterator iterator = frameMap.keySet().iterator();
        HashSet result = new HashSet();
        String key;
        while (iterator.hasNext())
        {
            key = (String) iterator.next();
            if (key.startsWith(identifier))
            {
                result.add(frameMap.get(key));
            }
        }
        return result.iterator();
    }




    /**
     * Delete Tag
     *
     * @param file to delete the tag from
     * @throws IOException if problem accessing the file
     *
     * @todo should clear all data and preferably recover lost space.
     */
    public void delete(RandomAccessFile file)
        throws IOException
    {
        // this works by just erasing the "TAG" tag at the beginning
        // of the file
        byte[] buffer = new byte[FIELD_TAGID_LENGTH];
        //Read into Byte Buffer
        final FileChannel fc = file.getChannel();
        fc.position();
        ByteBuffer  byteBuffer = ByteBuffer.allocate(FIELD_TAGID_LENGTH);
        fc.read(byteBuffer,0);
        byteBuffer.flip();
        if(seek(byteBuffer))
        {
            file.seek(0);
            file.write(buffer);
        }
    }

    /**
     * Is this tag equivalent to another
     *
     * @param obj to test for equivalence
     * @return true if they are equivalent
     */
    public boolean equals(Object obj)
    {
        if ((obj instanceof AbstractID3v2Tag) == false)
        {
            return false;
        }
        AbstractID3v2Tag object = (AbstractID3v2Tag) obj;
        if (this.frameMap.equals(object.frameMap) == false)
        {
            return false;
        }
        return super.equals(obj);
    }


    /**
     * Return the frames in the order they were added
     *
     * @return and iterator of the frmaes/list of multi value frames
     */
    public Iterator iterator()
    {
        return frameMap.values().iterator();
    }

    /**
     * Remove frame(s) with this identifier from tag
     *
     * @param identifier frameId to look for
     */
    public void removeFrame(String identifier)
    {
        frameMap.remove(identifier);
    }

    /**
     * Remove all frame(s) which have an unsupported body, in other words
     * remove all frames that are not part of the standard frameset for
     * this tag
     */
    public void removeUnsupportedFrames()
    {
        for(Iterator i =iterator();i.hasNext();)
        {
            Object o = i.next();
            if(o instanceof AbstractID3v2Frame)
            {
                if(((AbstractID3v2Frame)o).getBody() instanceof FrameBodyUnsupported)
                {
                    logger.finest("Removing frame"+((AbstractID3v2Frame)o).getIdentifier());
                    i.remove();
                }
            }
        }
    }

    /**
     * Remove any frames starting with this
     * identifier from tag
     *
     * @param identifier start of frameId to look for
     */
    public void removeFrameOfType(String identifier)
    {
        Iterator iterator = this.getFrameOfType(identifier);
        while (iterator.hasNext())
        {
            AbstractID3v2Frame frame = (AbstractID3v2Frame) iterator.next();
            frameMap.remove(frame.getIdentifier());
        }
    }


    /**
     *
     * @param file
     * @param audioStartByte
     * @throws IOException
     *
     * @TODO should be abstract
     *
     */
    public void write(File file, long audioStartByte)
        throws IOException
    {
    }

    /**
     *
     * @param file
     * @throws IOException
     *
     * @TODO should be abstract
     *
     */
    public void write(RandomAccessFile file)
        throws IOException
    {
    }

    /**
     * Read Tag Size from byteArray in format specified in spec and convert to int.
     *
     * @param buffer converted into ID3 Format
     * @return size as int
     */
    protected int byteArrayToSize(byte[] buffer)
    {
        /**
         * the decided not to use the top bit of the 4 bytes so we need to
         * convert the size back and forth
         */
        return (int) (buffer[0] << 21) + (buffer[1] << 14) + (buffer[2] << 7) + (buffer[3]);
    }

    /**
     * Write Tag Size to Byte array to format as required in Tag Header.
     *
     * @param size to convert into ID3 Format
     * @return size in ID3 Format
     */
    protected byte[] sizeToByteArray(int size)
    {
        byte[] buffer = new byte[FIELD_TAG_SIZE_LENGTH];
        buffer[0] = (byte) ((size & 0x0FE00000) >> 21);
        buffer[1] = (byte) ((size & 0x001FC000) >> 14);
        buffer[2] = (byte) ((size & 0x00003F80) >> 7);
        buffer[3] = (byte) (size & 0x0000007F);
        return buffer;
    }

    /**
     * Does a ID3v2_40 tag exist in this file.
     *
     * @param byteBuffer to search through
     * @return true if tag exists.
     * @throws IOException DOCUMENT ME!
     */
    public boolean seek(ByteBuffer byteBuffer)
        throws IOException
    {
        byteBuffer.rewind();
        logger.info("ByteBuffer pos:"
            +byteBuffer.position()
            +":limit"+byteBuffer.limit()
            +":cap"+byteBuffer.capacity());


        byte[] tagIdentifier = new byte[FIELD_TAGID_LENGTH];
        byteBuffer.get(tagIdentifier, 0, FIELD_TAGID_LENGTH);
        if (!(Arrays.equals(tagIdentifier,TAG_ID)))
        {
            return false;
        }
        //Major Version
        if (byteBuffer.get() != this.getMajorVersion())
        {
            return false;
        }
        //Minor Version
        if (byteBuffer.get() != this.getRevision())
        {
            return false;
        }
        return true;
    }

    /**
     * This method determines the total tag size taking into account
     * where the audio file starts, the size of the tagging data and
     * user options for defining how tags should shrink or grow.
     */
    protected int calculateTagSize(int tagSize, int audioStart)
    {
        /** We can fit in the tag so no adjustments required */
        if (tagSize <= audioStart)
        {
            return audioStart;
        }
        /** There is not enough room as we need to move the audio file we might
         *  as well increase it more than neccessary for future changes
         */
        return tagSize + TAG_SIZE_INCREMENT;
    }

    /**
     * Adjust the length of the ID3v2 padding at the beginning of the MP3 file
     * this datatype refers to. A new file will be created with enough size to fit
     * the <code>ID3v2</code> tag. The old file will be deleted, and the new file renamed.
     *
     * @param paddingSize Initial padding size. This size is doubled until the
     *                    ID3v2 tag will fit. A paddingSize of zero will create a padding
     *                    length exactly equal to the tag size.
     * @param file        The file to adjust the padding length of

     * @throws FileNotFoundException if the file exists but is a directory
     *                               rather than a regular file or cannot be opened for any other
     *                               reason
     * @throws IOException           on any I/O error
     */
    public void adjustPadding(File file, int paddingSize, long audioStart)
        throws FileNotFoundException, IOException
    {
        logger.finer("Need to move audio file to accomodate tag");
        FileChannel fcIn = null;
        FileChannel fcOut = null;
        /** Create buffer holds the neccessary padding */
        ByteBuffer paddingBuffer = ByteBuffer.wrap(new byte[paddingSize]);
        /** Create Temporary File and write channel*/
        File paddedFile = File.createTempFile("temp", ".mp3",
            file.getParentFile());
        fcOut = new FileOutputStream(paddedFile).getChannel();
        //Create read channel from original file
        fcIn = new FileInputStream(file).getChannel();
        //Write padding
        long written = fcOut.write(paddingBuffer);
        //Write rest of file starting from audio
        logger.finer("Copying:" + (file.length() - audioStart) + "bytes");
        long written2 = fcIn.transferTo(audioStart, file.length() - audioStart, fcOut);
        logger.finer("Written padding:" + written + " Data:" + written2);
        //Store original modification time
        long lastModified = file.lastModified();
        //Close Channels
        fcIn.close();
        fcOut.close();
        //Delete original File
        file.delete();
        //Rename temporary file and set modification time to original time.
        paddedFile.renameTo(file);
        paddedFile.setLastModified(lastModified);

    }

    /**
     * Add frame to HashMap used when converting between tag versions, take into account
     * occurences when two frame may both map to a single frame when converting between
     * versions
     */
    protected void copyFrameIntoMap(String id, AbstractID3v2Frame newFrame)
    {
        /* The frame already exists this shouldnt normally happen because frames
         * that are allowed to be multiple don't call this method. Frames that
         * arent allowed to be multiple arent added to hashmap in first place when
         * originally added.
         * However converting some frames from tag of one version to another may
         * mean that two different frames both get converted to one frame, this
         * particulary applies to DateTime fields which were originally two fields
         * in v2.3 but are one field in v2.4.
         */
        if (frameMap.containsKey(newFrame.getIdentifier()))
        {
            //Retrieve the frame with the same id we have already loaded into the map
            AbstractID3v2Frame firstFrame = (AbstractID3v2Frame) frameMap.get(newFrame.getIdentifier());

            //Two different frames both converted to TDRCFrames
            if(newFrame.getBody() instanceof FrameBodyTDRC)
            {
                logger.finest("Modifying frame in map:"+newFrame.getIdentifier());
                FrameBodyTDRC body = (FrameBodyTDRC) firstFrame.getBody();
                FrameBodyTDRC newBody = (FrameBodyTDRC) newFrame.getBody();
                //Just add the data to the frame
                if (newBody.getOriginalID().equals(ID3v23Frames.FRAME_ID_V3_TYER))
                {
                    body.setYear(newBody.getText());
                }
                else if (newBody.getOriginalID().equals(ID3v23Frames.FRAME_ID_V3_TDAT))
                {
                    body.setDate(newBody.getText());
                }
                else if (newBody.getOriginalID().equals(ID3v23Frames.FRAME_ID_V3_TIME))
                {
                    body.setTime(newBody.getText());
                }
                else if (newBody.getOriginalID().equals(ID3v23Frames.FRAME_ID_V3_TRDA))
                {
                    body.setReco(newBody.getText());
                }
            }
            else
            {
                logger.warning("Found duplicate frame in invalid situation:" + newFrame.getIdentifier());
            }
        }
        else
        //Just add frame to map
        {
            logger.finest("Adding frame to map:"+newFrame.getIdentifier());
            frameMap.put(newFrame.getIdentifier(), newFrame);
        }
    }

    /**
     * Decides what to with the frame that has just be read from file.
     * If the frame is an allowable duplicate frame and is a duplicate we add all
     * frames into an ArrayList and add the Arraylist to the hashMap. if not allowed
     * to be duplictae we store bytes in the duplicateBytes variable.
     */
    protected void loadFrameIntoMap(String frameId, AbstractID3v2Frame next)
    {
        if (
            (ID3v24Frames.getInstanceOf().isMultipleAllowed(frameId)) ||
            (ID3v23Frames.getInstanceOf().isMultipleAllowed(frameId)) ||
            (ID3v22Frames.getInstanceOf().isMultipleAllowed(frameId))
        )
        {
            //If a frame already exists of this type
            if (frameMap.containsKey(frameId))
            {
                Object o = frameMap.get(frameId);
                if (o instanceof ArrayList)
                {
                    ArrayList multiValues = (ArrayList) o;
                    multiValues.add(next);
                    logger.finer("Adding Multi Frame(1)" + frameId);
                }
                else
                {
                    ArrayList multiValues = new ArrayList();
                    multiValues.add(o);
                    multiValues.add(next);
                    frameMap.put(frameId, multiValues);
                    logger.finer("Adding Multi Frame(2)" + frameId);
                }
            }
            else
            {
                logger.finer("Adding Multi FrameList(3)" + frameId);
                frameMap.put(frameId, next);
            }
        }
        //If duplicate frame just stores it somewhere else
        else if (frameMap.containsKey(frameId))
        {
            logger.warning("Duplicate Frame" + frameId);
            this.duplicateFrameId += (frameId + "; ");
            this.duplicateBytes += ((AbstractID3v2Frame) frameMap.get(frameId)).getSize();
        }
        else
        {
            logger.finer("Adding Frame" + frameId);
            frameMap.put(frameId, next);
        }
    }

    /**
     * Return tag size based upon the sizes of the tags rather than the physical
     * no of bytes between start of ID3Tag and start of Audio Data.Should be extended
     * by subclasses to incude header.
     *
     * @return size of the tag
     */
    public int getSize()
    {
        int size = 0;
        Iterator iterator = frameMap.values().iterator();
        AbstractID3v2Frame frame;
        while (iterator.hasNext())
        {
            Object o = iterator.next();
            if (o instanceof AbstractID3v2Frame)
            {
                frame = (AbstractID3v2Frame) o;
                size += frame.getSize();
            }
            else
            {
                ArrayList multiFrames = (ArrayList) o;
                for (ListIterator li = multiFrames.listIterator(); li.hasNext();)
                {
                    frame = (AbstractID3v2Frame) li.next();
                    size += frame.getSize();
                }
            }
        }
        return size;
    }

    /**
     * Write all the frames to the byteArrayOutputStream
     *
     * @return ByteBuffer Contains all the frames written within the tag ready for writing to file
     * @throws IOException
     */
    protected ByteArrayOutputStream writeFramesToBuffer() throws IOException
    {
        //Increases as is required
        ByteArrayOutputStream bodyBuffer = new ByteArrayOutputStream();

        //Write all frames, defaults to the order in which they were loaded, newly
        //created frames will be at end of tag.
        AbstractID3v2Frame frame;
        Iterator iterator;
        iterator = frameMap.values().iterator();
        while (iterator.hasNext())
        {
            Object o = iterator.next();
            if (o instanceof AbstractID3v2Frame)
            {
                frame = (AbstractID3v2Frame) o;
                frame.write(bodyBuffer);
            }
            else
            {
                ArrayList multiFrames = (ArrayList) o;
                for (ListIterator li = multiFrames.listIterator(); li.hasNext();)
                {
                    frame = (AbstractID3v2Frame) li.next();
                    frame.write(bodyBuffer);
                }
            }
        }

        return bodyBuffer;
    }



    public void createStructure()
    {
        createStructureHeader();
        createStructureBody();
    }

    public void createStructureHeader()
    {
        MP3File.getStructureFormatter().addElement(this.TYPE_DUPLICATEBYTES, this.duplicateBytes);
        MP3File.getStructureFormatter().addElement(this.TYPE_DUPLICATEFRAMEID, this.duplicateFrameId);
        MP3File.getStructureFormatter().addElement(this.TYPE_EMPTYFRAMEBYTES, this.emptyFrameBytes);
        MP3File.getStructureFormatter().addElement(this.TYPE_FILEREADSIZE, this.fileReadSize);
        MP3File.getStructureFormatter().addElement(this.TYPE_INVALIDFRAMEBYTES, this.invalidFrameBytes);
    }

    public void createStructureBody()
    {
        MP3File.getStructureFormatter().openHeadingElement(TYPE_BODY, "");

        AbstractID3v2Frame frame;
        Iterator iterator = frameMap.values().iterator();
        while (iterator.hasNext())
        {
            Object o = iterator.next();
            if (o instanceof AbstractID3v2Frame)
            {
                frame = (AbstractID3v2Frame) o;
                frame.createStructure();
            }
            else
            {
                ArrayList multiFrames = (ArrayList) o;
                for (ListIterator li = multiFrames.listIterator(); li.hasNext();)
                {
                    frame = (AbstractID3v2Frame) li.next();
                    frame.createStructure();
                }
            }
        }
        MP3File.getStructureFormatter().closeHeadingElement(TYPE_BODY);
    }
}