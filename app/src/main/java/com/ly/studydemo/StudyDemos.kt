package com.ly.studydemo

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StudyDemos : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.studydemos_main)

        // get data
        val path:String? = this.intent.getStringExtra("com.example.android.apis.Path")

        this.supportActionBar?.title = path ?: "StudyDemos"

        val recyclerView: RecyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)

        // set LayoutManager
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linearLayoutManager

        // set Adapter
        val demos: MutableList<DemosAdapter.Demo> = getData(path?:"")
        recyclerView.adapter = DemosAdapter(demos, object: DemosAdapter.OnItemClickListener {
            override fun onClick(holder: DemosAdapter.DemosViewHolder, demo: DemosAdapter.Demo) {
                this@StudyDemos.startActivity(demo.intent)
            }
        })

        // set 分隔线
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    private fun getData(prefix: String): MutableList<DemosAdapter.Demo> {
        val demos: MutableList<DemosAdapter.Demo> = mutableListOf<DemosAdapter.Demo>()

        val intent: Intent = Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_SAMPLE_CODE)
            .setPackage(this.packageName)

        val list: List<ResolveInfo> =
            this.packageManager.queryIntentActivities(intent, 0) ?: return demos

        var prefixPath: List<String>? = null
        var prefixWithSlash: String = prefix
        if(prefix != "") {
            prefixPath = prefix.split("/")
            prefixWithSlash = "$prefix/"
        }

        val map:MutableMap<String, Boolean> = mutableMapOf<String, Boolean>()

        for (info in list) {
            val label:String = info.activityInfo.loadLabel(this.packageManager).toString()

            if(prefixPath == null || label.startsWith(prefixWithSlash)) {

                val labelPath = label.split("/")

                // 获取当前的显示名称
                val nextLabel: String = labelPath[prefixPath?.size ?: 0]

                // 通过长度判断当是否是最后一个path
                if ((prefixPath?.size ?: 0) == (labelPath.size - 1)) {
                    val result = Intent()
                    result.setClassName(info.activityInfo.packageName, info.activityInfo.name)
                    demos.add(DemosAdapter.Demo(nextLabel, result))
                } else {
                    if(map.get(nextLabel) == null){
                        val result = Intent(this, StudyDemos::class.java)
                        result.putExtra("com.example.android.apis.Path", if(prefix == "") nextLabel else "${prefix}/${nextLabel}")
                        demos.add(DemosAdapter.Demo(nextLabel, result))
                        map.put(nextLabel, true)
                    }
                }
            }
        }
        return demos
    }
}

class DemosAdapter(var demos: MutableList<Demo>, var itemClickListener: OnItemClickListener) : RecyclerView.Adapter<DemosAdapter.DemosViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DemosViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val itemView = inflater.inflate(R.layout.item_demo, parent, false)
        return DemosViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return demos.size
    }

    override fun onBindViewHolder(holder: DemosViewHolder, position: Int) {
        val demo = demos[position]
        holder.titleView.setText(demo.title)
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(holder, demo)
        }
    }

    interface OnItemClickListener {
        fun onClick(holder: DemosViewHolder, demo: Demo)
    }

    class Demo(var title: String, var intent: Intent) {}

    class DemosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val titleView: TextView

        init {
            titleView = itemView.findViewById(R.id.title_view)
        }
    }
}
