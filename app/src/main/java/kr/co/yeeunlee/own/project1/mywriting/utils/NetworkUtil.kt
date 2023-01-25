package kr.co.yeeunlee.own.project1.mywriting.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AlertDialog
import kr.co.yeeunlee.own.project1.mywriting.R

class NetworkUtil {

    companion object {
        @Suppress("DEPRECATION")    // Deprecation 경고 unused
        fun isConnectNetwork(
            context: Context,
            positive: () -> Unit,
            negative: () -> Unit
        ): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                connectivityManager?.let { cm ->
                    cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                        return when {
                            hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                            hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                            else -> false
                        }
                    }
                }
            } else {
                connectivityManager?.let { cm ->
                    cm.activeNetworkInfo?.run {
                        if (type == ConnectivityManager.TYPE_WIFI) {
                            return true
                        } else if (type == ConnectivityManager.TYPE_MOBILE) {
                            return false
                        }
                    }
                }
            }
            netWorkError(context, positive, negative)   // 연결 실패 시
            return false
        }

        private fun netWorkError(context: Context, positive: () -> Unit, negative: () -> Unit) {
            AlertDialog.Builder(context)
                .setMessage(context.getString(R.string.network_error))
                .setCancelable(false)
                .setPositiveButton(R.string.network_retry) { dialog, _ ->
                    positive()
                    dialog.cancel()
                }
                .setNegativeButton(R.string.network_finish) { dialog, _ ->
                    negative()
                    dialog.cancel()
                }
                .show()
                .setCanceledOnTouchOutside(false)
        }
    }
}