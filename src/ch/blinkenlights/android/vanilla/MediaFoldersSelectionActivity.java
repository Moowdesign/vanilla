/*
 * Copyright (C) 2017 Adrian Ulrich <adrian@blinkenlights.ch>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>. 
 */

package ch.blinkenlights.android.vanilla;

import ch.blinkenlights.android.medialibrary.MediaLibrary;

import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;

import java.io.File;

public class MediaFoldersSelectionActivity extends FolderPickerActivity {

	private SharedPreferences.Editor mPrefEditor;

	@Override  
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.media_folders_header);

		MediaLibrary.Preferences prefs = MediaLibrary.getPreferences(this);
		File startPath = FileUtils.getFilesystemBrowseStart(this);

		// Make sure that we display the current selection
		setCurrentDirectory(startPath);
		enableTritasticSelect(true, prefs.mediaFolders, prefs.blacklistedFolders);
	}


	@Override
	public void onFolderSelected(File directory) {
	}

}
