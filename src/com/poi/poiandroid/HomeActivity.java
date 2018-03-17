package com.poi.poiandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


public class HomeActivity extends Activity {
	private ImageButton btnOpen;
	private ListView lvRecent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);
		addControls();
		addEvents();
		populateList();
	}

	private void addControls()
	{
		btnOpen = (ImageButton) findViewById(R.id.btnOpen);
		lvRecent = (ListView) findViewById(R.id.lvRecent);
	}
	private void addEvents()
	{
		btnOpen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(HomeActivity.this, FileBrowserActivity.class);
				startActivity(i);
			}
		});
	}
	public List<String> readCache()
	{
		List<String> result = new ArrayList<String>();
		try {
			File pathCacheDir = getCacheDir();
			String strCacheFileName = "recent.cache";
			File newCacheFile = new File(pathCacheDir, strCacheFileName);
			Scanner sc = new Scanner(newCacheFile);
			while (sc.hasNext())
			{
				result.add(sc.nextLine());
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void populateList()
	{
		final ArrayList<String> directories = (ArrayList<String>) readCache();
		final ArrayList<String> names = new ArrayList<String>();
		for (String s : directories)
		{
			File f = new File(s);
			names.add(f.getName());
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
		lvRecent.setAdapter(adapter);
		lvRecent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
			{
				xuLyDocFileGanDay(i, names, directories);
			}
		});
	}

	private void xuLyDocFileGanDay(int position, ArrayList<String> names, ArrayList<String> directories)
	{
		String chosenFile = names.get(position);
		String chosenFileFullPath = directories.get(position);
		Toast.makeText(HomeActivity.this, chosenFileFullPath, Toast.LENGTH_SHORT).show();
		Intent i = new Intent(HomeActivity.this, MainActivity.class);
		i.putExtra("filename", chosenFile);
		i.putExtra("filepath", chosenFileFullPath);
		startActivity(i);
	}
}