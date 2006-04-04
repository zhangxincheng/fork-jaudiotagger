/**
 * Initial @author : Paul Taylor
 * <p/>
 * Version @version:$Id$
 * <p/>
 * Jaudiotagger Copyright (C)2004,2005
 * <p/>
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public  License as published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not,
 * you can get a copy from http://www.opensource.org/licenses/lgpl-license.php or write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * <p/>
 * Description:
 */
package org.jaudiotagger.tag.id3;

import java.util.*;

/**
 * Defines ID3 frames and collections that categorise frames
 */
public class ID3v23Frames extends ID3Frames
{
    /**
     * Define all frames that are valid within ID3v23
     * Frame IDs begining with T are text frames, & with W are url frames
     */
    public static final String FRAME_ID_V3_ACCOMPANIMENT = "TPE2";
    public static final String FRAME_ID_V3_ALBUM = "TALB";
    public static final String FRAME_ID_V3_ARTIST = "TPE1";
    public static final String FRAME_ID_V3_ATTACHED_PICTURE = "APIC";
    public static final String FRAME_ID_V3_AUDIO_ENCRYPTION = "AENC";
    public static final String FRAME_ID_V3_BPM = "TBPM";
    public static final String FRAME_ID_V3_COMMENT = "COMM";
    public static final String FRAME_ID_V3_COMMERCIAL_FRAME = "COMR";
    public static final String FRAME_ID_V3_COMPOSER = "TCOM";
    public static final String FRAME_ID_V3_CONDUCTOR = "TPE3";
    public static final String FRAME_ID_V3_CONTENT_GROUP_DESC = "TIT1";
    public static final String FRAME_ID_V3_COPYRIGHTINFO = "TCOP";
    public static final String FRAME_ID_V3_ENCODEDBY = "TENC";
    public static final String FRAME_ID_V3_ENCRYPTION = "ENCR";
    public static final String FRAME_ID_V3_EQUALISATION = "EQUA";
    public static final String FRAME_ID_V3_EVENT_TIMING_CODES = "ETCO";
    public static final String FRAME_ID_V3_FILE_OWNER = "TOWN";
    public static final String FRAME_ID_V3_FILE_TYPE = "TFLT";
    public static final String FRAME_ID_V3_GENERAL_ENCAPS_OBJECT = "GEOB";
    public static final String FRAME_ID_V3_GENRE = "TCON";
    public static final String FRAME_ID_V3_GROUP_ID_REG = "GRID";
    public static final String FRAME_ID_V3_HW_SW_SETTINGS = "TSSE";
    public static final String FRAME_ID_V3_INITIAL_KEY = "TKEY";
    public static final String FRAME_ID_V3_IPLS = "IPLS";
    public static final String FRAME_ID_V3_ISRC = "TSRC";
    public static final String FRAME_ID_V3_LANGUAGE = "TLAN";
    public static final String FRAME_ID_V3_LENGTH = "TLEN";
    public static final String FRAME_ID_V3_LINKED_INFO = "LINK";
    public static final String FRAME_ID_V3_LYRICIST = "TEXT";
    public static final String FRAME_ID_V3_MEDIA_TYPE = "TMED";
    public static final String FRAME_ID_V3_MPEG_LOCATION_LOOKUP_TABLE = "MLLT";
    public static final String FRAME_ID_V3_MUSIC_CD_ID = "MCDI";
    public static final String FRAME_ID_V3_ORIGARTIST = "TOPE";
    public static final String FRAME_ID_V3_ORIG_FILENAME = "TOFN";
    public static final String FRAME_ID_V3_ORIG_LYRICIST = "TOLY";
    public static final String FRAME_ID_V3_ORIG_TITLE = "TOAL";
    public static final String FRAME_ID_V3_OWNERSHIP = "OWNE";
    public static final String FRAME_ID_V3_PLAYLIST_DELAY = "TDLY";
    public static final String FRAME_ID_V3_PLAY_COUNTER = "PCNT";
    public static final String FRAME_ID_V3_POPULARIMETER = "POPM";
    public static final String FRAME_ID_V3_POSITION_SYNC = "POSS";
    public static final String FRAME_ID_V3_PRIVATE = "PRIV";
    public static final String FRAME_ID_V3_PUBLISHER = "TPUB";
    public static final String FRAME_ID_V3_RADIO_NAME = "TRSN";
    public static final String FRAME_ID_V3_RADIO_OWNER = "TRSO";
    public static final String FRAME_ID_V3_RECOMMENDED_BUFFER_SIZE = "RBUF";
    public static final String FRAME_ID_V3_RELATIVE_VOLUME_ADJUSTMENT = "RVAD";
    public static final String FRAME_ID_V3_REMIXED = "TPE4";
    public static final String FRAME_ID_V3_REVERB = "RVRB";
    public static final String FRAME_ID_V3_SET = "TPOS";
    public static final String FRAME_ID_V3_SYNC_LYRIC = "SYLT";
    public static final String FRAME_ID_V3_SYNC_TEMPO = "SYTC";
    public static final String FRAME_ID_V3_TDAT = "TDAT";
    public static final String FRAME_ID_V3_TERMS_OF_USE = "USER";
    public static final String FRAME_ID_V3_TIME = "TIME";
    public static final String FRAME_ID_V3_TITLE = "TIT2";
    public static final String FRAME_ID_V3_TITLE_REFINEMENT = "TIT3";
    public static final String FRAME_ID_V3_TORY = "TORY";
    public static final String FRAME_ID_V3_TRACK = "TRCK";
    public static final String FRAME_ID_V3_TRDA = "TRDA";
    public static final String FRAME_ID_V3_TSIZ = "TSIZ";
    public static final String FRAME_ID_V3_TYER = "TYER";
    public static final String FRAME_ID_V3_UNIQUE_FILE_ID = "UFID";
    public static final String FRAME_ID_V3_UNSYNC_LYRICS = "USLT";
    public static final String FRAME_ID_V3_URL_ARTIST_WEB = "WOAR";
    public static final String FRAME_ID_V3_URL_COMMERCIAL = "WCOM";
    public static final String FRAME_ID_V3_URL_COPYRIGHT = "WCOP";
    public static final String FRAME_ID_V3_URL_FILE_WEB = "WOAF";
    public static final String FRAME_ID_V3_URL_OFFICIAL_RADIO = "WORS";
    public static final String FRAME_ID_V3_URL_PAYMENT = "WPAY";
    public static final String FRAME_ID_V3_URL_PUBLISHERS = "WPUB";
    public static final String FRAME_ID_V3_URL_SOURCE_WEB = "WOAS";
    public static final String FRAME_ID_V3_USER_DEFINED_INFO = "TXXX";
    public static final String FRAME_ID_V3_USER_DEFINED_URL = "WXXX";


