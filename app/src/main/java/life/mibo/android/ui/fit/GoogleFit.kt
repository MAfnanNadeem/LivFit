/*
 *  Created by Sumeet Kumar on 6/27/20 7:15 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/27/20 7:15 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.fit

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessActivities
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.*
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.fitness.result.SessionReadResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import life.mibo.hardware.core.Logger
import org.threeten.bp.LocalDate
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class GoogleFit(val fragment: Fragment) {

    companion object {

        fun printData(dataReadResult: DataReadResponse) {
            // [START parse_read_data_result]
            // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
            // as buckets containing DataSets, instead of just DataSets.
            val dateFormat: DateFormat = DateFormat.getTimeInstance()
            if (dataReadResult.buckets.size > 0) {
                log("Number of returned buckets of DataSets is: " + dataReadResult.buckets.size)
                for (bucket in dataReadResult.buckets) {
                    val dataSets = bucket.dataSets
                    for (dataSet in dataSets) {
                        log("Data returned for Data type: " + dataSet.dataType.name)

                        for (dp in dataSet.dataPoints) {
                            log("DataBucket point:")
                            log("\tType: " + dp.dataType.name)
                            log("\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)))
                            log("\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)))
                            for (field in dp.dataType.fields) {
                                log(
                                    "\tField: " + field.name.toString() + " Value: " + dp.getValue(
                                        field
                                    )
                                )
                            }
                        }
                    }
                }
            }
            if (dataReadResult.dataSets.size > 0) {
                log("Number of returned DataSets is: " + dataReadResult.dataSets.size)
                for (dataSet in dataReadResult.dataSets) {
                    for (dp in dataSet.dataPoints) {
                        log("DataSet point:")
                        log("\tType: " + dp.dataType.name)
                        log("\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)))
                        log("\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)))
                        for (field in dp.dataType.fields) {
                            log("\tField: " + field.name.toString() + " Value: " + dp.getValue(field))
                        }
                    }
                }
            }
            // [END parse_read_data_result]
        }

        fun log(msg: String?) {
            Logger.e("GoogleFit", msg)
        }
    }

    fun isConnected(): Boolean {
        return try {
            val fit = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .build()
            GoogleSignIn.hasPermissions(
                GoogleSignIn.getLastSignedInAccount(fragment.requireContext()), fit
            )
        } catch (e: java.lang.Exception) {
            false
        }
    }

    fun isConnected(type: DataType): Boolean {
        val fit = FitnessOptions.builder().addDataType(type).build()
        return GoogleSignIn.hasPermissions(
            GoogleSignIn.getLastSignedInAccount(fragment.requireContext()),
            fit
        )
    }

    fun connect(type: DataType, listener: FitnessHelper.Listener<Int>?) {
        tempListener = listener
        val fit = FitnessOptions.builder().addDataType(type).build()
        GoogleSignIn.requestPermissions(
            fragment,
            FitnessHelper.GOOGLE_REQUEST_CODE,
            GoogleSignIn.getLastSignedInAccount(fragment.requireContext()),
            fit
        )
    }

    fun connect() {

        if (!isConnected()) {
            val fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .build()
            GoogleSignIn.requestPermissions(
                fragment,
                FitnessHelper.GOOGLE_REQUEST_CODE,
                GoogleSignIn.getLastSignedInAccount(fragment.requireContext()),
                fitnessOptions
            )
        }

        //navigate(GOOGLE_FIT, null)
        //201589375301-6mfkekfog0lhdo3813f0j206g6ti9sn7.apps.googleusercontent.com
        //201589375301-nmuvhdnos8pets17tb18ju12n1h1hslf.apps.googleusercontent.com


    }

    var tempListener: FitnessHelper.Listener<Int>? = null
    fun connectAndSubscribe(listener: FitnessHelper.Listener<Int>?) {
        if (isConnected()) {
            subscribe(listener = listener)
        } else {
            tempListener = listener
            val fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .build()
            GoogleSignIn.requestPermissions(
                fragment,
                FitnessHelper.GOOGLE_REQUEST_CODE,
                GoogleSignIn.getLastSignedInAccount(fragment.requireContext()),
                fitnessOptions
            )
        }
    }

    fun subscribe(
        type: DataType = DataType.TYPE_STEP_COUNT_CUMULATIVE,
        listener: FitnessHelper.Listener<Int>?
    ) {
        Fitness.getRecordingClient(
            fragment.requireContext(),
            GoogleSignIn.getLastSignedInAccount(fragment.requireContext())!!
        ).subscribe(type).addOnCompleteListener {
            listener?.onComplete(it.isSuccessful, 0, it?.exception)
        }
    }

    fun getDataSource(title: String): DataSource {
        return DataSource.Builder()
            .setAppPackageName(fragment.context)
            .setDataType(DataType.TYPE_WORKOUT_EXERCISE)
            .setDataType(DataType.TYPE_ACTIVITY_SEGMENT)
            .setDataType(DataType.TYPE_HEART_RATE_BPM)
            .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .setDataType(DataType.TYPE_MOVE_MINUTES)
            .setDataType(DataType.TYPE_CALORIES_EXPENDED)
            .setStreamName("MI.BO. EMS ($title)")
            .setType(DataSource.TYPE_RAW)
            .build()
    }

    fun subscribeWithSession(
        listener: FitnessHelper.Listener<Int>?,
        header: String?,
        duration: String?,
        time: Long,
        activity: String = FitnessActivities.CIRCUIT_TRAINING
    ) {

        if (!isConnected()) {
            return
        }
        val fit = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .addDataType(DataType.TYPE_WORKOUT_EXERCISE)
            .addDataType(DataType.TYPE_ACTIVITY_SEGMENT)
            .addDataType(DataType.TYPE_HEART_RATE_BPM)
            .addDataType(DataType.TYPE_MOVE_MINUTES)
            .addDataType(DataType.TYPE_CALORIES_EXPENDED)
            .build()


        if (GoogleSignIn.hasPermissions(
                GoogleSignIn.getLastSignedInAccount(fragment.requireContext()), fit
            )
        ) {
            val dataSource: DataSource = DataSource.Builder()
                .setAppPackageName(fragment.context)
                .setDataType(DataType.TYPE_WORKOUT_EXERCISE)
                .setDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                .setDataType(DataType.TYPE_HEART_RATE_BPM)
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setDataType(DataType.TYPE_MOVE_MINUTES)
                .setDataType(DataType.TYPE_CALORIES_EXPENDED)
                .setStreamName("MI.BO. EMS ($header)")
                .setType(DataSource.TYPE_RAW)
                .build()

            Fitness.getRecordingClient(
                fragment.requireContext(),
                GoogleSignIn.getLastSignedInAccount(fragment.requireContext())!!
            ).subscribe(dataSource).addOnCompleteListener {
                listener?.onComplete(it.isSuccessful, 0, it?.exception)
                //val time = System.currentTimeMillis()
                val date_ = LocalDate.now()
                val session: Session = Session.Builder()
                    .setName("MI.BO. EMS Session")
                    .setIdentifier("$time")
                    .setActivity(activity)
                    .setDescription("MI.BO. EMS Session - $header ($duration) on ${date_.toString()}")
                    .setStartTime(time, TimeUnit.MILLISECONDS)
                    .build()


                Fitness.getSessionsClient(
                    fragment.requireContext(),
                    GoogleSignIn.getLastSignedInAccount(fragment.requireContext())!!
                ).startSession(session)
            }
        }

    }

    fun unsubscribeWithSession(
        listener: FitnessHelper.Listener<Int>?, header: String?, time: Long
    ) {
        if (!isConnected()) {
            return
        }

        try {

            val dataSource: DataSource = DataSource.Builder()
                .setAppPackageName(fragment.context)
                .setDataType(DataType.TYPE_WORKOUT_EXERCISE)
                .setDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                .setDataType(DataType.TYPE_HEART_RATE_BPM)
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setDataType(DataType.TYPE_MOVE_MINUTES)
                .setDataType(DataType.TYPE_CALORIES_EXPENDED)
                .setStreamName("MI.BO. EMS ($header)")
                .setType(DataSource.TYPE_RAW)
                .build()

            Fitness.getRecordingClient(
                fragment.requireContext(),
                GoogleSignIn.getLastSignedInAccount(fragment.requireContext())!!
            ).unsubscribe(dataSource).addOnCompleteListener {
                listener?.onComplete(it.isSuccessful, 0, it?.exception)
            }


        } catch (e: Exception) {

        }

        try {
            Fitness.getSessionsClient(
                fragment.requireContext(),
                GoogleSignIn.getLastSignedInAccount(fragment.requireContext())!!
            ).stopSession("$time");
        } catch (e: java.lang.Exception) {

        }
    }

    fun scanBle() {
        val bleScanCallbacks: BleScanCallback = object : BleScanCallback() {
            override fun onScanStopped() {

            }

            override fun onDeviceFound(p0: BleDevice?) {
            }

        }

        val response = Fitness.getBleClient(this_(), GoogleSignIn.getLastSignedInAccount(this_())!!)
            .startBleScan(listOf(DataType.TYPE_STEP_COUNT_DELTA), 1000, bleScanCallbacks)
    }

    fun scanSensors(
        successListener: OnSuccessListener<List<DataSource>>,
        failureListener: OnFailureListener
    ) {
        Fitness.getSensorsClient(
            this_(),
            GoogleSignIn.getLastSignedInAccount(this_())!!
        ).findDataSources(
            DataSourcesRequest.Builder()
                .setDataTypes(DataType.TYPE_LOCATION_SAMPLE)
                .setDataTypes(DataType.TYPE_WEIGHT)
                .setDataTypes(DataType.TYPE_STEP_COUNT_DELTA)
                .setDataSourceTypes(DataSource.TYPE_RAW)
                .build()
        ).addOnSuccessListener(successListener).addOnFailureListener(failureListener)

//        for (dataSource in dataSources) {
//            Log.i(TAG, "Data source found: $dataSource")
//            Log.i(TAG, "Data Source type: " + dataSource.dataType.name)
//
//            // Let's register a listener to receive Activity data!
//            if (dataSource.dataType.equals(DataType.TYPE_LOCATION_SAMPLE)
//                && mListener == null
//            ) {
//                Log.i(TAG, "Data source for LOCATION_SAMPLE found!  Registering.")
//                registerFitnessDataListener(
//                    dataSource,
//                    DataType.TYPE_LOCATION_SAMPLE
//                )
//            }
//        }
    }


    fun this_() = fragment.requireContext()

    fun unsubscribe(
        type: DataType = DataType.TYPE_STEP_COUNT_CUMULATIVE,
        listener: FitnessHelper.Listener<Int>?
    ) {
        Fitness.getRecordingClient(
            fragment.requireContext(),
            GoogleSignIn.getLastSignedInAccount(fragment.requireContext())!!
        ).unsubscribe(type).addOnCompleteListener {
            listener?.onComplete(it.isSuccessful, 0, it?.exception)
        }
    }

    fun readDailySteps(listener: FitnessHelper.Listener<Int>?) {
        Fitness.getHistoryClient(
            fragment.requireContext(),
            GoogleSignIn.getLastSignedInAccount(fragment.requireContext())!!
        )
            .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener { dataSet ->
                val total =
                    if (dataSet.isEmpty) 0 else dataSet.dataPoints[0]
                        .getValue(Field.FIELD_STEPS).asInt()
                listener?.onComplete(true, total, null)
                //Log.i(TAG, "Total steps: $total")
            }
            .addOnFailureListener { e ->
                listener?.onComplete(false, null, e)
            }
    }

    fun readData(listener: FitnessHelper.Listener<Int>?) {
        Fitness.getHistoryClient(
            fragment.requireContext(),
            GoogleSignIn.getLastSignedInAccount(fragment.requireContext())!!
        )
            .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener { dataSet ->
                val total =
                    if (dataSet.isEmpty) 0 else dataSet.dataPoints[0]
                        .getValue(Field.FIELD_STEPS).asInt()
                listener?.onComplete(true, total, null)
                //Log.i(TAG, "Total steps: $total")
            }
            .addOnFailureListener { e ->
                listener?.onComplete(false, null, e)
            }
    }

    fun readHistoryData(
        startTime: Date,
        endTime: Date,
        completeListener: OnSuccessListener<DataReadResponse>
    ): Task<DataReadResponse?>? {
        return readHistoryData(startTime.time, endTime.time, completeListener)
    }

    fun readHistoryData(
        startTime: Long,
        endTime: Long,
        completeListener: OnSuccessListener<DataReadResponse>
    ): Task<DataReadResponse?>? {
        // Analogous to a "Group By" in SQL, defines how data should be aggregated.
        // bucketByTime allows for a time span, whereas bucketBySession would allow
        // bucketing by "sessions", which would need to be defined in code.

        val readRequest =
            DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS).build()
        // Invoke the History API to fetch the data with the query
        return Fitness.getHistoryClient(
            fragment.requireContext(),
            GoogleSignIn.getLastSignedInAccount(fragment.requireContext())!!
        ).readData(readRequest).addOnSuccessListener(completeListener).addOnFailureListener { e ->
            log("There was a problem reading the data. $e")
        }
    }

    fun readyWeekly(completeListener: OnSuccessListener<DataReadResponse>): Task<DataReadResponse?>? {
        val cal = Calendar.getInstance()
        val now = Date()
        cal.time = now
        val endTime = cal.timeInMillis
        cal.add(Calendar.WEEK_OF_YEAR, -1)
        val startTime = cal.timeInMillis
        log("startTime $startTime")
        log("endTime $endTime")
        val readRequest =
            DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS).build()
        // Invoke the History API to fetch the data with the query
        return Fitness.getHistoryClient(
            fragment.requireContext(),
            GoogleSignIn.getLastSignedInAccount(fragment.requireContext())!!
        ).readData(readRequest).addOnSuccessListener(completeListener).addOnFailureListener { e ->
            log("There was a problem reading the data. $e")
        }
    }

    fun readyMonthly(completeListener: OnSuccessListener<DataReadResponse>): Task<DataReadResponse?>? {
        val cal = Calendar.getInstance()
        val now = Date()
        cal.time = now
        val endTime = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, -30)
        val startTime = cal.timeInMillis
        log("startTime $startTime")
        log("endTime $endTime")
        val readRequest =
            DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS).build()
        // Invoke the History API to fetch the data with the query
        return Fitness.getHistoryClient(
            fragment.requireContext(),
            GoogleSignIn.getLastSignedInAccount(fragment.requireContext())!!
        ).readData(readRequest).addOnSuccessListener(completeListener).addOnFailureListener { e ->
            log("There was a problem reading the data. $e")
        }
    }

    fun getSessions(
        type: DataType,
        completeListener: OnSuccessListener<SessionReadResponse>
    ): Task<SessionReadResponse> {
        log("SessionReadResponse getSession type $type")
        val cal = Calendar.getInstance()
        val now = Date()
        cal.time = now
        val endTime = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, -30)
        val startTime = cal.timeInMillis
        log("startTime $startTime")
        log("endTime $endTime")

        val readRequest = SessionReadRequest.Builder()
            .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
            .read(type)
            .build()

        return Fitness.getSessionsClient(
            fragment.requireContext(),
            GoogleSignIn.getLastSignedInAccount(fragment.requireContext())!!
        ).readSession(readRequest).addOnSuccessListener(completeListener)
            .addOnFailureListener { log("SessionReadResponse getSession Failed to read session " + it.message) }

        // Get a list of the sessions that match the criteria to check the result.
//        val sessions =
//            sessionReadResponse.sessions
//        Log.i(
//            TAG, "Session read was successful. Number of returned sessions is: "
//                    + sessions.size
//        )
//        for (session in sessions) {
//            // Process the session
//            dumpSession(session)
//
//            // Process the data sets for this session
//            val dataSets =
//                sessionReadResponse.getDataSet(session)
//            for (dataSet in dataSets) {
//                dumpDataSet(dataSet)
//            }
//        }
    }


    fun saveSessions(
        completeListener: OnSuccessListener<SessionReadResponse>
    ) {
//        log("SessionReadResponse getSession type $type")
//        val cal = Calendar.getInstance()
//        val now = Date()
//        cal.time = now
//        val endTime = cal.timeInMillis
//        cal.add(Calendar.DAY_OF_YEAR, -30)
//        val startTime = cal.timeInMillis
//        log("startTime $startTime")
//        log("endTime $endTime")
//
//        val readRequest = SessionReadRequest.Builder()
//            .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
//            .read(type)
//            .build()
//
//        Fitness.getSessionsClient(
//            fragment.requireContext(),
//            GoogleSignIn.getLastSignedInAccount(fragment.requireContext())!!
//        )
//            .insertSession(insertRequest)
//            .addOnSuccessListener { // At this point, the session has been inserted and can be read.
//                Log.i(TAG, "Session insert was successful!")
//            }
//            .addOnFailureListener { e ->
//                Log.i(
//                    TAG, "There was a problem inserting the session: " +
//                            e.localizedMessage
//                )
//            }

    }


    private fun insertData(
        startTime: Long,
        endTime: Long,
        subject: String,
        steps: Int,
        completeListener: OnCompleteListener<Void>
    ): Task<Void> {
        val dataSource: DataSource = DataSource.Builder()
            .setAppPackageName(fragment.context)
            .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .setStreamName(subject)
            .setType(DataSource.TYPE_RAW)
            .build()

        val dataSet = DataSet.create(dataSource)
        // For each data point, specify a start time, end time, and the data value -- in this case,
        // the number of new steps.
        val dataPoint: DataPoint = dataSet.createDataPoint()
        dataPoint.setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
        dataPoint.getValue(Field.FIELD_STEPS).setInt(steps)
        dataSet.add(dataPoint)

        return Fitness.getHistoryClient(
            fragment.requireContext(),
            GoogleSignIn.getLastSignedInAccount(fragment.requireContext())!!
        ).insertData(dataSet).addOnCompleteListener(completeListener)
    }

    private fun deleteData(
        startTime: Long,
        endTime: Long,
        completeListener: OnCompleteListener<Void>
    ) {

        //  Create a delete request object, providing a data type and a time interval
        val request = DataDeleteRequest.Builder()
            .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .build()

        // Invoke the History API with the HistoryClient object and delete request, and then
        // specify a callback that will check the result.
        Fitness.getHistoryClient(
            fragment.requireContext(),
            GoogleSignIn.getLastSignedInAccount(fragment.requireContext())!!
        ).deleteData(request).addOnCompleteListener(completeListener)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            FitnessHelper.GOOGLE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    tempListener?.onComplete(true, 100, null)
                    //subscribe(listener = tempListener)
                }
            }
        }
    }


}