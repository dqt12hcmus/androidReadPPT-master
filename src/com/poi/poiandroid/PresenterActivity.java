package com.poi.poiandroid;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import net.pbdavey.awt.Graphics2D;

import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.usermodel.SlideShow;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import and.awt.Dimension;

/**
 * Created by QuangThai on 6/17/2017.
 */

public class PresenterActivity extends Activity
{
    private ImageView imgNext;
    private TextView txtNote;
    private TextView txtNum;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private SlideShow ppt;
    private Slide[] slide;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_presenter);
        addControls();
        initializeSlideshow();
        initializeSlideNum();
        initializeNextSlide();
        initializeNote();
        
    }

    private void initializeSlideNum()
    {
        int slideCount = slide.length;
        int slideNow = mViewPager.getCurrentItem();
        String SlideNum = "Slide " + slideNow + " of " + slideCount;
        txtNum.setText(SlideNum);
    }
    private void initializeNextSlide() {
        int slideNow = mViewPager.getCurrentItem();
        int nextSlide = slideNow + 1;
        convertSlideToBitmap(nextSlide);
    }

    private void convertSlideToBitmap(int nextSlide) {
        
    }

    private void initializeNote() {
    }

    private void addControls() {
        imgNext = (ImageView) findViewById(R.id.imgNext);
        txtNote = (TextView) findViewById(R.id.txtNote);
        txtNum = (TextView) findViewById(R.id.txtNum);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setPageMargin(10);
        mViewPager.setPageMarginDrawable(new ColorDrawable(Color.BLACK));
        mViewPager.setOffscreenPageLimit(1);
        Intent i = getIntent();
        try
        {
            ppt = new SlideShow(new File(i.getStringExtra("path")));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private void initializeSlideshow()
    {
        final Dimension pgsize = ppt.getPageSize();
        slide = ppt.getSlides();
        int slideCount;
        slideCount = slide.length;
        final ExecutorService es = Executors.newSingleThreadExecutor();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (mViewPager == null) {
                    return;
                }
                switch (msg.what) {
                    case 0: {
                        View v = (View) msg.obj;
                        v.invalidate();
                        int position = msg.arg1;
                        if (position == mViewPager.getCurrentItem()) {
                            setProgress(10000);
                        }
                    }
                    break;
                    case 1: {
                        int progress = msg.arg1;
                        int max = msg.arg2;
                        int p = (int) ((float) progress / max * 10000);
                        int position = (Integer) msg.obj;
                        if (position == 1) {
                            setProgressBarIndeterminate(false);
                        }
                        if (position == mViewPager.getCurrentItem()) {
                            if (position != 0 && progress == 0) {
                                setProgressBarIndeterminate(false);
                            }
                            setProgress(p);
                        }
                    }
                    break;
                    default:
                        break;
                }
            }
        };

        mPagerAdapter = new PagerAdapter() {
            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == ((ImageView) object);
            }

            @Override
            public int getCount() {
                return slide.length;
            }

            @Override
            public void startUpdate(View container) {
            }

            @Override
            public Object instantiateItem(View container, final int position) {
                if (position == mViewPager.getCurrentItem()) {
                    setProgressBarIndeterminate(true);
                }
                final ImageViewTouch imageView = new ImageViewTouch(PresenterActivity.this);
                imageView.setLayoutParams(new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT));
                imageView.setBackgroundColor(Color.BLACK);
                imageView.setFocusableInTouchMode(true);
                //interesting, this is the shit
                Bitmap bmp = Bitmap.createBitmap((int) pgsize.getWidth(), (int) pgsize.getHeight(), Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(bmp);
                Paint paint = new Paint();
                paint.setColor(android.graphics.Color.WHITE);
                paint.setFlags(Paint.ANTI_ALIAS_FLAG);
                canvas.drawPaint(paint);
                final Graphics2D graphics2d = new Graphics2D(canvas);
                final AtomicBoolean isCanceled = new AtomicBoolean(false);
                // render
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        slide[position].draw(graphics2d, isCanceled, handler,
                                position);
                        handler.sendMessage(Message.obtain(handler, 0,
                                position, 0, imageView));
                    }
                };
                Future<?> task = es.submit(runnable);
                imageView.setTag(task);
                imageView.setIsCanceled(isCanceled);
                imageView.setImageBitmapResetBase(bmp, true);

                ((ViewGroup) container).addView(imageView);
                //mCache.put(position, imageView);
                return imageView;
            }

            @Override
            public void destroyItem(View container, int position, Object object) {
                ImageViewTouch view = (ImageViewTouch) object;

                view.getCanceled().set(true);
                Future<?> task = (Future<?>) view.getTag();
                task.cancel(false);

                ((ViewGroup) container).removeView(view);

                BitmapDrawable bitmapDrawable = (BitmapDrawable) view
                        .getDrawable();
                if (!bitmapDrawable.getBitmap().isRecycled()) {
                    bitmapDrawable.getBitmap().recycle();
                }
                //mCache.remove(position);
            }

            @Override
            public void finishUpdate(View container) {
            }

            @Override
            public Parcelable saveState() {
                return null;
            }

            @Override
            public void restoreState(Parcelable state, ClassLoader loader) {
            }
        };
        mViewPager.setAdapter(mPagerAdapter);
    }

}
