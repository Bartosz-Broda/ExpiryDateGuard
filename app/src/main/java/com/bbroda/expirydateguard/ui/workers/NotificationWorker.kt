package com.bbroda.expirydateguard.ui.workers

import android.content.ContentValues
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.classes.NotificationHandler
import com.bbroda.expirydateguard.ui.classes.productdatabase.ProductsDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationWorker(private val appContext: Context, workerParameters: WorkerParameters) : Worker(appContext, workerParameters) {

    protected val scope = CoroutineScope(Dispatchers.Main)
    override fun doWork(): Result {

        try {
            scope.launch {
                Log.d(ContentValues.TAG, "run: NOTIFICATION SCOPE LANUCH")
                val database = ProductsDatabase.getDatabase(appContext)
                val productList = database.productDao().getAll()
                val today = LocalDate.now()
                Log.d(ContentValues.TAG, "run: NOTIFICATION today: $today")

                for(i in productList) {
                    Log.d(ContentValues.TAG, "run: NOTIFICATION LOOP. PRODUCT: ${i.name}")

                    if (i.expiryDate < today) {
                        Log.d(ContentValues.TAG, "run: NOTIFICATION EXPIRED PRODUCT")
                        /*val textContent = String.format(
                            ContextCompat.getString(
                                appContext,
                                R.string.notification_text_on_expiration
                            ),
                            i.name
                        )*/
                        val textContent = ContextCompat.getString(appContext,R.string.notification_text_on_expiration2)
                        val id = 1

                        Handler(Looper.getMainLooper()).post(Runnable {
                            NotificationHandler.createReminderNotification(appContext,textContent,id)
                        })


                    //Expiration date is within 3 days from now
                    }
                    if (i.expiryDate < today.plusDays(3) && i.expiryDate >= today) {
                        Log.d(ContentValues.TAG, "run: NOTIFICATION PRODUCT WILL EXPIRE")
                        /*val textContent = String.format(
                            ContextCompat.getString(
                                appContext,
                                R.string.notification_text_before_expiration
                            ),
                            i.name
                        )*/
                        val textContent = ContextCompat.getString(appContext,R.string.notification_text_before_expiration2)
                        val id = 2
                        Handler(Looper.getMainLooper()).post(Runnable {
                            NotificationHandler.createReminderNotification(appContext, textContent, id)
                            }
                        )


                    }
                }
            }



            return Result.success()
        }catch (e:Exception){
            Log.d(ContentValues.TAG, "doWork: $e")
            return Result.failure()
        }
    }

    companion object {
        fun schedule(appContext: Context, hourOfDay: Int, minute: Int, second: Int) {
            Log.d(ContentValues.TAG,"Reminder scheduling request received for $hourOfDay:$minute")
            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, second)
            }

            if (target.before(now)) {
                target.add(Calendar.DAY_OF_YEAR, 1)
            }

            Log.d(ContentValues.TAG,"Scheduling reminder notification for ${target.timeInMillis - System.currentTimeMillis()} ms from now")

            val TAG_WORKER = "Notification_Worker"
            val notificationRequest = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
                .addTag(TAG_WORKER)
                .setInitialDelay(target.timeInMillis - System.currentTimeMillis(), TimeUnit.MILLISECONDS).build()
            WorkManager.getInstance(appContext)
                .enqueueUniquePeriodicWork(
                    "reminder_notification_work",
                    ExistingPeriodicWorkPolicy.UPDATE,
                    notificationRequest
                )
        }
    }
}
