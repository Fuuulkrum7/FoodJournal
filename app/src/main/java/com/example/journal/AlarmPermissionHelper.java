package com.example.journal;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LifecycleOwner;

public class AlarmPermissionHelper {
    private final Context context;
    private final AlarmManager alarmManager;
    private final ActivityResultLauncher<Intent> launcher;
    private Runnable onGranted;

    public AlarmPermissionHelper(Activity activity, ActivityResultRegistry registry, LifecycleOwner lifecycleOwner) {
        this.context = activity;
        this.alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);

        this.launcher = registry.register(
                "exact_alarm_launcher",
                lifecycleOwner,
                new ActivityResultContracts.StartActivityForResult(),
                result -> handleActivityResult()
        );
    }

    public void ensurePermission(Runnable onGranted) {
        this.onGranted = onGranted;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager == null || alarmManager.canScheduleExactAlarms()) {
            onGranted.run(); // разрешение уже есть
        } else {
            showPermissionDialog();
        }
    }

    public void handleActivityResult() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager == null) return;

        if (alarmManager.canScheduleExactAlarms() && onGranted != null) {
            onGranted.run();
        } else {
            showPermissionDialog(); // повторно
        }
    }

    private void showPermissionDialog() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            // Разрешение не нужно — сразу выполняем действие
            if (onGranted != null) onGranted.run();
            return;
        }

        new AlertDialog.Builder(context)
            .setTitle("Разрешение на точные уведомления")
            .setMessage("Чтобы напоминания приходили точно по времени, нужно разрешение \"Точные будильники\". Нажмите OK, выберите приложение и включите переключатель.")
            .setCancelable(false)
            .setPositiveButton("OK", (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                launcher.launch(intent);
            })
            .show();
    }

}
