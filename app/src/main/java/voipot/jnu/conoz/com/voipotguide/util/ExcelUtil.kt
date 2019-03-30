package voipot.jnu.conoz.com.voipotguide.util

import android.annotation.SuppressLint
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import voipot.jnu.conoz.com.voipotguide.room.Time
import voipot.jnu.conoz.com.voipotguide.room.entity.ScheduleEntity

import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*

class ExcelUtil {
    val resultVOList: LinkedList<ScheduleEntity> = LinkedList()
        get() {
            if (field.size == 0) {
                RuntimeException("fileOpen()을 이용해서 파일을 읽어주세요")
            }
            return field
        }

    @SuppressLint("SimpleDateFormat")
    fun fileOpen(filePath: String) {
        val fileExtension = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length)
        val scheduleFile = FileInputStream(filePath)

        if (fileExtension == "xls") {
            val workBook = HSSFWorkbook(scheduleFile)
            val sheet = workBook.getSheetAt(0)
            val row = sheet.physicalNumberOfRows

            for (i in row downTo 0) {
                val scheduleEntity = ScheduleEntity()
                scheduleEntity.time = Time(sheet.getRow(i).getCell(0).toString())
                scheduleEntity.title = sheet.getRow(i).getCell(1).toString()
                scheduleEntity.memo = sheet.getRow(i).getCell(2).toString()

                resultVOList.add(scheduleEntity)
            }
        } else if (fileExtension == "xlsx") {
            val workBook = XSSFWorkbook(scheduleFile)
            val sheet = workBook.getSheetAt(0)
            val row = sheet.physicalNumberOfRows

            println("sheet : $sheet")

            for (i in 0 until row) {
                val scheduleEntity = ScheduleEntity()
//                println("i : $i , cell count : " +sheet.getRow(i).physicalNumberOfCells)
                try {
                    scheduleEntity.time = Time(SimpleDateFormat("HH:mm").format(DateUtil.getJavaDate(sheet.getRow(i).getCell(0).numericCellValue)))
                    scheduleEntity.title = sheet.getRow(i).getCell(1).toString()
                    scheduleEntity.memo = sheet.getRow(i).getCell(2).toString()
                } catch (e: NullPointerException) {
                    //엑셀파일 이상있는경우 처리해주어야함
                }

                resultVOList.add(scheduleEntity)
            }
        }
    }
}