    private static ID3v23Frames id3v23Frames;

    public static ID3v23Frames getInstanceOf()
    {
        if (id3v23Frames == null)
        {
            id3v23Frames = new ID3v23Frames();
        }
        return id3v23Frames;
    }

    private ID3v23Frames()
    {
        /** The defined v23 frames, every one define above must have a description */
        idToValue.put(FRAME_ID_V3_ACCOMPANIMENT, "Text: Band/Orchestra/Accompaniment");
        idToValue.put(FRAME_ID_V3_ALBUM, "Text: Album/Movie/Show title");
        idToValue.put(FRAME_ID_V3_ARTIST, "Text: Lead artist(s)/Lead performer(s)/Soloist(s)/Performing group");
        idToValue.put(FRAME_ID_V3_ATTACHED_PICTURE, "Attached picture");
        idToValue.put(FRAME_ID_V3_AUDIO_ENCRYPTION, "Audio encryption");
        idToValue.put(FRAME_ID_V3_BPM, "Text: BPM (Beats Per Minute)");
        idToValue.put(FRAME_ID_V3_COMMENT, "Comments");
        idToValue.put(FRAME_ID_V3_COMMERCIAL_FRAME,"");
        idToValue.put(FRAME_ID_V3_COMPOSER, "Text: Composer");
        idToValue.put(FRAME_ID_V3_CONDUCTOR, "Text: Conductor/Performer refinement");
        idToValue.put(FRAME_ID_V3_CONTENT_GROUP_DESC, "Text: Content group description");
        idToValue.put(FRAME_ID_V3_COPYRIGHTINFO, "Text: Copyright message");
        idToValue.put(FRAME_ID_V3_ENCODEDBY, "Text: Encoded by");
        idToValue.put(FRAME_ID_V3_ENCRYPTION, "Encryption method registration");
        idToValue.put(FRAME_ID_V3_EQUALISATION, "Equalization");
        idToValue.put(FRAME_ID_V3_EVENT_TIMING_CODES, "Event timing codes");
        idToValue.put(FRAME_ID_V3_FILE_OWNER,"");
        idToValue.put(FRAME_ID_V3_FILE_TYPE, "Text: File type");
        idToValue.put(FRAME_ID_V3_GENERAL_ENCAPS_OBJECT, "General encapsulated datatype");
        idToValue.put(FRAME_ID_V3_GENRE, "Text: Content type");
        idToValue.put(FRAME_ID_V3_GROUP_ID_REG ,"");
        idToValue.put(FRAME_ID_V3_HW_SW_SETTINGS, "Text: Software/hardware and settings used for encoding");
        idToValue.put(FRAME_ID_V3_INITIAL_KEY, "Text: Initial key");
        idToValue.put(FRAME_ID_V3_IPLS, "Involved people list");
        idToValue.put(FRAME_ID_V3_ISRC, "Text: ISRC (International Standard Recording Code)");
        idToValue.put(FRAME_ID_V3_LANGUAGE, "Text: Language(s)");
        idToValue.put(FRAME_ID_V3_LENGTH, "Text: Length");
        idToValue.put(FRAME_ID_V3_LINKED_INFO, "Linked information");
        idToValue.put(FRAME_ID_V3_LYRICIST, "Text: Lyricist/text writer");
        idToValue.put(FRAME_ID_V3_MEDIA_TYPE, "Text: Media type");
        idToValue.put(FRAME_ID_V3_MPEG_LOCATION_LOOKUP_TABLE, "MPEG location lookup table");
        idToValue.put(FRAME_ID_V3_MUSIC_CD_ID, "Music CD Identifier");
        idToValue.put(FRAME_ID_V3_ORIGARTIST, "Text: Original artist(s)/performer(s)");
        idToValue.put(FRAME_ID_V3_ORIG_FILENAME, "Text: Original filename");
        idToValue.put(FRAME_ID_V3_ORIG_LYRICIST, "Text: Original Lyricist(s)/text writer(s)");
        idToValue.put(FRAME_ID_V3_ORIG_TITLE, "Text: Original album/Movie/Show title");
        idToValue.put(FRAME_ID_V3_OWNERSHIP,"");
        idToValue.put(FRAME_ID_V3_PLAYLIST_DELAY, "Text: Playlist delay");
        idToValue.put(FRAME_ID_V3_PLAY_COUNTER, "Play counter");
        idToValue.put(FRAME_ID_V3_POPULARIMETER, "Popularimeter");
        idToValue.put(FRAME_ID_V3_POSITION_SYNC, "Position Sync");
        idToValue.put(FRAME_ID_V3_PRIVATE,"Private frame");
        idToValue.put(FRAME_ID_V3_PUBLISHER, "Text: Publisher");
        idToValue.put(FRAME_ID_V3_RADIO_NAME,"");
        idToValue.put(FRAME_ID_V3_RADIO_OWNER ,"");
        idToValue.put(FRAME_ID_V3_RECOMMENDED_BUFFER_SIZE, "Recommended buffer size");
        idToValue.put(FRAME_ID_V3_RELATIVE_VOLUME_ADJUSTMENT, "Relative volume adjustment");
        idToValue.put(FRAME_ID_V3_REMIXED, "Text: Interpreted, remixed, or otherwise modified by");
        idToValue.put(FRAME_ID_V3_REVERB, "Reverb");
        idToValue.put(FRAME_ID_V3_SET, "Text: Part of a set");
        idToValue.put(FRAME_ID_V3_SYNC_LYRIC, "Synchronized lyric/text");
        idToValue.put(FRAME_ID_V3_SYNC_TEMPO, "Synced tempo codes");
        idToValue.put(FRAME_ID_V3_TDAT, "Text: Date");
        idToValue.put(FRAME_ID_V3_TERMS_OF_USE,"");
        idToValue.put(FRAME_ID_V3_TIME, "Text: Time");
        idToValue.put(FRAME_ID_V3_TITLE, "Text: Title/Songname/Content description");
        idToValue.put(FRAME_ID_V3_TITLE_REFINEMENT, "Text: Subtitle/Description refinement");
        idToValue.put(FRAME_ID_V3_TORY, "Text: Original release year");
        idToValue.put(FRAME_ID_V3_TRACK, "Text: Track number/Position in set");
        idToValue.put(FRAME_ID_V3_TRDA, "Text: Recording dates");
        idToValue.put(FRAME_ID_V3_TSIZ, "Text: Size");
        idToValue.put(FRAME_ID_V3_TYER, "Text: Year");
        idToValue.put(FRAME_ID_V3_UNIQUE_FILE_ID, "Unique file identifier");
        idToValue.put(FRAME_ID_V3_UNSYNC_LYRICS, "Unsychronized lyric/text transcription");
        idToValue.put(FRAME_ID_V3_URL_ARTIST_WEB, "URL: Official artist/performer webpage");
        idToValue.put(FRAME_ID_V3_URL_COMMERCIAL, "URL: Commercial information");
        idToValue.put(FRAME_ID_V3_URL_COPYRIGHT, "URL: Copyright/Legal information");
        idToValue.put(FRAME_ID_V3_URL_FILE_WEB, "URL: Official audio file webpage");
        idToValue.put(FRAME_ID_V3_URL_OFFICIAL_RADIO,"Official Radio");
        idToValue.put(FRAME_ID_V3_URL_PAYMENT,"URL: Payment");
        idToValue.put(FRAME_ID_V3_URL_PUBLISHERS, "URL: Publishers official webpage");
        idToValue.put(FRAME_ID_V3_URL_SOURCE_WEB, "URL: Official audio source webpage");
        idToValue.put(FRAME_ID_V3_USER_DEFINED_INFO, "User defined text information frame");
        idToValue.put(FRAME_ID_V3_USER_DEFINED_URL, "User defined URL link frame");

        createMaps();
        multipleFrames = new TreeSet();
        multipleFrames.add(FRAME_ID_V3_USER_DEFINED_INFO);
        multipleFrames.add(FRAME_ID_V3_USER_DEFINED_URL);
        multipleFrames.add(FRAME_ID_V3_ATTACHED_PICTURE);
        multipleFrames.add(FRAME_ID_V3_PRIVATE);
        multipleFrames.add(FRAME_ID_V3_COMMENT);

        discardIfFileAlteredFrames = new TreeSet();
        discardIfFileAlteredFrames.add(FRAME_ID_V3_EVENT_TIMING_CODES);
        discardIfFileAlteredFrames.add(FRAME_ID_V3_EQUALISATION);
        discardIfFileAlteredFrames.add(FRAME_ID_V3_MPEG_LOCATION_LOOKUP_TABLE);
        discardIfFileAlteredFrames.add(FRAME_ID_V3_POSITION_SYNC);
        discardIfFileAlteredFrames.add(FRAME_ID_V3_SYNC_LYRIC);
        discardIfFileAlteredFrames.add(FRAME_ID_V3_SYNC_TEMPO);
        discardIfFileAlteredFrames.add(FRAME_ID_V3_RELATIVE_VOLUME_ADJUSTMENT);
        discardIfFileAlteredFrames.add(FRAME_ID_V3_EVENT_TIMING_CODES);
        discardIfFileAlteredFrames.add(FRAME_ID_V3_ENCODEDBY);
        discardIfFileAlteredFrames.add(FRAME_ID_V3_LENGTH);
        discardIfFileAlteredFrames.add(FRAME_ID_V3_TSIZ);
    }
}