/*
 *  Created by Sumeet Kumar on 1/15/20 3:39 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/15/20 3:39 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.program


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import life.mibo.hardware.core.Logger
import life.mibo.android.database.ProgramConverter

//@Parcelize
@Entity(tableName = "programs")
data class Program(
    @SerializedName("AccessType")
    var accessType: String?,
    @TypeConverters(ProgramConverter::class)
    @SerializedName("Blocks")
    var blocks: List<Block?>?,
    @SerializedName("BorgRating")
    var borgRating: Int?,
    @SerializedName("Category")
    var category: String?,
    @SerializedName("CircuitID")
    var circuitID: Int?,
    @SerializedName("CreatedBy")
    var createdBy: Int?,
    @SerializedName("Description")
    var description: String?,
    @SerializedName("Duration")
    @TypeConverters(ProgramConverter::class)
    var duration: Duration?,
    @SerializedName("Id")
    @PrimaryKey var id: Int?,
    @SerializedName("MemberID")
    var memberID: Int?,
    @SerializedName("Name")
    var name: String?,
    @SerializedName("Type")
    var type: String?
) {

    var isSelected = false

    // DialogListener wrapper to convert user program to hardware library program compatible
    fun create(): life.mibo.hardware.models.program.Program {
        val program = life.mibo.hardware.models.program.Program()
        program.setDuration(duration!!.valueInt())
        program.id = "$id"
        program.name = "$name"
        program.description = "$description"
        program.borgRating = borgRating!!

        if (blocks != null) {
            //val list = Array<life.mibo.hardware.models.program.Block>()[blocks!!.size];
            val list = ArrayList<life.mibo.hardware.models.program.Block>()

            blocks?.forEach {
                it?.create()?.let { it1 ->
                    list.add(it1)
                }
            }

            program.addBlocks(list)
        }

//        val block1 = life.mibo.hardware.models.program.Block()
//        block1.setBlockDuration(8000)
//        block1.setPauseDuration(4000)
//        block1.setActionDuration(4000)
//        block1.setUpRampDuration(0)
//        block1.setDownRampDuration(0)
//        block1.setPulseWidth(350)
//        block1.setFrequency(85)
//        val blocks = arrayOf(block1);
//        program.setBlocks(blocks)

        Logger.e("SelectProgramFragment program created $program")
        return program
    }

    constructor(color: Int) : this(
        null, null, null, null,
        null, null, null, null,
        color, null, null, null
    ) {

    }
}