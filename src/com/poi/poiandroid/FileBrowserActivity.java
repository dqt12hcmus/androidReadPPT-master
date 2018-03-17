package com.poi.poiandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

public class FileBrowserActivity extends Activity implements IFolderItemListener{
	private ImageButton btnHome;
	private FolderLayout localFolders;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_file_browser);
		addControls();
		addEvents();
	}

	private void addControls() {
		btnHome = (ImageButton) findViewById(R.id.btnHome);
		localFolders = (FolderLayout)findViewById(R.id.localfolders);
		localFolders.setIFolderItemListener(this);
		localFolders.setDir("./");
	}
	private void addEvents() {
		btnHome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(FileBrowserActivity.this, HomeActivity.class);
				startActivity(i);
			}
		});

	}

	@Override
	public void OnFileClicked(File file) {
		// TODO Auto-generated method stub
		String chosenFile = file.getName();
		String chosenFileFullPath = file.getAbsolutePath();
		writeCache(chosenFileFullPath);
		Intent i = new Intent(FileBrowserActivity.this, MainActivity.class);
		i.putExtra("filename", chosenFile);
		i.putExtra("filepath", chosenFileFullPath);
		startActivity(i);
	}

	private void writeCache(String chosenFileFullPath) {
		try {
			File pathCacheDir = getCacheDir();
			String strCacheFileName = "recent.cache";
			File newCacheFile = new File(pathCacheDir, strCacheFileName);
            if (!newCacheFile.exists()) {
                newCacheFile.createNewFile();
            }
			FileOutputStream foCache = new FileOutputStream(newCacheFile.getAbsolutePath());
			String strFileContents = chosenFileFullPath;

			foCache.write(strFileContents.getBytes());
			foCache.write(System.getProperty("line.separator").getBytes());
			foCache.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}
	@Override
	public void OnCannotFileRead(File file) {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(this)
				.setTitle(
						"[" + file.getName()
								+ "] folder can't be read!")
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
												int which) {


							}
						}).show();
	}
}