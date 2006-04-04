/**
 *  Initial @author : Paul Taylor
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
 */
package org.jaudiotagger.audio.mp3;

import org.jaudiotagger.audio.AbstractAudioHeader;
import org.jaudiotagger.audio.InvalidAudioFrameException;

import java.util.*;
import java.util.logging.Level;
import java.io.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;

/* Represents the audio header of an MP3 File, the audio header consists of a number of
 * audio frames. Because we are not trying to play the audio but only extract some information
 * regarding the audio we only need to read the first  audio frames to ensure that we have correctly
 * identified them as audio frames and extracted the metadata we reuire.

 * Start of Audio id 0xFF (11111111) and then second byte anded with 0xE0(11100000).
 * For example 2nd byte doesnt have to be 0xE0 is just has to have the top 3 signicant
 * bits set. For example 0xFB (11111011) is a common occurence of the second match. The 2nd byte
 * defines flags to indicate various mp3 values.
 *
 * Having found these two values we then read the header which comprises these two bytes plus a further
 * two to ensure this really is a MP3Header, sometimes the first frame is actually a dummy frame with summary information
 * held within about the whole file, typically using a Xing Header or LAme Header. This is most useful when the file
 * is variable bit rate, if the file is variable bit rate but does not use a summary header it will not be correclty identified
 * as a VBR frame and the track length will be incorreclty calculated.
 *
 * Strictly speaking MP3 means an MPEG-1, Layer III file but MP2 (MPEG-1,Layer II), MP1 (MPEG-1,Layer I) and MPEG-2 files are
 * sometimes used and named with the .mp3 suffix so this library attempts to supports all these formats.
*/
public final class MP3AudioHeader extends AbstractAudioHeader
{
    private MPEGFrameHeader mp3FrameHeader;
    private XingFrame       mp3XingFrame;

    private long    fileSize;
    private long    startByte;
    private double  timePerFrame;
    private double  trackLength;
    private long    numberOfFrames;
    private long    numberOfFramesEstimate;
    private long    bitrate;

    private static final SimpleDateFormat timeInFormat    = new SimpleDateFormat("ss");
    private static final SimpleDateFormat timeOutFormat   = new SimpleDateFormat("mm:ss");
    private static final char             isVbrIdentifier = '~';
    private static final int              CONVERT_TO_KILOBITS = 1000;
    private static final String           TYPE_MP3 = "mp3";
    private static final int              CONVERTS_BYTE_TO_BITS = 8;

    
    /** After testing the average location of the first MP3Header bit was at 5000 bytes so this is
     *  why chosen as a default.
     */
    private final static int FILE_BUFFER_SIZE = 5000;
    private final static int MIN_BUFFER_REMAINING_REQUIRED =
        MPEGFrameHeader.HEADER_SIZE + XingFrame.MAX_BUFFER_SIZE_NEEDED_TO_READ_XING;

    public MP3AudioHeader(final File seekFile)throws IOException,InvalidAudioFrameException
    {
         if(seek(seekFile)==false)
         {
             throw new InvalidAudioFrameException("No audio header found within"+seekFile.getName());
         }
    }

