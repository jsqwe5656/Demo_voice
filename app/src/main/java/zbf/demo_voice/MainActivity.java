package zbf.demo_voice;

import android.Manifest;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.carlos.voiceline.mylibrary.VoiceLineView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
{
    VoiceLineView voiceLineView;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        voiceLineView = (VoiceLineView) findViewById(R.id.voiceline);
        btn = (Button) findViewById(R.id.btn);
//        requestAlertWindowPermission();
        addDesktopIcon(this);
        //申请权限
//        requestPermission();
        checkPermissions();
        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(MainActivity.this,PermissionsActivity.class));
            }
        });
    }

    String TAG_zbf = "zbf";

    /**
     * 检查权限
     */
    private void checkPermissions()
    {
        int permissions_audio = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if (permissions_audio != PackageManager.PERMISSION_GRANTED)
        {
            //是否需要解释为何申明权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS))
            {
                Log.e(TAG_zbf, "123456");
            }
            else
            {
                //上下文，权限组，返回值
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        12138);
                Log.e(TAG_zbf, "654321");
            }
        }
        Log.e(TAG_zbf, permissions_audio + "");

    }

    /**
     * 申请权限
     */
    private void requestPermission()
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED)
            {
                //判断是否需要 向用户解释，为什么要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION))
                    Toast.makeText(this, "need to open location info for discovery bluetooth device in android6.0 version，otherwise find not！", Toast.LENGTH_LONG).show();

                //请求权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                return;
            }
            //检查AndroidManiFest中是否配置了WRITE_CONTACTS权限
            int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_CONTACTS);
            //若未配置该权限
            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED)
            {
                //申请配置该权限
                requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS},
                        123);
                //直接返回，不执行insertDummyContact()方法
                return;
            }
            //若配置了该权限，才能调用方法
            insertDummyContact();
            int hasWriteContactsPermission1 = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWriteContactsPermission1 != PackageManager.PERMISSION_GRANTED)
            {
                Activity activty = this;
                ActivityCompat.requestPermissions(activty, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        0);
                return;
            }
        }


    }

    private static final String TAG = "Contacts";

    private void insertDummyContact()
    {
        // Two operations are needed to insert a new contact.
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>(2);

        // 1、设置一个新的联系人
        ContentProviderOperation.Builder op =
                ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null);
        operations.add(op.build());

        // 1、为联系人设置姓名
        op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        "__DUMMY CONTACT from runtime permissions sample");
        operations.add(op.build());

        // 3、使用ContentResolver添加该联系人
        ContentResolver resolver = getContentResolver();
        try
        {
            resolver.applyBatch(ContactsContract.AUTHORITY, operations);
        }
        catch (RemoteException e)
        {
            Log.d(TAG, "Could not add a new contact: " + e.getMessage());
        }
        catch (OperationApplicationException e)
        {
            Log.d(TAG, "Could not add a new contact: " + e.getMessage());
        }
    }

    /**
     * 创建桌面图标
     */
    private void addDesktopIcon(Context context)
    {
        if (!ShortcutUtils.hasInstallShortcut(context))
        {
            //启动应用后以广播的形式添加图标
            sendBroadcast(ShortcutUtils.getShortcutToDesktopIntent(this));
        }
    }

    private static final int REQUEST_CODE = 1;

    /**
     * 添加悬浮窗权限
     */
    private void requestAlertWindowPermission()
    {
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
        {
            intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (Settings.canDrawOverlays(this))
                {
                    Log.i("zbf", "onActivityResult granted");
                }
            }
        }
    }

    /**
     * 处理权限授权回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        Log.e(TAG_zbf, requestCode + "," + permissions + "," + grantResults);
        switch (requestCode)
        {
            case 12138:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {

                }
                else
                {

                }
                return;
            }

        }

    }
}
