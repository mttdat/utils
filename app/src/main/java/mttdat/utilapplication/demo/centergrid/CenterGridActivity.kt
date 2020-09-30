package mttdat.utilapplication.demo.centergrid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import mttdat.centergridlayout.CenterGridAdapter
import mttdat.centergridlayout.CenterGridLayout
import mttdat.centergridlayout.CenterGridLayoutManager
import mttdat.utilapplication.R
import java.util.*

class CenterGridActivity : AppCompatActivity() {

    var centerGridLayout: CenterGridLayout? = null
    var centerGridAdapter: CenterGridAdapter<*>? = null
    var centerGridLayoutManager: CenterGridLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_center_grid)

        centerGridLayout = findViewById(R.id.center_grid)

        centerGridLayoutManager = CenterGridLayoutManager(
                CenterGridLayoutManager.VERTICAL, intArrayOf(2, 3, 3, 2),
                120)

        centerGridLayout?.setLayoutManager(centerGridLayoutManager)

        val list = ArrayList<String>()
        list.add("1")
        list.add("2")
        list.add("3")
        list.add("4")
        list.add("5")
        list.add("6")
        list.add("7")
        list.add("8")
        list.add("9")
        list.add("10")

        centerGridAdapter = TestCenterGridAdapter(R.layout.item_center_grid, list)

        centerGridLayout?.setAdapter(centerGridAdapter)
    }
}