    /**
     * Returns true if the first MP3 frame can be found for the MP3 file
     * argument. This is the first byte of  music data and not the ID3 Tag Frame.
     *
     *
     * @param seekFile MP3 file to seek
     * @return true if the first MP3 frame can be found
     * @throws IOException on any I/O error
     */
    public boolean seek(final File seekFile)
        throws IOException
    {
        //This is substantially faster than updating the filechannels position
        int filePointerCount = 0;

        final FileInputStream     fis = new FileInputStream(seekFile);
        final FileChannel fc = fis.getChannel();

        //Read into Byte Buffer in Chunks
        ByteBuffer  bb = ByteBuffer.allocateDirect(FILE_BUFFER_SIZE);
        fc.read(bb,0);
        bb.flip();

        boolean syncFound  = false;
        try
        {
            do
            {
                if(bb.remaining()==MIN_BUFFER_REMAINING_REQUIRED)
                {
                    bb.clear();
                    fc.position(filePointerCount);
                    fc.read(bb,fc.position());
                    bb.flip();
                    if(bb.limit()<=MIN_BUFFER_REMAINING_REQUIRED)
                    {
                        //No mp3 exists
                        return false;
                    }
                }
                //MP3File.logger.finest("fc:"+fc.position() + "bb"+bb.position());
                if(MPEGFrameHeader.isMPEGFrame(bb))
                {
                    try
                    {
                        if(MP3File.logger.isLoggable(Level.FINEST))
                        {
                            MP3File.logger.finest("Found Possible header at:"+filePointerCount);
                        }

                        mp3FrameHeader = MPEGFrameHeader.parseMPEGHeader(bb);
                        syncFound = true;

                        if(XingFrame.isXingFrame(bb,mp3FrameHeader))
                        {
                            if(MP3File.logger.isLoggable(Level.FINEST))
                            {
                                MP3File.logger.finest("Found Possible XingHeader");
                            }
                            try
                            {
                                mp3XingFrame = XingFrame.parseXingFrame();
                            }
                            catch (InvalidAudioFrameException ex)
                            {
                                // We Ignore because even if Xing Header is corrupted
                                //doesn't mean file is corrupted
                            }
                            break;
                        }
                        /* There is a small but real chance that an unsynchronised
                         * ID3 Frame could fool the MPEG Parser into thinking it was
                         * an MPEG Header. If this happens the chances of the next bytes
                         * forming a Xing frame header are very remote. On the basis that
                         * most files these days have Xing headers we do an additional check
                         * for when an apparent frame header has been found but is not followed
                         * by a Xing Header:We check the next header this wont impose a large overhead
                         * because will apply to most MPEGS anyway ( Most likely to occur if audio
                         * has an  APIC frame which should have been unsynchronised but hasnt been) */
                         else
                         {
                             syncFound=isNextFrameValid(seekFile,filePointerCount,bb,fc);
                             if(syncFound==true)
                             {
                                break;
                             }
                         }

                    }
                    catch (InvalidAudioFrameException ex)
                    {
                        // We Ignore because likely to be incorrect sync bits ,
                        // will just continue in loop
                    }
                }
                bb.position(bb.position()+1);
                filePointerCount++;
            }
            while(!syncFound);
        }
        catch (EOFException ex)
        {
            ex.printStackTrace();
            syncFound = false;
        }
        catch (IOException iox)
        {
            iox.printStackTrace();
            syncFound = false;
            throw iox;
        }
        finally
        {
            if (fc != null)
            {
                fc.close();
            }

            if (fis != null)
            {
                fis.close();
            } 
        }

        //Return to start of audio header
        if(MP3File.logger.isLoggable(Level.FINEST))
        {
            MP3File.logger.finer("Return found matching mp3 header starting at" + filePointerCount);
        }
        setFileSize(seekFile.length());
        setMp3StartByte(filePointerCount);
        setTimePerFrame();
        setNumberOfFrames();
        setTrackLength();
        setBitRate();
        return syncFound;
    }

    /** Called in some circumstances to check the next frame to ensure we have the correct audio header
     *
     * @return
     */
    private boolean isNextFrameValid(File seekFile,int filePointerCount,ByteBuffer  bb,FileChannel fc)
    throws IOException
    {
        if(MP3File.logger.isLoggable(Level.FINEST))
        {
             MP3File.logger.finer("Checking next frame"+seekFile.getName()+ ":fpc:"+filePointerCount+"skipping to:"+ (filePointerCount + mp3FrameHeader.getFrameLength()));
        }
        boolean result=false;
        int currentPosition = bb.position();

        //Our buffer is not large enough to fit in the whole of this frame, something must
        //have gone wrong because frames are not this large, so just return false
        //bad frame header
        if( mp3FrameHeader.getFrameLength()>( FILE_BUFFER_SIZE - MIN_BUFFER_REMAINING_REQUIRED))
        {
            MP3File.logger.finer("Frame size is too large to be a frame:"+mp3FrameHeader.getFrameLength());
            return false;
        }

        //Check for end of buffer if not enough room get some more
        if(bb.remaining()<=MIN_BUFFER_REMAINING_REQUIRED + mp3FrameHeader.getFrameLength())
        {
            bb.clear();
            fc.position(filePointerCount);
            fc.read(bb,fc.position());
            bb.flip();
            if(bb.limit()<=MIN_BUFFER_REMAINING_REQUIRED)
            {
                //No mp3 exists
                MP3File.logger.finer("Nearly at end of file:");
                return false;
            }
        }

        //Position bb to the start of the alleged next frame
        bb.position(bb.position() + mp3FrameHeader.getFrameLength());
        if(MPEGFrameHeader.isMPEGFrame(bb))
        {
            try
            {
                MPEGFrameHeader.parseMPEGHeader(bb);
                MP3File.logger.finer("Check next frame confirms is an audio header ");
                result=true;
            }
            catch (InvalidAudioFrameException ex)
            {
                MP3File.logger.finer("Check next frame confirms is an audio header ");
                result=false;
            }
        }

        //Set back to the start of the previous frame
        bb.position(currentPosition);
        return result;
    }

