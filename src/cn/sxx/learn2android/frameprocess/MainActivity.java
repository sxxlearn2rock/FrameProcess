package cn.sxx.learn2android.frameprocess;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements CvCameraViewListener2
{
	 private static final String  TAG              = "CallCamera::MainAc";
	
	  private CameraBridgeViewBase mOpenCvCameraView;
//	  private boolean mIsJavaCamera = true;
//	  private MenuItem mItemSwitchCamera = null;
	  private FrameLayout mainLayout;
	  private TextView infoTv;
	  private Button mBtn;
	 
//	  private DrawView drawView;
	  private Mat mRgba;
	  private boolean	 isProcess = false;
    
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) 
    {
        @Override
        public void onManagerConnected(int status) {
            switch (status) 
            {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                 //   mCameraView.setOnTouchListener(MainActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    
    public MainActivity()
	{
    	Log.i(TAG, "Instantiated new " + this.getClass());
	}
    
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//as long as this window is visible to the user, keep the device's screen turned on and bright.
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		setContentView(R.layout.activity_main);
	
		infoTv = (TextView) findViewById(R.id.info_tv);
		mainLayout = (FrameLayout) findViewById(R.id.main_layout);
		mainLayout.setOnTouchListener(new OnTouchListener()
		{	
			DrawView rectView = new DrawView(MainActivity.this);
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{	
				mainLayout.removeView(rectView);
//				rectView.invalidate();
				rectView.setRectCenter(event.getX(), event.getY());
				mainLayout.addView(rectView);// »­¿ò
				infoTv.setText("´¥Ãþ×ø±ê x:" + event.getX() + " y:" + event.getY());
				return false;
			}
		});
		
//		  if (mIsJavaCamera)
		      mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.java_surface_view);
//		  else
//		      mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.native_surface_view);
		  
		 mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		 mOpenCvCameraView.setCvCameraViewListener(this);
		 
		 mBtn = (Button) findViewById(R.id.buttonGray);
		 mBtn.setOnClickListener(new OnClickListener()
		{			
			@Override
			public void onClick(View v)
			{
				isProcess = !isProcess;				
			}
		});
	}	
 
	@Override
	public void onCameraViewStarted(int width, int height)
	{
		mRgba = new Mat(height, width, CvType.CV_8UC4);		
	}


	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame)
	{
		if(isProcess)
		{
			
		    Imgproc.cvtColor(inputFrame.gray(), mRgba, Imgproc.COLOR_GRAY2RGBA, 4);			
		}

		   else
		     mRgba = inputFrame.rgba();
		   return mRgba;
	}
	
	@Override
	public void onCameraViewStopped()
	{
		mRgba.release();		
	}
	    
	  @Override
	    public void onPause()
	    {
	        super.onPause();
	        if (mOpenCvCameraView != null)
			{
	        	mOpenCvCameraView.disableView();
			}
	    }

	    @Override
	    public void onResume()
	    {
	        super.onResume();
	        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_10, this, mLoaderCallback);
	    }

	    public void onDestroy() 
	    {
	        super.onDestroy();
	        if (mOpenCvCameraView != null)
			{
	        	mOpenCvCameraView.disableView();
			}
	    }	    	    
}

class DrawView extends View
{
	private Rect rect;
	private Paint painter;
	private final int halfLength = 60;
	
	public DrawView(Context context)
	{	
		super(context);
		rect = new Rect();
		painter = new Paint();
		painter.setStyle(Paint.Style.STROKE);
		painter.setColor(Color.GREEN);
		painter.setStrokeWidth(8);
	}

	public void setRectCenter(float x, float y)
	{
		rect.set((int)x-halfLength, (int)y-halfLength, 
					(int)x+halfLength, (int)y+halfLength);
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{		
		super.onDraw(canvas);
		canvas.drawRect(rect, painter);
	}	
}
