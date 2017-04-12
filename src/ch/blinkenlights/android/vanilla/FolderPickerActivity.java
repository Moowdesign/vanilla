/*
 * Copyright (C) 2013-2017 Adrian Ulrich <adrian@blinkenlights.ch>
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

import java.util.Arrays;
import java.util.ArrayList;
import java.io.File;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.mobeta.android.dslv.DragSortListView;

public abstract class FolderPickerActivity extends Activity
	implements AdapterView.OnItemClickListener,
	           AdapterView.OnItemLongClickListener
{

	/**
	 * The path we should currently display
	 */
	private File mCurrentPath;
	/**
	 * Our listview
	 */
	private DragSortListView mListView;
	/**
	 * View displaying the current path
	 */
	private TextView mPathDisplay;
	/**
	 * Save button
	 */
	private Button mSaveButton;
	/**
	 * The array adapter of our listview
	 */
	private FolderPickerAdapter mListAdapter;

	private boolean mTritastic;
	private ArrayList<String> mIncludedDirs;
	private ArrayList<String> mExcludedDirs;

	@Override  
	public void onCreate(Bundle savedInstanceState) {
		ThemeHelper.setTheme(this, R.style.BackActionBar);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.filebrowser_content);

		mCurrentPath = new File("/");
		mListAdapter = new FolderPickerAdapter(this, 0);
		mPathDisplay = (TextView) findViewById(R.id.path_display);
		mListView    = (DragSortListView)findViewById(R.id.list);
		mSaveButton  = (Button) findViewById(R.id.save_button);

		mListView.setAdapter(mListAdapter);
		mListView.setOnItemClickListener(this);

		mSaveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onFolderSelected(mCurrentPath);
			}});
		// init defaults
		enableTritasticSelect(false, null, null);
	}

	/**
	 * Called after a folder was selected
	 *
	 * @param directory the selected directory
	 */
	public abstract void onFolderSelected(File directory);

	public void enableTritasticSelect(boolean enabled, ArrayList<String> included, ArrayList<String> excluded) {
		mTritastic = enabled;
		mIncludedDirs = included;
		mExcludedDirs = excluded;

		mListView.setOnItemLongClickListener(enabled ? this : null);
		mSaveButton.setText(enabled ? R.string.save : R.string.select);
	}

	/**
	 * Jumps to given directory
	 *
	 * @param directory the directory to jump to
	 */
	void setCurrentDirectory(File directory) {
		mCurrentPath = directory;
		refreshDirectoryList();
	}

	/**
	 * Called when we are displayed (again)
	 * This will always refresh the whole song list
	 */
	@Override
	public void onResume() {
		super.onResume();
		refreshDirectoryList();
	}
	
	/**
	 * Create a bare-bones actionbar
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Called if user taps a row
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		FolderPickerAdapter.Item item = mListAdapter.getItem(pos);
		File newPath = null;

		if(pos == 0) {
			newPath = mCurrentPath.getParentFile();
		}
		else {
			newPath = new File(mCurrentPath, item.name);
		}

		if (newPath != null)
			setCurrentDirectory(newPath);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
		return true;
	}

	/**
	 * display mCurrentPath in the dialog
	 */
	private void refreshDirectoryList() {
		File path = mCurrentPath;
		File[]dirs = path.listFiles();
		
		mListAdapter.clear();
		mListAdapter.add(new FolderPickerAdapter.Item("../", 0));
		
		if(dirs != null) {
			Arrays.sort(dirs);
			for(File fentry: dirs) {
				if(fentry.isDirectory()) {
					int color = 0;
					if (mTritastic) {
						if (mIncludedDirs.contains(fentry.getAbsolutePath()))
							color = 0xff88cece;
						if (mExcludedDirs.contains(fentry.getAbsolutePath()))
							color = 0xffce88ce;
					}
					FolderPickerAdapter.Item item = new FolderPickerAdapter.Item(fentry.getName(), color);
					mListAdapter.add(item);
				}
			}
		}
		else {
			Toast.makeText(this, "Failed to display " + path.getAbsolutePath(), Toast.LENGTH_SHORT).show();
		}
		mPathDisplay.setText(path.getAbsolutePath());
		mListView.setSelectionFromTop(0, 0);
	}
	
}