    /**
     * Set the location of where the Audio file begins in the file
     *
     * @param startByte
     */
    private void setMp3StartByte(final long startByte)
    {
        this.startByte = startByte;
    }


    /**
     * Returns the byte position of the first MP3 Frame that the
     * <code>file</code> arguement refers to. This is the first byte of music
     * data and not the ID3 Tag Frame.
     *
     * @return the byte position of the first MP3 Frame
     */
    public long getMp3StartByte()
    {
        return startByte;
    }


    /**
     *  Set number of frames in this file, use Xing if exists otherwise ((File Size - Non Audio Part)/Frame Size)
     */
    private void setNumberOfFrames()
    {
         numberOfFramesEstimate = (fileSize - startByte) / mp3FrameHeader.getFrameLength();

         if(mp3XingFrame!=null&&mp3XingFrame.isFrameCountEnabled()==true)
         {
            numberOfFrames = mp3XingFrame.getFrameCount();
         }
         else
         {
            numberOfFrames = numberOfFramesEstimate;
         }

    }

    /**
     *
     * @return The number of frames within the Audio File, calculated as accurrately as possible
     */
    public long getNumberOfFrames()
    {
        return numberOfFrames;
    }

    /**
     *
     * @return The number of frames within the Audio File, calculated by dividing the filesize by
     * the number of frames, this may not be the most accurate method available.
     */
    public long getNumberOfFramesEstimate()
    {
        return numberOfFramesEstimate;
    }
     /**
     * Set the time each frame contributes to the audio in fractions of seconds, the higher
     * the sampling rate the shorter the audio segment provided by the frame,
     * the number of samples is fixed by the MPEG Version and Layer
     */
    private void setTimePerFrame()
    {
        timePerFrame = mp3FrameHeader.getNoOfSamples() / mp3FrameHeader.getSamplingRate().doubleValue();

        //Because when calculating framelenggth we alter the calculation slightly for MPEGVersion2
        //we seem to have to make a correspondinf modification to get the correct time
        if(
          (mp3FrameHeader.getVersion()==MPEGFrameHeader.VERSION_2) ||
          (mp3FrameHeader.getVersion()==MPEGFrameHeader.VERSION_2_5))
        {
            if(
                (mp3FrameHeader.getLayer()==MPEGFrameHeader.LAYER_II)||
                (mp3FrameHeader.getLayer()==MPEGFrameHeader.LAYER_III)
            )
            {
                timePerFrame = timePerFrame / 2;
            }
        }
    }

    /**
     *
     * @return the the time each frame contributes to the audio in fractions of seconds
     */
    private double getTimePerFrame()
    {
         return timePerFrame;
    }

     /**
     * Estimate the length of the audio track in seconds
     * Calculation is Number of frames multiplied by the Time Per Frame using the first frame as a prototype
     * Time Per Frame is the number of samples in the frame (which is defined by the MPEGVersion/Layer combination)
     * divided by the sampling rate, i.e the higher the sampling rate the shorter the audio represented by the frame is going
     * to be.
     */
    private void setTrackLength()
    {
        trackLength = numberOfFrames * getTimePerFrame();
    }


    /**
     *
     * @return Track Length in seconds
     */
    public double getTrackLength()
    {
        return trackLength;
    }

