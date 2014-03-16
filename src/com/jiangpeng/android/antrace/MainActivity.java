package com.jiangpeng.android.antrace;
import java.io.File;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static int CAMERA_STATUS_CODE = 111;
    private static int EDIT_IMAGE_CODE = 122;
    private static int SELECT_PHOTO = 100;
    public static String PHOTO_FILE_TEMP_ = "__antrace.jpg";
    public static String TEMP_FOLDER = ".antraceTemp_"; 
    public static String FILE_NAME = "FILE_NAME"; 
	Button m_takePicture = null;
	Button m_selectPicture = null;
	Button m_about = null;
    protected String m_photoFile = "/sdcard/__antrace_tmp";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		m_takePicture = (Button)findViewById(R.id.take_picture);
		m_selectPicture = (Button)findViewById(R.id.select_picture);
		m_about = (Button)findViewById(R.id.about);
		
        OnClickListener takeListener = new TakePictureListener();
        m_takePicture.setOnClickListener(takeListener);
        
        OnClickListener selectListener = new SelectPictureListener();
        m_selectPicture.setOnClickListener(selectListener);

        /*
        OnClickListener aboutListener = new AboutListener();
        m_about.setOnClickListener(aboutListener);
        */

		m_photoFile = FileUtils.getRootFolder() + FileUtils.sep + TEMP_FOLDER + FileUtils.sep + PHOTO_FILE_TEMP_;
		FileUtils.checkAndCreateFolder(FileUtils.getRootFolder() + FileUtils.sep + TEMP_FOLDER);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
    class TakePictureListener implements OnClickListener
    {
        @Override
        public void onClick(View v)
        {
    	    Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
    	    i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(m_photoFile)));

           	try
           	{
           	    startActivityForResult(i, CAMERA_STATUS_CODE);
           	}
           	catch(ActivityNotFoundException err)
           	{
        	    Toast t = Toast.makeText(MainActivity.this, err.getLocalizedMessage(), Toast.LENGTH_SHORT);
          	    t.show();
           	}
        }
    } 

    class SelectPictureListener implements OnClickListener
    {
        @Override
        public void onClick(View v)
        {
        	Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        	photoPickerIntent.setType("image/*");
        	startActivityForResult(photoPickerIntent, SELECT_PHOTO);   
        }
    }
    
    /*
    class AboutListener implements OnClickListener
    {
        @Override
        public void onClick(View view)
        {
        	AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        	View v = getLayoutInflater().inflate(R.layout.about_dialog, (ViewGroup) findViewById(R.id.about_layout));
        	//TextView tv = (TextView) v.findViewById(R.id.appVersion);
        	String ver = getResources().getString(R.string.app_version, getResources().getString(R.string.app_name),
        			CommonUtils.getCurrentVersionName(MainActivity.this));
        	builder.setTitle(ver);
        	//tv.setText(ver);
        	builder.setView(v);
        	builder.setIcon(R.drawable.ic_launcher);
        	builder.setPositiveButton(android.R.string.ok,
        			new DialogInterface.OnClickListener()
        	{
        		@Override
        		public void onClick(DialogInterface dialog, int whichButton)
        		{
        		}
        	});
        	AlertDialog dialog = builder.create();
        	dialog.show();
        }
    }
    */
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == CAMERA_STATUS_CODE && resultCode == RESULT_OK)
        {
            super.onActivityResult(requestCode, resultCode, intent);
            return;
        }
        if(requestCode == SELECT_PHOTO && resultCode == RESULT_OK)
        {
            Uri selectedImage = intent.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            
            m_photoFile = filePath;
            launchPreviewActivity(m_photoFile);
        }
        super.onActivityResult(requestCode, resultCode, intent);
	}
	
	protected void launchPreviewActivity(String filename) {
		Intent i = new Intent();	    
		i.setClass(MainActivity.this, PreviewActivity.class);
	    i.putExtra(PreviewActivity.FILENAME, filename);
	    startActivityForResult(i, EDIT_IMAGE_CODE);

	}

}
