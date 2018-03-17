package com.poi.poiandroid;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import net.arnx.wmf2svg.Main;
import net.pbdavey.awt.Graphics2D;

import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.record.MainMaster;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.w3c.dom.Text;

import and.awt.Dimension;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private TextView txtFilename;
	private ImageButton btnBrowser;
    private Button btnPresenter;
	private String chosenfile;
	private String fullpath;

	private SlideShow ppt;
	private ListView lvSlides;
	private ArrayList<Bitmap> dsBitmap;
	private ImageViewAdapter adapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		addControls();//
		addEvents();
	}
	private void addEvents() {
		btnBrowser.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(MainActivity.this, FileBrowserActivity.class);
				startActivity(i);
			}
		});
        btnPresenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, PresenterActivity.class);
				i.putExtra("path", fullpath);
				startActivity(i);
            }
        });
		lvSlides.setOnItemClickListener(new AdapterView.OnItemClickListener() {
											@Override
											public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
											{
												Intent intent = new Intent(MainActivity.this, SlideActivity.class);
												intent.putExtra("position", i);
												intent.putExtra("path", fullpath);
												startActivity(intent);
											}
										}
		);
	}
	private void addControls()
	{
        btnPresenter = (Button) findViewById(R.id.btnPresenter);
        btnBrowser = (ImageButton) findViewById(R.id.btnBrowser);
		txtFilename = (TextView) findViewById(R.id.txtFilename);

		lvSlides = (ListView) findViewById(R.id.lvSlides);
		dsBitmap = new ArrayList<Bitmap>();

		Intent i = getIntent();
		chosenfile = i.getStringExtra("filename");
		fullpath = i.getStringExtra("filepath");
		txtFilename.setText(chosenfile);
		try
		{
			ppt = new SlideShow(new File(fullpath));
			dsBitmap = convertAllSlidesToBitmaps(ppt);
			adapter = new ImageViewAdapter(MainActivity.this, R.layout.item, dsBitmap);
			lvSlides.setAdapter(adapter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private ArrayList<Bitmap>  convertAllSlidesToBitmaps(SlideShow ppt)
	{
		ArrayList<Bitmap> bitmapVector = new ArrayList<Bitmap>();
		Bitmap bmp;
		Canvas canvas;
		Paint paint;
		final Dimension pgsize = ppt.getPageSize();
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {

			}
		};
		Slide[] slide = ppt.getSlides();
		for (int position = 0; position < slide.length; position ++)
		{
			bmp = Bitmap.createBitmap((int) pgsize.getWidth(), (int) pgsize.getHeight(), Config.RGB_565);
			canvas = new Canvas(bmp);
			paint = new Paint();
			paint.setColor(android.graphics.Color.WHITE);
			paint.setFlags(Paint.ANTI_ALIAS_FLAG);
			canvas.drawPaint(paint);
			final Graphics2D graphics2d = new Graphics2D(canvas);
			final AtomicBoolean isCanceled = new AtomicBoolean(false);
			slide[position].draw(graphics2d, isCanceled, handler, position);
			bitmapVector.add(bmp);
		}
		return bitmapVector;
	}

    @Override
	protected void onDestroy() {
		super.onDestroy();
        if (ppt != null) {
            ppt = null;
        }
        if (lvSlides != null) {
            lvSlides = null;
        }
		for (Bitmap bmp : dsBitmap)
		{
			bmp.recycle();
			bmp = null;
		}
	}
}