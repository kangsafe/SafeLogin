package com.iflytek.facedemo;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.FaceDetector;
import com.iflytek.facedemo.util.FaceRect;
import com.iflytek.facedemo.util.FaceUtil;
import com.iflytek.facedemo.util.ParseResult;

/**
 * 离线人脸检测示例
 * 该业务仅支持离线人脸检测SDK，请开发者前往<a href="http://www.xfyun.cn/">讯飞语音云</a>SDK下载界面，下载对应离线SDK
 *
 * @author iFlytek &nbsp;&nbsp;&nbsp;<a href="http://www.xfyun.cn/">讯飞语音云</a>
 */
public class OfflineFaceDemo extends Activity implements OnClickListener {
	private static final String TAG = OfflineFaceDemo.class.getSimpleName();
	
	private Bitmap mImage = null;
	private Toast mToast;
	private File mPictureFile;
	// FaceDetector对象，集成了离线人脸识别：人脸检测、视频流检测功能
	private FaceDetector mFaceDetector;
	// 人脸识别结果
	private FaceRect[] mFaces;
	
	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_offline_demo);
		
		findViewById(R.id.offline_pick).setOnClickListener(OfflineFaceDemo.this);
		findViewById(R.id.offline_camera).setOnClickListener(OfflineFaceDemo.this);
		findViewById(R.id.offline_detect).setOnClickListener(OfflineFaceDemo.this);
		
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		mFaceDetector = FaceDetector.createDetector(this, null);
	}

	@Override
	public void onClick(View view) {
		if( null == mFaceDetector ){
			/**
			 * 离线人脸检测功能需要单独下载支持离线人脸的SDK
			 * 请开发者前往语音云官网下载对应SDK
			 */
			// 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
			this.showTip( "创建对象失败，请确认 libmsc.so 放置正确，\n 且有调用 createUtility 进行初始化" );
			return;
		}
		
		switch (view.getId()) {
		case R.id.offline_pick:
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_PICK);
			startActivityForResult(intent, FaceUtil.REQUEST_PICTURE_CHOOSE);
			break;
		case R.id.offline_detect:
			if (null != mImage) {				
				// 启动图片人脸检测
				String result = mFaceDetector.detectARGB(mImage);
				Log.d(TAG, "result:"+result);
				// 解析人脸结果
				mFaces = ParseResult.parseResult(result);
				if (null != mFaces && mFaces.length > 0) {
					drawFaceRects(mFaces);
				} else {
					// 在无人脸的情况下，判断结果信息
					int errorCode = 0;
					JSONObject object;
					try {
						object = new JSONObject(result);
						errorCode = object.getInt("ret");
					} catch (JSONException e) {
					}
					// errorCode!=0，表示人脸发生错误，请根据错误处理
					if(ErrorCode.SUCCESS == errorCode) {
						showTip("没有检测到人脸");
					} else {
						showTip("检测发生错误，错误码："+errorCode);
					}
				}
			} else {
				showTip("请选择图片后再检测");
			}
			break;
		case R.id.offline_camera:
			// 设置相机拍照后照片保存路径
			mPictureFile = new File(Environment.getExternalStorageDirectory(), 
					"picture" + System.currentTimeMillis()/1000 + ".jpg");
			// 启动拍照,并保存到临时文件
			Intent mIntent = new Intent();
			mIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			mIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPictureFile));
			mIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
			startActivityForResult(mIntent, FaceUtil.REQUEST_CAMERA_IMAGE);
			break;

		default:
			break;
		}
	}
	
	private void drawFaceRects(FaceRect[] faces) {
		Paint paint = new Paint();
		paint.setColor(Color.YELLOW);
		paint.setStrokeWidth(Math.max(mImage.getWidth(), mImage.getHeight()) / 100f);
		paint.setStyle(Style.STROKE);

		Bitmap bitmap = Bitmap.createBitmap(mImage.getWidth(),
				mImage.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawBitmap(mImage, new Matrix(), null);
		
		for (FaceRect face: faces) {
			canvas.drawRect(face.bound, paint);
			
			if (null != face.point) {
				for (Point p: face.point) {
					canvas.drawPoint(p.x, p.y, paint);
				}
			}
		}
		
		((ImageView) findViewById(R.id.offline_img)).setImageBitmap(bitmap);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		
		String fileSrc = null;
		if (requestCode == FaceUtil.REQUEST_PICTURE_CHOOSE) {
			if ("file".equals(data.getData().getScheme())) {
				// 有些低版本机型返回的Uri模式为file
				fileSrc = data.getData().getPath();
			} else {
				// Uri模型为content
				String[] proj = {MediaStore.Images.Media.DATA};
				Cursor cursor = getContentResolver().query(data.getData(), proj,
						null, null, null);
				cursor.moveToFirst();
				int idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				fileSrc = cursor.getString(idx);
				cursor.close();
			}
			// 跳转到图片裁剪页面
			FaceUtil.cropPicture(this,Uri.fromFile(new File(fileSrc)));
		} else if (requestCode == FaceUtil.REQUEST_CAMERA_IMAGE) {
			if (null == mPictureFile) {
				showTip("拍照失败，请重试");
				return;
			}
			
			fileSrc = mPictureFile.getAbsolutePath();
			updateGallery(fileSrc);
			// 跳转到图片裁剪页面
			FaceUtil.cropPicture(this,Uri.fromFile(new File(fileSrc)));
		} 
		else if (requestCode == FaceUtil.REQUEST_CROP_IMAGE) {
			// 获取返回数据
			Bitmap bmp = data.getParcelableExtra("data");
			// 若返回数据不为null，保存至本地，防止裁剪时未能正常保存
			if(null != bmp){
				FaceUtil.saveBitmapToFile(OfflineFaceDemo.this, bmp);
			}

			// 获取图片保存路径
			fileSrc = FaceUtil.getImagePath(OfflineFaceDemo.this);
			// 获取图片的宽和高
			Options options = new Options();
			options.inJustDecodeBounds = true;
			mImage = BitmapFactory.decodeFile(fileSrc, options);

			// 适当压缩图片可以加快检测速度
			options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max(
					(double) options.outWidth / 1024f,
					(double) options.outHeight / 1024f)));
			options.inJustDecodeBounds = false;
			mImage = BitmapFactory.decodeFile(fileSrc, options);
			
			// 部分手机会对图片做旋转，这里检测旋转角度
			int degree = FaceUtil.readPictureDegree(fileSrc);
			if (degree != 0) {
				// 把图片旋转为正的方向
				mImage = FaceUtil.rotateImage(degree, mImage);
			}

			((ImageView) findViewById(R.id.offline_img)).setImageBitmap(mImage);
			// 清除上次人脸检测结果
			mFaces = null;
		}
	}
	
	private void updateGallery(String filename) {
		MediaScannerConnection.scanFile(this, new String[] {filename}, null,
				new MediaScannerConnection.OnScanCompletedListener() {
					
					@Override
					public void onScanCompleted(String path, Uri uri) {

					}
				});
	}

	@Override
	protected void onDestroy() {
		// 销毁对象
		if( null != this.mFaceDetector ){
			mFaceDetector.destroy();
		}
		
		super.onDestroy();
	}
	

	private void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
	}

	
}