    /**
     * Return the length in user friendly format
     */
    public String getTrackLengthAsString()
    {
        try
        {
            final long lengthInSecs = (long)getTrackLength();
            final Date timeIn = timeInFormat.parse(String.valueOf(lengthInSecs));
            return timeOutFormat.format(timeIn);
        }
        catch (ParseException pe)
        {
            return "";
        }
    }

     /**
     *
     * @return the audio file type
     */
    public String getType()
    {
        return TYPE_MP3;
    }

     /**
     * Set bitrate in kbps, if Vbr use Xingheader if possible
     */
    private void setBitRate()
    {

        if(mp3XingFrame!=null&&mp3XingFrame.isVbr())
        {
            if(mp3XingFrame.isAudioSizeEnabled()&&mp3XingFrame.getAudioSize()>0)
            {
                bitrate = (long)((mp3XingFrame.getAudioSize() * CONVERTS_BYTE_TO_BITS)/ (timePerFrame * getNumberOfFrames() * CONVERT_TO_KILOBITS ));
            }
            else
            {
                bitrate = (long)(((fileSize - startByte) * CONVERTS_BYTE_TO_BITS) / (timePerFrame * getNumberOfFrames() * CONVERT_TO_KILOBITS ));
            }
        }
        else
        {
            bitrate = mp3FrameHeader.getBitRate().intValue();
        }
    }

    /**
     *
     * @return the BitRate of the Audio, to distinguish cbr from vbr we add a '~'
     * for vbr.
     */
    public String getBitRate()
    {
        if(mp3XingFrame!=null&&mp3XingFrame.isVbr()==true)
        {
            return  isVbrIdentifier + String.valueOf(bitrate);
        }
        else
        {
            return  String.valueOf(bitrate);
        }
    }

    /**

     * @return  the sampling rate
     */
    public String getSampleRate()
    {
         return  String.valueOf(mp3FrameHeader.getSamplingRate());
    }

    public String getMpegVersion()
    {
        return mp3FrameHeader.getVersionAsString();
    }

    public String getMpegLayer()
    {
        return mp3FrameHeader.getLayerAsString();
    }

    /**
     *
     * @return the format
     */
    public String getFormat()
    {
        return mp3FrameHeader.getVersionAsString()+ mp3FrameHeader.getLayerAsString();
    }

    /**
     *
     * @return the Channel Mode such as Stero or Mono
     */
    public String getChannels()
    {
         return mp3FrameHeader.getChannelModeAsString();
    }

    public String getEmphasis()
    {
         return mp3FrameHeader.getEmphasisAsString();
    }

    /**
     *
     * @return if the bitrate is variable, Xing header takes precedence if we have one
     */
    public boolean isVariableBitRate()
    {
         if(mp3XingFrame!=null)
         {
             return mp3XingFrame.isVbr();
         }
         else
         {
            return mp3FrameHeader.isVariableBitRate();
         }
    }

    public boolean isProtected()
    {
        return mp3FrameHeader.isProtected();
    }

    public boolean isPrivate()
    {
        return mp3FrameHeader.isPrivate();
    }

    public boolean isCopyrighted()
    {
        return mp3FrameHeader.isCopyrighted();
    }

    public boolean isOriginal()
    {
        return mp3FrameHeader.isOriginal();
    }

    public boolean isPadding()
    {
        return mp3FrameHeader.isPadding();
    }

    /**
     * Set the size of the file, required in some calculations
     * @param fileSize
     */
    private void setFileSize(long fileSize)
    {
        this.fileSize = fileSize;
    }


    /**
     *
     * @return a string represntation
     */
    public String toString()
    {
        String s = "fileSize:"          + fileSize
                +  " startByte:"        + startByte
                +  " numberOfFrames:"   + numberOfFrames
                +  " numberOfFramesEst:"+ numberOfFramesEstimate
                +  " timePerFrame:"     + timePerFrame
                +  " bitrate:"          + bitrate
                +  " trackLength:"      + getTrackLengthAsString();

        if(this.mp3FrameHeader!=null)
        {
            s +=mp3FrameHeader.toString();
        }

        if(this.mp3XingFrame!=null)
        {
            s +=mp3XingFrame.toString();
        }
        return s;
    }
}