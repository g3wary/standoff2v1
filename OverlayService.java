package com.standoff2.cheat;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class OverlayService extends Service {
    private WindowManager wm;
    private LinearLayout overlayLayout;
    private WindowManager.LayoutParams params;
    private Handler handler = new Handler();
    private Runnable updateRunnable;
    private MemoryScanner scanner;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        scanner = new MemoryScanner();

        overlayLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.overlay_menu, null);
        
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 50;
        params.y = 100;

        overlayLayout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                params.x = (int) event.getRawX() - overlayLayout.getWidth() / 2;
                params.y = (int) event.getRawY() - overlayLayout.getHeight() / 2;
                wm.updateViewLayout(overlayLayout, params);
            }
            return true;
        });

        wm.addView(overlayLayout, params);

        Button btnToggle = overlayLayout.findViewById(R.id.btnToggle);
        Button btnClose = overlayLayout.findViewById(R.id.btnClose);

        btnToggle.setOnClickListener(v -> {
            if (scanner.isEnabled()) {
                scanner.disable();
                btnToggle.setText("ВКЛ");
                Toast.makeText(this, "WH выключен", Toast.LENGTH_SHORT).show();
            } else {
                scanner.enable();
                btnToggle.setText("ВЫКЛ");
                Toast.makeText(this, "WH включен", Toast.LENGTH_SHORT).show();
            }
        });

        btnClose.setOnClickListener(v -> stopSelf());

        updateRunnable = () -> {
            if (scanner.isEnabled() && scanner.isGameProcessFound()) {
                scanner.scanAndDraw();
            }
            handler.postDelayed(this::updateRunnable, 50);
        };
        handler.post(updateRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateRunnable);
        scanner.disable();
        if (overlayLayout != null) wm.removeView(overlayLayout);
    }
}
