package dev.ragnarok.fenrir;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.umerov.rlottie.RLottieImageView;

import dev.ragnarok.fenrir.domain.InteractorFactory;
import dev.ragnarok.fenrir.link.LinkHelper;
import dev.ragnarok.fenrir.settings.Settings;
import dev.ragnarok.fenrir.util.CustomToast;
import dev.ragnarok.fenrir.util.RxUtils;
import dev.ragnarok.fenrir.util.Utils;

public class CheckUpdate {
    private static boolean isFullVersionTelegram(Context context) {
        if (Settings.get().accounts().getCurrent() != 572488303 && !Utils.isValueAssigned(Settings.get().accounts().getCurrent(), Utils.donate_users)) {
            MaterialAlertDialogBuilder dlgAlert = new MaterialAlertDialogBuilder(context);

            View view = LayoutInflater.from(context).inflate(R.layout.donate_alert_telegram, null);
            dlgAlert.setTitle(R.string.info);
            dlgAlert.setIcon(R.drawable.client_round);
            dlgAlert.setCancelable(true);
            dlgAlert.setView(view);
            dlgAlert.show();
            return false;
        }
        return true;
    }

    public static boolean isFullVersionPropriety(Context context) {
        if (Constants.IS_DONATE == 2) {
            return isFullVersionTelegram(context);
        }
        return isFullVersion(context);
    }

    public static boolean isFullVersion(Context context) {
        if (Settings.get().accounts().getCurrent() != 572488303 && !Utils.isValueAssigned(Settings.get().accounts().getCurrent(), Utils.donate_users) && Constants.IS_DONATE == 0) {
            MaterialAlertDialogBuilder dlgAlert = new MaterialAlertDialogBuilder(context);

            View view = LayoutInflater.from(context).inflate(R.layout.donate_alert, null);
            view.findViewById(R.id.item_donate).setOnClickListener(v -> LinkHelper.openLinkInBrowser(context, "https://play.google.com/store/apps/details?id=dev.ragnarok.fenrir_full"));
            RLottieImageView anim = view.findViewById(R.id.lottie_animation);
            anim.setAutoRepeat(true);
            anim.setAnimation(R.raw.google_store, Utils.dp(200), Utils.dp(200));
            anim.playAnimation();

            dlgAlert.setTitle(R.string.info);
            dlgAlert.setIcon(R.drawable.client_round);
            dlgAlert.setCancelable(true);
            dlgAlert.setView(view);
            dlgAlert.show();
            return false;
        }
        return true;
    }

    public static void isDonated(Activity context, int account_id) {
        Utils.donate_users.clear();
        Utils.donate_users.addAll(Settings.get().other().getDonates());

        //noinspection ResultOfMethodCallIgnored
        InteractorFactory.createUpdateToolInteractor().get_update_info()
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(t -> {
                    if (!Utils.isEmpty(t.donates)) {
                        Utils.donate_users.clear();
                        Utils.donate_users.addAll(t.donates);
                        Settings.get().other().registerDonatesId(Utils.donate_users);
                    }
                    MaterialAlertDialogBuilder dlgAlert = new MaterialAlertDialogBuilder(context);
                    boolean isDon = (Utils.isValueAssigned(account_id, Utils.donate_users) || account_id == 572488303);
                    View view = LayoutInflater.from(context).inflate(R.layout.is_donate_alert, null);
                    ((TextView) view.findViewById(R.id.item_status)).setText(isDon ? R.string.button_yes : R.string.button_no);
                    RLottieImageView anim = view.findViewById(R.id.lottie_animation);
                    anim.setAutoRepeat(true);
                    anim.setAnimation(isDon ? R.raw.is_donated : R.raw.is_not_donated, Utils.dp(200), Utils.dp(200));
                    anim.playAnimation();

                    dlgAlert.setTitle(R.string.info);
                    dlgAlert.setIcon(R.drawable.client_round);
                    dlgAlert.setCancelable(true);
                    dlgAlert.setView(view);
                    dlgAlert.show();
                }, e -> {
                    Utils.showErrorInAdapter(context, e);
                });
    }

    private static boolean isSafe(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                return "com.android.vending".equals(context.getPackageManager().getInstallSourceInfo(Constants.APK_ID).getInitiatingPackageName());
            } catch (PackageManager.NameNotFoundException ignored) {
                return false;
            }
        } else {
            try {
                return "com.android.vending".equals(context.getPackageManager().getInstallerPackageName(Constants.APK_ID));
            } catch (IllegalArgumentException ignored) {
                return false;
            }
        }
    }

    private static void checkInstall(Activity context) {
        if (Constants.IS_DONATE != 2 && !isSafe(context)) {
            if (!HelperSimple.INSTANCE.needHelp(HelperSimple.NOT_GP_HELPER, 2)) {
                return;
            }
            Settings.get().ui().setMainTheme("red");
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.error)
                    .setCancelable(false)
                    .setMessage(R.string.not_gp)
                    .setPositiveButton(R.string.button_ok, (dialog, which) -> context.recreate());
            if (Settings.get().other().isDeveloper_mode()) {
                CustomToast.CreateCustomToast(context).showToastError(context.getPackageManager().getInstallerPackageName(Constants.APK_ID));
            }
            builder.create().show();
        }
    }

    public static void Do(Activity context, int account_id) {
        Utils.donate_users.clear();
        Utils.donate_users.addAll(Settings.get().other().getDonates());

        //noinspection ResultOfMethodCallIgnored
        InteractorFactory.createUpdateToolInteractor().get_update_info()
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(t -> {
                    if (!Utils.isEmpty(t.donates)) {
                        Utils.donate_users.clear();
                        Utils.donate_users.addAll(t.donates);
                        Settings.get().other().registerDonatesId(Utils.donate_users);
                    }

                    if ((t.apk_version <= Constants.VERSION_APK && Constants.APK_ID.equals(t.app_id)) || !Settings.get().other().isAuto_update() || Constants.IS_DONATE != 2) {
                        checkInstall(context);
                        return;
                    }
                    View update = View.inflate(context, R.layout.dialog_update, null);
                    MaterialButton doUpdate = update.findViewById(R.id.item_view_latest);
                    doUpdate.setOnClickListener(v -> LinkHelper.openUrl(context, account_id, "https://github.com/umerov1999/Fenrir-for-VK/releases/latest"));
                    ((TextView) update.findViewById(R.id.item_latest_info)).setText(t.changes);

                    AlertDialog dlg = new MaterialAlertDialogBuilder(context)
                            .setTitle("Обновление клиента")
                            .setView(update)
                            .setPositiveButton(R.string.close, null)
                            .setCancelable(true)
                            .create();
                    dlg.show();
                }, e -> {
                    Utils.showErrorInAdapter(context, e);
                    checkInstall(context);
                });
    }
}
