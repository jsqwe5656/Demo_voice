package zbf.demo_voice;

import android.Manifest;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * 测试授权界面
 */
@RuntimePermissions
public class PermissionsActivity extends AppCompatActivity
{
    Button btn_location,btn_voice,btn_camera,btn_ble;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);
        viewInit();
    }

    private void viewInit()
    {
        btn_location = (Button) findViewById(R.id.btn_location);
        btn_camera = (Button) findViewById(R.id.btn_camera);
        btn_voice = (Button) findViewById(R.id.btn_voice);
        btn_ble = (Button) findViewById(R.id.btn_ble);
        btn_location.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                PermissionsActivityPermissionsDispatcher.showLocationWithCheck(PermissionsActivity.this);
            }
        });
        btn_voice.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                PermissionsActivityPermissionsDispatcher.showVoiceWithCheck(PermissionsActivity.this);
            }
        });
    }

    @NeedsPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    void showLocation()
    {   //处理当用户允许该权限时需要处理的方法
        showToast("需要位置权限");
    }

    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    void showVoice()
    {
        showToast("需要录音权限");
    }

    @OnShowRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
    void showWhyLocation(PermissionRequest request)
    {
        showRationaleDialog(R.string.need_location,request);
    }

    @OnShowRationale(Manifest.permission.RECORD_AUDIO)
    void showWhyVoice(PermissionRequest request)
    {
        // 提示用户权限使用的对话框
        showRationaleDialog(R.string.need_voice,request);
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.RECORD_AUDIO})
    void onDenied()
    {
        showToast("不给权限用不了啊亲");
    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.RECORD_AUDIO})
    void onNotAsk()
    {
        showToast("好吧好吧你牛逼");
    }


    private void showRationaleDialog(@StringRes int messageResId, final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setPositiveButton("同意", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.proceed();                  //执行请求
                    }
                })
                .setNegativeButton("不同意", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
    }


    void showToast(String values)
    {
        Toast.makeText(PermissionsActivity.this,values,Toast.LENGTH_SHORT).show();
    }

    /**
     * 权限请求回调，提示用户之后，用户点击“允许”或者“拒绝”之后调用此方法
     * @param requestCode  定义的权限编码
     * @param permissions 权限名称
     * @param grantResults 允许/拒绝
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }
}
