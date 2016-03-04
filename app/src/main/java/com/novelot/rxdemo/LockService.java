package com.novelot.rxdemo;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class LockService extends Service {
    private static final String TAG = "novelot.lock";
    private BroadcastReceiver mBatInfoReceiver;

    public LockService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mBatInfoReceiver == null) {
              /*注册屏幕亮灭的广播监听*/
            final IntentFilter filter = new IntentFilter();
            // 屏幕灭屏广播
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            // 屏幕亮屏广播
            filter.addAction(Intent.ACTION_SCREEN_ON);
            // 屏幕解锁广播
            filter.addAction(Intent.ACTION_USER_PRESENT);
            // 当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
            // example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
            // 所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
            filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

            mBatInfoReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(final Context context, final Intent intent) {
                    Log.d(TAG, "onReceive");
                    String action = intent.getAction();

                    if (Intent.ACTION_SCREEN_ON.equals(action)) {
                        Log.d(TAG, "屏幕亮");
                        /*屏蔽系统锁屏*/
                        Log.d(TAG, "屏蔽系统锁屏");
                        KeyguardManager mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
                        KeyguardManager.KeyguardLock mKeyguardLock = mKeyguardManager.newKeyguardLock("my_lockscreen");
                        mKeyguardLock.disableKeyguard();
                        /*调用自己的锁屏*/
                        Log.d(TAG, "调用自己的锁屏");
                        Intent i = new Intent(LockService.this, LockActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        LockService.this.startActivity(i);

                    } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                        Log.d(TAG, "屏幕暗");
                    } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                        Log.d(TAG, "屏幕解锁");
                    } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
                        Log.i(TAG, "关机对话框");
                    }
                }
            };
            Log.d(TAG, "注册锁屏监听");
            registerReceiver(mBatInfoReceiver, filter);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBatInfoReceiver != null) {
            Log.d(TAG, "注销锁屏监听");
            unregisterReceiver(mBatInfoReceiver);
        }
    }
}
