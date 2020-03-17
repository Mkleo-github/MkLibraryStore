package com.mkleo.librarys.permissions;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import com.mkleo.helper.MkLog;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

/**
 * des:
 * by: Mk.leo
 * date: 2019/5/27
 */
public class EasyPermissionActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int REQUEST_CODE = 0x29;
    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();
    }

    /**
     * 请求权限
     */
    private void requestPermissions() {
        if (EasyPermissions.hasPermissions(this, PERMISSIONS)) {
            //所有的权限都通过
            onAllPermissionsAllowed();
        } else {
            //用户拒绝了权限
            //正常情况（没有勾选《拒绝后不再询问》）会先弹下面这个框一，点击确定会出现系统的框二
            //非正常情况（有勾选《拒绝后不再询问》）框一和系统的框二都不会出现
            //无论哪种情况，最终，都会根据用户的选择同意还是拒绝，而回调对应的方法；（勾选过《拒绝后不再询问》，这种情况也是属于拒绝，所以他也会走拒绝的回调）
            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(this, REQUEST_CODE, PERMISSIONS)
                            .setRationale("为了不影响您的使用,需要以下权限")
                            .build()
            );
        }
    }

    /**
     * 所有权限通过,带有这个注释的方法，
     * 会在某一次请求的所有权限都通过后，才回调
     */
    @AfterPermissionGranted(REQUEST_CODE)
    private void onAllPermissionsAllowed() {
        MkLog.print("所有权限通过");
    }

    /**
     * 某些权限被拒绝
     */
    private void onSomePermissionsDenied() {
        MkLog.print("某些权限被拒绝!");
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 把请求权限的操作转交给EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    /**
     * 一次请求中，只要有一个权限允许了，就会走这个方法
     *
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        for (String permission : perms) {
            MkLog.print("通过权限:" + permission);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        for (String permission : perms) {
            MkLog.print("拒绝权限:" + permission);
        }

        //如果曾经有勾选《拒绝后不再询问》，则会进入下面这个条件
        //建议做一个判断，判断用户是不是刚刚勾选的《拒绝后不再询问》，如果是，就不做下面这个判断，而只进行相应提示，这样就可以避免再一次弹框，影响用户体验
        //否则就是用户可能在之前曾经勾选过《拒绝后不再询问》，那就可以用下面这个判断，强制弹出一个对话框
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            //但是这个api有个问题，他会显示一个对话框，但是这个对话框，点空白区域是可以取消的，如果用户点了空白区域，你就没办法进行后续操作了
            new AppSettingsDialog.Builder(this)
                    .setTitle("温馨提示")
                    .setRationale("为了不影响您的使用体验,请在\"设置\"中开启所需要的权限!")
                    .build().show();
        } else {
            //否则权限不通过
            onSomePermissionsDenied();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //EasyPermissions会有一个默认的请求码，根据这个请求码，就可以判断是不是从APP的设置界面过来的
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            //在这儿，你可以再对权限进行检查，从而给出提示，或进行下一步操作
            if (EasyPermissions.hasPermissions(this, PERMISSIONS)) {
                onAllPermissionsAllowed();
            } else {
                onSomePermissionsDenied();
            }
        }
    }

}
